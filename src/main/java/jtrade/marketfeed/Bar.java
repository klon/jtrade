package jtrade.marketfeed;

import jtrade.Symbol;
import jtrade.util.Util;

import org.joda.time.DateTime;
import org.joda.time.Duration;

public class Bar {
	Duration barSize;
	Symbol symbol;
	DateTime dateTime;

	double open;
	double close;
	double high;
	double low;
	double wap;
	long volume;
	int trades;

	public Bar(Duration barSize, Symbol symbol, DateTime dateTime) {
		this.barSize = barSize;
		this.symbol = symbol;
		this.dateTime = dateTime;
	}

	public Bar(Duration barSize, Symbol symbol, DateTime dateTime, double open, double high, double low, double close, double wap,
			long volume, int trades) {
		this.barSize = barSize;
		this.symbol = symbol;
		this.dateTime = dateTime;
		this.open = open;
		this.high = high;
		this.low = low;
		this.close = close;
		this.wap = wap;
		this.volume = volume;
		this.trades = trades;
	}

	public Bar(Bar bar, DateTime dateTime, double open, double high, double low, double close, double wap, long volume, int trades) {
		this(bar.getBarSize(), bar.getSymbol(), dateTime, open, high, low, close, wap, volume, trades);
	}

	public Bar(Bar bar) {
		this.barSize = bar.barSize;
		this.symbol = bar.symbol;
		this.dateTime = bar.dateTime;
		this.open = bar.open;
		this.high = bar.high;
		this.low = bar.low;
		this.close = bar.close;
		this.wap = bar.wap;
		this.volume = bar.volume;
		this.trades = bar.trades;
	}

	public void setValues(double open, double high, double low, double close, double wap, long volume, int trades) {
		this.open = open;
		this.high = high;
		this.low = low;
		this.close = close;
		this.wap = wap;
		this.volume = volume;
		this.trades = trades;
	}

	public boolean isComplete() {
		return !(dateTime == null || wap <= 0 || open <= 0 || high <= 0 || low <= 0 || close <= 0);
	}

	public Duration getBarSize() {
		return barSize;
	}

	public Symbol getSymbol() {
		return symbol;
	}

	public DateTime getDateTime() {
		return dateTime;
	}

	public double getOpen() {
		return open;
	}

	public double getHigh() {
		return high;
	}

	public double getLow() {
		return low;
	}

	public double getClose() {
		return close;
	}

	public double getPrice() {
		return wap;
	}

	public long getVolume() {
		return volume;
	}

	public int getTrades() {
		return trades;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((barSize == null) ? 0 : barSize.hashCode());
		result = prime * result + ((dateTime == null) ? 0 : dateTime.hashCode());
		result = prime * result + ((symbol == null) ? 0 : symbol.hashCode());
		long temp = Double.doubleToLongBits(wap);
		result = prime * result + (int) (temp ^ (temp >>> 32));
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
		Bar other = (Bar) obj;
		if (dateTime == null) {
			if (other.dateTime != null)
				return false;
		} else if (!dateTime.equals(other.dateTime))
			return false;
		if (symbol == null) {
			if (other.symbol != null)
				return false;
		} else if (!symbol.equals(other.symbol))
			return false;
		if (Double.doubleToLongBits(wap) != Double.doubleToLongBits(other.wap))
			return false;
		if (barSize == null) {
			if (other.barSize != null)
				return false;
		} else if (!barSize.equals(other.barSize))
			return false;
		if (Double.doubleToLongBits(close) != Double.doubleToLongBits(other.close))
			return false;
		if (Double.doubleToLongBits(high) != Double.doubleToLongBits(other.high))
			return false;
		if (Double.doubleToLongBits(low) != Double.doubleToLongBits(other.low))
			return false;
		if (Double.doubleToLongBits(open) != Double.doubleToLongBits(other.open))
			return false;
		if (trades != other.trades)
			return false;
		if (volume != other.volume)
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		sb.append(dateTime.toString("yyyyMMdd HH:mm:ss"));
		sb.append(' ');
		sb.append(symbol.getCode());
		sb.append(' ');
		sb.append(Util.round(open, 2));
		sb.append(' ');
		sb.append(Util.round(high, 2));
		sb.append(' ');
		sb.append(Util.round(low, 2));
		sb.append(' ');
		sb.append(Util.round(close, 2));
		sb.append(" v=");
		sb.append(volume);
		sb.append(" t=");
		sb.append(trades);
		sb.append(" w=");
		sb.append(Util.round(wap, 2));
		sb.append(']');
		return sb.toString();
	}
}
