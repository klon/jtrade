package jtrade.marketfeed;

import jtrade.indicator.AbstractIndicator;
import jtrade.indicator.Median;

public class MedianCleaner extends AbstractIndicator implements Cleaner {
	private double multiple;
	private Median median;
	private long t0;
	private double v0;

	public MedianCleaner(int period, double multiple) {
		this.multiple = multiple;
		median = new Median(period);
	}

	@Override
	public void reset() {
		median.reset();
		t0 = 0;
		v0 = 0;
	}

	@Override
	public double update(long t, double v) {
		if (t < 0 || (t0 >= 0 && t < t0) || v != v || v <= 0) {
			return Double.NaN;
		}
		if (v0 <= 0) {
			t0 = t;
			v0 = v;
			return v;
		}
		double logReturn = Math.log(v / v0);
		double tDiff = Math.sqrt(t - t0) / 86400.0;
		double adjReturn = Math.abs(logReturn / tDiff);
		double adjReturnMedian = adjReturn > 0 ? median.update(t, adjReturn) : median.get();
		if (adjReturn != adjReturn) {
			return Double.NaN;
		}
		if (adjReturnMedian > 0.0 && adjReturn > adjReturnMedian * multiple) {
			return Double.NaN;
		}
		t0 = t;
		v0 = v;
		return v;
	}

	@Override
	public double get() {
		return v0;
	}
}
