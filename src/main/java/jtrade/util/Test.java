package jtrade.util;

import java.util.Random;

import org.joda.time.DateTime;

public class Test {

	public static void main(String[] args) {
	  System.out.println(new DateTime(2013, 10, 28, 12, 23, 12).dayOfMonth().roundCeilingCopy());
	}
	
	public static void main2(String[] args) {
		long start = System.currentTimeMillis();
		double[] data = gaussian(10000000, System.nanoTime());
		DoubleBuffer buf = new DoubleBuffer(data);
		System.out.println(buf.min());
		System.out.println(buf.max());
		System.out.println(buf.mean());
		System.out.println(buf.std());
		System.out.println(System.currentTimeMillis() - start);
	}

	
	public static double[] gaussian(int len, long seed) {
		Random rnd = new Random(seed);
		double[] data = new double[len];
		for (int i = 1; i < len; i++) {
			data[i] = rnd.nextGaussian();
		}
		return data;
	}
}
