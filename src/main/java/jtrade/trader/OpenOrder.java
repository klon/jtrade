package jtrade.trader;

import jtrade.Symbol;

import org.joda.time.DateTime;

public class OpenOrder {
	private int orderId;
	private Symbol symbol;
	private OrderType type;
	private OrderStatus status;
	private int quantity;
	private double price;
	private double stopPrice;
	private double trailStopOffset;
	private int quantityFilled;
	private int lastFillQuantity;
	private double lastFillPrice;
	private double avgFillPrice;
	private double commission;
	private double maxPrice;
	private double minPrice;
	private DateTime orderDate;
	private DateTime fillDate;
	private String reference;

	public OpenOrder(int orderId, Symbol symbol, OrderType type, int quantity, double price, double stopPrice, double trailStopOffset, DateTime orderDate,
			String reference) {
		this.orderId = orderId;
		this.symbol = symbol;
		this.type = type;
		this.quantity = quantity;
		this.price = price;
		this.stopPrice = stopPrice;
		this.trailStopOffset = trailStopOffset;
		this.orderDate = orderDate;
		this.reference = reference;
		this.status = OrderStatus.OPEN;
		lastFillPrice = Double.NaN;
		avgFillPrice = Double.NaN;
		commission = Double.NaN;
		maxPrice = Double.MIN_VALUE;
		minPrice = Double.MAX_VALUE;

	}

	public int getOrderId() {
		return orderId;
	}

	public Symbol getSymbol() {
		return symbol;
	}

	public OrderAction getAction() {
		return isBuy() ? OrderAction.BUY : OrderAction.SELL;
	}

	public OrderType getType() {
		return type;
	}

	public OrderStatus getStatus() {
		return status;
	}

	public int getQuantity() {
		return quantity;
	}

	public double getPrice() {
		return price;
	}

	public double getStopPrice() {
		return stopPrice;
	}

	public double getTrailStopOffset() {
		return trailStopOffset;
	}

	public double getCommission() {
		return commission;
	}

	public void setCommission(double commission) {
		this.commission = commission;
	}

	public int getQuantityFilled() {
		return quantityFilled;
	}

	public int getLastFillQuantity() {
		return lastFillQuantity;
	}

	public double getLastFillPrice() {
		return lastFillPrice;
	}

	public double getAvgFillPrice() {
		return avgFillPrice;
	}

	public DateTime getOrderDate() {
		return orderDate;
	}

	public DateTime getFillDate() {
		return fillDate;
	}

	public String getReference() {
		return reference;
	}

	public void update(int quantityChange, double price, DateTime dateTime) {
		lastFillQuantity = quantityChange;
		lastFillPrice = price;
		if (avgFillPrice != avgFillPrice) {
			avgFillPrice = price;
		} else {
			avgFillPrice = (avgFillPrice * quantityFilled + quantityChange * price) / (quantityFilled + quantityChange);
		}
		quantityFilled += quantityChange;
		fillDate = dateTime;
		if (Math.abs(this.quantityFilled) >= Math.abs(this.quantity)) {
			status = OrderStatus.FILLED;
		}
	}

	public void setFilled(double avgFillPrice, DateTime fillDate) {
		this.avgFillPrice = avgFillPrice;
		this.fillDate = fillDate;
		quantityFilled = quantity;
		status = OrderStatus.FILLED;
	}

	public void setFailed() {
		status = OrderStatus.FAILED;
	}

	public void setCancelled() {
		status = OrderStatus.CANCELLED;
	}

	public double getMaxPrice() {
		return maxPrice;
	}

	public double getMinPrice() {
		return minPrice;
	}

	public void updateMinMaxPrice(double price) {
		this.maxPrice = Math.max(maxPrice, price);
		this.minPrice = Math.min(minPrice, price);
	}

	public boolean isOpen() {
		return status == OrderStatus.OPEN;
	}

	public boolean isFailed() {
		return status == OrderStatus.FAILED;
	}

	public boolean isCancelled() {
		return status == OrderStatus.CANCELLED;
	}

	public boolean isFilled() {
		return status == OrderStatus.FILLED;
	}

	public double getFillValue() {
		return quantityFilled * avgFillPrice;
	}

	public boolean isBuy() {
		return quantity > 0;
	}

	public boolean isSell() {
		return quantity < 0;
	}

	public boolean isMarket() {
		return type == OrderType.MARKET;
	}

	public boolean isLimit() {
		return type == OrderType.LIMIT;
	}

	public boolean isStopMarket() {
		return type == OrderType.STOP_MARKET;
	}

	public boolean isStopLimit() {
		return type == OrderType.STOP_LIMIT;
	}

	public boolean isTrailMarket() {
		return type == OrderType.TRAIL_MARKET;
	}

	public boolean isTrailLimit() {
		return type == OrderType.TRAIL_LIMIT;
	}

	public boolean isStop() {
		return type == OrderType.STOP_MARKET || type == OrderType.STOP_LIMIT || type == OrderType.TRAIL_MARKET || type == OrderType.TRAIL_LIMIT;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("OpenOrder [");
		sb.append(isBuy() ? "BUY" : "SELL");
		sb.append(" ");
		sb.append(type);
		sb.append(" ");
		sb.append(symbol);
		sb.append(" ");
		sb.append(quantity);
		if (price > 0) {
			sb.append("@");
			sb.append(price);
		}
		if (stopPrice > 0) {
			sb.append(", stopPrice=");
			sb.append(stopPrice);
		}
		if (trailStopOffset > 0) {
			sb.append(", trailStopOffset=");
			sb.append(trailStopOffset);
		}
		sb.append(", orderId=");
		sb.append(orderId);
		sb.append(", orderDate=");
		sb.append(orderDate);
		sb.append(", status=");
		sb.append(status);
		if (isFilled()) {
			sb.append(", ");
			sb.append("quantityFilled=");
			sb.append(quantityFilled);
			sb.append(", fillDate=");
			sb.append(fillDate);
			sb.append(", avgFillPrice=");
			sb.append(avgFillPrice);
			sb.append(", commission=");
			sb.append(commission);
		}
		if (reference != null) {
			sb.append(", ");
			sb.append("reference=");
			sb.append(reference);
		}
		sb.append("]");
		return sb.toString();
	}

}
