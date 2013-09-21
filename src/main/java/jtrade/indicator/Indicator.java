package jtrade.indicator;

import jtrade.marketfeed.Bar;
import jtrade.marketfeed.Tick;

import org.joda.time.DateTime;

public interface Indicator {

	public double update(long time, double value);

	public double update(long time, double[] values);

	public double update(DateTime dt, double value);

	public double update(DateTime dt, double[] values);

	public double[] update(long[] time, double[] values);

	public double[] update(long[] time, double[][] values);

	public double update(Bar bar);
	
	public double update(Tick tick);

	public double get();

	public void reset();
}
