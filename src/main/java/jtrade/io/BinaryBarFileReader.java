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
import jtrade.marketfeed.Bar;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Duration;
import org.joda.time.format.DateTimeFormatter;

public class BinaryBarFileReader extends DataInputStream implements BarReader {
	Symbol symbol;
	Duration barSize;
	DateTimeZone tz;
	DateTimeFormatter dateFormatter;
	String[] tmp;

	public BinaryBarFileReader(String file) throws IOException {
		this(new File(file));
	}

	public BinaryBarFileReader(File file) throws IOException {
		super(new BufferedInputStream(file.getPath().endsWith(".gz") ? new GZIPInputStream(new FileInputStream(file), 8192) : new FileInputStream(file)));
		tmp = new String[8];
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
		barSize = new Duration(Integer.parseInt(sb.toString()) * 1000);
	}

	@Override
	public Bar readBar() throws IOException {
		try {
			return new Bar(barSize, symbol, new DateTime(readLong(), tz), readFloat(), readFloat(), readFloat(), readFloat(), readFloat(), readLong(),
					readInt());
		} catch (EOFException e) {
			return null;
		}
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
