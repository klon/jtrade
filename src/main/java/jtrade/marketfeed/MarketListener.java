package jtrade.marketfeed;

import org.joda.time.DateTime;

public interface MarketListener {

	public void onDay(DateTime dateTime);

	public void onHour(DateTime dateTime);

	public void onMinute(DateTime dateTime);

}
