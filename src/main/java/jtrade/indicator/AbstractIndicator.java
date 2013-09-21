package jtrade.indicator;

import jtrade.marketfeed.Bar;
import jtrade.marketfeed.BarListener;
import jtrade.marketfeed.Tick;
import jtrade.marketfeed.TickListener;
import jtrade.timeseries.TimeSeries;
import jtrade.timeseries.TimeSeriesArray;
import jtrade.timeseries.TimeSeriesOp;
import jtrade.util.Util;

import org.joda.time.DateTime;

public abstract class AbstractIndicator implements Indicator, TimeSeriesOp, BarListener, TickListener {

	public AbstractIndicator() {
	}

	@Override
	public double update(long t, double v) {
		throw new UnsupportedOperationException();
	}

	@Override
	public double update(long time, double[] values) {
		return update(time, values[0]);
	}

	@Override
	public double update(DateTime dt, double v) {
		return update(dt.getMillis(), v);
	}

	@Override
	public double update(DateTime dt, double[] values) {
		return update(dt.getMillis(), values);
	}

	@Override
	public double[] update(long[] times, double[] values) {
		final int len = times.length;
		double[] d = new double[len];
		for (int i = 0; i < len; i++) {
			d[i] = update(times[i], values[i]);
		}
		return d;
	}

	@Override
	public double[] update(long[] times, double[][] values) {
		final int len = times.length;
		double[] d = new double[len];
		for (int i = 0; i < len; i++) {
			d[i] = update(times[i], values[i]);
		}
		return d;
	}

	public double[] update(DateTime[] dates, double[] values) {
		final int len = dates.length;
		double[] d = new double[len];
		for (int i = 0; i < len; i++) {
			d[i] = update(dates[i].getMillis(), values[i]);
		}
		return d;
	}

	public double[] update(DateTime[] dates, double[][] values) {
		final int len = dates.length;
		double[] d = new double[len];
		for (int i = 0; i < len; i++) {
			d[i] = update(dates[i].getMillis(), values[i]);
		}
		return d;
	}

	public double[] update(DateTime[] dates, double[] values, String property) {
		try {
			final int len = dates.length;
			double[] d = new double[len];
			for (int i = 0; i < len; i++) {
				update(dates[i].getMillis(), values[i]);
				d[i] = Util.getDoubleProperty(this, property);
			}
			return d;
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		}
	}

	public double[] update(DateTime[] dates, double[][] values, String property) {
		try {
			final int len = dates.length;
			double[] d = new double[len];
			for (int i = 0; i < len; i++) {
				update(dates[i].getMillis(), values[i]);
				d[i] = Util.getDoubleProperty(this, property);
			}
			return d;
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		}
	}

	public double[] update(long[] times, double[] values, String property) {
		try {
			final int len = times.length;
			double[] d = new double[len];
			for (int i = 0; i < len; i++) {
				update(times[i], values[i]);
				d[i] = Util.getDoubleProperty(this, property);
			}
			return d;
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		}
	}

	public double[] update(long[] times, double[][] values, String property) {
		try {
			final int len = times.length;
			double[] d = new double[len];
			for (int i = 0; i < len; i++) {
				update(times[i], values[i]);
				d[i] = Util.getDoubleProperty(this, property);
			}
			return d;
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		}
	}

	@Override
	public double update(Bar bar) {
		return update(bar.getDateTime().getMillis(), new double[] { bar.getOpen(), bar.getHigh(), bar.getLow(), bar.getClose(), bar.getVolume(), bar.getTrades() });
	}

	@Override
	public double update(Tick tick) {
		return update(tick.getDateTime().getMillis(), new double[] { tick.getAsk(), tick.getBid(), tick.getPrice(), tick.getVolume(), tick.getLastSize() });
	}

	@Override
	public TimeSeries apply(TimeSeries ts) {
		double[] values = update(ts.dates(), ts.data());
		ts = new TimeSeriesArray(ts.dates().clone(), values);
		reset();
		return ts;
	}
	
	public TimeSeries apply(TimeSeries ts, String property) {
		final int len = ts.size();
		double[] newData = new double[len];
		for (int i = 0; i < len; i++) {
			update(ts.dates()[i].getMillis(), ts.data()[i]);
			newData[i] = Util.getDoubleProperty(this, property);
		}
		ts = new TimeSeriesArray(ts.dates().clone(), newData);
		reset();
		return ts;
	}

	@Override
	public void onTick(Tick tick) {
		update(tick.getDateTime().getMillis(), tick.getMidPrice());
	}
	
	@Override
	public void onBar(Bar bar) {
		update(bar.getDateTime().getMillis(), bar.getClose());
	}
}
