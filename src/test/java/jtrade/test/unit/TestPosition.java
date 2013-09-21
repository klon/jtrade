package jtrade.test.unit;

import static org.testng.AssertJUnit.assertEquals;
import jtrade.Symbol;
import jtrade.test.TestDurationWrapper;
import jtrade.trader.Position;

import org.joda.time.DateTime;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

@Listeners({ TestDurationWrapper.class })
public class TestPosition {
	static final float TOL = 1E-9f;

	Symbol symbol;
	DateTime date;

	@BeforeClass
	public void setUp() {
		symbol = new Symbol("ABB-SFB-SEK-STOCK");
		date = new DateTime(2013, 1, 1, 0, 0, 0, 0);
	}

	@Test()
	public void testPositionCreation() {
		Position p = new Position(symbol, 100, 10.01, 1.0);
		assertEquals(symbol, p.getSymbol());
		assertEquals(100, p.getQuantity());
		assertEquals(10.01, p.getCostBasis());
		assertEquals(1.0, p.getCommission());
	}

	@Test()
	public void testPositionUpdate() {
		Position p = new Position(symbol, 100, 10.01, 1.0);
		double[] values = p.update(date, -100, 20.0, 1.0);
		assertEquals(0, p.getQuantity(), TOL);
		assertEquals(Double.NaN, p.getCostBasis());
		assertEquals(Double.NaN, p.getCommission());
		assertEquals(-100, values[0], TOL);
		assertEquals(10.01, values[1], TOL);
		assertEquals(19.99, values[2], TOL);
		assertEquals(998.0, values[3], TOL);
		assertEquals(0.0, values[4], TOL);
	}

	@Test()
	public void testPositioUpdateSellAllWithLoss() {
		Position p = new Position(symbol, 100, 10.01, 1.0);
		double[] values = p.update(date, -100, 9.0, 1.0);
		assertEquals(0, p.getQuantity(), TOL);
		assertEquals(Double.NaN, p.getCostBasis());
		assertEquals(Double.NaN, p.getCommission());
		assertEquals(-100, values[0], TOL);
		assertEquals(10.01, values[1], TOL);
		assertEquals(8.99, values[2], TOL);
		assertEquals(-102.0, values[3], TOL);
		assertEquals(0.0, values[4], TOL);
	}

	@Test()
	public void testPositionUpdateSell() {
		Position p = new Position(symbol, 100, 10.01, 1.0);
		double[] values = p.update(date, -50, 20.0, 1.0);
		assertEquals(50, p.getQuantity(), TOL);
		assertEquals(10.01, p.getCostBasis(), TOL);
		assertEquals(1.0, p.getCommission());
		assertEquals(-50, values[0], TOL);
		assertEquals(10.01, values[1], TOL);
		assertEquals(19.98, values[2], TOL);
		assertEquals(498.5, values[3], TOL);
		assertEquals(499.5, values[4], TOL);
	}

	@Test()
	public void testPositionUpdateBuy() {
		Position p = new Position(symbol, 100, 10.01, 1.0);
		double[] values = p.update(date, 50, 20.0, 1.0);
		assertEquals(150, p.getQuantity(), TOL);
		assertEquals(13.34666666666, p.getCostBasis(), TOL);
		assertEquals(1.0, p.getCommission());
		assertEquals(0, values[0], TOL);
		assertEquals(13.34666666666, values[1], TOL);
		assertEquals(20.02, values[2], TOL);
		assertEquals(0.0, values[3], TOL);
		assertEquals(998.0, values[4], TOL);
	}

	@Test()
	public void testPositionUpdateBuySell() {
		Position p = new Position(symbol);
		double[] values = p.update(date, 100, 10.0, 1.0);
		assertEquals(0, values[0], TOL);
		assertEquals(10.01, values[1], TOL);
		assertEquals(-1.0, values[3], TOL);
		assertEquals(0.0, values[4], TOL);

		values = p.update(date.plusDays(1), 100, 20.0, 1.0);
		assertEquals(0, values[0], TOL);
		assertEquals(15.01, values[1], TOL);
		assertEquals(20.01, values[2], TOL);
		assertEquals(0.0, values[3], TOL);
		assertEquals(998.0, values[4], TOL);

		values = p.update(date.plusDays(2), -100, 30.0, 1.0);
		assertEquals(-100, values[0], TOL);
		assertEquals(15.01, values[1], TOL);
		assertEquals(29.99, values[2], TOL);
		assertEquals(1498.0, values[3], TOL);
		assertEquals(1499.0, values[4], TOL);

		values = p.update(date.plusDays(3), -100, 10.0, 1.0);
		assertEquals(-100, values[0], TOL);
		assertEquals(15.01, values[1], TOL);
		assertEquals(9.99, values[2], TOL);
		assertEquals(-502.0, values[3], TOL);
		assertEquals(0.0, values[4], TOL);
	}
}
