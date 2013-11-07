package jtrade.trader;

import jtrade.Symbol;
import jtrade.util.Util;

import org.joda.time.DateTime;

public class Position {
	Symbol symbol;
 	int quantity;
 	double costBasis;
 	double commission;

	public Position(Symbol symbol) {
		this(symbol, 0, Double.NaN, Double.NaN);
	}

	public Position(Symbol symbol, int quantity, double costBasis, double commission) {
		this.symbol = symbol;
		this.quantity = quantity;
		this.costBasis = costBasis;
		this.commission = commission;
	}

	public Symbol getSymbol() {
		return symbol;
	}

	public int getQuantity() {
		return quantity;
	}

	public double getCostBasis() {
		return costBasis;
	}

	public double getCommission() {
		return commission;
	}

	public boolean isLong() {
		return quantity > 0;
	}

	public boolean isShort() {
		return quantity < 0;
	}

	public boolean isFlat() {
		return quantity == 0;
	}

	public void set(int quantity, double costBasis, double commission) {
		this.quantity = quantity;
		this.costBasis = costBasis;
		this.commission = commission;
	}

	public double[] update(DateTime date, int quantityChange, double price, double commission) {
		if (quantityChange == 0) {
			throw new IllegalArgumentException("Quantity changed cannot be zero");
		}
		int multiplier = symbol.getMultiplier();
		int quantityRealized = Math.min(Math.abs(quantity), Math.abs(quantityChange)) * (quantityChange > 0 ? 1 : -1);
		double priceIncCommission = price + (commission / quantityChange / multiplier);
		double costBasis = quantity == 0 ? priceIncCommission : this.costBasis;
		double[] values = null;
		if (quantity == 0) {
			this.costBasis = priceIncCommission;
			this.commission = commission;
			this.quantity += quantityChange;
			values = new double[] { 0, costBasis, priceIncCommission, -commission, 0.0 };
		} else if (quantity + quantityChange == 0) {
			this.costBasis = Double.NaN;
			this.commission = Double.NaN;
			this.quantity = 0;
			values = new double[] { quantityRealized, costBasis, priceIncCommission, quantityRealized * (costBasis - priceIncCommission) * multiplier , 0.0 };
		} else {
			this.commission = commission;
			if ((quantity > 0 && quantityChange > 0) || (quantity < 0 && quantityChange < 0)) {
				this.costBasis = (this.quantity * this.costBasis + quantityChange * priceIncCommission) / (quantity + quantityChange);
				this.quantity += quantityChange;
				values = new double[] { 0, this.costBasis, priceIncCommission, 0.0, (price - this.costBasis) * quantity * multiplier };
			} else {
				this.quantity += quantityChange;
				values = new double[] { quantityRealized, this.costBasis, priceIncCommission, quantityRealized * (costBasis - priceIncCommission), (price - costBasis) * quantity * multiplier };
			}
		}
		return values;
	}

	public double getCost() {
		return Math.abs(quantity * costBasis * symbol.getMultiplier());
	}

	public double getProfitLoss(double price) {
		return (quantity * price - quantity * costBasis) * symbol.getMultiplier();
	}

	public double getReturn(double price) {
		double buyTotal = getCost();
		return (getProfitLoss(price) + buyTotal) / buyTotal - 1;
	}
	
	public double getValue(double price) {
		return quantity * price * symbol.getMultiplier();
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("[symbol=");
		builder.append(symbol.getFullCode());
		builder.append(", quantity=");
		builder.append(quantity);
		builder.append(", costBasis=");
		builder.append(Util.round(costBasis, 4));
		builder.append(", commission=");
		builder.append(Util.round(commission, 4));
		builder.append("]");
		return builder.toString();
	}

}
