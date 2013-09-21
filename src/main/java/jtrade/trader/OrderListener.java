package jtrade.trader;

public interface OrderListener {

	public void onOrderPlaced(OpenOrder openOrder);

	public void onOrderFilled(OpenOrder openOrder, Position position);

	public void onOrderFailed(OpenOrder openOrder);

	public void onOrderCancelled(OpenOrder openOrder);
}
