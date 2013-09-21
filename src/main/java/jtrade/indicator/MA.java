package jtrade.indicator;

import jtrade.util.DoubleBuffer;

public class MA extends AbstractIndicator {
	int period;
	DoubleBuffer buf;
	double value;

	public MA(int period) {
		this.period = period;
		this.buf = new DoubleBuffer(period);
		value = Double.NaN;
	}

	public void reset() {
		buf.clear();
		value = Double.NaN;
	}
	
	public int getPeriod() {
		return period;
	}


	public double update(long t, double v) {
		if (v == v) {
			buf.push(v);
		}
		if (buf.isFull()) {
			value = buf.mean();
		}
		return value;
	}

	public double get() {
		return value;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("MA [value=");
		sb.append(get());
		sb.append(", period=");
		sb.append(period);
		sb.append("]");
		return sb.toString();
	}

}
