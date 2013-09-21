package jtrade.indicator;

import jtrade.util.DoubleBuffer;
import jtrade.util.DoubleSkipList;

/**
 * Fast running median with O(log n) updates where n is the window size
 * http://code
 * .activestate.com/recipes/576930-efficient-running-median-using-an-indexable
 * -skipli/
 * 
 * @author jonkle
 */
public class Median extends AbstractIndicator {
	int period;
	double percentile;
	DoubleBuffer buf;
	DoubleSkipList skip;
	double value;

	public Median() {
		this(100, 0.5);
	}

	public Median(int period) {
		this(period, 0.5);
	}

	public Median(int period, double percentile) {
		this.period = period;
		this.percentile = percentile;
		buf = new DoubleBuffer(period);
		skip = new DoubleSkipList(period);
		value = Double.NaN;
	}

	@Override
	public void reset() {
		buf.clear();
		skip.clear();
		value = Double.NaN;
	}

	@Override
	public double update(long t, double v) {
		buf.push(v);
		skip.add(v);
		value = skip.get((int) (skip.size() * percentile));
		if (skip.size() >= period) {
			skip.remove(buf.first());
		}
		return value;
	}

	@Override
	public double get() {
		return value;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Median [value=");
		sb.append(get());
		sb.append(", period=");
		sb.append(period);
		sb.append("]");
		return sb.toString();
	}

}
