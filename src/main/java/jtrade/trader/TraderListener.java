package jtrade.trader;

public interface TraderListener {

	public void onTraderConnected(Trader trader);

	public void onTraderDisconnected(Trader trader);
}
