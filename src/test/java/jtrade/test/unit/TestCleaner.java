package jtrade.test.unit;

import static org.testng.AssertJUnit.assertEquals;

import java.util.Random;

import jtrade.marketfeed.Cleaner;
import jtrade.marketfeed.MedianCleaner;
import jtrade.test.TestDurationWrapper;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

@Listeners({ TestDurationWrapper.class })
public class TestCleaner {
	
	
	public static double[] randomWalk(int len, double initial, double drift, double volatility, long seed) {
		Random rnd = new Random(seed);
		double[] data = new double[len];
		data[0] = initial;
		for (int i = 1; i < len; i++) {
			data[i] = data[i - 1] + data[i - 1] * drift + (rnd.nextGaussian() * volatility);
		}
		return data;
	}

	@BeforeClass
	public void setUp() {

	}

	@Test()
	public void testMedianCleaner() {
		double[] values = randomWalk(1000, 100.0, 0.0001, 1.0, 1);
		Cleaner cleaner = new MedianCleaner(480, 6.0);
		values[500] *= 0.95;
		values[700] *= 0.95;
		for (int i = 0; i < values.length; i++) {
			double value = cleaner.update(i * 1000 * 60, values[i]);
			//System.out.println(values[i] + " = " + value);
			values[i] = value;
		}
		assertEquals(Double.NaN, values[500]);
		assertEquals(Double.NaN, values[700]);
	}
}
