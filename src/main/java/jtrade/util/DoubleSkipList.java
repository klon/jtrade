package jtrade.util;

import java.util.Arrays;
import java.util.Iterator;

public class DoubleSkipList implements Iterable<Double> {
	private static final double LOG_2 = Math.log(2);

	int count;
	int maxLevels;
	Node head;

	public DoubleSkipList() {
		this(100);
	}

	public DoubleSkipList(int expectedSize) {
		maxLevels = (int) (1.0 + Math.log(expectedSize) / LOG_2);
		head = new Node(maxLevels, Double.NaN);
	}

	public int size() {
		return count;
	}

	public boolean isEmpty() {
		return count == 0;
	}

	public void clear() {
		head = new Node(maxLevels, Double.NaN);
		count = 0;
	}

	public double get(int index) {
		if (index >= count || index < 0) {
			throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + count);
		}
		Node node = head;
		index++;
		for (int l = maxLevels - 1; l >= 0; l--) {
			while (node.width[l] <= index) {
				index -= node.width[l];
				node = node.next[l];
			}
		}
		return node.value;
	}

	public void add(double value) {
		// find first node on each level where node.next[levels].value > value
		Node[] update = new Node[maxLevels];
		int[] stepsAtLevel = new int[maxLevels];
		Node node = head;
		for (int l = maxLevels - 1; l >= 0; l--) {
			while (node.next[l] != null && node.next[l].value <= value) {
				stepsAtLevel[l] += node.width[l];
				node = node.next[l];
			}
			update[l] = node;
		}
		// insert a link to the new node at each level
		int d = randomLevel();
		Node newNode = new Node(d, value);
		int steps = 0;
		for (int l = 0; l < d; l++) {
			Node prevNode = update[l];
			newNode.next[l] = prevNode.next[l];
			prevNode.next[l] = newNode;
			newNode.width[l] = prevNode.width[l] - steps;
			prevNode.width[l] = steps + 1;
			steps += stepsAtLevel[l];
		}
		for (int level = d; level < maxLevels; level++) {
			update[level].width[level] += 1;
		}
		count++;
	}

	public boolean contains(double value) {
		// find first node on each level where node.next[levels].value > value
		Node[] update = new Node[maxLevels];
		Node node = head;
		for (int l = maxLevels - 1; l >= 0; l--) {
			while (node.next[l] != null && node.next[l].value < value) {
				node = node.next[l];
			}
			update[l] = node;
		}
		return value == update[0].next[0].value;
	}

	public boolean remove(int index) {
		return remove(get(index));
	}

	public boolean remove(double value) {
		// find first node on each level where node.next[levels].value >= value
		Node[] update = new Node[maxLevels];
		Node node = head;
		for (int l = maxLevels - 1; l >= 0; l--) {
			while (node.next[l] != null && node.next[l].value < value) {
				node = node.next[l];
			}
			update[l] = node;
		}
		node = update[0].next[0];

		if (node.value != value) {
			return false;
		}
		int d = node.levels();
		for (int l = 0; l < d; l++) {
			Node prevNode = update[l];
			prevNode.width[l] += prevNode.next[l].width[l] - 1;
			prevNode.next[l] = prevNode.next[l].next[l];
		}
		for (int l = d; l < maxLevels; l++) {
			update[l].width[l]--;
		}
		count--;
		return true;
	}

	@Override
	public String toString() {
		return Arrays.toString(toArray());
	}

	public double[] toArray() {
		if (count == 0) {
			return new double[0];
		}
		double[] array = new double[count];
		int i = 0;
		Node next = head.next[0];
		while (next != null) {
			array[i++] = next.value;
			next = next.next[0];
		}
		return array;
	}

	@Override
	public Iterator<Double> iterator() {
		return new Iterator<Double>() {
			Node next = head.next[0];
			Node curr = null;

			@Override
			public boolean hasNext() {
				return next != null;
			}

			@Override
			public Double next() {
				curr = next;
				next = next.next[0];
				return curr.value;
			}

			@Override
			public void remove() {
				DoubleSkipList.this.remove(curr.value);
			}
		};
	}

	private int randomLevel() {
		return Math.min(maxLevels, 1 - (int) (Math.log(Math.random()) / LOG_2));
	}

	class Node {
		Node[] next;
		int[] width;
		double value;

		Node(int levels, double value) {
			next = new Node[levels];
			width = new int[levels];
			for (int i = 0; i < width.length; i++) {
				width[i] = 1;
			}
			this.value = value;
		}

		int levels() {
			return next.length;
		}
	}

	public static void main2(String[] args) {
		DoubleSkipList ss = new DoubleSkipList();
		while (true) {
			ss.add(Math.random());
			if (ss.size() > 100) {
				ss.remove(ss.size() / 2);
			}
		}
	}

	public static void main(String[] args) {
		DoubleSkipList ss = new DoubleSkipList();
		ss.add(5);
		ss.add(10);
		ss.add(7);
		ss.add(7);
		ss.add(6);

		System.out.println(ss.size());
		System.out.println(ss.get(ss.size() / 2));

		if (ss.contains(7)) {
			System.out.println("7 is in the list");
		}
		ss.remove(Double.NaN);
		System.out.println(ss);
		ss.remove(2);
		System.out.println(ss);
		ss.remove(7.0);
		System.out.println(ss);

		if (!ss.contains(7)) {
			System.out.println("7 has been deleted");
		}
		System.out.println(ss.size());
		System.out.println(ss.get(ss.size() / 2));

		ss.clear();
		System.out.println(ss);
	}
}
