package jtrade.util;

import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Hours;
import org.joda.time.LocalTime;
import org.joda.time.MutableDateTime;
import org.joda.time.ReadablePeriod;

public class DateTimeRange implements Iterable<DateTime> {
	DateTime start;
	DateTime end;

	public DateTimeRange(DateTime start, DateTime end) {
		this.start = start;
		this.end = end;
	}

	public DateTime getStart() {
		return start;
	}

	public DateTime getEnd() {
		return end;
	}

	public int getDays() {
		return Days.daysBetween(start, end).getDays();
	}
	
	public int getWorkingDays() {
		MutableDateTime date = start.toMutableDateTime();
		int count = 0;
		while (date.isBefore(end)) {
			if (date.getDayOfWeek() <= 5) {
				count++;
			}
			date.addDays(1);
		}
		return count;
	}

	@Override
	public Iterator<DateTime> iterator() {
		return new DateTimeIterator(start, end, Days.ONE);
	}

	public Iterator<DateTime> iterator(ReadablePeriod period) {
		return new DateTimeIterator(start, end, period);
	}

	public Iterator<DateTime> iterator(ReadablePeriod period, LocalTime startTime, LocalTime endTime, int... days) {
		return new DateTimeExceptionsIterator(start, end, period, startTime, endTime, days);
	}

	public Iterator<DateTime> dayIterator() {
		return dayIterator(new int[] { 1, 2, 3, 4, 5, 6, 7 });
	}

	public Iterator<DateTime> workingDayIterator() {
		return dayIterator(new int[] { 1, 2, 3, 4, 5 });
	}

	public Iterator<DateTime> dayIterator(int... days) {
		return new DateTimeExceptionsIterator(start, end, Days.ONE, null, null, days);
	}

	static class DateTimeExceptionsIterator implements Iterator<DateTime> {
		private DateTime current;
		private DateTime end;
		private ReadablePeriod period;
		private long startTime;
		private long endTime;
		private int[] days;

		private DateTimeExceptionsIterator(DateTime start, DateTime end, ReadablePeriod period, LocalTime startTime, LocalTime endTime, int... days) {
			this.end = end;
			this.period = period;
			this.startTime = startTime != null ? startTime.getMillisOfDay() : -1;
			this.endTime = endTime != null ? endTime.getMillisOfDay() : -1;
			this.days = days;
			this.current = nextValid(start.minus(period));
		}

		private DateTime nextValid(DateTime dt) {
			while (true) {
				dt = dt.plus(period);
				if (!dt.isAfter(end)) {
					long millisOfDay = dt.getMillisOfDay();
					if (Arrays.binarySearch(days, dt.getDayOfWeek()) >= 0 && (startTime < 0 || (millisOfDay >= startTime && millisOfDay <= endTime))) {
						break;
					}
				} else {
					dt = null;
					break;
				}
			}
			return dt;
		}

		@Override
		public boolean hasNext() {
			return current != null;
		}

		@Override
		public DateTime next() {
			if (current == null) {
				throw new NoSuchElementException();
			}
			DateTime ret = current;
			current = nextValid(current);
			return ret;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}

	static class DateTimeIterator implements Iterator<DateTime> {
		private DateTime current;
		private DateTime end;
		private ReadablePeriod period;

		private DateTimeIterator(DateTime start, DateTime end, ReadablePeriod period) {
			this.current = start;
			this.end = end;
			this.period = period;
		}

		@Override
		public boolean hasNext() {
			return current != null;
		}

		@Override
		public DateTime next() {
			if (current == null) {
				throw new NoSuchElementException();
			}
			DateTime ret = current;
			current = current.plus(period);
			if (current.isAfter(end)) {
				current = null;
			}
			return ret;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}

	public static void main(String args[]) {
		DateTime start = new DateTime(2009, 7, 20, 0, 0, 0, 0);
		DateTime end = new DateTime(2009, 8, 3, 0, 0, 0, 0);
		for (Iterator<DateTime> dates = new DateTimeRange(start, end).iterator(Hours.ONE, new LocalTime(9, 0, 0, 0), new LocalTime(17, 0, 0, 0), 6, 7); dates
				.hasNext();) {
			System.out.println(dates.next());
		}
	}
}
