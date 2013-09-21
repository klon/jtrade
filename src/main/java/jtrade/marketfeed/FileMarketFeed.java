package jtrade.marketfeed;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Set;
import java.util.TreeMap;

import jtrade.Symbol;
import jtrade.SymbolFactory;
import jtrade.io.MarketDataIO;
import jtrade.util.DateTimeRange;
import jtrade.util.Util;

import org.joda.time.DateTime;
import org.joda.time.Months;

public abstract class FileMarketFeed extends AbstractMarketFeed {
	private static final NavigableMap<DateTime, Bar> emptyBarMap = new TreeMap<DateTime, Bar>();

	protected static Symbol[] parseSymbols(String[] symbols) {
		if (symbols == null) {
			return null;
		}
		Symbol[] s = new Symbol[symbols.length];
		for (int i = 0; i < symbols.length; i++) {
			s[i] = SymbolFactory.getSymbol(symbols[i]);
		}
		return s;
	}

	protected boolean connected;
	protected DateTime fromDate;
	protected DateTime toDate;
	protected Symbol[] symbols;
	protected Map<Symbol, List<File>> filesBySymbol;

	public FileMarketFeed() {
		this((File) null);
	}

	public FileMarketFeed(File dataDir) {
		super(dataDir);
	}

	protected Map<Symbol, List<File>> findSymbolFiles(Symbol[] symbols, DateTime fromDate, DateTime toDate, int barSizeSeconds, boolean useTickData) {
		if (symbols == null) {
			return new TreeMap<Symbol, List<File>>();
		}
		Set<Symbol> set = new HashSet<Symbol>(symbols.length);
		for (final Symbol s : symbols) {
			if (s.isFuture()) {
				String[] files = dataDir.list(new FilenameFilter() {
					@Override
					public boolean accept(File dir, String name) {
						return name.startsWith(s.getFullCode());
					}
				});
				for (String f : files) {
					set.add(SymbolFactory.getSymbol(Util.join(Util.split(f, '-'), "-", 0, 5)));
				}
			} else {
				set.add(s);
			}
		}

		Map<Symbol, List<File>> filesBySymbol = new TreeMap<Symbol, List<File>>();
		DateTimeRange range = new DateTimeRange(fromDate.withDayOfMonth(1), toDate.withDayOfMonth(1));
		for (Symbol s : set) {
			List<File> sf = filesBySymbol.get(s);
			if (sf == null) {
				sf = new ArrayList<File>();
				filesBySymbol.put(s, sf);
			}
			for (Iterator<DateTime> dates = range.iterator(Months.ONE); dates.hasNext();) {
				File f = MarketDataIO.createReadFile(s, dates.next(), useTickData ? 0 : barSizeSeconds, dataDir);
				if (f.exists()) {
					sf.add(f);
				}
			}
		}
		for (Iterator<List<File>> iter = filesBySymbol.values().iterator(); iter.hasNext();) {
			if (iter.next().isEmpty()) {
				iter.remove();
			}
		}
		return filesBySymbol;
	}

	@Override
	public void connect() {
		if (connected) {
			return;
		}
		connected = true;
		doConnect();
	}

	@Override
	public void disconnect() {
		if (!connected) {
			return;
		}
		doDisconnect();
		connected = false;
	}

	public abstract void reset(DateTime fromDate, DateTime toDate, Symbol... symbol);

	protected abstract void doConnect();

	protected abstract void doDisconnect();

	@Override
	public boolean isConnected() {
		return connected;
	}

	@Override
	protected NavigableMap<DateTime, Bar> fetchHistoricalData(Symbol symbol, DateTime fromDate, DateTime toDate, int barSizeSeconds) {
		return emptyBarMap;
	}

	@Override
	public Bar getLastBar(Symbol symbol) {
		throw new UnsupportedOperationException(String.format("%s does not support BarListeners", this.getClass().getSimpleName()));
	}

	@Override
	public void addBarListener(BarListener listener) {
		throw new UnsupportedOperationException(String.format("%s does not support BarListeners", this.getClass().getSimpleName()));
	}

	@Override
	public void removeBarListener(BarListener listener) {
		throw new UnsupportedOperationException(String.format("%s does not support BarListeners", this.getClass().getSimpleName()));
	}

	@Override
	public void addBarListener(Symbol symbol, BarListener listener) {
		throw new UnsupportedOperationException(String.format("%s does not support BarListeners", this.getClass().getSimpleName()));
	}

	@Override
	public void addBarListener(Symbol symbol, BarListener listener, int barSizeSeconds, Cleaner cleaner) {
		throw new UnsupportedOperationException(String.format("%s does not support BarListeners", this.getClass().getSimpleName()));
	}

	@Override
	public void removeBarListener(Symbol symbol, BarListener listener) {
		throw new UnsupportedOperationException(String.format("%s does not support BarListeners", this.getClass().getSimpleName()));
	}

	@Override
	public void addTickListener(TickListener listener) {
		throw new UnsupportedOperationException(String.format("%s does not support TickListeners", this.getClass().getSimpleName()));
	}

	@Override
	public void removeTickListener(TickListener listener) {
		throw new UnsupportedOperationException(String.format("%s does not support TickListeners", this.getClass().getSimpleName()));
	}

	@Override
	public void addTickListener(Symbol symbol, TickListener listener) {
		throw new UnsupportedOperationException(String.format("%s does not support TickListeners", this.getClass().getSimpleName()));
	}

	@Override
	public void addTickListener(Symbol symbol, TickListener listener, boolean marketDepth, Cleaner cleaner) {
		throw new UnsupportedOperationException(String.format("%s does not support TickListeners", this.getClass().getSimpleName()));
	}

	@Override
	public void removeTickListener(Symbol symbol, TickListener listener) {
		throw new UnsupportedOperationException(String.format("%s does not support TickListeners", this.getClass().getSimpleName()));
	}
}
