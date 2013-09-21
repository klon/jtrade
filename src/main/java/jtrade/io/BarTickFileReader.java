package jtrade.io;

import java.io.File;
import java.io.IOException;
import java.util.NavigableMap;
import java.util.TreeMap;

import jtrade.marketfeed.Bar;
import jtrade.marketfeed.Tick;
import jtrade.util.Util;

import org.joda.time.DateTime;
import org.joda.time.Duration;

public class BarTickFileReader implements BarReader {
	TickReader reader;
	int barSizeMillis;
	Duration barSize;
	Bar currBar;
	Bar prevBar;

	public BarTickFileReader(File file, int barSizeSeconds) throws IOException {
		reader = MarketDataIO.createTickReader(file, true);
		barSizeMillis = barSizeSeconds * 1000;
		barSize = new Duration(barSizeMillis);
	}

	@Override
	public Bar readBar() throws IOException {
		Tick tick = null;
		double open = 0.0, high = 0.0, low = 0.0, close = 0.0, wap = 0.0;
		int trades = 0;
		long volume = 0;
		while ((tick = reader.readTick()) != null) {
			long now = tick.getDateTime().getMillis();
			double tickPrice = tick.getPrice();
			int tickVol = tick.getLastSize();
			boolean complete = false;
			if (currBar == null) {
				currBar = new Bar(barSize, tick.getSymbol(), new DateTime(now - (now % barSizeMillis)));
			} else if (currBar.getDateTime().getMillis() + barSizeMillis <= now) {
				prevBar = currBar;
				currBar = new Bar(barSize, tick.getSymbol(), new DateTime(now - (now % barSizeMillis)));
				complete = true;
			}
			if (open == 0.0) {
				open = tickPrice;
				high = tickPrice;
				low = tickPrice;
			} else {
				if (tickPrice > high) {
					high = tickPrice;
				}
				if (tickPrice < low) {
					low = tickPrice;
				}
			}
			close = tickPrice;
			wap = Util.round((wap * volume + tickPrice * tickVol) / (volume + tickVol), 2);
			volume += tickVol;
			trades++;
			currBar.setValues(open, high, low, close, wap, volume, trades);
			if (complete) {
				return prevBar;
			}
		}
		if (currBar != null && currBar.isComplete()) {
			return currBar;
		}
		return null;
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

	@Override
	public void close() throws IOException {
		reader.close();
	}

}
