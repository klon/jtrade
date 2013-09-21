package jtrade.strategy;

import java.io.File;

import jtrade.Symbol;
import jtrade.SymbolFactory;
import jtrade.marketfeed.Bar;
import jtrade.marketfeed.BarListener;
import jtrade.trader.Position;

import org.joda.time.DateTime;

public class TestStrategy extends BaseStrategy implements BarListener {
	static Symbol symbol = SymbolFactory.getSymbol("AAPL-SMART-USD-STOCK");

	public TestStrategy() {
		super();
	}

	@Override
	public void init() {
		setVerbose(true);
		setActive(true);
		marketFeed.addBarListener(symbol, this);
	}

	@Override
	public void onBar(Bar bar) {
		if (bar.getDateTime().equals(new DateTime(2013, 1, 2, 16, 1, 0, 0))) {
			trader.placeOrder(bar.getSymbol(), 2, "testref_buy");
		} else if (bar.getDateTime().equals(new DateTime(2013, 1, 2, 16, 50, 0, 0))) {
			trader.placeOrder(bar.getSymbol(), -1, "testref_sell");
		}
	}

	public static void main(String[] args) {
		try {
			TestStrategy strategy = new TestStrategy();
			File dataDir = new File("~/marketdata/ib");
			DateTime fromDate = new DateTime(2013, 1, 2, 16, 0, 0, 0);
			DateTime toDate = new DateTime(2013, 1, 2, 17, 0, 0, 0);

			StrategySim sim = new StrategySim(strategy, dataDir, fromDate, toDate, false, 60, symbol);
			sim.run();

			for (Position p : strategy.getTrader().getPortfolio().getPositions()) {
				System.out.println(p);
			}
			strategy.getRecorder().plot();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
