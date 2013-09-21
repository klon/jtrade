package jtrade.timeseries;

import java.util.NavigableMap;

import org.joda.time.DateTime;
import org.joda.time.LocalTime;
import org.joda.time.Period;

/**
 * Interface for classes representing time series data.
 * 
 * @author jonkle
 * 
 */
public interface TimeSeries extends Iterable<TimeSeriesValuePair> {

	/**
	 * Fill methods used when replacing missing
	 * 
	 * @author jonkle
	 */
	enum FillMethod {
		NAN, FORWARDFILL, BACKFILL, FORWARDBACKFILL, INTERPOLATE,
	}

	/**
	 * Frequency conversion templates
	 * 
	 * @author jonkle
	 */
	enum FreqMethod {
		END_OF_DAY, END_OF_MONTH, END_OF_YEAR,
	}

	/**
	 * 
	 * @return Array of dates for the time series data points.
	 */
	public DateTime[] dates();

	/**
	 * 
	 * @return Array of values for the time series data points.
	 */
	public double[] data();

	/**
	 * 
	 * @return Number of data points.
	 */
	public int size();

	/**
	 * 
	 * @return Length of time series in milliseconds.
	 */
	public long duration();

	/**
	 * 
	 * @return Start of time series.
	 */
	public DateTime start();

	/**
	 * 
	 * @return End of time series.
	 */
	public DateTime end();

	/**
	 * 
	 * @return First value of time series.
	 */
	public double first();

	/**
	 * 
	 * @return Last value of time series.
	 */
	public double last();

	/**
	 * Gets the value of the specified date
	 * 
	 * @param date
	 * @return Value for specified date
	 */
	public double get(DateTime date);

	/**
	 * Sets the value for the specified date
	 * 
	 * @param date
	 * @param value
	 */
	public void set(DateTime date, double value);

	/**
	 * Creates a copy of this time series
	 * 
	 * @return A copy of this time series
	 */
	public TimeSeries copy();

	/**
	 * Truncates a copy of this time series to the specified from and to indices.
	 * 
	 * @param from
	 * @param to
	 * @return A truncated time series.
	 */
	public TimeSeries truncate(int from, int to);

	/**
	 * Truncates a copy of this time series to the specified from and to dates.
	 * 
	 * @param from
	 * @param to
	 * @return A truncated time series.
	 */
	public TimeSeries truncate(DateTime from, DateTime to);

	/**
	 * Shifts the time series values forward or backward and leaves the dates
	 * unchanged.
	 * 
	 * @param periods
	 * @return A shifted time series
	 */
	public TimeSeries shift(int periods);

	/**
	 * Shifts the time series dates forward or backward and leaves the data
	 * unchanged.
	 * 
	 * @param period
	 * @return A shifted time series
	 */
	public TimeSeries shift(Period period);

	/**
	 * Creates a new time series that is the union of the two time series.
	 * 
	 * @param ts
	 *          (will have precedence if the same dates are in both series)
	 * @return A union of this and ts
	 */
	public TimeSeries union(TimeSeries ts);

	/**
	 * Creates a new time series that is the intersection of the two time series.
	 * 
	 * @param ts
	 * @return An intersection of this and ts
	 */
	public TimeSeries intersect(TimeSeries ts);

	/**
	 * Splits this time series into several time series.
	 * 
	 * @param period
	 * @return Array of time series of the same length.
	 */
	public TimeSeries[] split(Period period);

	/**
	 * Removes all NaN values from time series
	 * 
	 * @return A time series without NaNs.
	 */
	public TimeSeries valid();

	/**
	 * Removes all data points with NaN values at the beginning and end of this
	 * time series.
	 * 
	 * @return A time series without NaNs at the beginning and end.
	 */
	public TimeSeries trim();

	/**
	 * Crates a new TimeSeries with only the last specified number of values
	 * 
	 * @return A new time series
	 */
	public TimeSeries last(int size);

	/**
	 * Crates a new TimeSeries with only the first specified number of values
	 * 
	 * @return A new time series
	 */
	public TimeSeries first(int size);

	/**
	 * Replaces all dates in this time series with the new specified dates. Values
	 * that do not exist for the new dates are set according to fill method.
	 * 
	 * @param dates
	 * @param method
	 * @return A new time series
	 */
	public TimeSeries reindex(DateTime[] dates, FillMethod method);

	/**
	 * Creates a new time series with data made to conform to the new frequency
	 * specified according to fill method.
	 * 
	 * @param period
	 * @param method
	 * @return A new time series
	 */
	public TimeSeries asFreq(Period period, FillMethod method);

	/**
	 * Creates a new time series with data made to conform to the new frequency
	 * specified according to frequency method.
	 * 
	 * @param method
	 * @return A new time series
	 */
	public TimeSeries asFreq(FreqMethod method);

	/**
	 * Creates a new time series that matches the specified ts frequency and
	 * interval.
	 * 
	 * @param method
	 * @return A new time series
	 */
	public TimeSeries match(TimeSeries ts, FillMethod method);

	/**
	 * Creates a new time series that only contains values within the given times
	 * inclusive.
	 * 
	 * @param from
	 * @param to
	 * @return A new time series
	 */
	public TimeSeries filter(LocalTime from, LocalTime to);

	/**
	 * Creates a new time series that only contains values within the given range
	 * inclusive.
	 * 
	 * @param from
	 * @param to
	 * @return A new time series
	 */
	public TimeSeries filter(double min, double max);

	/**
	 * Creates a new time series with millis of day set to zero for each value.
	 * 
	 * @param from
	 * @param to
	 * @return A new time series
	 */
	public TimeSeries clearTime();

	/**
	 * Creates a new time series with all NaN values replaced with specified
	 * value.
	 * 
	 * @param value
	 * @return A new time series
	 */
	public TimeSeries fill(double value);

	/**
	 * Creates a new time series with all NaN values replaced according to fill
	 * method.
	 * 
	 * @param method
	 * @return A new time series
	 */
	public TimeSeries fill(FillMethod method);

	/**
	 * Creates a new time series with specified value added to all data points.
	 * 
	 * @param value
	 * @return A new time series
	 */
	public TimeSeries add(double value);

	/**
	 * Creates a new time series with specfied ts values added with all data
	 * points.
	 * 
	 * @param value
	 * @return A new time series
	 */
	public TimeSeries add(TimeSeries ts);

	/**
	 * Creates a new time series with specified value subtracted from all data
	 * points.
	 * 
	 * @param value
	 * @return A new time series
	 */
	public TimeSeries sub(double value);

	/**
	 * Creates a new time series with specfied ts values subtracted from all data
	 * points.
	 * 
	 * @param value
	 * @return A new time series
	 */
	public TimeSeries sub(TimeSeries ts);

	/**
	 * Creates a new time series with all data points multiplied with specified
	 * value.
	 * 
	 * @param value
	 * @return A new time series
	 */
	public TimeSeries mul(double value);

	/**
	 * Creates a new time series with specfied ts values multiplied with all data
	 * points.
	 * 
	 * @param value
	 * @return A new time series
	 */
	public TimeSeries mul(TimeSeries ts);

	/**
	 * Creates a new time series with all data points divided with specified
	 * value.
	 * 
	 * @param value
	 * @return A new time series
	 */
	public TimeSeries div(double value);

	/**
	 * Creates a new time series with specfied ts values divided with all data
	 * points.
	 * 
	 * @param value
	 * @return A new time series
	 */
	public TimeSeries div(TimeSeries ts);

	/**
	 * Creates a new time series with all data points clipped to specified min and
	 * max.
	 * 
	 * @param value
	 * @return A new time series
	 */
	public TimeSeries clip(double min, double max);

	/**
	 * Creates a new time series with the first order difference using specified
	 * lag.
	 * 
	 * @param value
	 * @return A new time series
	 */
	public TimeSeries diff(int lag);

	/**
	 * Creates a new time series with the arithmetic return using specified lag.
	 * 
	 * @param value
	 * @return A new time series
	 */
	public TimeSeries arithReturn(int lag);

	/**
	 * Creates a new time series with the log return using specified lag.
	 * 
	 * @param value
	 * @return A new time series
	 */
	public TimeSeries logReturn(int lag);

	/**
	 * Creates a new time series with all values standardized ie mean subtracted
	 * and divided by stdev.
	 * 
	 * @param value
	 * @return A new time series
	 */
	public TimeSeries standardize();

	/**
	 * Creates a new time series with all values normalized according to specified
	 * min and max or by series own min and max if NaNs are specified.
	 * 
	 * @param min
	 * @param max
	 * @return A new time series
	 */
	public TimeSeries normalize(double min, double max);

	/**
	 * Creates a new time series with all values set to the cumulative sum.
	 * 
	 * @return A new time series
	 */
	public TimeSeries cumsum();

	/**
	 * Calculates the time series arithmetic mean.
	 * 
	 * @return The mean
	 */
	public double mean();

	/**
	 * Calculates the time series median.
	 * 
	 * @return The median
	 */
	public double median();

	/**
	 * Calculates the time series mode.
	 * 
	 * @return The mode
	 */
	public double mode();

	/**
	 * Calculates the time series sum.
	 * 
	 * @return The sum
	 */
	public double sum();

	/**
	 * Calculates the time series min.
	 * 
	 * @return The min
	 */
	public double min();

	/**
	 * Calculates the time series max.
	 * 
	 * @return The max
	 */
	public double max();

	/**
	 * Calculates the time series variance.
	 * 
	 * @return The variance
	 */
	public double var();

	/**
	 * Calculates the time series standard deviation.
	 * 
	 * @return The stdev
	 */
	public double std();

	/**
	 * Calculates the Pearson correlation coefficient between the two time series.
	 * 
	 * @param ts
	 * @return The correlation coefficient
	 */
	public double corr(TimeSeries ts);

	/**
	 * Calculates the time series autocorrelation at the specified lag.
	 * 
	 * @param lag
	 * @return
	 */
	public double autoCorr(int lag);

	/**
	 * Applies time series operation the time series.
	 * 
	 * @param op
	 * @return A new time series
	 */
	public TimeSeries apply(TimeSeriesOp op);

	/**
	 * Creates a string representation of this time series.
	 * 
	 * @param tabular
	 *          , if true the values are returned in a tab delimited fashion.
	 * @return A string representation
	 */
	public String toString(boolean tabular);

	/**
	 * Creates a NavigableMap of this time series.
	 * 
	 * @return A map
	 */
	public NavigableMap<DateTime, Double> toMap();

	/**
	 * Calculates the time series rolling variance.
	 * 
	 * @return A new time series
	 */
	public TimeSeries rollingMean(int window);

	/**
	 * Calculates the time series rolling variance.
	 * 
	 * @return A new time series
	 */
	public TimeSeries rollingVar(int window);

	/**
	 * Calculates the time series rolling standard deviation.
	 * 
	 * @return A new time series
	 */
	public TimeSeries rollingStd(int window);

	/**
	 * Calculates the time series rolling median.
	 * 
	 * @return A new time series
	 */
	public TimeSeries rollingMedian(int window);

}
