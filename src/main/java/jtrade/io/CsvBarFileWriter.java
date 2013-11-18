package jtrade.io;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.NavigableMap;
import java.util.zip.GZIPOutputStream;

import jtrade.marketfeed.Bar;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class CsvBarFileWriter extends BufferedWriter implements BarWriter {
	private static final String dateFormat = "yyyy-MM-dd HH:mm:ss.SSS";

	protected String source;
	protected boolean headerWritten;
	protected DateTimeFormatter dateFormatter;

	public CsvBarFileWriter(Bar bar, File dataDir, boolean append, String source) throws IOException {
		this(MarketDataIO.createWriteFile(bar, dataDir), append, source);
	}

	public CsvBarFileWriter(File file, boolean append, String source) throws IOException {
		this(file, append, false, source);
	}

	public CsvBarFileWriter(File file, boolean append, boolean compress, String source) throws IOException {
		super(compress ? new OutputStreamWriter(new GZIPOutputStream(new FileOutputStream(file.getPath().concat(".gz"), append))) : new FileWriter(file, append));
		this.source = source;
		if (append && compress) {
			close();
			throw new IllegalArgumentException("Cannot both append and compress");
		}
		if (file.exists() && file.length() > 0 && append) {
			headerWritten = true;
		}
		dateFormatter = DateTimeFormat.forPattern(dateFormat);
	}

	private void writeHeader(Bar bar) throws IOException {
		DateTime now = new DateTime();
		write("# This dataset is from ");
		write(source != null ? source : "<unknown>");
		write(", created on ");
		write(now.toString("yyyy-MM-dd HH:mm:ss"));
		write(".\n");
		write("# symbol=");
		write(bar.getSymbol().getFullCode());
		write('\n');
		write("# barSizeSeconds=");
		write(Integer.toString(bar.getBarSize().toStandardSeconds().getSeconds()));
		write('\n');
		write("# dateFormat=");
		write(dateFormat);
		write('\n');
		write("# timeZone=");
		write(now.getZone().getID());
		write('\n');
		write("date,open,high,low,close,volume,trades,wap");
		write('\n');
	}

	@Override
	public void write(Bar bar) throws IOException {
		if (!headerWritten) {
			writeHeader(bar);
			headerWritten = true;
		}
		write(dateFormatter.print(bar.getDateTime()));
		write(',');
		write(Double.toString(bar.getOpen()));
		write(',');
		write(Double.toString(bar.getHigh()));
		write(',');
		write(Double.toString(bar.getLow()));
		write(',');
		write(Double.toString(bar.getClose()));
		write(',');
		write(Long.toString(bar.getVolume()));
		write(',');
		write(Integer.toString(bar.getTrades()));
		write(',');
		write(Double.toString(bar.getPrice()));
		write('\n');
	}

	@Override
	public void writeAll(NavigableMap<DateTime, Bar> bars) throws IOException {
		try {
			for (Bar b : bars.values()) {
				write(b);
			}
		} finally {
			try {
				close();
			} catch (IOException e) {
			}
		}
	}
}
