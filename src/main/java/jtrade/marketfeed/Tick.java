package jtrade.marketfeed;

import jtrade.Symbol;

import org.joda.time.DateTime;

public class Tick {
	Symbol symbol;
	DateTime dateTime;
	double ask;
	double bid;
	double price;
	int askSize;
	int bidSize;
	int lastSize;
	int volume;
	MarketDepth marketDepth;

	public Tick(Symbol symbol) {
		this.symbol = symbol;
	}

	public Tick(Symbol symbol, DateTime dateTime, double ask, int askSize, double bid, int bidSize, double price, int lastSize,
			int volume, MarketDepth marketDepth) {
		this.symbol = symbol;
		this.dateTime = dateTime;
		this.ask = ask;
		this.bid = bid;
		this.price = price;
		this.askSize = askSize;
		this.bidSize = bidSize;
		this.lastSize = lastSize;
		this.volume = volume;
		this.marketDepth = marketDepth;
	}

	public Tick(Tick tick) {
		this.symbol = tick.symbol;
		this.dateTime = tick.dateTime;
		this.ask = tick.ask;
		this.bid = tick.bid;
		this.price = tick.price;
		this.askSize = tick.askSize;
		this.bidSize = tick.bidSize;
		this.lastSize = tick.lastSize;
		this.volume = tick.volume;
		this.marketDepth = tick.marketDepth != null ? new MarketDepth(tick.marketDepth) : null;
	}

	public boolean isComplete() {
		return !(dateTime == null || ask < 0 || bid < 0 || price < 0 || askSize < 0 || bidSize < 0 || lastSize < 0 || volume < 0);
	}

	public Symbol getSymbol() {
		return symbol;
	}

	public DateTime getDateTime() {
		return dateTime;
	}

	public double getAsk() {
		return ask;
	}

	public double getBid() {
		return bid;
	}

	public double getPrice() {
		return price;
	}

	public double getMidPrice() {
		return (ask + bid) / 2;
	}

	public int getAskSize() {
		return askSize;
	}

	public int getBidSize() {
		return bidSize;
	}

	public int getLastSize() {
		return lastSize;
	}

	public int getVolume() {
		return volume;
	}

	public MarketDepth getMarketDepth() {
		return marketDepth;
	}

	@Override
	public int hashCode() {
		int h = 31 * (dateTime != null ? dateTime.hashCode() : 0);
		return h + 31 * (symbol != null ? symbol.hashCode() : 0);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		Tick other = (Tick) obj;
		if ((dateTime == null && other.dateTime != null) || !dateTime.equals(other.dateTime))
			return false;
		if ((symbol == null && other.symbol != null) || !symbol.equals(other.symbol))
			return false;
		if (ask != other.ask)
			return false;
		if (askSize != other.askSize)
			return false;
		if (bid != other.bid)
			return false;
		if (bidSize != other.bidSize)
			return false;
		if (price != other.price)
			return false;
		if (lastSize != other.lastSize)
			return false;
		if (volume != other.volume)
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		sb.append(dateTime);
		sb.append(' ');
		sb.append(symbol);
		sb.append(", a=");
		sb.append(ask);
		sb.append(", as=");
		sb.append(askSize);
		sb.append(", b=");
		sb.append(bid);
		sb.append(", bs=");
		sb.append(bidSize);
		sb.append(", p=");
		sb.append(price);
		sb.append(", s=");
		sb.append(lastSize);
		sb.append(", v=");
		sb.append(volume);
		sb.append(']');
		return sb.toString();
	}

}
