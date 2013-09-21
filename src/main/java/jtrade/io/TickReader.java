package jtrade.io;

import java.io.IOException;
import java.util.NavigableMap;

import jtrade.marketfeed.Tick;

import org.joda.time.DateTime;

public interface TickReader {

	public Tick readTick() throws IOException;

	public NavigableMap<DateTime, Tick> readTicks() throws IOException;

	public void close() throws IOException;
}
