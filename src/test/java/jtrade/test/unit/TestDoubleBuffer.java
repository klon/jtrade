package jtrade.test.unit;

import static jtrade.test.TestUtil.assertContains;
import static jtrade.test.TestUtil.assertEqualsArray;
import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.fail;

import java.util.Arrays;
import java.util.Iterator;

import jtrade.test.TestDurationWrapper;
import jtrade.util.DoubleBuffer;

import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

@Listeners({ TestDurationWrapper.class })
public class TestDoubleBuffer {

	@Test()
	public void testDoubleBuffer() {
		DoubleBuffer buf = new DoubleBuffer(2);
		assertEquals(2, buf.capacity());
		assertEquals(0, buf.size());
		assertEquals(true, buf.isEmpty());
		assertEquals(false, buf.push(1.0));
		assertEquals(false, buf.push(4.0));
		assertEquals(true, buf.push(2.0));
		assertEquals(false, buf.offer(3.0));
		assertEquals(4.0, buf.remove());
		assertEquals(true, buf.offer(3.0));
		assertEquals(true, buf.isFull());
		assertEquals(2, buf.size());
		assertEquals(2.0, buf.get(0));
		assertEquals(3.0, buf.get(1));
		assertEquals(2.0, buf.first());
		assertEquals(3.0, buf.last());
		assertEquals(false, buf.contains(1.0));
		assertEquals(true, buf.contains(2.0));
		assertEquals(true, buf.contains(3.0));
		assertEquals(true, buf.containsAll(Arrays.asList(2.0, 3.0)));
		assertEqualsArray(new double[] { 2.0, 3.0 }, buf.toArray());
		assertEqualsArray(new double[] { 3.0 }, buf.toArray(1, 2));
		assertEqualsArray(new Double[] { 2.0, 3.0 }, buf.toArray(new Double[2]));
		assertContains(new String[] { "2.0", "3.0" }, buf.toString());

		double expected = 2.0;
		for (double d : buf) {
			assertEquals(d, expected);
			expected += 1.0;
		}

		try {
			buf.add(10.0);
			fail("add() should throw IllegalStateException");
		} catch (IllegalStateException e) {
			assertEquals(buf.size(), 2);
		}

		try {
			buf.get(3);
			fail("get() should throw IndexOutOfBoundsException");
		} catch (IndexOutOfBoundsException e) {
		}
		assertEquals(3.0, buf.remove(1));
		assertEquals(1, buf.size());

		buf.clear();
		assertEquals(0, buf.size());
		assertEquals(true, buf.isEmpty());
	}

	@Test()
	public void testDoubleBufferIterator() {
		DoubleBuffer buf = new DoubleBuffer(new double[] { 1.0, 2.0, 3.0 });
		assertEquals(3, buf.size());
		assertEquals(true, buf.isFull());

		Iterator<Double> i = buf.iterator();
		assertEquals(Arrays.equals(buf.toArray(), new double[] { 1.0, 2.0, 3.0 }), true);
		assertEquals(true, i.hasNext());
		assertEquals(1.0, i.next().doubleValue());
		i.remove();

		assertEquals(Arrays.equals(buf.toArray(), new double[] { 2.0, 3.0 }), true);
		assertEquals(true, i.hasNext());
		assertEquals(2.0, i.next().doubleValue());
		i.remove();

		assertEquals(Arrays.equals(buf.toArray(), new double[] { 3.0 }), true);
		assertEquals(true, i.hasNext());
		assertEquals(3.0, i.next().doubleValue());
		i.remove();

		assertEquals(0, buf.size());
		assertEquals(true, buf.isEmpty());
	}

	@Test()
	public void testDoubleBufferStats() {
		DoubleBuffer buf = new DoubleBuffer(new double[] { 2.0, 4.0, 6.0 });
		assertEquals(6.0, buf.max());
		assertEquals(2.0, buf.min());
		assertEquals(12.0, buf.sum());
		assertEquals(4.0, buf.mean());
		assertEquals(4.0, buf.median());
		assertEquals(2.0, buf.std());
		assertEquals(4.0, buf.var());
		assertEquals(2.0, buf.slope());
		assertEquals(0.25, buf.percentRank(3.0));
		assertEquals(0.666666666667, buf.arithReturn(), 1.0E-9);
		assertEquals(1.098612288668, buf.logReturn(), 1.0E-9);
		assertEquals(Arrays.equals(new double[] { 2.0, 2.0, 1.0 }, buf.lr()), true);
	}

}
