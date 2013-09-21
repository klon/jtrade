package jtrade.test;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;

public class TestUtil {

	public static <T> void assertContains(String[] expected, String actual) {
		for (int i = 0; i < expected.length; i++) {
			assertTrue("String '" + actual + "' does not contain '" + expected[i] + "'", actual.contains(expected[i]));
		}
	}

	public static <T> void assertEqualsArray(T[] expected, T... actual) {
		for (int i = 0; i < Math.min(expected.length, actual.length); ++i) {
			assertEquals("Array mismatch at index " + i + ":", expected[i], actual[i]);
		}
		assertEquals("Array length mismatch", expected.length, actual.length);
	}

	public static void assertEqualsArray(double[] expected, double... actual) {
		for (int i = 0; i < Math.min(expected.length, actual.length); ++i) {
			assertEquals("Array mismatch at index " + i + ":", expected[i], actual[i]);
		}
		assertEquals("Array length mismatch", expected.length, actual.length);
	}

	public static void assertEqualsArray(float[] expected, float... actual) {
		for (int i = 0; i < Math.min(expected.length, actual.length); ++i) {
			assertEquals("Array mismatch at index " + i + ":", expected[i], actual[i]);
		}
		assertEquals("Array length mismatch", expected.length, actual.length);
	}

	public static void assertEqualsArray(long[] expected, long... actual) {
		for (int i = 0; i < Math.min(expected.length, actual.length); ++i) {
			assertEquals("Array mismatch at index " + i + ":", expected[i], actual[i]);
		}
		assertEquals("Array length mismatch", expected.length, actual.length);
	}

	public static void assertEqualsArray(int[] expected, int... actual) {
		for (int i = 0; i < Math.min(expected.length, actual.length); ++i) {
			assertEquals("Array mismatch at index " + i + ":", expected[i], actual[i]);
		}
		assertEquals("Array length mismatch", expected.length, actual.length);
	}

	public static void assertEqualsArray(short[] expected, short... actual) {
		for (int i = 0; i < Math.min(expected.length, actual.length); ++i) {
			assertEquals("Array mismatch at index " + i + ":", expected[i], actual[i]);
		}
		assertEquals("Array length mismatch", expected.length, actual.length);
	}

	public static void assertEqualsArray(byte[] expected, byte... actual) {
		for (int i = 0; i < Math.min(expected.length, actual.length); ++i) {
			assertEquals("Array mismatch at index " + i + ":", expected[i], actual[i]);
		}
		assertEquals("Array length mismatch", expected.length, actual.length);
	}

	public static void assertEqualsArray(boolean[] expected, boolean... actual) {
		for (int i = 0; i < Math.min(expected.length, actual.length); ++i) {
			assertEquals("Array mismatch at index " + i + ":", expected[i], actual[i]);
		}
		assertEquals("Array length mismatch", expected.length, actual.length);
	}
}
