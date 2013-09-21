package jtrade.test.unit;

import static org.testng.AssertJUnit.assertEquals;

import java.io.File;
import java.io.IOException;

import jtrade.io.BarReader;
import jtrade.io.MarketDataIO;
import jtrade.io.TickReader;
import jtrade.marketfeed.Bar;
import jtrade.marketfeed.Tick;
import jtrade.test.TestDurationWrapper;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

@Listeners({ TestDurationWrapper.class })
public class TestMarketDataIO {

	@BeforeClass
	public void setUp() {

	}

	@Test()
	public void testAsciiTickPerformance() throws IOException {
		TickReader reader = null;
		try {
			reader = MarketDataIO.createTickReader(new File("./test/fixture/ES-GLOBEX-USD-FUTURE-20130315-201301-TICK.txt.gz"));
			Tick t = null;
			long n = 0;
			double sum = 0.0;
			while ((t = reader.readTick()) != null) {
				n++;
				sum += t.getPrice();
			}
			assertEquals(3246078, n);
			assertEquals(4.782247945E9, sum);
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
	}

	@Test()
	public void testBinaryTickPerformance() throws IOException {
		TickReader reader = null;
		try {
			reader = MarketDataIO.createTickReader(new File("./test/fixture/ES-GLOBEX-USD-FUTURE-20130315-201301-TICK.bin.gz"));
			Tick t = null;
			long n = 0;
			double sum = 0.0;
			while ((t = reader.readTick()) != null) {
				n++;
				sum += t.getPrice();
			}
			assertEquals(3246078, n);
			assertEquals(4.782247945E9, sum);
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
	}

	@Test()
	public void testAsciiBarPerformance() throws IOException {
		BarReader reader = null;
		try {
			reader = MarketDataIO.createBarReader(new File("./test/fixture/ES-GLOBEX-USD-FUTURE-20130315-201301-60.txt.gz"));
			Bar b = null;
			long n = 0;
			double sum = 0.0;
			while ((b = reader.readBar()) != null) {
				n++;
				sum += b.getClose();
			}
			assertEquals(29310, n);
			assertEquals(4.31913905E7, sum);
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
	}

	@Test()
	public void testBinaryBarPerformance() throws IOException {
		BarReader reader = null;
		try {
			reader = MarketDataIO.createBarReader(new File("./test/fixture/ES-GLOBEX-USD-FUTURE-20130315-201301-60.bin.gz"));
			Bar b = null;
			long n = 0;
			double sum = 0.0;
			while ((b = reader.readBar()) != null) {
				n++;
				sum += b.getClose();
			}
			assertEquals(29310, n);
			assertEquals(4.31913905E7, sum);
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
	}

}
