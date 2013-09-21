package jtrade.io;

import java.io.IOException;
import java.util.NavigableMap;

import jtrade.marketfeed.Tick;

import org.joda.time.DateTime;

public interface TickWriter {

	public void write(Tick tick) throws IOException;

	public void writeAll(NavigableMap<DateTime, Tick> ticks) throws IOException;

	public void flush() throws IOException;

	public void close() throws IOException;
}
