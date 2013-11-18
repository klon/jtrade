package jtrade.io;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.NavigableMap;
import java.util.zip.GZIPOutputStream;

import jtrade.marketfeed.MarketDepth;
import jtrade.marketfeed.Tick;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class CsvTickFileWriter extends BufferedWriter implements TickWriter {
	private static final String dateFormat = "yyyy-MM-dd HH:mm:ss.SSS";

	protected String source;
	protected boolean headerWritten;
	protected DateTimeFormatter dateFormatter;

	public CsvTickFileWriter(File file, boolean append, String source) throws IOException {
		this(file, append, false, source);
	}

	public CsvTickFileWriter(File file, boolean append, boolean compress, String source) throws IOException {
		super(compress ? new OutputStreamWriter(new GZIPOutputStream(new FileOutputStream(file.getPath().concat(".gz"), append))) : new FileWriter(file, append));
		if (append && compress) {
			close();
			throw new IllegalArgumentException("Cannot both append and compress");
		}
		if (file.exists() && file.length() > 0 && append) {
			headerWritten = true;
		}
		dateFormatter = DateTimeFormat.forPattern(dateFormat);
	}

	private void writeHeader(Tick tick) throws IOException {
		DateTime now = new DateTime();
		write("# This dataset is from ");
		write(source != null ? source : "<unknown>");
		write(", created on ");
		write(now.toString("yyyy-MM-dd HH:mm:ss"));
		write(".\n");
		write("# version=2\n");
		write("# symbol=");
		write(tick.getSymbol().getFullCode());
		write('\n');
		write("# dateFormat=");
		write(dateFormat);
		write('\n');
		write("# timeZone=");
		write(now.getZone().getID());
		write('\n');
		write("date,ask,asksize,bid,bidsize,last,lastsize,volume,ask1,asksize1,ask2,asksize2,ask3,asksize3,ask4,asksize4,ask5,asksize5,ask6,asksize6,ask7,asksize7,ask8,asksize8,ask9,asksize9,bid1,bidsize1,bid2,bidsize2,bid3,bidsize3,bid4,bidsize4,bid5,bidsize5,bid6,bidsize6,bid7,bidsize7,bid8,bidsize8,bid9,bidsize9");
		write('\n');
	}

	@Override
	public void write(Tick tick) throws IOException {
		if (!headerWritten) {
			writeHeader(tick);
			headerWritten = true;
		}
		write(dateFormatter.print(tick.getDateTime()));
		write(',');
		write(Double.toString(tick.getAsk()));
		write(',');
		write(Integer.toString(tick.getAskSize()));
		write(',');
		write(Double.toString(tick.getBid()));
		write(',');
		write(Integer.toString(tick.getBidSize()));
		write(',');
		write(Double.toString(tick.getPrice()));
		write(',');
		write(Integer.toString(tick.getLastSize()));
		write(',');
		write(Integer.toString(tick.getVolume()));

		MarketDepth marketDepth = tick.getMarketDepth();
		if (marketDepth != null) {
			int levels = marketDepth.getLevels();
			double[] prices = marketDepth.getAskPrices();
			int[] sizes = marketDepth.getAskSizes();
			for (int i = 1; i < levels; i++) {
				write(',');
				if (prices[i] > 0.0) {
					write(Double.toString(prices[i]));
				}
				write(',');
				if (sizes[i] > 0) {
					write(Integer.toString(sizes[i]));
				}
			}
			for (int i = levels; i < 10; i++) {
				write(",,");
			}
			prices = marketDepth.getBidPrices();
			sizes = marketDepth.getBidSizes();
			for (int i = 1; i < levels; i++) {
				write(',');
				if (prices[i] > 0.0) {
					write(Double.toString(prices[i]));
				}
				write(',');
				if (sizes[i] > 0) {
					write(Integer.toString(sizes[i]));
				}
			}
			for (int i = levels; i < 10; i++) {
				write(",,");
			}
		}
		write('\n');
	}

	@Override
	public void writeAll(NavigableMap<DateTime, Tick> ticks) throws IOException {
		try {
			for (Tick t : ticks.values()) {
				write(t);
			}
		} finally {
			try {
				close();
			} catch (IOException e) {
			}
		}
	}

}
