package jtrade.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.zip.GZIPInputStream;

import jtrade.Symbol;
import jtrade.SymbolFactory;
import jtrade.marketfeed.MarketDepth;
import jtrade.marketfeed.MarketFeed;
import jtrade.marketfeed.Tick;
import jtrade.util.Util;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class CsvTickFileReader extends BufferedReader implements TickReader {
	MarketFeed marketFeed;
	Symbol symbol;
	DateTimeZone tz;
	DateTimeFormatter dateFormatter;
	boolean skipMarketDepth;
	String[] tmp;

	public CsvTickFileReader(File file) throws IOException {
		this(file, null, false);
	}

	public CsvTickFileReader(File file, boolean skipMarketDepth) throws IOException {
		this(file, null, skipMarketDepth);
	}

	public CsvTickFileReader(File file, MarketFeed marketFeed) throws IOException {
		this(file, marketFeed, false);
	}

	public CsvTickFileReader(File file, MarketFeed marketFeed, boolean skipMarketDepth) throws IOException {
		super(file.getPath().endsWith(".gz") ? new InputStreamReader(new GZIPInputStream(new FileInputStream(file), 8192)) : new FileReader(file));
		this.marketFeed = marketFeed;
		this.skipMarketDepth = skipMarketDepth;
		this.tmp = new String[45];
		readHeader();
	}

	private void readHeader() throws IOException {
		tz = DateTimeZone.getDefault();
		dateFormatter = DateTimeFormat.forPattern("yyyyMMdd HH:mm:ss");
		String line = null;
		while ((line = readLine()) != null) {
			if (line.startsWith("# symbol")) {
				symbol = SymbolFactory.getSymbol(line.substring(9).trim());
				continue;
			}
			if (line.startsWith("# timeZone")) {
				tz = DateTimeZone.forID(line.substring(11).trim());
				dateFormatter = dateFormatter.withZone(tz);
				continue;
			}
			if (line.startsWith("# dateFormat")) {
				dateFormatter = DateTimeFormat.forPattern(line.substring(13).trim()).withZone(tz);
				continue;
			}
			if (line.startsWith("#")) {
				continue;
			}
			if (line.startsWith("date,")) {
				if (!line.endsWith(",bidsize9")) {
					skipMarketDepth = true;
				}
				return;
			}
		}
	}

	@Override
	public Tick readTick() throws IOException {
		String line = readLine();
		if (line == null) {
			return null;
		}
		String[] values = Util.split(line, ',', tmp);
		try {
			if (skipMarketDepth || values[8] == null) {
				return parseTick(values);
			}
			return parseTickMarketDepth(values);

		} catch (Exception e) {
			throw new IOException("Failed to read tick, unknown format: " + line, e);
		}
	}

	private Tick parseTick(String[] values) {
		Tick tick = new Tick(symbol, dateFormatter.parseDateTime(values[0]), Double.parseDouble(values[1]), Integer.parseInt(values[2]),
				Double.parseDouble(values[3]), Integer.parseInt(values[4]), Double.parseDouble(values[5]), Integer.parseInt(values[6]), Integer.parseInt(values[7]),
				null);
		return tick;
	}

	private Tick parseTickMarketDepth(String[] values) {
		MarketDepth md = new MarketDepth(10);

		double[] askPrices = md.getAskPrices();
		int[] askSizes = md.getAskSizes();
		double askPrice = askPrices[0] = Double.parseDouble(values[1]);
		int askSize = askSizes[0] = Integer.parseInt(values[2]);
		for (int i = 8, p = 1; p < 10; i++, p++) {
			if (values[i].length() == 0) {
				continue;
			}
			double price = Double.parseDouble(values[i]);
			if (price <= 0.0) {
				continue;
			}
			i++;
			if (values[i].length() == 0) {
				continue;
			}
			int size = Integer.parseInt(values[i]);
			if (size <= 0) {
				continue;
			}
			askPrices[p] = price;
			askSizes[p] = size;
		}
		double[] bidPrices = md.getBidPrices();
		int[] bidSizes = md.getBidSizes();
		double bidPrice = bidPrices[0] = Double.parseDouble(values[3]);
		int bidSize = bidSizes[0] = Integer.parseInt(values[4]);
		for (int i = 26, p = 1; p < 10; i++, p++) {
			if (values[i].length() == 0) {
				continue;
			}
			double price = Double.parseDouble(values[i]);
			if (price <= 0.0) {
				continue;
			}
			i++;
			if (values[i].length() == 0) {
				continue;
			}
			int size = Integer.parseInt(values[i]);
			if (size <= 0) {
				continue;
			}
			bidPrices[p] = price;
			bidSizes[p] = size;
		}

		Tick tick = new Tick(symbol, dateFormatter.parseDateTime(values[0]), askPrice, askSize, bidPrice, bidSize, Double.parseDouble(values[5]),
				Integer.parseInt(values[6]), Integer.parseInt(values[7]), md);
		return tick;
	}

	@Override
	public NavigableMap<DateTime, Tick> readTicks() throws IOException {
		try {
			NavigableMap<DateTime, Tick> ticks = new TreeMap<DateTime, Tick>();
			Tick t = null;
			while ((t = readTick()) != null) {
				ticks.put(t.getDateTime(), t);
			}
			return ticks;
		} finally {
			try {
				close();
			} catch (IOException e) {
			}
		}
	}
}
