package jtrade.timeseries;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

import jtrade.util.DoubleBuffer;
import jtrade.util.Util;

import org.joda.time.DateTime;
import org.joda.time.LocalTime;
import org.joda.time.Period;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class TimeSeriesMap implements TimeSeries {
	TreeMap<DateTime, Double> map;

	public TimeSeriesMap() {
		map = new TreeMap<DateTime, Double>();
	}

	public TimeSeriesMap(TimeSeries ts) {
		this();
		for (TimeSeriesValuePair vp : ts) {
			map.put(vp.getDateTime(), vp.getValue());
		}
	}

	public TimeSeriesMap(Map<DateTime, Double> map) {
		this();
		this.map.putAll(map);
	}

	public TimeSeriesMap(DateTime[] dates, double[] data) {
		this();
		if (dates.length != data.length) {
			throw new IllegalArgumentException("Dates and data lengths must match");
		}
		for (int i = 0; i < dates.length; i++) {
			map.put(dates[i], data[i]);
		}
	}

	public TimeSeriesMap(DateTime start, Period period, double[] data) {
		this();
		DateTime date = start;
		for (int i = 1; i < data.length; i++) {
			map.put(date, data[i]);
			date = date.plus(period);
		}
	}

	public TimeSeriesMap(DateTime start, DateTime end, Period period, double value) {
		this();
		long periodMillis = period.toStandardSeconds().getSeconds() * 1000;
		int len = (int) Math.ceil((end.getMillis() - start.getMillis()) / periodMillis);
		long s = start.getMillis();
		Double v = Double.valueOf(value);
		for (int i = 0; i < len; i++) {
			map.put(new DateTime(s + i * periodMillis), v);
		}
	}

	public TimeSeriesMap(DateTime start, Period period, int size, double value) {
		this();
		DateTime date = start;
		Double v = Double.valueOf(value);
		for (int i = 0; i < size; i++) {
			map.put(date, v);
			date = date.plus(period);
		}
	}

	@Override
	public DateTime[] dates() {
		return map.keySet().toArray(new DateTime[size()]);
	}

	@Override
	public double[] data() {
		double[] data = new double[size()];
		int idx = 0;
		for (Double d : map.values()) {
			data[idx++] = d;
		}
		return data;
	}
	
	@Override
	public NavigableMap<DateTime, Double> toMap() {
		return map;
	}

	@Override
	public int size() {
		return map.size();
	}

	@Override
	public long duration() {
		return size() == 0 ? 0 : end().getMillis() - start().getMillis();
	}

	@Override
	public DateTime start() {
		return size() == 0 ? null : map.firstKey();
	}

	@Override
	public DateTime end() {
		return size() == 0 ? null : map.lastKey();
	}

	@Override
	public double first() {
		return size() == 0 ? Double.NaN : get(map.firstKey());
	}

	@Override
	public double last() {
		return size() == 0 ? Double.NaN : get(map.lastKey());
	}

	@Override
	public double get(DateTime date) {
		Double d = map.get(date);
		return d != null ? d.doubleValue() : Double.NaN;
	}

	@Override
	public void set(DateTime date, double value) {
		map.put(date, value);
	}

	public double remove(DateTime date) {
		Double previous = map.remove(date);
		return previous == null ? Double.NaN : previous;
	}

	@Override
	public TimeSeries copy() {
		return (TimeSeriesMap) new TimeSeriesMap(map);
	}

	@Override
	public TimeSeries truncate(int from, int to) {
		TimeSeriesMap ts = (TimeSeriesMap)copy();
		for (int i = 0; i < from; i++) {
			ts.map.remove(ts.map.firstKey());
		}
		for (int i = size() - 1; i >= to; i--) {
			ts.map.remove(ts.map.lastKey());
		}
		return ts;
	}

	@Override
	public TimeSeries truncate(DateTime from, DateTime to) {
		return new TimeSeriesMap(map.subMap(from, true, to, true));
	}

	@Override
	public TimeSeries shift(int periods) {
		double[] newData = data();
		if (periods > 0) {
			System.arraycopy(newData, 0, newData, periods, newData.length - periods);
			Arrays.fill(newData, 0, periods, Double.NaN);
		} else if (periods < 0) {
			System.arraycopy(newData, -periods, newData, 0, newData.length + periods);
			Arrays.fill(newData, newData.length + periods, newData.length, Double.NaN);
		}
		return new TimeSeriesMap(dates(), newData);
	}

	@Override
	public TimeSeries last(int size) {
		return truncate(size() - size, size());
	}

	@Override
	public TimeSeries first(int size) {
		return truncate(0, size);
	}

	@Override
	public TimeSeries shift(Period period) {
		TimeSeriesMap ts = new TimeSeriesMap();
		for (Map.Entry<DateTime, Double> e : map.entrySet()) {
			ts.map.put(e.getKey().plus(period), e.getValue());
		}
		return ts;
	}

	@Override
	public TimeSeries union(TimeSeries ts) {
		TimeSeriesMap newTs = (TimeSeriesMap) copy();
		newTs.map.putAll(ts.toMap());
		return newTs;
	}

	@Override
	public TimeSeries intersect(TimeSeries ts) {
		TimeSeriesMap newTs = (TimeSeriesMap) copy();
		newTs.map.keySet().retainAll(ts.toMap().keySet());
		return newTs;
	}

	@Override
	public TimeSeries[] split(Period period) {
		if (size() == 0) {
			return new TimeSeriesMap[0];
		}
		List<DateTime> partitions = new ArrayList<DateTime>();
		DateTime d = map.firstKey().plus(period);
		for (DateTime dt : map.keySet()) {
			if (d.isBefore(dt) || d.equals(dt)) {
				partitions.add(dt);
				d = dt.plus(period);
			}
		}
		TimeSeries[] result = new TimeSeriesMap[partitions.size() + 1];
		DateTime from = map.firstKey();
		DateTime end = map.lastKey().plus(period);
		for (int i = 0; i < result.length; i++) {
			DateTime to = i < partitions.size() ? partitions.get(i) : end;
			result[i] = new TimeSeriesMap(map.subMap(from, true, to, false));
			from = to;
		}
		return result;
	}

	@Override
	public TimeSeries valid() {
		TimeSeriesMap ts = new TimeSeriesMap();
		for (Map.Entry<DateTime, Double> e : map.entrySet()) {
			if (!e.getValue().isNaN()) {
				ts.map.put(e.getKey(), e.getValue());
			}
		}
		return ts;
	}

	@Override
	public TimeSeries trim() {
		TimeSeriesMap ts = (TimeSeriesMap) copy();
		Iterator<Double> values = ts.map.values().iterator();
		while (values.hasNext() && values.next().isNaN()) {
			values.remove();
		}
		values = ts.map.descendingMap().values().iterator();
		while (values.hasNext() && values.next().isNaN()) {
			values.remove();
		}
		return ts;
	}

	@Override
	public TimeSeries reindex(DateTime[] newDates, FillMethod method) {
		double[] newData = new double[newDates.length];
		for (int i = 0; i < newDates.length; i++) {
			Double d = get(newDates[i]);
			newData[i] = d != null ? d : Double.NaN;
		}
		if (FillMethod.BACKFILL.equals(method)) {
			for (int i = 0; i < newDates.length; i++) {
				if (Double.isNaN(newData[i])) {
					DateTime date = newDates[i];
					while ((date = map.higherKey(date)) != null) {
						Double d = get(date);
						if (!d.isNaN()) {
							newData[i] = d;
							break;
						}
					}
				}
			}
		} else if (FillMethod.FORWARDFILL.equals(method)) {
			for (int i = 0; i < newDates.length; i++) {
				if (Double.isNaN(newData[i])) {
					DateTime date = newDates[i];
					while ((date = map.lowerKey(date)) != null) {
						Double d = get(date);
						if (!d.isNaN()) {
							newData[i] = d;
							break;
						}
					}
				}
			}
		} else if (FillMethod.FORWARDBACKFILL.equals(method)) {
			for (int i = 0; i < newDates.length; i++) {
				if (Double.isNaN(newData[i])) {
					DateTime date = newDates[i];
					while ((date = map.lowerKey(date)) != null) {
						Double d = get(date);
						if (!d.isNaN()) {
							newData[i] = d;
							break;
						}
					}
				}
			}
			for (int i = 0; i < newDates.length; i++) {
				if (Double.isNaN(newData[i])) {
					DateTime date = newDates[i];
					while ((date = map.higherKey(date)) != null) {
						Double d = get(date);
						if (!d.isNaN()) {
							newData[i] = d;
							break;
						}
					}
				}
			}
		} else if (FillMethod.INTERPOLATE.equals(method)) {
			DoubleBuffer datesBuf = new DoubleBuffer(size());
			DoubleBuffer dataBuf = new DoubleBuffer(size());
			for (TimeSeriesValuePair p : this) {
				if (!Double.isNaN(p.getValue())) {
					datesBuf.add(p.getDateTime().getMillis());
					dataBuf.add(p.getValue());
				}
			}
			double[] cleanedDates = datesBuf.toArray();
			double[] cleanedData = dataBuf.toArray();
			for (int i = 0; i < newDates.length; i++) {
				if (Double.isNaN(newData[i])) {
					double x = newDates[i].getMillis();
					int i0 = Arrays.binarySearch(cleanedDates, x);
					if (i0 < 0) {
						i0 = -(i0 + 2);
					}
					if (i0 < 0) {
						i0 = 0;
					}
					int i1 = i0 + 1;
					if (i1 >= cleanedDates.length) {
						i0 = cleanedDates.length - 2;
						i1 = cleanedDates.length - 1;
					}	
					newData[i] = cleanedData[i0] + (cleanedData[i1] - cleanedData[i0]) * ((x - cleanedDates[i0]) / (cleanedDates[i1] - cleanedDates[i0]));
				}
			}
		} else if (FillMethod.NAN.equals(method)) {
			// Leave NaNs as is
		} else {
			throw new IllegalArgumentException("Invalid method: " + method);
		}
		return new TimeSeriesMap(newDates, newData);
	}

	@Override
	public TimeSeries asFreq(FreqMethod method) {
		if (size() == 0) {
			return new TimeSeriesMap();
		}
		// Create new dates according to period
		DateTime[] newDates = null;
		List<DateTime> ds = new ArrayList<DateTime>();
		DateTime d = map.firstKey();
		if (FreqMethod.END_OF_DAY.equals(method)) {
			int lastDate = d.getDayOfYear();
			for (DateTime dt : map.keySet()) {
				int currDate = dt.getDayOfYear();
				if (lastDate != currDate) {
					ds.add(d);
					lastDate = currDate;
				}
				d = dt;
			}
		} else if (FreqMethod.END_OF_MONTH.equals(method)) {
			int lastDate = d.getMonthOfYear();
			for (DateTime dt : map.keySet()) {
				int currDate = dt.getMonthOfYear();
				if (lastDate != currDate) {
					ds.add(d);
					lastDate = currDate;
				}
				d = dt;
			}
		} else if (FreqMethod.END_OF_YEAR.equals(method)) {
			int lastDate = d.getYear();
			for (DateTime dt : map.keySet()) {
				int currDate = dt.getYear();
				if (lastDate != currDate) {
					ds.add(d);
					lastDate = currDate;
				}
				d = dt;
			}
		} else {
			throw new IllegalArgumentException("Invalid method: " + method);
		}
		ds.add(d);
		newDates = ds.toArray(new DateTime[ds.size()]);
		return reindex(newDates, FillMethod.FORWARDFILL);
	}

	@Override
	public TimeSeries asFreq(Period period, FillMethod method) {
		if (size() == 0) {
			return new TimeSeriesMap();
		}
		// Create new dates according to period
		long periodMillis = period.toStandardSeconds().getSeconds() * 1000;
		int newLen = (int) (duration() / periodMillis + 1);
		DateTime[] dates = new DateTime[newLen];
		dates[0] = new DateTime(start().getMillis() - (start().getMillis() % periodMillis));
		for (int i = 1; i < dates.length; i++) {
			dates[i] = dates[i - 1].plus(period);
		}
		return reindex(dates, method);
	}

	@Override
	public TimeSeries match(TimeSeries ts, FillMethod method) {
		Period p = new Period(duration() / (ts.size() - 1));
		return asFreq(p, method).truncate(start(), end());
	}

	@Override
	public TimeSeries filter(LocalTime from, LocalTime to) {
		long fromMillis = from.getMillisOfDay();
		long toMillis = to.getMillisOfDay();
		TimeSeriesMap ts = (TimeSeriesMap) copy();
		for (TimeSeriesValuePair vp : this) {
			long m = vp.getDateTime().getMillisOfDay();
			if (m < fromMillis || m > toMillis) {
				ts.map.remove(vp.getDateTime());
			}
		}
		return ts;
	}

	@Override
	public TimeSeries filter(double min, double max) {
		TimeSeriesMap ts = (TimeSeriesMap) copy();
		for (TimeSeriesValuePair vp : this) {
			double v = vp.getValue();
			if (v < min || v > max) {
				ts.map.remove(vp.getDateTime());
			}
		}
		return ts;
	}

	@Override
	public TimeSeries clearTime() {
		TimeSeriesMap ts = new TimeSeriesMap();
		for (Map.Entry<DateTime, Double> e : map.entrySet()) {
			ts.map.put(e.getKey().withMillisOfDay(0), e.getValue());
		}
		return ts;
	}

	@Override
	public TimeSeries fill(double value) {
		TimeSeriesMap ts = (TimeSeriesMap) copy();
		for (Map.Entry<DateTime, Double> e : ts.map.entrySet()) {
			if (Double.isNaN(e.getValue())) {
				e.setValue(value);
			}
		}
		return ts;
	}

	@Override
	public TimeSeries fill(FillMethod method) {
		return reindex(dates(), method);
	}

	@Override
	public TimeSeries add(double value) {
		TimeSeriesMap map = (TimeSeriesMap) copy();
		for (Map.Entry<DateTime, Double> e : map.map.entrySet()) {
			e.setValue(e.getValue() + value);
		}
		return map;
	}

	@Override
	public TimeSeries add(TimeSeries other) {
		TimeSeriesMap ts = (TimeSeriesMap) copy();
		for (Map.Entry<DateTime, Double> e : ts.map.entrySet()) {
			double d = other.get(e.getKey());
			if (d == d) {
				e.setValue(e.getValue() + d);
			}
		}
		return ts;
	}

	@Override
	public TimeSeries sub(double value) {
		TimeSeriesMap ts = (TimeSeriesMap) copy();
		for (Map.Entry<DateTime, Double> e : ts.map.entrySet()) {
			e.setValue(e.getValue() - value);
		}
		return ts;
	}

	@Override
	public TimeSeries sub(TimeSeries other) {
		TimeSeriesMap ts = (TimeSeriesMap) copy();
		for (Map.Entry<DateTime, Double> e : ts.map.entrySet()) {
			double d = other.get(e.getKey());
			if (d == d) {
				e.setValue(e.getValue() - d);
			}
		}
		return ts;
	}

	@Override
	public TimeSeries mul(double value) {
		TimeSeriesMap ts = (TimeSeriesMap) copy();
		for (Map.Entry<DateTime, Double> e : ts.map.entrySet()) {
			e.setValue(e.getValue() * value);
		}
		return ts;
	}

	@Override
	public TimeSeries mul(TimeSeries other) {
		TimeSeriesMap ts = (TimeSeriesMap) copy();
		for (Map.Entry<DateTime, Double> e : ts.map.entrySet()) {
			double d = other.get(e.getKey());
			if (d == d) {
				e.setValue(e.getValue() * d);
			}
		}
		return ts;
	}

	@Override
	public TimeSeries div(double value) {
		TimeSeriesMap map = (TimeSeriesMap) copy();
		for (Map.Entry<DateTime, Double> e : map.map.entrySet()) {
			e.setValue(e.getValue() / value);
		}
		return map;
	}

	@Override
	public TimeSeries div(TimeSeries other) {
		TimeSeriesMap ts = (TimeSeriesMap) copy();
		for (Map.Entry<DateTime, Double> e : ts.map.entrySet()) {
			double d = other.get(e.getKey());
			if (d == d) {
				e.setValue(e.getValue() / d);
			}
		}
		return ts;
	}

	@Override
	public TimeSeries clip(double min, double max) {
		TimeSeriesMap ts = (TimeSeriesMap) copy();
		for (Map.Entry<DateTime, Double> e : ts.map.entrySet()) {
			double d = e.getValue();
			d = d < min ? min : d;
			e.setValue(d > max ? max : d);
		}
		return ts;
	}

	@Override
	public TimeSeries diff(int lag) {
		double[] data = data();
		double[] newData = new double[size()];
		Arrays.fill(newData, 0, lag, 0);
		for (int i = lag; i < data.length; i++) {
			newData[i] = data[i] - data[i - lag];
		}
		return new TimeSeriesMap(dates(), newData);
	}

	@Override
	public TimeSeries arithReturn(int lag) {
		double[] data = data();
		double[] newData = new double[size()];
		Arrays.fill(newData, 0, lag, 0);
		for (int i = lag; i < data.length; i++) {
			double d2 = data[i];
			double d1 = data[i - lag];
			newData[i] = (d2 - d1) / d1;
		}
		return new TimeSeriesMap(dates(), newData);
	}

	@Override
	public TimeSeries logReturn(int lag) {
		double[] data = data();
		double[] newData = new double[size()];
		Arrays.fill(newData, 0, lag, 0);
		for (int i = lag; i < data.length; i++) {
			newData[i] = Math.log(data[i] / data[i - lag]);
		}
		return new TimeSeriesMap(dates(), newData);
	}

	@Override
	public TimeSeries standardize() {
		double[] newData = data();
		double mean = mean();
		double sumSq = 0;
		for (int i = 0; i < newData.length; i++) {
			double v = newData[i] - mean;
			sumSq += v * v;
		}
		double stdev = Math.sqrt(sumSq / (newData.length - 1));
		for (int i = 0; i < newData.length; i++) {
			newData[i] = (newData[i] - mean) / stdev;
		}
		return new TimeSeriesMap(dates(), newData);
	}

	@Override
	public TimeSeries normalize(double min, double max) {
		double[] newData = data();
		double tsMin = Double.POSITIVE_INFINITY, tsMax = Double.NEGATIVE_INFINITY;
		if (Double.isNaN(min) || Double.isNaN(max)) {
			for (int i = 0; i < newData.length; i++) {
				double d = newData[i];
				tsMin = tsMin < d ? tsMin : d;
				tsMax = tsMax > d ? tsMax : d;
			}
		} else {
			tsMin = min;
			tsMax = max;
		}
		if (tsMin == tsMax) {
			Arrays.fill(newData, 0);
		} else {
			for (int i = 0; i < newData.length; i++) {
				double d = newData[i];
				if (d == tsMin) {
					newData[i] = -1.0;
				} else if (d == tsMax) {
					newData[i] = 1.0;
				} else {
					newData[i] = -1.0 + 2.0 * (d - tsMin) / (tsMax - tsMin);
				}
			}
		}
		return new TimeSeriesMap(dates(), newData);
	}

	@Override
	public TimeSeries cumsum() {
		TimeSeriesMap ts = (TimeSeriesMap) copy();
		double pd = 0;
		for (Map.Entry<DateTime, Double> e : ts.map.entrySet()) {
			double d = e.getValue();
			if (Double.isNaN(d)) {
				d = 0;
			}
			e.setValue(d + pd);
			pd = d + pd;
		}
		return ts;
	}

	@Override
	public double mean() {
		if (map.isEmpty()) {
			return Double.NaN;
		}
		double sum = 0.0;
		for (Double d : map.values()) {
			sum += d;
		}
		return sum / size();
	}

	@Override
	public double median() {
		if (map.isEmpty()) {
			return Double.NaN;
		}
		double[] d = data();
		Arrays.sort(d);
		return d[d.length / 2];
	}

	@Override
	public double mode() {
		if (map.isEmpty()) {
			return Double.NaN;
		}
		double[] sorted = data();
		Arrays.sort(sorted);
		int f = 0;
		int fMax = 0;
		double mode = Double.NaN;
		double last = Double.NaN;
		for (int i = 0; i < sorted.length; i++) {
			double v = sorted[i];
			if (v == last) {
				f++;
				if (f > fMax) {
					fMax = f;
					mode = v;
				}
			} else {
				f = 0;
			}
			last = v;
		}
		return mode;
	}

	@Override
	public double sum() {
		if (map.isEmpty()) {
			return Double.NaN;
		}
		double sum = 0.0;
		for (Double d : map.values()) {
			sum += d;
		}
		return sum;
	}

	@Override
	public double min() {
		if (map.isEmpty()) {
			return Double.NaN;
		}
		double min = Double.POSITIVE_INFINITY;
		for (Double d : map.values()) {
			min = Math.min(d, min);
		}
		return min;
	}

	@Override
	public double max() {
		if (map.isEmpty()) {
			return Double.NaN;
		}
		double max = Double.NEGATIVE_INFINITY;
		for (Double d : map.values()) {
			max = Math.max(d, max);
		}
		return max;
	}

	@Override
	public double var() {
		if (map.isEmpty()) {
			return Double.NaN;
		}
		if (size() == 1) {
			return 0.0;
		}
		double mean = mean();
		double sum = 0;
		for (Double d : map.values()) {
			double v = d - mean;
			sum += v * v;
		}
		return sum / (size() - 1);
	}

	@Override
	public double std() {
		return Math.sqrt(var());
	}

	@Override
	public TimeSeries rollingMean(int window) {
		if (window <= 1) {
			throw new IllegalArgumentException("Window must be greater than 1");
		}
		double[] data = data();
		double[] newData = new double[size()];
		Arrays.fill(newData, 0, window, Double.NaN);
		for (int i = window - 1; i < data.length; i++) {
			double sum = 0.0;
			for (int j = i - (window - 1); j <= i; j++) {
				sum += data[j];
			}
			newData[i] = sum / window;
		}
		return new TimeSeriesMap(dates(), newData);
	}

	@Override
	public TimeSeries rollingMedian(int window) {
		if (window <= 1) {
			throw new IllegalArgumentException("Window must be greater than 1");
		}
		double[] data = data();
		double[] newData = new double[size()];
		Arrays.fill(newData, 0, window, Double.NaN);
		for (int i = window - 1; i < data.length; i++) {
			double[] d = Arrays.copyOfRange(data, i - (window - 1), i + 1);
			Arrays.sort(d);
			if ((d.length & 1) == 0) {
				int k = d.length / 2;
				newData[i] = (d[k - 1] + d[k]) / 2;
			} else {
				newData[i] = d[d.length / 2];
			}
		}
		return new TimeSeriesMap(dates(), newData);
	}

	@Override
	public TimeSeries rollingVar(int window) {
		if (window <= 1) {
			throw new IllegalArgumentException("Window must be greater than 1");
		}
		double[] data = data();
		double[] newData = new double[size()];
		Arrays.fill(newData, 0, window, Double.NaN);
		for (int i = window - 1; i < data.length; i++) {
			double sum = 0.0;
			for (int j = i - (window - 1); j <= i; j++) {
				sum += data[j];
			}
			double mean = sum / window;
			sum = 0.0;
			for (int j = i - (window - 1); j <= i; j++) {
				double v = data[j] - mean;
				sum += v * v;
			}
			newData[i] = sum / (window - 1);
		}
		return new TimeSeriesMap(dates(), newData);
	}

	@Override
	public TimeSeries rollingStd(int window) {
		if (window <= 1) {
			throw new IllegalArgumentException("Window must be greater than 1");
		}
		double[] data = data();
		double[] newData = new double[size()];
		Arrays.fill(newData, 0, window, Double.NaN);
		for (int i = window - 1; i < data.length; i++) {
			double sum = 0.0;
			for (int j = i - (window - 1); j <= i; j++) {
				sum += data[j];
			}
			double mean = sum / window;
			sum = 0.0;
			for (int j = i - (window - 1); j <= i; j++) {
				double v = data[j] - mean;
				sum += v * v;
			}
			newData[i] = Math.sqrt(sum / (window - 1));
		}
		return new TimeSeriesMap(dates(), newData);
	}

	@Override
	public double corr(TimeSeries ts) {
		double[] data = data();
		double[] otherData = ts.data();
		if (data.length != otherData.length) {
			throw new IllegalArgumentException(String.format("Data length must match: %s != %s", data.length, otherData.length));
		}
		final int len = data.length;
		double sum = 0;
		double sumOther = 0;
		double sumSq = 0;
		double sumSqOther = 0;
		double sumProduct = 0;
		for (int i = 0; i < len; i++) {
			sum += data[i];
			sumOther += otherData[i];
			sumSq += data[i] * data[i];
			sumSqOther += otherData[i] * otherData[i];
			sumProduct += data[i] * otherData[i];
		}
		double numerator = sumProduct - sum * sumOther / len;
		double denominator = Math.sqrt((sumSq - sum * sum / len) * (sumSqOther - sumOther * sumOther / len));
		return numerator / denominator;
	}

	@Override
	public double autoCorr(int lag) {
		double[] data = data();
		int n = data.length;
		if (lag >= n) {
			throw new IllegalArgumentException("Lag is too large");
		}
		double mean = mean();
		double sum = 0;
		for (int i = 0; i < data.length; i++) {
			double v = data[i] - mean;
			sum += v * v;
		}
		double variance = sum / data.length; // (data.length - 1);
		double run = 0;
		for (int i = lag; i < n; ++i) {
			run += (data[i] - mean) * (data[i - lag] - mean);
		}
		return (run / (n - lag)) / variance;
	}

	@Override
	public TimeSeries apply(TimeSeriesOp op) {
		return op.apply(this);
	}
	@Override
	public Iterator<TimeSeriesValuePair> iterator() {
		return new Iterator<TimeSeriesValuePair>() {
			Iterator<Map.Entry<DateTime, Double>> iter = map.entrySet().iterator();

			@Override
			public boolean hasNext() {
				return iter.hasNext();
			}

			@Override
			public TimeSeriesValuePair next() {
				return new TimeSeriesValuePair() {
					Map.Entry<DateTime, Double> entry = iter.next();

					@Override
					public DateTime getDateTime() {
						return entry.getKey();
					}

					@Override
					public double getValue() {
						return entry.getValue();
					}

					@Override
					public double setValue(double v) {
						return entry.setValue(v);
					}
				};
			}

			@Override
			public void remove() {
				iter.remove();
			}
		};
	}

	@Override
	public String toString(boolean tabular) {
		if (map.isEmpty()) {
			return "[]";
		}
		StringBuilder sb = new StringBuilder();
		if (tabular) {
			for (TimeSeriesValuePair vp : this) {
				sb.append(vp.getDateTime());
				sb.append("  ");
				sb.append(vp.getValue());
				sb.append('\n');
			}
		} else {
			sb.append("[");
			DateTimeFormatter dateFormatter = DateTimeFormat.forPattern("yyyyMMdd HH:mm:ss");
			int i = 0;
			for (TimeSeriesValuePair vp : this) {
				if (i++ != 0) {
					sb.append(", ");
				}
				sb.append(dateFormatter.print(vp.getDateTime()));
				sb.append(' ');
				sb.append(Util.roundSig(vp.getValue(), 4));
			}
			sb.append(']');
			return sb.toString();
		}
		return sb.toString();
	}

	@Override
	public String toString() {
		return toString(false);
	}

	@Override
	public int hashCode() {
		return map.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TimeSeriesMap other = (TimeSeriesMap) obj;
		return map.equals(other.map);
	}


}
