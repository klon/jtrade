package jtrade.timeseries;

import org.joda.time.DateTime;

public interface TimeSeriesValuePair {

	public DateTime getDateTime();

	public double getValue();

	public double setValue(double v);

}
