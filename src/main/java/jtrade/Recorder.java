package jtrade;

import org.joda.time.DateTime;

public interface Recorder {

	public void record(String name, DateTime dateTime, double... value);

	public void record(String name, DateTime dateTime, boolean autoScale, double... value);

	public void plot();

}