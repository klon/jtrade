package jtrade.marketfeed;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import jtrade.JTradeException;
import jtrade.Symbol;
import jtrade.io.MarketDataIO;
import jtrade.io.TickReader;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TickFileMarketFeed extends FileMarketFeed {
	private static final Logger logger = LoggerFactory.getLogger(TickFileMarketFeed.class);

	protected SynchronizedTickDataReader tickDataReader;
	protected Class<? extends TickDataFileReader> tickDataFileReaderClass;
	protected List<TickListener> tickListeners;

	public TickFileMarketFeed() {
		this(null, null, null, (Symbol[]) null);
	}

	public TickFileMarketFeed(File dataDir) {
		this(dataDir, null, null, (Symbol[]) null);
	}

	public TickFileMarketFeed(String dataDir) {
		this(new File(dataDir), null, null, (Symbol[]) null);
	}

	public TickFileMarketFeed(File dataDir, DateTime fromDate, DateTime toDate) {
		this(dataDir, fromDate, toDate, (Symbol[]) null);
	}

	public TickFileMarketFeed(TickFileMarketFeed marketFeed) {
		this(marketFeed.dataDir, marketFeed.fromDate, marketFeed.toDate, marketFeed.symbols);
	}

	public TickFileMarketFeed(String dataDir, String fromDate, String toDate, String... symbols) {
		this(new File(dataDir), DateTimeFormat.forPattern("yyyyMMdd").parseDateTime(fromDate), DateTimeFormat.forPattern("yyyyMMdd").parseDateTime(toDate),
				parseSymbols(symbols));
	}

	public TickFileMarketFeed(String dataDir, String fromDate, String toDate, List<Symbol> symbols) {
		this(new File(dataDir), DateTimeFormat.forPattern("yyyyMMdd").parseDateTime(fromDate), DateTimeFormat.forPattern("yyyyMMdd").parseDateTime(toDate), symbols
				.toArray(new Symbol[symbols.size()]));
	}

	public TickFileMarketFeed(File dataDir, DateTime fromDate, DateTime toDate, Symbol... symbols) {
		super(dataDir);
		this.tickDataFileReaderClass = DefaultTickDataFileReader.class;
		reset(fromDate, toDate, symbols);
	}

	@Override
	public void reset(DateTime fromDate, DateTime toDate, Symbol... symbols) {
		if (isConnected()) {
			disconnect();
		}
		this.fromDate = fromDate;
		this.toDate = toDate;
		this.symbols = symbols;
		filesBySymbol = findSymbolFiles(symbols, fromDate, toDate, 0, true);
		tickDataReader = new SynchronizedTickDataReader();
		tickListeners = new ArrayList<TickListener>();

		if (fromDate != null && toDate != null) {
			logger.info(String.format("Initialized market feed, from %s to %s using data folder \"%s\": %s", fromDate.toString("yyyy-MM-dd"),
					toDate.toString("yyyy-MM-dd"), dataDir, filesBySymbol.toString()));
		} else {
			logger.info(String.format("Initialized market feed using data folder \"%s\": %s", dataDir, filesBySymbol.toString()));
		}
	}

	@Override
	public void doConnect() {
		new Thread(tickDataReader).start();
	}

	@Override
	public void doDisconnect() {
		tickDataReader.stop();
		try {
			while (!tickDataReader.isDone()) {
				Thread.sleep(50);
			}
		} catch (InterruptedException e) {
		}
	}

	@Override
	public void removeAllListeners() {
		super.removeAllListeners();
		for (TickDataFileReader r : tickDataReader.readers) {
			r.listeners.clear();
		}
		tickListeners.clear();
	}

	@Override
	public Tick getLastTick(Symbol symbol) {
		TickDataFileReader r = tickDataReader.readersBySymbol.get(symbol);
		return r != null ? r.prevTick : null;
	}

	@Override
	public void addTickListener(TickListener listener) {
		tickListeners.add(listener);
	}

	@Override
	public void removeTickListener(TickListener listener) {
		tickListeners.remove(listener);
		for (TickDataFileReader reader : tickDataReader.readersBySymbol.values()) {
			removeTickListener(reader.symbol, listener);
		}
	}

	@Override
	public void addTickListener(Symbol symbol, TickListener listener) {
		addTickListener(symbol, listener, false, null);
	}

	@Override
	public void addTickListener(Symbol symbol, TickListener listener, boolean marketDepth, Cleaner cleaner) {
		TickDataFileReader reader = tickDataReader.readersBySymbol.get(symbol);
		if (reader == null) {
			throw new IllegalArgumentException(String.format("Symbol has no matching data file: %s", symbol));
		}
		if (reader.cleaner == null && cleaner != null) {
			reader.cleaner = cleaner;
		}
		if (reader.skipMarketDepth && marketDepth) {
			reader.skipMarketDepth = false;
		}
		if (!reader.listeners.contains(listener)) {
			reader.listeners.add(listener);
		}
	}

	@Override
	public void removeTickListener(Symbol symbol, TickListener listener) {
		TickDataFileReader reader = tickDataReader.readersBySymbol.get(symbol);
		if (reader != null) {
			reader.listeners.remove(listener);
			if (reader.listeners.isEmpty() && reader.cleaner != null) {
				reader.cleaner.reset();
			}
		}
	}
	
	private void fireTickEvent(TickDataFileReader reader) {
		boolean valid = isValidTick(reader, reader.currTick);
		if (!valid && isValidTick(reader, reader.prevTick)) {
			reader.currTick.ask = reader.prevTick.ask;
			reader.currTick.bid = reader.prevTick.bid;
			reader.currTick.askSize = reader.prevTick.askSize;
			reader.currTick.bidSize = reader.prevTick.bidSize;
			reader.currTick.price = reader.prevTick.price;
			reader.currTick.lastSize = 0;
			reader.currTick.volume = reader.prevTick.volume;
			valid = true;
		}
		if (valid) {
			int len = reader.listeners.size();
			for (int i = 0; i < len; i++) {
				try {
					reader.listeners.get(i).onTick(reader.currTick);
				} catch (Throwable t) {
					logger.error(t.getMessage(), t);
				}
			}
			len = tickListeners.size();
			for (int i = 0; i < len; i++) {
				try {
					tickListeners.get(i).onTick(reader.currTick);
				} catch (Throwable t) {
					logger.error(t.getMessage(), t);
				}
			}
		}
	}

	private boolean isValidTick(TickDataFileReader reader, Tick tick) {
		if (tick == null) {
			return false;
		}
		if (tick.ask <= 0 || tick.bid <= 0 || tick.price <= 0 || tick.ask < tick.bid) {
			return false;
		}
		if (reader.cleaner != null && Double.isNaN(reader.cleaner.update(tick.dateTime, tick.price))) {
			return false;
		}
		return true;
	}

	final class SynchronizedTickDataReader implements Runnable {
		Map<Symbol, TickDataFileReader> readersBySymbol;
		LinkedList<TickDataFileReader> readers;
		boolean running;
		boolean done;

		SynchronizedTickDataReader() {
			this.readersBySymbol = new LinkedHashMap<Symbol, TickDataFileReader>();
			this.readers = new LinkedList<TickDataFileReader>();
			for (Entry<Symbol, List<File>> entry : filesBySymbol.entrySet()) {
				try {
					TickDataFileReader context = (TickDataFileReader) TickFileMarketFeed.this.tickDataFileReaderClass.getConstructors()[0].newInstance(
							TickFileMarketFeed.this, entry.getKey(), entry.getValue());
					readersBySymbol.put(entry.getKey(), context);
				} catch (Exception e) {
					throw new JTradeException(e);
				}
			}
		}

		public void stop() {
			running = false;
			for (TickDataFileReader reader : readersBySymbol.values()) {
				reader.reset();
			}
		}

		public boolean isDone() {
			return done;
		}

		@Override
		public void run() {
			long start = System.currentTimeMillis();
			running = true;
			try {
				Tick t = null;
				DateTime nextDay = null;
				DateTime nextHour = null;
				DateTime nextMinute = null;

				for (TickDataFileReader reader : readersBySymbol.values()) {
					reader.openNextFile();
					while ((t = reader.nextTick()) != null && !fromDate.isBefore(t.getDateTime())) {
					}
					if (t == null) {
						reader.reader.close();
						continue;
					}
					nextMinute = (nextMinute == null || t.getDateTime().isBefore(nextMinute) ? t.getDateTime() : nextMinute);
					readers.add(reader);
				}
				if (readers.isEmpty()) {
					return;
				}

				while (running && !readers.isEmpty()) {
					Collections.sort(readers);
					TickDataFileReader reader = readers.removeFirst();
					t = reader.currTick;

					nextDay = nextMinute.dayOfMonth().roundCeilingCopy();
					nextHour = nextMinute.hourOfDay().roundCeilingCopy();
					nextMinute = nextMinute.minuteOfHour().roundCeilingCopy();

					while (nextMinute.isBefore(t.dateTime) || nextMinute.isEqual(t.dateTime)) {
						fireMinuteEvent(nextMinute);
						if (nextHour.isBefore(nextMinute) || nextHour.isEqual(nextMinute)) {
							fireHourEvent(nextHour);
							if (nextDay.isBefore(nextHour) || nextDay.isEqual(nextHour)) {
								fireDayEvent(nextDay);
								nextDay = nextDay.plusDays(1);
							}
							nextHour = nextHour.plusHours(1);
						}
						nextMinute = nextMinute.plusMinutes(1);
					}

					fireTickEvent(reader);

					t = reader.nextTick();
					if (t == null || t.getDateTime().isAfter(toDate)) {
						reader.reader.close();
						continue;
					}
					readers.add(reader);
				}

			} catch (Throwable t) {
				logger.error(t.getMessage(), t);
			} finally {
				for (TickDataFileReader reader : readers) {
					try {
						reader.reader.close();
					} catch (Exception e) {
					}
				}
				readers.clear();
				done = true;
				if (logger.isDebugEnabled()) {
					logger.debug("run() time: {}", (System.currentTimeMillis() - start));
				}
				TickFileMarketFeed.this.disconnect();
			}
		}
	}

	public abstract class TickDataFileReader implements Comparable<TickDataFileReader> {
		Symbol symbol;
		List<File> files;
		int next_file;
		TickReader reader;
		Tick currTick;
		Tick prevTick;
		List<TickListener> listeners;
		Cleaner cleaner;
		boolean skipMarketDepth;

		public TickDataFileReader(Symbol symbol, List<File> files) throws IOException {
			this.symbol = symbol;
			this.files = files;
			this.listeners = new ArrayList<TickListener>();
		}

		public void reset() {
			next_file = 0;
			currTick = null;
			prevTick = null;
			if (cleaner != null) {
				cleaner.reset();
			}
		}

		public abstract void openNextFile() throws IOException;

		public boolean hasNextFile() {
			return next_file < files.size();
		}

		public abstract Tick nextTick() throws IOException;

		@Override
		public int compareTo(TickDataFileReader o) {
			return currTick.dateTime.compareTo(o.currTick.dateTime);
		}

		@Override
		protected void finalize() throws Throwable {
			if (reader != null) {
				reader.close();
			}
		}
	}

	public class DefaultTickDataFileReader extends TickDataFileReader {

		public DefaultTickDataFileReader(Symbol symbol, List<File> file) throws IOException {
			super(symbol, file);
		}

		@Override
		public void openNextFile() throws IOException {
			if (reader != null) {
				reader.close();
			}
			reader = MarketDataIO.createTickReader(files.get(next_file++), skipMarketDepth);
		}

		@Override
		public Tick nextTick() throws IOException {
			while (true) {
				Tick tick = reader.readTick();
				if (tick == null) {
					if (hasNextFile()) {
						openNextFile();
						continue;
					}
					return null;
				}
				prevTick = currTick;
				currTick = tick;
				return currTick;
			}
		}
	}
}
