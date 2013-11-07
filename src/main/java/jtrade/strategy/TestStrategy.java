package jtrade.strategy;

import java.io.File;

import jtrade.Symbol;
import jtrade.SymbolFactory;
import jtrade.indicator.MA;
import jtrade.marketfeed.Bar;
import jtrade.marketfeed.BarListener;
import jtrade.trader.Position;
import jtrade.trader.Trade;
import jtrade.util.Configurable;

import org.joda.time.DateTime;

public class TestStrategy extends BaseStrategy implements BarListener {
	public Configurable<Symbol> SYMBOL = new Configurable<Symbol>("SYMBOL", SymbolFactory.getSymbol("SPY-ARCA-USD-STOCK"));

	MA ma;
	
	public TestStrategy() {
		super();
	}

	@Override
	public void init() {
		setVerbose(true);
		ma = new MA(100);
		marketFeed.addBarListener(SYMBOL.get(), ma);
		marketFeed.addBarListener(SYMBOL.get(), this);
	}

	@Override
	public void onBar(Bar bar) {
		Position position = trader.getPortfolio().getPosition(SYMBOL.get());
		if (position.isFlat() && bar.getClose() >= ma.get()) {
			setPosition(SYMBOL.get(), (int)(trader.getPortfolio().getCashValue() / bar.getClose()));
		} else if (bar.getClose() < ma.get()) {
			setPosition(SYMBOL.get(), 0);
		}
	}

	public static void main(String[] args) {
		try {
			TestStrategy strategy = new TestStrategy();
			File dataDir = new File("~/marketdata/yahoo");
			DateTime fromDate = new DateTime(2004, 1, 1, 0, 0, 0, 0);
			DateTime toDate = new DateTime(2012, 1, 1, 0, 0, 0, 0);

			StrategySim sim = new StrategySim(strategy, dataDir, fromDate, toDate, false, 86400, strategy.SYMBOL.get());
			sim.run();

			for (Trade t : strategy.getTrader().getPerformanceTracker().getTrades()) {
				System.out.println(t);
			}
			System.out.println(strategy.getTrader().getPerformanceTracker());
			strategy.getRecorder().plot();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
