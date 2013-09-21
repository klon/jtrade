package jtrade.test.unit;

import static jtrade.test.TestUtil.assertContains;
import static jtrade.test.TestUtil.assertEqualsArray;
import static org.testng.AssertJUnit.assertEquals;

import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

import jtrade.test.TestDurationWrapper;
import jtrade.timeseries.TimeSeries;
import jtrade.timeseries.TimeSeries.FillMethod;
import jtrade.timeseries.TimeSeriesArray;
import jtrade.timeseries.TimeSeriesMap;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Minutes;
import org.joda.time.Period;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

@Listeners({ TestDurationWrapper.class })
public class TestTimeSeries {
	DateTime[] dates;
	double[] data;
	NavigableMap<DateTime, Double> map;

	@BeforeClass
	public void setUp() {
		dates = new DateTime[] { new DateTime(2013, 1, 1, 0, 0, 0, 0), new DateTime(2013, 1, 2, 0, 0, 0, 0), new DateTime(2013, 1, 3, 0, 0, 0, 0) };
		data = new double[] { 100.0, 105.0, 110.0 };
		map = new TreeMap<DateTime, Double>();
		for (int i = 0; i < dates.length; i++) {
			map.put(dates[i], data[i]);
		}
	}

	@DataProvider(name = "testTimeSeriesProvider")
	public static Object[][] createTimeSeriesClasses() {
		return new Class[][] { { TimeSeriesArray.class }, { TimeSeriesMap.class } };
	}

	@Test(dataProvider = "testTimeSeriesProvider")
	public void testTimeSeriesBasic(Class<TimeSeries> cls) throws Exception {
		TimeSeries ts1 = cls.newInstance();
		assertEquals(0, ts1.size());
		assertEquals(0, ts1.duration());

		TimeSeries ts2 = cls.getConstructor(Map.class).newInstance(map);
		assertEquals(3, ts2.size());
		assertEquals(2 * 24 * 60 * 60 * 1000, ts2.duration());
		assertEqualsArray(new DateTime[] { new DateTime(2013, 1, 1, 0, 0, 0, 0), new DateTime(2013, 1, 2, 0, 0, 0, 0), new DateTime(2013, 1, 3, 0, 0, 0, 0) },
				ts2.dates());
		assertEqualsArray(new double[] { 100.0, 105.0, 110.0 }, ts2.data());
		assertEquals(map.firstEntry().getValue().doubleValue(), ts2.get(map.firstKey()));
		assertEquals(Double.NaN, ts2.get(new DateTime(2013, 1, 1, 12, 0, 0, 0)));
		assertEquals(map.firstEntry().getValue().doubleValue(), ts2.first());
		assertEquals(map.lastEntry().getValue().doubleValue(), ts2.last());
		assertEquals(map.firstKey(), ts2.start());
		assertEquals(map.lastKey(), ts2.end());
		assertEquals(map, ts2.toMap());
		assertEquals(ts2, ts2.copy());
		assertContains(new String[] { "100.0", "105.0", "110.0" }, ts2.toString());
		assertContains(new String[] { "100.0", "105.0", "110.0" }, ts2.toString(true));
	}

	@Test(dataProvider = "testTimeSeriesProvider")
	public void testTimeSeriesOperations(Class<TimeSeries> cls) throws Exception {
		TimeSeries ts1 = cls.getConstructor(Map.class).newInstance(map);
		assertEquals(3, ts1.size());
		TimeSeries ts2 = cls.getConstructor(Map.class).newInstance(map.headMap(dates[2], false));
		assertEquals(2, ts2.size());
		TimeSeries ts3 = cls.getConstructor(Map.class).newInstance(map.tailMap(dates[0], false));
		assertEquals(2, ts3.size());

		assertEquals(ts2, ts1.truncate(dates[0], dates[1]));
		assertEquals(ts3, ts1.truncate(dates[1], dates[2]));
		assertEquals(ts2, ts1.first(2));
		assertEquals(ts3, ts1.last(2));
		assertEquals(map.subMap(dates[1], true, dates[1], true), ts2.intersect(ts3).toMap());
		assertEquals(ts1, ts2.union(ts3));

		assertEqualsArray(new double[] { 105.0, 110.0, 115.0 }, ts1.add(5.0).data());
		assertEqualsArray(new double[] { 95.0, 100.0, 105.0 }, ts1.sub(5.0).data());
		assertEqualsArray(new double[] { 200.0, 210.0, 220.0 }, ts1.mul(2.0).data());
		assertEqualsArray(new double[] { 50.0, 52.5, 55.0 }, ts1.div(2.0).data());
		assertEqualsArray(new double[] { 104.0, 105.0, 106.0 }, ts1.clip(104.0, 106.0).data());
		assertEqualsArray(new double[] { 105.0 }, ts1.filter(104.0, 106.0).data());
		assertEqualsArray(new double[] { Double.NaN, 100.0, 105.0 }, ts1.shift(1).data());
		assertEqualsArray(new double[] { 105.0, 110.0, Double.NaN }, ts1.shift(-1).data());
		assertEqualsArray(new DateTime[] { new DateTime(2013, 1, 2, 0, 0, 0, 0), new DateTime(2013, 1, 3, 0, 0, 0, 0), new DateTime(2013, 1, 4, 0, 0, 0, 0) }, ts1
				.shift(Days.ONE.toPeriod()).dates());
		assertEqualsArray(new TimeSeries[] { ts1.first(1), ts1.truncate(1, 2), ts1.last(1) }, ts1.split(Days.ONE.toPeriod()));
	}

	@Test(dataProvider = "testTimeSeriesProvider")
	public void testTimeSeriesMissing(Class<TimeSeries> cls) throws Exception {
		TimeSeries ts1 = cls.getConstructor(Map.class).newInstance(map);
		ts1.set(dates[0], Double.NaN);
		ts1.set(dates[2], Double.NaN);

		assertEquals(ts1.truncate(1, 2), ts1.valid());
		assertEquals(ts1.truncate(1, 2), ts1.trim());
		assertEqualsArray(new double[] { 1.0, 105.0, 1.0 }, ts1.fill(1.0).data());
		assertEqualsArray(new double[] { Double.NaN, 105.0, 105.0 }, ts1.fill(TimeSeries.FillMethod.FORWARDFILL).data());
		assertEqualsArray(new double[] { 105.0, 105.0, Double.NaN }, ts1.fill(TimeSeries.FillMethod.BACKFILL).data());
		assertEqualsArray(new double[] { 105.0, 105.0, 105.0 }, ts1.fill(TimeSeries.FillMethod.FORWARDBACKFILL).data());
		ts1.set(dates[0], 100.0);
		ts1.set(dates[1], Double.NaN);
		ts1.set(dates[2], 110.0);
		assertEqualsArray(new double[] { 100.0, 105.0, 110.0 }, ts1.fill(TimeSeries.FillMethod.INTERPOLATE).data());
	}

	@Test(dataProvider = "testTimeSeriesProvider")
	public void testTimeSeriesReindex(Class<TimeSeries> cls) throws Exception {
		TimeSeries ts1 = cls.getConstructor(Map.class).newInstance(map);
		DateTime[] newDates = new DateTime[] { new DateTime(2012, 12, 31, 12, 0, 0, 0), new DateTime(2013, 1, 1, 0, 0, 0, 0),
				new DateTime(2013, 1, 1, 12, 0, 0, 0), new DateTime(2013, 1, 2, 0, 0, 0, 0), new DateTime(2013, 1, 3, 0, 0, 0, 0) };
		assertEqualsArray(new double[] { Double.NaN, 100.0, Double.NaN, 105.0, 110.0 }, ts1.reindex(newDates, FillMethod.NAN).data());
		assertEqualsArray(new double[] { Double.NaN, 100.0, 100.0, 105.0, 110.0 }, ts1.reindex(newDates, FillMethod.FORWARDFILL).data());
		assertEqualsArray(new double[] { 100.0, 100.0, 105.0, 105.0, 110.0 }, ts1.reindex(newDates, FillMethod.BACKFILL).data());
		assertEqualsArray(new double[] { 100.0, 100.0, 100.0, 105.0, 110.0 }, ts1.reindex(newDates, FillMethod.FORWARDBACKFILL).data());
		assertEqualsArray(new double[] { 97.5, 100.0, 102.5, 105.0, 110.0 }, ts1.reindex(newDates, FillMethod.INTERPOLATE).data());
	}

	@Test(dataProvider = "testTimeSeriesProvider")
	public void testTimeSeriesFreq(Class<TimeSeries> cls) throws Exception {
		TimeSeries ts1 = cls.getConstructor(DateTime.class, Period.class, int.class, double.class).newInstance(new DateTime(2013, 1, 1, 0, 0, 0, 0),
				Days.ONE.toPeriod(), 365, 0.0);
		TimeSeries ts2 = cls.getConstructor(DateTime[].class, double[].class).newInstance(
				new DateTime[] { new DateTime(2013, 1, 31, 0, 0, 0, 0), new DateTime(2013, 2, 28, 0, 0, 0, 0), new DateTime(2013, 3, 31, 0, 0, 0, 0),
						new DateTime(2013, 4, 30, 0, 0, 0, 0), new DateTime(2013, 5, 31, 0, 0, 0, 0), new DateTime(2013, 6, 30, 0, 0, 0, 0),
						new DateTime(2013, 7, 31, 0, 0, 0, 0), new DateTime(2013, 8, 31, 0, 0, 0, 0), new DateTime(2013, 9, 30, 0, 0, 0, 0),
						new DateTime(2013, 10, 31, 0, 0, 0, 0), new DateTime(2013, 11, 30, 0, 0, 0, 0), new DateTime(2013, 12, 31, 0, 0, 0, 0) },
				new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 });

		assertEquals(ts2, ts1.asFreq(TimeSeries.FreqMethod.END_OF_MONTH));
		assertEquals(ts2.last(1), ts1.asFreq(TimeSeries.FreqMethod.END_OF_YEAR));
		TimeSeries ts3 = cls.getConstructor(DateTime.class, Period.class, int.class, double.class).newInstance(new DateTime(2013, 1, 1, 0, 0, 0, 0),
				Minutes.ONE.toPeriod(), 1440 * 2, 0.0);
		TimeSeries ts4 = cls.getConstructor(DateTime[].class, double[].class).newInstance(
				new DateTime[] { new DateTime(2013, 1, 1, 23, 59, 0, 0), new DateTime(2013, 1, 2, 23, 59, 0, 0) }, new double[] { 0.0, 0.0 });
		assertEquals(ts4, ts3.asFreq(TimeSeries.FreqMethod.END_OF_DAY));
	}

	@Test(dataProvider = "testTimeSeriesProvider")
	public void testTimeSeriesStats(Class<TimeSeries> cls) throws Exception {
		TimeSeries ts = cls.getConstructor(Map.class).newInstance(map);

		assertEquals(100.0, ts.min());
		assertEquals(110.0, ts.max());
		assertEquals(315.0, ts.sum());
		assertEquals(105.0, ts.mean());
		assertEquals(105.0, ts.median());
		assertEquals(5.0, ts.std());
		assertEquals(25.0, ts.var());
		assertEquals(1.0, ts.corr(ts));
		assertEquals(-1.0, ts.corr(ts.mul(-1.0)));
		assertEquals(1.0, ts.autoCorr(0));
		assertEqualsArray(new double[] { -1.0, 0.0, 1.0 }, ts.standardize().data());
		assertEqualsArray(new double[] { 100.0, 205.0, 315.0 }, ts.cumsum().data());
		assertEqualsArray(new double[] { 0.0, 5.0, 5.0 }, ts.diff(1).data());
		assertEquals(0.048790164169432, ts.logReturn(1).data()[1], 1.0E-9);
		assertEquals(0.05, ts.arithReturn(1).data()[1], 1.0E-9);
		
		TimeSeries ts2 = cls.getConstructor(DateTime.class, Period.class, double[].class).newInstance(
				new DateTime(2013, 1, 1, 0, 0, 0, 0), Minutes.ONE.toPeriod(), new double[] { 2.0, 1.0, 3.0, 5.0, 4.0, 6.0 });
		assertEquals(5.0, ts2.last(3).median());
	}
}
