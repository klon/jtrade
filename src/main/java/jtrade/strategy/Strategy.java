package jtrade.strategy;

import jtrade.Recorder;
import jtrade.marketfeed.MarketFeed;
import jtrade.trader.Trader;

public interface Strategy {

	public void init();

	public void destroy();

	public MarketFeed getMarketFeed();

	public void setMarketFeed(MarketFeed marketFeed);

	public Trader getTrader();

	public void setTrader(Trader trader);

	public Recorder getRecorder();

	public void setRecorder(Recorder plotter);

}
