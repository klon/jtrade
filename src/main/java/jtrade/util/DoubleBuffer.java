package jtrade.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * A simple circular buffer for doubles with some fancy stat methods
 * 
 * @author jonkle
 * 
 */
public class DoubleBuffer implements Iterable<Double> {
	private double[] items;
	private int takeIndex; // front pointer
	private int putIndex; // rear pointer
	private int count;

	public DoubleBuffer(int capacity) {
		items = new double[capacity];
	}

	public DoubleBuffer(double[] data) {
		items = data.clone();
		count = data.length;
	}

	/**
	 * Circularly increment i.
	 */
	private int inc(int i) {
		return (++i == items.length) ? 0 : i;
	}

	/**
	 * Adds a value to the end of the buffer. Fails if the buffer is full.
	 * 
	 * @param value
	 * @return True if successful
	 */
	public boolean add(double value) {
		if (count >= items.length) {
			throw new IllegalStateException("Buffer is full");
		}
		items[putIndex] = value;
		putIndex = inc(putIndex);
		count++;
		return true;
	}

	/**
	 * Tries to add a value to the end of the buffer.
	 * 
	 * @param value
	 * @return True if successful, false if the buffer is full.
	 */
	public boolean offer(double value) {
		if (count == items.length) {
			return false;
		}
		items[putIndex] = value;
		putIndex = inc(putIndex);
		count++;
		return true;
	}

	/**
	 * Adds a value to the end of the buffer, if it is full the first value is
	 * dropped silently.
	 * 
	 * @param value
	 * @return True if the first value was dropped, false if not.
	 */
	public boolean push(double value) {
		boolean removed = false;
		if (count >= items.length) {
			remove();
			removed = true;
		}
		add(value);
		return removed;
	}

	/**
	 * Sets the head value in the buffer
	 * 
	 * @param value
	 */
	public void setHead(double value) {
		set(count - 1, value);
	}

	/**
	 * Sets the tail value in the buffer
	 * 
	 * @param value
	 */
	public void setTail(double value) {
		set(0, value);
	}

	/**
	 * Sets a value in the buffer
	 * 
	 * @param i
	 * @param value
	 */
	public void set(int i, double value) {
		items[(takeIndex + i) % items.length] = value;
	}

	/**
	 * Removes the first value from the buffer.
	 * 
	 * @return
	 */
	public double remove() {
		if (count == 0) {
			throw new IllegalStateException("Buffer is empty");
		}
		double o = items[takeIndex];
		takeIndex = inc(takeIndex);
		count--;
		return o;
	}

	/**
	 * Remove value at specified index.
	 * 
	 * @param i
	 * @return The value that was removed or NaN if no value was removed.
	 */
	public double remove(int i) {
		double o = Double.NaN;
		// if removing front item, just advance
		if (i == takeIndex) {
			o = items[takeIndex];
			takeIndex = inc(takeIndex);
		} else {
			// slide over all others up through putIndex.
			while (true) {
				int nexti = inc(i);
				if (nexti != putIndex) {
					items[i] = items[nexti];
					i = nexti;
				} else {
					o = items[i];
					putIndex = i;
					break;
				}
			}
		}
		count--;
		return o;
	}

	/**
	 * Returns the value at specified index, or throws exception if the index is
	 * invalid.
	 * 
	 * @param i
	 * @return The value at i
	 */
	public double get(int i) {
		if (i >= count || i < 0) {
			throw new IndexOutOfBoundsException("Index: " + i + ", Size: " + count);
		}
		return items[(takeIndex + i) % items.length];
	}

	/**
	 * Returns the value at specified index, or NaN if the index is invalid.
	 * 
	 * @param i
	 * @return The value at i
	 */
	public double peekAt(int i) {
		if (i >= count || i < 0) {
			return Double.NaN;
		}
		return items[(takeIndex + i) % items.length];
	}

	/**
	 * Returns the last (newest) value, or NaN if the buffer is empty.
	 * 
	 * @return Last value
	 */
	public double last() {
		return peekAt(count - 1);
	}

	/**
	 * Returns the first (oldest) value, or NaN if the buffer is empty.
	 * 
	 * @return First value
	 */
	public double first() {
		return peekAt(0);
	}

	/**
	 * Returns the current size of the buffer.
	 * 
	 * @return Size of buffer.
	 */
	public int size() {
		return count;
	}

	/**
	 * Returns the capacity of the buffer.
	 * 
	 * @return Capacity of buffer.
	 */
	public int capacity() {
		return items.length;
	}

	/**
	 * Returns true if the buffer is not empty.
	 * 
	 * @return True if the buffer is not empty.
	 */
	public boolean isEmpty() {
		return count == 0;
	}

	/**
	 * Returns true if the buffer is full.
	 * 
	 * @return True if the buffer is full.
	 */
	public boolean isFull() {
		return count == items.length;
	}

	/**
	 * Returns a copy of the buffer as a double array.
	 * 
	 * @return Double array of the buffer
	 */
	public double[] toArray() {
		double[] array = new double[count];
		int k = 0;
		int i = takeIndex;
		while (k < count) {
			array[k++] = items[i];
			i = inc(i);
		}
		return array;
	}

	/**
	 * Returns a copy of the buffer as a double array.
	 * 
	 * @return Double array of the buffer
	 */
	public double[] toArray(int start, int end) {
		int len = end - start;
		if (start < 0) {
			throw new IndexOutOfBoundsException("Start: " + start);
		}
		if (len > count) {
			throw new IndexOutOfBoundsException("End: " + end + ", Size: " + count);
		}
		double[] array = new double[len];
		int k = 0;
		int i = (takeIndex + start) % items.length;
		while (k < len) {
			array[k++] = items[i];
			i = inc(i);
		}
		return array;
	}

	/**
	 * Returns a string representation of the buffer.
	 */
	@Override
	public String toString() {
		if (count == 0) {
			return "[]";
		}
		StringBuilder sb = new StringBuilder();
		sb.append('[');
		int k = 0;
		int i = takeIndex;
		while (k++ < count) {
			sb.append(items[i]);
			if (k < count) {
				sb.append(", ");
			}
			i = inc(i);
		}
		return sb.append(']').toString();
	}

	/**
	 * Checks if a value exist in the buffer.
	 * 
	 * @param o
	 * @return True if value o exist in the buffer.
	 */
	public boolean contains(double o) {
		int k = 0;
		int i = takeIndex;
		while (k++ < count) {
			if (o == items[i] || (Double.isNaN(o) && Double.isNaN(items[i]))) {
				return true;
			}
			i = inc(i);
		}
		return false;
	}

	/**
	 * Returns an iterator of the buffer.
	 */
	@Override
	public Iterator<Double> iterator() {
		return new Iterator<Double>() {
			int k = 0;
			int i = takeIndex;
			int j = -1;
			int c = count;

			@Override
			public boolean hasNext() {
				return k < c;
			}

			@Override
			public Double next() {
				if (k >= c) {
					throw new NoSuchElementException();
				}
				double d = items[i];
				k++;
				j = i;
				i = inc(i);
				return d;
			}

			@Override
			public void remove() {
				if (j < 0) {
					throw new IllegalStateException();
				}
				DoubleBuffer.this.remove(j);
				i = DoubleBuffer.this.inc(j);
			}
		};
	}

	/**
	 * Returns a copy of the buffer as an array.
	 * 
	 * @param a
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T> T[] toArray(T[] a) {
		if (a.length < count) {
			a = (T[]) java.lang.reflect.Array.newInstance(a.getClass().getComponentType(), count);
		}
		int k = 0;
		int i = takeIndex;
		while (k < count) {
			a[k++] = (T) Double.valueOf(items[i]);
			i = inc(i);
		}
		return a;
	}

	/**
	 * Removes a value from the buffer if it exists.
	 * 
	 * @param d
	 * @return True if a value was removed.
	 */
	public boolean remove(double d) {
		int i = takeIndex;
		int k = 0;
		while (true) {
			if (k++ >= count) {
				return false;
			}
			if (d == items[i] || (Double.isNaN(d) && Double.isNaN(items[i]))) {
				remove(i);
				return true;
			}
			i = inc(i);
		}

	}

	/**
	 * Checks if all values exists in the buffer.
	 * 
	 * @param col
	 * @return True if all values exist.
	 */
	public boolean containsAll(Collection<? extends Number> col) {
		for (Iterator<? extends Number> e = col.iterator(); e.hasNext();) {
			if (!contains(e.next().doubleValue())) {
				return false;
			}
		}
		return true;
	}

	public boolean addAll(Collection<? extends Number> col) {
		boolean modified = false;
		for (Iterator<? extends Number> e = col.iterator(); e.hasNext();) {
			if (add(e.next().doubleValue()))
				modified = true;
		}
		return modified;
	}

	public boolean removeAll(Collection<? extends Number> c) {
		boolean modified = false;
		for (Iterator<? extends Number> e = c.iterator(); e.hasNext();) {
			if (c.contains(e.next().doubleValue())) {
				e.remove();
				modified = true;
			}
		}
		return modified;
	}

	public boolean retainAll(Collection<? extends Number> col) {
		boolean modified = false;
		for (Iterator<? extends Number> e = iterator(); e.hasNext();) {
			if (!col.contains(e.next().doubleValue())) {
				e.remove();
				modified = true;
			}
		}
		return modified;
	}

	/**
	 * Clears the buffer of all values, resets size to 0.
	 */
	public void clear() {
		count = 0;
		putIndex = 0;
		takeIndex = 0;
	}

	/**
	 * Returns the sum of all values in the buffer.
	 * 
	 * @return Sum of all values
	 */
	public double sum() {
		if (count <= 1) {
			return peekAt(0);
		}
		double sum = 0.0;
		int k = 0;
		int i = takeIndex;
		while (k++ < count) {
			sum += items[i];
			i = inc(i);
		}
		return sum;
	}

	/**
	 * Returns the minimum value in the buffer.
	 * 
	 * @return Minimum value in the buffer.
	 */
	public double min() {
		if (count <= 1) {
			return peekAt(0);
		}
		double min = Double.POSITIVE_INFINITY;
		int k = 0;
		int i = takeIndex;
		while (k++ < count) {
			min = Math.min(items[i], min);
			i = inc(i);
		}
		return min;
	}

	/**
	 * Returns the maximum value in the buffer.
	 * 
	 * @return Maximum value in the buffer.
	 */
	public double max() {
		if (count <= 1) {
			return peekAt(0);
		}
		double max = Double.NEGATIVE_INFINITY;
		int k = 0;
		int i = takeIndex;
		while (k++ < count) {
			max = Math.max(items[i], max);
			i = inc(i);
		}
		return max;
	}

	/**
	 * Returns the mean of all values in the buffer.
	 * 
	 * @return Mean of all values.
	 */
	public double mean() {
		return sum() / count;
	}

	/**
	 * Returns the median of all values in the buffer.
	 * 
	 * @return Median of all values.
	 */
	public double median2() {
		if (count <= 1) {
			return peekAt(0);
		}
		double[] d = toArray();
		Arrays.sort(d);
		if ((d.length & 1) == 0) {
			int i = d.length / 2;
			return (d[i - 1] + d[i]) / 2;
		}
		return d[d.length / 2];
	}

	/**
	 * Returns the median of all values in the buffer.
	 * 
	 * @return Median of all values.
	 */
	public double median() {
		if (count <= 1) {
			return peekAt(0);
		}
		double[] d = toArray();
		int k = d.length / 2 + 1;
		Util.quickSelect(d, k);
		if ((d.length & 1) == 0) { // even
			return (d[k - 1] + d[k - 2]) / 2;
		}
		return d[k - 1];
	}

	/**
	 * Returns the variance of all values in the buffer.
	 * 
	 * @return Variance of all values.
	 */
	public double var() {
		if (count == 0) {
			return Double.NaN;
		}
		if (count == 1) {
			return 0.0;
		}
		double mean = mean();
		double sum = 0;
		int k = 0;
		int i = takeIndex;
		while (k++ < count) {
			double v = items[i] - mean;
			sum += v * v;
			i = inc(i);
		}
		return sum / (count - 1);
	}

	/**
	 * Returns the standard deviation of all values in the buffer.
	 * 
	 * @return Standard deviation of all values.
	 */
	public double std() {
		return Math.sqrt(var());
	}

	/**
	 * Linear regression of y = ax + b Returns coefficients to the regression line
	 * "y=ax+b" from xs[] and ys[], and r2 Value
	 * 
	 * @param xs
	 * @param ys
	 * @return double array with coefficients to the regression line "y=ax+b" and
	 *         r2 value
	 */
	public double[] lr() {
		double sumX = 0.0, sumY = 0.0, sumXX = 0.0, sumXY = 0.0;
		for (int x = 0, i = takeIndex; x < count; x++) {
			double y = items[i];
			sumX += x;
			sumY += y;
			sumXX += x * x;
			sumXY += x * y;
			i = inc(i);
		}
		double det = sumXX * count - sumX * sumX;
		double a = (sumXY * count - sumY * sumX) / det;
		double b = (sumXX * sumY - sumX * sumXY) / det;
		double meanY = sumY / count;
		double meanError = 0.0, residual = 0.0;
		for (int x = 0, i = takeIndex; x < count; x++) {
			double y = items[i];
			meanError += Math.pow(y - meanY, 2);
			residual += Math.pow(y - a * x - b, 2);
			i = inc(i);
		}
		double r2 = 1 - residual / meanError;
		return new double[] { a, b, r2 };
	}

	/**
	 * Returns the rise over run of the first and the last value in the buffer.
	 * 
	 * @return Rise over run of buffer.
	 */
	public double slope() {
		if (count <= 1) {
			return Double.NaN;
		}
		return (last() - first()) / (count - 1);
	}

	/**
	 * Returns the log return of the first and the last value in the buffer.
	 * 
	 * @return Rise over run of buffer.
	 */
	public double logReturn() {
		if (count <= 1) {
			return Double.NaN;
		}
		return Math.log(last() / first());
	}

	/**
	 * Returns the log return of the first and the last value in the buffer.
	 * 
	 * @return Rise over run of buffer.
	 */
	public double arithReturn() {
		if (count <= 1) {
			return Double.NaN;
		}
		double last = last();
		return (last - first()) / last;
	}

	/**
	 * Returns the percent rank of the specified value compared to all values in
	 * the buffer.
	 * 
	 * @return Percent rank of value.
	 */
	public double percentRank(double value) {
		final int denom = count - 1;
		double[] sorted = toArray();
		Arrays.sort(sorted);
		if (value < sorted[0] || value > sorted[denom]) {
			return Double.NaN;
		}
		int index = Arrays.binarySearch(sorted, value);
		if (index < 0) {
			int r = (-index) - 2;
			double v0 = sorted[r];
			double v1 = sorted[r + 1];
			return (r + (value - v0) / (v1 - v0)) / denom;
		}
		return ((double) index) / denom;
	}

	public double percentRank() {
		return percentRank(last());
	}

}
