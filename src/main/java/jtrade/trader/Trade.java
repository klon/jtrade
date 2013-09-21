package jtrade.trader;

import jtrade.Symbol;

import org.joda.time.DateTime;

public class Trade {
	DateTime date;
	Symbol symbol;
	int quantity;
	double costBasis;
	double price;
	double profitLoss;
	
	public Trade(DateTime date, Symbol symbol, int quantity, double costBasis, double price, double profitLoss) {
		super();
		this.date = date;
		this.symbol = symbol;
		this.quantity = quantity;
		this.costBasis = costBasis;
		this.price = price;
		this.profitLoss = profitLoss;
	}

	public Symbol getSymbol() {
		return symbol;
	}

	public DateTime getDate() {
		return date;
	}

	public double getQuantity() {
		return quantity;
	}
	
	public double getCostBasis() {
		return costBasis;
	}
	
	public double getPrice() {
		return price;
	}

	public double getProfitLoss() {
		return profitLoss;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		sb.append("symbol=");
		sb.append(symbol);
		sb.append(", ");
		sb.append("date=");
		sb.append(date);
		sb.append(", ");
		sb.append("quantity=");
		sb.append(quantity);
		sb.append(", costBasis=");
		sb.append(costBasis);
		sb.append(", price=");
		sb.append(price);
		sb.append(", profitLoss=");
		sb.append(profitLoss);
		sb.append("]");
		return sb.toString();
	}
	
	
}
