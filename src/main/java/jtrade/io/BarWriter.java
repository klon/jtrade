package jtrade.io;

import java.io.IOException;
import java.util.NavigableMap;

import jtrade.marketfeed.Bar;

import org.joda.time.DateTime;

public interface BarWriter {

	public void write(Bar bar) throws IOException;

	public void writeAll(NavigableMap<DateTime, Bar> bars) throws IOException;

	public void flush() throws IOException;

	public void close() throws IOException;
}
