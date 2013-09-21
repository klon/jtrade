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
import jtrade.marketfeed.Bar;
import jtrade.util.Util;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Duration;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class CsvBarFileReader extends BufferedReader implements BarReader {
	Symbol symbol;
	Duration barSize;
	DateTimeZone tz;
	DateTimeFormatter dateFormatter;
	String[] tmp;

	public CsvBarFileReader(File file) throws IOException {
		super(file.getPath().endsWith(".gz") ? new InputStreamReader(new GZIPInputStream(new FileInputStream(file), 8192)) : new FileReader(file));
		tmp = new String[8];
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
			if (line.startsWith("# barSizeSeconds")) {
				barSize = Duration.standardSeconds(Integer.parseInt(line.substring(17).trim()));
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
				return;
			}
		}
	}

	@Override
	public Bar readBar() throws IOException {
		String line = readLine();
		if (line == null) {
			return null;
		}
		String[] values = Util.split(line, ',', tmp);
		Bar bar = new Bar(barSize, symbol, dateFormatter.parseDateTime(values[0]), Double.parseDouble(values[1]), Double.parseDouble(values[2]),
				Double.parseDouble(values[3]), Double.parseDouble(values[4]), Double.parseDouble(values[7]), Long.parseLong(values[5]), Integer.parseInt(values[6]));

		return bar;
	}

	@Override
	public NavigableMap<DateTime, Bar> readBars() throws IOException {
		try {
			NavigableMap<DateTime, Bar> bars = new TreeMap<DateTime, Bar>();
			Bar bar = null;
			while ((bar = readBar()) != null) {
				bars.put(bar.getDateTime(), bar);
			}
			return bars;
		} finally {
			try {
				close();
			} catch (IOException e) {
			}
		}
	}

}
