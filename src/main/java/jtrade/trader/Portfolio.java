package jtrade.trader;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import jtrade.Symbol;
import jtrade.marketfeed.MarketFeed;
import jtrade.marketfeed.Tick;

public class Portfolio {
	MarketFeed marketFeed;
	Map<Symbol, Position> positions;
	Map<String, DoubleValue> cash;
	Map<String, Double> exchangeRatesInBase;
	String baseCurrency;

	@SuppressWarnings("serial")
	public Portfolio(MarketFeed marketFeed) {
		this.marketFeed = marketFeed;
		this.cash = new TreeMap<String, DoubleValue>() {
			@Override
			public DoubleValue get(Object key) {
				DoubleValue value = super.get(key);
				if (value == null) {
					super.put((String) key, value = new DoubleValue(0.0));
				}
				return value;
			}
		};
		this.positions = new TreeMap<Symbol, Position>() {
			@Override
			public Position get(Object key) {
				Position value = super.get(key);
				if (value == null) {
					super.put((Symbol) key, value = new Position((Symbol) key));
				}
				return value;
			}
		};
		this.exchangeRatesInBase = new TreeMap<String, Double>() {
			@Override
			public Double get(Object key) {
				Double value = super.get(key);
				if (value == null) {
					super.put((String) key, value = Double.valueOf(1.0));
				}
				return value;
			}
		};
		baseCurrency = "USD";
	}

	public Position getPosition(Symbol symbol) {
		return positions.get(symbol);
	}

	public void setPosition(Symbol symbol, Position position) {
		positions.put(symbol, position);
	}

	public List<Position> getPositions() {
		return new ArrayList<Position>(positions.values());
	}
	
	public double getPortfolioValue() {
		double total = 0.0;
		for (Position p : positions.values()) {
			Tick lastTick = marketFeed.getLastTick(p.getSymbol());
			if (lastTick != null) {
				total += p.getValue(marketFeed.getLastTick(p.getSymbol()).getMidPrice()) * getExchangeRate(p.getSymbol().getCurrency());
			}
		}
		return total;
	}
	
	public double getCashValue() {
		double total = 0.0;
		for (String currency : cash.keySet()) {
			total += getCash(currency) * getExchangeRate(currency);
		}
		return total;
	}

	public double getCash(String currency) {
		return cash.get(currency).value;
	}

	public void setCash(String currency, double amount) {
		cash.get(currency).value = amount;
	}
	
	public void addCash(String currency, double amount) {
		cash.get(currency).value += amount;
	}

	public double getExchangeRate(String currency) {
		return exchangeRatesInBase.get(currency);
	}

	public void setExchangeRate(String currency, double exchangeRate) {
		exchangeRatesInBase.put(currency, exchangeRate);
	}

	public String getBaseCurrency() {
		return baseCurrency;
	}

	public void setBaseCurrency(String baseCurrency) {
		this.baseCurrency = baseCurrency;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		if (positions != null) {
			sb.append("positions=");
			sb.append(positions.values());
			sb.append(", ");
		}
		if (cash != null) {
			sb.append("cash=");
			sb.append(cash);
			sb.append(", ");
		}
		if (exchangeRatesInBase != null) {
			sb.append("exchangeRatesInBase=");
			sb.append(exchangeRatesInBase);
			sb.append(", ");
		}
		if (baseCurrency != null) {
			sb.append("baseCurrency=");
			sb.append(baseCurrency);
		}
		sb.append("]");
		return sb.toString();
	}

	class DoubleValue {
		double value;
		
		public DoubleValue(double d) {
			value = d;
		}
		
		public String toString() {
			return String.valueOf(value);
		}
	}
}
