package jtrade.io;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.zip.GZIPInputStream;

import jtrade.Symbol;
import jtrade.SymbolFactory;
import jtrade.marketfeed.MarketDepth;
import jtrade.marketfeed.MarketFeed;
import jtrade.marketfeed.Tick;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

public class BinaryTickFileReader extends DataInputStream implements TickReader {
	MarketFeed marketFeed;
	Symbol symbol;
	DateTimeZone tz;
	boolean skipMarketDepth;
	boolean hasMarketDepth;
	int marketDepthLevels;

	public BinaryTickFileReader(File file) throws IOException {
		this(file, null, false);
	}

	public BinaryTickFileReader(File file, boolean skipMarketDepth) throws IOException {
		this(file, null, skipMarketDepth);
	}

	public BinaryTickFileReader(File file, MarketFeed marketFeed) throws IOException {
		this(file, marketFeed, false);
	}

	public BinaryTickFileReader(File file, MarketFeed marketFeed, boolean skipMarketDepth) throws IOException {
		super(new BufferedInputStream(file.getPath().endsWith(".gz") ? new GZIPInputStream(new FileInputStream(file), 8192) : new FileInputStream(file)));
		this.marketFeed = marketFeed;
		this.skipMarketDepth = skipMarketDepth;
		this.marketDepthLevels = 5;
		readHeader();
	}

	private void readHeader() throws IOException {
		StringBuilder sb = new StringBuilder();
		char c;
		while ((c = readChar()) != '|') {
			sb.append(c);
		}
		symbol = SymbolFactory.getSymbol(sb.toString().toUpperCase().trim());
		sb.setLength(0);
		while ((c = readChar()) != '|') {
			sb.append(c);
		}
		tz = DateTimeZone.forID(sb.toString().trim());
		sb.setLength(0);
		while ((c = readChar()) != '|') {
			sb.append(c);
		}
		if (sb.toString().equals("L1")) {
			hasMarketDepth = false;
		} else if (sb.toString().equals("L2")) {
			hasMarketDepth = true;
		} else {
			throw new IOException("Invalid header");
		}
	}

	@Override
	public Tick readTick() throws IOException {
		try {
			if (!hasMarketDepth) {
				return new Tick(symbol, new DateTime(readLong(), tz), readFloat(), readInt(), readFloat(), readInt(), readFloat(), readInt(), readInt(),
						null);
			}
			DateTime dateTime = new DateTime(readLong(), tz);
			double askPrice = readFloat();
			int askSize = readInt();
			double bidPrice = readFloat();
			int bidSize = readInt();
			double lastPrice = readFloat();
			int lastSize = readInt();
			int volume = readInt();
			if (skipMarketDepth) {
				skipBytes(144);
				return new Tick(symbol, dateTime, askPrice, askSize, bidPrice, bidSize, lastPrice, lastSize, volume, null);
			}
			MarketDepth md = new MarketDepth(marketDepthLevels);
			double[] askPrices = md.getAskPrices();
			int[] askSizes = md.getAskSizes();
			askPrices[0] = askPrice;
			askSizes[0] = askSize;
			for (int p = 1; p < askPrices.length; p++) {
				askPrices[p] = readFloat();
				askSizes[p] = readInt();
			}
			skipBytes((10 - marketDepthLevels) * 8);
			double[] bidPrices = md.getBidPrices();
			int[] bidSizes = md.getBidSizes();
			bidPrices[0] = bidPrice;
			bidSizes[0] = bidSize;
			for (int p = 1; p < bidPrices.length; p++) {
				bidPrices[p] = readFloat();
				bidSizes[p] = readInt();
			}
			skipBytes((10 - marketDepthLevels) * 8);
			return new Tick(symbol, dateTime, askPrice, askSize, bidPrice, bidSize, lastPrice, lastSize, volume, md);
		} catch (EOFException e) {
			return null;
		}
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
