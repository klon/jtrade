package jtrade.io;

import java.io.IOException;
import java.util.NavigableMap;

import jtrade.marketfeed.Bar;

import org.joda.time.DateTime;

public interface BarReader {

	public Bar readBar() throws IOException;

	public NavigableMap<DateTime, Bar> readBars() throws IOException;

	public void close() throws IOException;

}
