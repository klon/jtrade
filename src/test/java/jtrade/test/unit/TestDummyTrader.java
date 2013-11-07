package jtrade.test.unit;

import static org.testng.AssertJUnit.assertEquals;
import jtrade.Symbol;
import jtrade.test.MockBarMarketFeed;
import jtrade.test.TestDurationWrapper;
import jtrade.trader.DummyTrader;
import jtrade.trader.OpenOrder;
import jtrade.trader.OrderAction;
import jtrade.trader.OrderListener;
import jtrade.trader.OrderType;
import jtrade.trader.Position;
import jtrade.util.Configurable;
import jtrade.util.Util;

import org.joda.time.DateTime;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

@Listeners({ TestDurationWrapper.class })
public class TestDummyTrader {
	Symbol symbol;
	DateTime date;
	MockBarMarketFeed marketFeed;

	@BeforeClass
	public void setUp() {
		symbol = new Symbol("ABB-SFB-SEK-STOCK");
		date = new DateTime(2013, 1, 2, 0, 0, 0, 0);
		double[] opens = { 5.0, 6.0, 7.0, 8.0 };
		double[] highs = { 7.0, 8.0, 9.0, 10.0 };
		double[] lows = { 4.0, 5.0, 6.0, 7.0 };
		double[] closes = { 5.0, 6.0, 7.0, 8.0 };
		double[] waps = { 5.0, 6.0, 7.0, 8.0 };
		int[] volumes = { 10, 20, 30, 40, 50 };
		int[] trades = { 1, 1, 1, 1, 1 };
		marketFeed = new MockBarMarketFeed(symbol, 60, date, opens, highs, lows, closes, waps, volumes, trades);

		Configurable.configure("jtrade.trader.DummyTrader#EXECUTION_DELAY_MILLIS", 0);
	}

	@Test()
	public void testDummyTraderCreation() {
		DummyTrader trader = new DummyTrader(marketFeed);
		trader.connect();
		assertEquals(trader.isConnected(), true);
		assertEquals(trader.getOpenOrder(symbol, OrderType.MARKET), null);
		assertEquals(trader.getOpenOrders().size(), 0);
		assertEquals(trader.getOpenOrders(symbol, null).size(), 0);
		assertEquals(trader.getPortfolio().getPositions().size(), 0);
		assertEquals(trader.getPortfolio().getPosition(symbol).getQuantity(), 0);

	}

	@Test()
	public void testDummyTraderPlaceOrder() {
		DummyTrader trader = new DummyTrader(marketFeed);
		trader.connect();

		final boolean[] listenerCalled = new boolean[4];
		trader.addOrderListener(new OrderListener() {
			@Override
			public void onOrderPlaced(OpenOrder openOrder) {
				assertEquals(openOrder.getOrderId(), 1);
				listenerCalled[0] = true;
			}

			@Override
			public void onOrderFilled(OpenOrder openOrder, Position position) {
				assertEquals(openOrder.getOrderId(), 1);
				assertEquals(position.getQuantity(), 1);
				listenerCalled[1] = true;
			}

			@Override
			public void onOrderFailed(OpenOrder openOrder) {
			}

			@Override
			public void onOrderCancelled(OpenOrder openOrder) {
			}
		});

		OpenOrder openOrder = trader.placeOrder(symbol, OrderType.MARKET, 1, -1.0, -1.0, "test");
		assertEquals(trader.getOpenOrder(symbol, OrderType.MARKET) != null, true);
		assertEquals(trader.getOpenOrders().size(), 1);
		assertEquals(trader.getOpenOrders(symbol, OrderType.MARKET).size(), 1);
		assertEquals(openOrder.getAction(), OrderAction.BUY);
		assertEquals(openOrder.getType(), OrderType.MARKET);
		assertEquals(openOrder.getAvgFillPrice(), Double.NaN);
		assertEquals(openOrder.getCommission(), Double.NaN);
		assertEquals(openOrder.getLastFillPrice(), Double.NaN);
		assertEquals(openOrder.getOrderDate(), date);
		assertEquals(openOrder.getOrderId(), 1);
		assertEquals(openOrder.getPrice(), -1.0);
		assertEquals(openOrder.getStopPrice(), -1.0);
		assertEquals(openOrder.getTrailStopOffset(), -1.0);
		assertEquals(openOrder.getQuantity(), 1);
		assertEquals(openOrder.getQuantityFilled(), 0);
		assertEquals(openOrder.getReference(), "test");
		assertEquals(listenerCalled[0], true);

		Util.callMethod(trader, "processOpenOrders", date);

		assertEquals(listenerCalled[1], true);
	}
}
