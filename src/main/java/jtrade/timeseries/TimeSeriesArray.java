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

public class TimeSeriesArray implements TimeSeries {
	DateTime[] dates;
	double[] data;

	public TimeSeriesArray() {
		dates = new DateTime[0];
		data = new double[0];
	}

	public TimeSeriesArray(TimeSeries ts) {
		dates = ts.dates().clone();
		data = ts.data().clone();
	}

	public TimeSeriesArray(Map<DateTime, ? extends Number> map) {
		dates = new DateTime[map.size()];
		data = new double[map.size()];
		int i = 0;
		for (Map.Entry<DateTime, ? extends Number> e : map.entrySet()) {
			dates[i] = e.getKey();
			data[i++] = e.getValue().doubleValue();
		}
	}

	public TimeSeriesArray(Map<DateTime, ?> map, String property) {
		dates = new DateTime[map.size()];
		data = new double[map.size()];
		int i = 0;
		try {
			for (Map.Entry<DateTime, ?> e : map.entrySet()) {
				dates[i] = e.getKey();
				data[i++] = Util.getDoubleProperty(e.getValue(), property);
			}
		} catch (Exception e) {
			throw new IllegalArgumentException("Invalid property " + property, e);
		}
	}

	public TimeSeriesArray(DateTime[] dates, double[] data) {
		if (dates.length != data.length) {
			throw new IllegalArgumentException("Dates and data lengths must match");
		}
		this.dates = dates;
		this.data = data;
	}

	public TimeSeriesArray(DateTime start, Period period, double[] data) {
		DateTime[] dates = new DateTime[data.length];
		dates[0] = start;
		for (int i = 1; i < dates.length; i++) {
			dates[i] = dates[i - 1].plus(period);
		}
		this.dates = dates;
		this.data = data;
	}

	public TimeSeriesArray(DateTime start, DateTime end, Period period, double value) {
		long periodMillis = period.toStandardSeconds().getSeconds() * 1000;
		int len = (int) Math.ceil((end.getMillis() - start.getMillis()) / periodMillis);
		DateTime[] dates = new DateTime[len];
		double[] data = new double[len];
		long s = start.getMillis();
		for (int i = 0; i < len; i++) {
			dates[i] = new DateTime(s + i * periodMillis);
			data[i] = value;
		}
		this.dates = dates;
		this.data = data;
	}

	public TimeSeriesArray(DateTime start, Period period, int size, double value) {
		DateTime[] dates = new DateTime[size];
		double[] data = new double[size];
		dates[0] = start;
		data[0] = value;
		for (int i = 1; i < dates.length; i++) {
			dates[i] = dates[i - 1].plus(period);
			data[i] = value;
		}
		this.dates = dates;
		this.data = data;
	}

	@Override
	public DateTime[] dates() {
		return dates;
	}

	@Override
	public double[] data() {
		return data;
	}

	@Override
	public int size() {
		return dates.length;
	}

	@Override
	public long duration() {
		return size() == 0 ? 0 : end().getMillis() - start().getMillis();
	}

	@Override
	public DateTime start() {
		return dates.length == 0 ? null : dates[0];
	}

	@Override
	public DateTime end() {
		return dates.length == 0 ? null : dates[dates.length - 1];
	}

	@Override
	public double first() {
		return data.length == 0 ? Double.NaN : data[0];
	}

	@Override
	public double last() {
		return data.length == 0 ? Double.NaN : data[data.length - 1];
	}

	private int nearestIndex(DateTime date) {
		int index = Arrays.binarySearch(dates, date);
		if (index < 0) {
			index = (-index) - 2;
		}
		return index;
	}

	@Override
	public double get(DateTime date) {
		int index = Arrays.binarySearch(dates, date);
		return index >= 0 ? data[index] : Double.NaN;
	}

	@Override
	public void set(DateTime date, double value) {
		int index = Arrays.binarySearch(dates, date);
		if (index < 0) {
			throw new IllegalArgumentException("Date '" + date + "' is not valid for this TimeSeries");
		}
		data[index] = value;
	}

	@Override
	public TimeSeries copy() {
		return new TimeSeriesArray(dates.clone(), data.clone());
	}

	@Override
	public TimeSeries truncate(int from, int to) {
		return new TimeSeriesArray(Arrays.copyOfRange(this.dates, from, to), Arrays.copyOfRange(this.data, from, to));
	}

	@Override
	public TimeSeries truncate(DateTime from, DateTime to) {
		int fromIndex = from.isBefore(start()) ? 0 : nearestIndex(from);
		int toIndex = to.isAfter(end()) ? size() : nearestIndex(to) + 1;
		return new TimeSeriesArray(Arrays.copyOfRange(this.dates, fromIndex, toIndex), Arrays.copyOfRange(this.data, fromIndex, toIndex));
	}

	@Override
	public TimeSeries last(int size) {
		return new TimeSeriesArray(Arrays.copyOfRange(this.dates, dates.length - size, dates.length),
				Arrays.copyOfRange(this.data, data.length - size, data.length));
	}

	@Override
	public TimeSeries first(int size) {
		return new TimeSeriesArray(Arrays.copyOfRange(this.dates, 0, size), Arrays.copyOfRange(this.data, 0, size));
	}

	@Override
	public TimeSeries shift(int periods) {
		double[] newData = data.clone();
		if (periods > 0) {
			System.arraycopy(newData, 0, newData, periods, newData.length - periods);
			Arrays.fill(newData, 0, periods, Double.NaN);
		} else if (periods < 0) {
			System.arraycopy(newData, -periods, newData, 0, newData.length + periods);
			Arrays.fill(newData, newData.length + periods, newData.length, Double.NaN);
		}
		return new TimeSeriesArray(dates.clone(), newData);
	}

	@Override
	public TimeSeries shift(Period period) {
		DateTime[] newDates = new DateTime[size()];
		for (int i = 0; i < newDates.length; i++) {
			newDates[i] = dates[i].plus(period);
		}
		return new TimeSeriesArray(newDates, data.clone());
	}

	@Override
	public TimeSeries union(TimeSeries ts) {
		Map<DateTime, Double> map = toMap();
		Map<DateTime, Double> otherMap = ts.toMap();
		map.putAll(otherMap);
		return new TimeSeriesArray(map);
	}

	@Override
	public TimeSeries intersect(TimeSeries ts) {
		Map<DateTime, Double> map = toMap();
		Map<DateTime, Double> otherMap = ts.toMap();
		map.keySet().retainAll(otherMap.keySet());
		return new TimeSeriesArray(map);
	}

	@Override
	public TimeSeries[] split(Period period) {
		if (size() == 0) {
			return new TimeSeries[0];
		}
		List<Integer> partitions = new ArrayList<Integer>();
		DateTime d = start().plus(period);
		for (int i = 1; i < dates.length; i++) {
			if (d.isBefore(dates[i]) || d.equals(dates[i])) {
				partitions.add(i);
				d = dates[i].plus(period);
			}
		}
		TimeSeries[] result = new TimeSeriesArray[partitions.size() + 1];
		for (int i = 0, j = 0; i < result.length; i++) {
			int p = i < partitions.size() ? partitions.get(i) : dates.length;
			DateTime[] newDates = Arrays.copyOfRange(dates, j, p);
			double[] newData = Arrays.copyOfRange(data, j, p);
			result[i] = new TimeSeriesArray(newDates, newData);
			j = p;
		}
		return result;
	}

	@Override
	public TimeSeries valid() {
		List<DateTime> validDates = new ArrayList<DateTime>(size());
		DoubleBuffer validData = new DoubleBuffer(size());
		for (int i = 0; i < data.length; i++) {
			double d = data[i];
			if (d == d) {
				validDates.add(dates[i]);
				validData.add(d);
			}
		}
		return new TimeSeriesArray(validDates.toArray(new DateTime[validDates.size()]), validData.toArray());
	}

	@Override
	public TimeSeries trim() {
		int from = 0;
		int to = data.length;
		for (int i = 0; i < data.length; i++) {
			double d = data[i];
			if (d == d) {
				from = i;
				break;
			}
		}
		for (int i = data.length - 1; i >= from; i--) {
			double d = data[i];
			if (d == d) {
				to = i + 1;
				break;
			}
		}
		return new TimeSeriesArray(Arrays.copyOfRange(this.dates, from, to), Arrays.copyOfRange(this.data, from, to));
	}

	@Override
	public TimeSeries reindex(DateTime[] newDates, FillMethod method) {
		NavigableMap<DateTime, Double> map = toMap();
		double[] newData = new double[newDates.length];
		for (int i = 0; i < newDates.length; i++) {
			Double d = map.get(newDates[i]);
			newData[i] = d != null ? d : Double.NaN;
		}
		if (FillMethod.BACKFILL.equals(method)) {
			for (int i = 0; i < newDates.length; i++) {
				if (Double.isNaN(newData[i])) {
					DateTime date = newDates[i];
					while ((date = map.higherKey(date)) != null) {
						Double d = map.get(date);
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
						Double d = map.get(date);
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
						Double d = map.get(date);
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
						Double d = map.get(date);
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
		return new TimeSeriesArray(newDates, newData);
	}

	@Override
	public TimeSeries asFreq(FreqMethod method) {
		if (size() == 0) {
			return new TimeSeriesArray();
		}
		DateTime[] newDates = null;
		List<DateTime> ds = new ArrayList<DateTime>();
		DateTime d = dates[0];
		if (FreqMethod.END_OF_DAY.equals(method)) {
			int lastDate = d.getDayOfYear();
			for (int i = 1; i < dates.length; i++) {
				int currDate = dates[i].getDayOfYear();
				if (lastDate != currDate) {
					ds.add(d);
					lastDate = currDate;
				}
				d = dates[i];
			}
		} else if (FreqMethod.END_OF_MONTH.equals(method)) {
			int lastDate = d.getMonthOfYear();
			for (int i = 1; i < dates.length; i++) {
				int currDate = dates[i].getMonthOfYear();
				if (lastDate != currDate) {
					ds.add(d);
					lastDate = currDate;
				}
				d = dates[i];
			}
		} else if (FreqMethod.END_OF_YEAR.equals(method)) {
			int lastDate = d.getYear();
			for (int i = 1; i < dates.length; i++) {
				int currDate = dates[i].getYear();
				if (lastDate != currDate) {
					ds.add(d);
					lastDate = currDate;
				}
				d = dates[i];
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
			return new TimeSeriesArray();
		}
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
		if (ts.size() == 0) {
			return new TimeSeriesArray();
		}
		return reindex(ts.dates(), method).truncate(ts.start(), ts.end());
	}

	@Override
	public TimeSeries filter(LocalTime from, LocalTime to) {
		long fromMillis = from.getMillisOfDay();
		long toMillis = to.getMillisOfDay();
		NavigableMap<DateTime, Double> ts = toMap();
		for (int i = 0; i < dates.length; i++) {
			DateTime d = dates[i];
			long m = d.getMillisOfDay();
			if (m < fromMillis || m > toMillis) {
				ts.remove(d);
			}
		}
		return new TimeSeriesArray(ts);
	}

	@Override
	public TimeSeries filter(double min, double max) {
		NavigableMap<DateTime, Double> ts = toMap();
		for (int i = 0; i < data.length; i++) {
			double v = data[i];
			if (v < min || v > max) {
				ts.remove(dates[i]);
			}
		}
		return new TimeSeriesArray(ts);
	}

	@Override
	public TimeSeries clearTime() {
		DateTime[] newDates = new DateTime[dates.length];
		for (int i = 0; i < dates.length; i++) {
			newDates[i] = dates[i].withMillisOfDay(0);
		}
		return new TimeSeriesArray(newDates, data.clone());
	}

	@Override
	public TimeSeries fill(double value) {
		double[] newData = data.clone();
		for (int i = 0; i < data.length; i++) {
			if (data[i] != data[i]) {
				newData[i] = value;
			}
		}
		return new TimeSeriesArray(dates.clone(), newData);
	}

	@Override
	public TimeSeries fill(FillMethod method) {
		return reindex(dates, method);
	}

	@Override
	public TimeSeries add(double value) {
		double[] newData = new double[size()];
		for (int i = 0; i < newData.length; i++) {
			newData[i] = data[i] + value;
		}
		return new TimeSeriesArray(dates.clone(), newData);
	}

	@Override
	public TimeSeries add(TimeSeries ts) {
		double[] tsData = ts.data();
		double[] newData = new double[size()];
		for (int i = 0; i < newData.length; i++) {
			newData[i] = data[i] + tsData[i];
		}
		return new TimeSeriesArray(dates.clone(), newData);
	}

	@Override
	public TimeSeries sub(double value) {
		double[] newData = new double[size()];
		for (int i = 0; i < newData.length; i++) {
			newData[i] = data[i] - value;
		}
		return new TimeSeriesArray(dates.clone(), newData);
	}

	@Override
	public TimeSeries sub(TimeSeries ts) {
		double[] tsData = ts.data();
		double[] newData = new double[size()];
		for (int i = 0; i < newData.length; i++) {
			newData[i] = data[i] - tsData[i];
		}
		return new TimeSeriesArray(dates.clone(), newData);
	}

	@Override
	public TimeSeries mul(double value) {
		double[] newData = new double[size()];
		for (int i = 0; i < newData.length; i++) {
			newData[i] = data[i] * value;
		}
		return new TimeSeriesArray(dates.clone(), newData);
	}

	@Override
	public TimeSeries mul(TimeSeries ts) {
		double[] tsData = ts.data();
		double[] newData = new double[size()];
		for (int i = 0; i < newData.length; i++) {
			newData[i] = data[i] * tsData[i];
		}
		return new TimeSeriesArray(dates.clone(), newData);
	}

	@Override
	public TimeSeries div(double value) {
		double[] newData = new double[size()];
		for (int i = 0; i < newData.length; i++) {
			newData[i] = data[i] / value;
		}
		return new TimeSeriesArray(dates.clone(), newData);
	}

	@Override
	public TimeSeries div(TimeSeries ts) {
		double[] tsData = ts.data();
		double[] newData = new double[size()];
		for (int i = 0; i < newData.length; i++) {
			newData[i] = data[i] / tsData[i];
		}
		return new TimeSeriesArray(dates.clone(), newData);
	}

	@Override
	public TimeSeries clip(double min, double max) {
		double[] newData = new double[size()];
		for (int i = 0; i < newData.length; i++) {
			double d = data[i];
			d = d < min ? min : d;
			newData[i] = d > max ? max : d;
		}
		return new TimeSeriesArray(dates.clone(), newData);
	}

	@Override
	public TimeSeries diff(int lag) {
		double[] newData = new double[data.length];
		Arrays.fill(newData, 0, lag, 0);
		for (int i = lag; i < data.length; i++) {
			newData[i] = data[i] - data[i - lag];
		}
		return new TimeSeriesArray(dates.clone(), newData);
	}

	@Override
	public TimeSeries arithReturn(int lag) {
		double[] newData = new double[data.length];
		Arrays.fill(newData, 0, lag, 0);
		for (int i = lag; i < data.length; i++) {
			double d2 = data[i];
			double d1 = data[i - lag];
			newData[i] = (d2 - d1) / d1;
		}
		return new TimeSeriesArray(dates.clone(), newData);
	}

	@Override
	public TimeSeries logReturn(int lag) {
		double[] newData = new double[data.length];
		Arrays.fill(newData, 0, lag, 0);
		for (int i = lag; i < data.length; i++) {
			newData[i] = Math.log(data[i] / data[i - lag]);
		}
		return new TimeSeriesArray(dates.clone(), newData);
	}

	@Override
	public TimeSeries standardize() {
		double[] newData = data.clone();
		double mean = mean();
		double sum = 0;
		for (int i = 0; i < newData.length; i++) {
			double v = newData[i] - mean;
			sum += v * v;
		}
		double stdev = Math.sqrt(sum / (newData.length - 1));
		for (int i = 0; i < newData.length; i++) {
			newData[i] = (newData[i] - mean) / stdev;
		}
		return new TimeSeriesArray(dates.clone(), newData);
	}

	@Override
	public TimeSeries normalize(double min, double max) {
		double[] newData = data.clone();
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
		return new TimeSeriesArray(dates.clone(), newData);
	}

	@Override
	public TimeSeries cumsum() {
		double[] newData = new double[size()];
		newData[0] = Double.isNaN(data[0]) ? 0 : data[0];
		for (int i = 1; i < newData.length; i++) {
			double d = data[i];
			newData[i] = (Double.isNaN(d) ? 0 : d) + newData[i - 1];
		}
		return new TimeSeriesArray(dates.clone(), newData);
	}

	@Override
	public double mean() {
		if (data.length == 0) {
			return Double.NaN;
		}
		double sum = 0.0;
		for (int i = 0; i < data.length; i++) {
			sum += data[i];
		}
		return sum / data.length;
	}

	@Override
	public double median() {
		if (data.length == 0) {
			return Double.NaN;
		}
		double[] d = data.clone();
		Arrays.sort(d);
		if ((d.length & 1) == 0) {
			int i = d.length / 2;
			return (d[i - 1] + d[i]) / 2;
		}
		return d[d.length / 2];
	}

	@Override
	public double mode() {
		if (data.length == 0) {
			return Double.NaN;
		}
		double[] sorted = data.clone();
		Arrays.sort(sorted);
		int f = 0;
		int fMax = 0;
		double mode = Double.NaN;
		double last = Double.NaN;
		for (int i = 0; i < data.length; i++) {
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
		if (data.length == 0) {
			return Double.NaN;
		}
		double sum = 0.0;
		for (int i = 0; i < data.length; i++) {
			sum += data[i];
		}
		return sum;
	}

	@Override
	public double min() {
		if (data.length == 0) {
			return Double.NaN;
		}
		double min = Double.POSITIVE_INFINITY;
		for (int i = 0; i < data.length; i++) {
			min = Math.min(data[i], min);
		}
		return min;
	}

	@Override
	public double max() {
		if (data.length == 0) {
			return Double.NaN;
		}
		double max = Double.NEGATIVE_INFINITY;
		for (int i = 0; i < data.length; i++) {
			max = Math.max(data[i], max);
		}
		return max;
	}

	@Override
	public double var() {
		if (data.length == 0) {
			return Double.NaN;
		}
		if (data.length == 1) {
			return 0.0;
		}
		double mean = mean();
		double sum = 0;
		for (int i = 0; i < data.length; i++) {
			double v = data[i] - mean;
			sum += v * v;
		}
		return sum / (data.length - 1);
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
		double[] newData = new double[data.length];
		Arrays.fill(newData, 0, window, Double.NaN);
		for (int i = window - 1; i < data.length; i++) {
			double sum = 0.0;
			for (int j = i - (window - 1); j <= i; j++) {
				sum += data[j];
			}
			newData[i] = sum / window;
		}
		return new TimeSeriesArray(dates.clone(), newData);
	}

	@Override
	public TimeSeries rollingMedian(int window) {
		if (window <= 1) {
			throw new IllegalArgumentException("Window must be greater than 1");
		}
		double[] newData = new double[data.length];
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
		return new TimeSeriesArray(dates.clone(), newData);
	}

	@Override
	public TimeSeries rollingVar(int window) {
		if (window <= 1) {
			throw new IllegalArgumentException("Window must be greater than 1");
		}
		double[] newData = new double[data.length];
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
		return new TimeSeriesArray(dates.clone(), newData);
	}

	@Override
	public TimeSeries rollingStd(int window) {
		if (window <= 1) {
			throw new IllegalArgumentException("Window must be greater than 1");
		}
		double[] newData = new double[data.length];
		Arrays.fill(newData, 0, window - 1, Double.NaN);
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
		return new TimeSeriesArray(dates.clone(), newData);
	}

	@Override
	public double corr(TimeSeries ts) {
		double[] data = this.data;
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
	public NavigableMap<DateTime, Double> toMap() {
		NavigableMap<DateTime, Double> map = new TreeMap<DateTime, Double>();
		for (int i = 0; i < dates.length; i++) {
			map.put(dates[i], data[i]);
		}
		return map;
	}

	@Override
	public Iterator<TimeSeriesValuePair> iterator() {
		return new Iterator<TimeSeriesValuePair>() {
			int i = 0;

			@Override
			public boolean hasNext() {
				return i < dates.length;
			}

			@Override
			public TimeSeriesValuePair next() {
				return new TimeSeriesValuePair() {
					final int idx = i++;

					@Override
					public DateTime getDateTime() {
						return dates[idx];
					}

					@Override
					public double getValue() {
						return data[idx];
					}

					@Override
					public double setValue(double v) {
						double old = data[idx];
						data[idx] = v;
						return old;
					}
				};
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}

	@Override
	public String toString(boolean tabular) {
		StringBuilder sb = new StringBuilder();
		if (tabular) {
			for (int i = 0; i < dates.length; i++) {
				sb.append(dates[i]);
				sb.append("  ");
				sb.append(data[i]);
				sb.append('\n');
			}
		} else {
			sb.append("[");
			DateTimeFormatter dateFormatter = DateTimeFormat.forPattern("yyyyMMdd HH:mm:ss");
			for (int i = 0; i < data.length; i++) {
				sb.append(dateFormatter.print(dates[i]));
				sb.append(' ');
				sb.append(Util.roundSig(data[i], 4));
				if (i + 1 < data.length) {
					sb.append(", ");
				}
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
		int result = 31 + Arrays.hashCode(data);
		result = 31 * result + Arrays.hashCode(dates);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TimeSeriesArray other = (TimeSeriesArray) obj;
		if (size() != other.size())
			return false;
		if (!Arrays.equals(dates, other.dates))
			return false;
		if (!Arrays.equals(data, other.data))
			return false;
		return true;
	}

}
