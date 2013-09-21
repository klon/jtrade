package jtrade.util;

import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.Duration;
import org.joda.time.LocalTime;
import org.joda.time.Period;
import org.joda.time.format.DateTimeFormat;

public class Util {
	private static final int INSERTION_SORT_CUTOFF = 10;

	public static double select(double[] data, int k) {
		double[] a = data.clone();
		quickSelect(a, k);
		return a[k - 1];
	}

	public static void quickSelect(double[] a, int k) {
		quickSelect(a, 0, a.length - 1, k);
	}

	private static void quickSelect(double[] a, int low, int high, int k) {
		if (low + INSERTION_SORT_CUTOFF > high) {
			insertionSort(a, low, high);
			return;
		}
		// Sort low, middle, high
		int middle = (low + high) / 2;
		if (a[middle] < a[low]) {
			swap(a, low, middle);
		}
		if (a[high] < a[low]) {
			swap(a, low, high);
		}
		if (a[high] < a[middle]) {
			swap(a, middle, high);
		}

		// Place pivot at position high - 1
		swap(a, middle, high - 1);
		double pivot = a[high - 1];

		// Begin partitioning
		int i, j;
		for (i = low, j = high - 1;;) {
			while (a[++i] < pivot)
				;
			while (pivot < (a[--j]))
				;
			if (i >= j) {
				break;
			}
			swap(a, i, j);
		}

		// Restore pivot
		swap(a, i, high - 1);

		// Recurse; only this part changes
		if (k <= i) {
			quickSelect(a, low, i - 1, k);
		} else if (k > i + 1) {
			quickSelect(a, i + 1, high, k);
		}
	}

	public static void insertionSort(double[] a, int low, int high) {
		for (int p = low + 1; p <= high; p++) {
			double tmp = a[p];
			int j;
			for (j = p; j > low && tmp < (a[j - 1]); j--) {
				a[j] = a[j - 1];
			}
			a[j] = tmp;
		}
	}

	private static <T> void swap(double[] a, int i, int j) {
		double tmp = a[i];
		a[i] = a[j];
		a[j] = tmp;
	}

	public static <T extends Comparable<T>> T select(T[] data, int k) {
		T[] a = data.clone();
		quickSelect(a, k);
		return a[k - 1];
	}

	/**
	 * Quick selection algorithm. Places the kth smallest item in a[k-1].
	 * 
	 * @param a
	 *          an array of Comparable items.
	 * @param k
	 *          the desired rank (1 is minimum) in the entire array.
	 */
	public static <T extends Comparable<T>> void quickSelect(T[] a, int k) {
		quickSelect(a, 0, a.length - 1, k);
	}

	/**
	 * Internal selection method that makes recursive calls. Uses median-of-three
	 * partitioning and a cutoff of 10. Places the kth smallest item in a[k-1].
	 * 
	 * @param a
	 *          an array of Comparable items.
	 * @param low
	 *          the left-most index of the subarray.
	 * @param high
	 *          the right-most index of the subarray.
	 * @param k
	 *          the desired rank (1 is minimum) in the entire array.
	 */
	private static <T extends Comparable<T>> void quickSelect(T[] a, int low, int high, int k) {
		if (low + INSERTION_SORT_CUTOFF > high) {
			insertionSort(a, low, high);
			return;
		}
		// Sort low, middle, high
		int middle = (low + high) / 2;
		if (a[middle].compareTo(a[low]) < 0) {
			swap(a, low, middle);
		}
		if (a[high].compareTo(a[low]) < 0) {
			swap(a, low, high);
		}
		if (a[high].compareTo(a[middle]) < 0) {
			swap(a, middle, high);
		}

		// Place pivot at position high - 1
		swap(a, middle, high - 1);
		T pivot = a[high - 1];

		// Begin partitioning
		int i, j;
		for (i = low, j = high - 1;;) {
			while (a[++i].compareTo(pivot) < 0)
				;
			while (pivot.compareTo(a[--j]) < 0)
				;
			if (i >= j) {
				break;
			}
			swap(a, i, j);
		}

		// Restore pivot
		swap(a, i, high - 1);

		// Recurse; only this part changes
		if (k <= i) {
			quickSelect(a, low, i - 1, k);
		} else if (k > i + 1) {
			quickSelect(a, i + 1, high, k);
		}
	}

	/**
	 * Internal insertion sort routine for subarrays that is used by quicksort.
	 * 
	 * @param a
	 *          an array of Comparable items.
	 * @param low
	 *          the left-most index of the subarray.
	 * @param n
	 *          the number of items to sort.
	 */
	private static <T extends Comparable<T>> void insertionSort(T[] a, int low, int high) {
		for (int p = low + 1; p <= high; p++) {
			T tmp = a[p];
			int j;
			for (j = p; j > low && tmp.compareTo(a[j - 1]) < 0; j--) {
				a[j] = a[j - 1];
			}
			a[j] = tmp;
		}
	}

	private static <T> void swap(T[] a, int i, int j) {
		T tmp = a[i];
		a[i] = a[j];
		a[j] = tmp;
	}
	
	
	public static Object callMethod(Object obj, String method, Object... args) {
		try {
			Class<?>[] classes = new Class<?>[args.length];
			for (int i = 0; i < args.length; i++) {
				classes[i] = args[i].getClass();
			}
			Method m = obj.getClass().getDeclaredMethod(method, classes);
			m.setAccessible(true);
			return m.invoke(obj, args);
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		}
	}

	public static Field getField(Class<?> cls, String name) {
		Field[] fields = cls.getFields();
		for (int i = 0; i < fields.length; i++) {
			if (fields[i].getName().equals(name)) {
				return fields[i];
			}
		}
		if (cls.getSuperclass() != null) {
			Field f = getField(cls.getSuperclass(), name);
			if (f != null) {
				return f;
			}
		}
		return null;
	}

	public static Collection<Field> getFields(Class<?> cls) {
		Collection<Field> result = new LinkedHashSet<Field>();
		if (cls.getSuperclass() != null) {
			result.addAll(getFields(cls.getSuperclass()));
		}
		Field[] fields = cls.getFields();
		for (int i = 0; i < fields.length; i++) {
			result.add(fields[i]);
		}
		return result;
	}

	public static Object coerceType(Object obj, Class<?> type) {
		if (obj == null) {
			return null;
		}
		if (type == null) {
			return obj;
		}
		if (type.isAssignableFrom(obj.getClass())) {
			return obj;
		}
		if (type.equals(String.class)) {
			return obj.toString();
		} else if (type.equals(Boolean.class)) {
			return Boolean.valueOf(obj.toString());
		} else if (type.equals(Integer.class)) {
			if (obj instanceof Number) {
				return Integer.valueOf(((Number) obj).intValue());
			}
			return Integer.valueOf(obj.toString());
		} else if (type.equals(Long.class)) {
			if (obj instanceof Number) {
				return Long.valueOf(((Number) obj).longValue());
			}
			return Long.valueOf(obj.toString());
		} else if (type.equals(Float.class)) {
			if (obj instanceof Number) {
				return Float.valueOf(((Number) obj).floatValue());
			}
			return Float.valueOf(obj.toString().replace(',', '.'));
		} else if (type.equals(Double.class)) {
			if (obj instanceof Number) {
				return Double.valueOf(((Number) obj).doubleValue());
			}
			return Double.valueOf(obj.toString().replace(',', '.'));
		} else if (type.equals(Byte.class)) {
			return Byte.valueOf(obj.toString());
		} else if (type.equals(Short.class)) {
			return Short.valueOf(obj.toString());
		} else if (type.equals(BigInteger.class)) {
			return new BigInteger(obj.toString());
		} else if (type.equals(BigDecimal.class)) {
			return new BigDecimal(obj.toString().replace(',', '.'));
		} else if (type.equals(LocalTime.class)) {
			return DateTimeFormat.forPattern("HH:mm:ss").parseLocalTime(obj.toString());
		} else if (type.equals(DateTime.class)) {
			return DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss").parseDateTime(obj.toString());
		} else if (type.equals(File.class)) {
			return new File(obj.toString());
		} else if (type.equals(Class.class)) {
			try {
				return Class.forName(obj.toString());
			} catch (ClassNotFoundException e) {
				throw new IllegalArgumentException(e);
			}
		} else {
			try {
				return type.getConstructor(String.class).newInstance(obj.toString());
			} catch (Exception e) {
			} 
		}
		throw new IllegalArgumentException("Cannot coerce '" + obj + "' to '" + type + "'");
	}

	public static void copyProperties(Object source, Object dest) {
		if (source instanceof Map) {
			for (Iterator<?> iter = ((Map<?, ?>) source).entrySet().iterator(); iter.hasNext();) {
				Map.Entry<?, ?> entry = (Map.Entry<?, ?>) iter.next();
				setProperty(dest, (String) entry.getKey(), entry.getValue());
			}
			return;
		}
		PropertyDescriptor[] descriptors = getPropertyDescriptors(source.getClass());
		for (int i = 0; i < descriptors.length; i++) {
			PropertyDescriptor d = descriptors[i];
			Object value;
			try {
				value = d.getReadMethod().invoke(source);
			} catch (Exception e) {
				throw new IllegalArgumentException("Cannot get property '" + d.getName() + "' from object '" + source + "'", e);
			}
			setProperty(dest, d.getName(), value);
		}
	}

	@SuppressWarnings("unchecked")
	public static void setProperty(Object object, String property, Object value) {
		if (object instanceof Map) {
			((Map<String, Object>) object).put(property, value);
			return;
		}
		PropertyDescriptor descriptor = getPropertyDescriptor(object.getClass(), property);
		if (descriptor == null || descriptor.getWriteMethod() == null) {
			return;
		}
		try {
			descriptor.getWriteMethod().invoke(object, coerceType(value, descriptor.getPropertyType()));
		} catch (Exception e) {
			throw new IllegalArgumentException("Cannot set property '" + property + "' from object '" + object + "'", e);
		}

	}

	public static Object getProperty(Object object, String property) {
		if (object instanceof Map) {
			return ((Map<?, ?>) object).get(property);
		}
		PropertyDescriptor descriptor = getPropertyDescriptor(object.getClass(), property);
		if (descriptor == null || descriptor.getReadMethod() == null) {
			return null;
		}
		try {
			return descriptor.getReadMethod().invoke(object);
		} catch (Exception e) {
			throw new IllegalArgumentException("Cannot get property '" + property + "' from object '" + object + "'", e);
		}
	}

	public static double getDoubleProperty(Object object, String property) {
		Object val = getProperty(object, property);
		if (val == null) {
			return Double.NaN;
		}
		return ((Number) val).doubleValue();
	}

	public static Map<String, Object> getProperties(Object object) {
		Map<String, Object> properties = new TreeMap<String, Object>();
		if (object instanceof Map) {
			for (Map.Entry<?, ?> entry : ((Map<?, ?>) object).entrySet()) {
				properties.put(entry.getKey() != null ? entry.getKey().toString() : "null", entry.getValue());
			}
			return properties;
		}
		Class<?> objectClass = null;
		if (object instanceof Class) {
			objectClass = (Class<?>) object;
		} else {
			objectClass = object.getClass();
		}
		PropertyDescriptor[] descriptors = getPropertyDescriptors(objectClass);
		for (int i = 0; i < descriptors.length; i++) {
			try {
				properties.put(descriptors[i].getName(), descriptors[i].getReadMethod().invoke(object));
			} catch (Exception e) {
				throw new IllegalArgumentException("Cannot get property '" + descriptors[i].getName() + "' from object '" + object + "'", e);
			}
		}
		return properties;
	}

	public static PropertyDescriptor[] getPropertyDescriptors(Class<?> cls) {
		try {
			return Introspector.getBeanInfo(cls).getPropertyDescriptors();
		} catch (Exception e) {
			throw new IllegalArgumentException("Cannot get descriptors from class '" + cls + "'", e);
		}
	}

	public static PropertyDescriptor getPropertyDescriptor(Class<?> entityClass, String property) {
		PropertyDescriptor[] descriptors = getPropertyDescriptors(entityClass);
		for (int i = 0; i < descriptors.length; i++) {
			PropertyDescriptor d = descriptors[i];
			if (d.getName().equals(property)) {
				return d;
			}
		}
		return null;
	}

	public static boolean isInt(String str) {
		try {
			Integer.parseInt(str);
			return true;
		} catch (NumberFormatException nfe) {
			return false;
		}
	}

	public static boolean isLong(String str) {
		try {
			Long.parseLong(str);
			return true;
		} catch (NumberFormatException nfe) {
			return false;
		}
	}
	
	public static boolean isDouble(String str) {
		try {
			Double.parseDouble(str);
			return true;
		} catch (NumberFormatException nfe) {
			return false;
		}
	}
	
	public static String join(String[] strings, String delimiter) {
		return join(strings, delimiter, 0, strings.length);
	}

	public static String join(String[] strings, String delimiter, int start, int end) {
		int len = end - start;
		if (len <= 0) {
			return "";
		}
		if (len == 1) {
			return strings[0];
		}
		StringBuilder buf = new StringBuilder();
		int j = end - 1;
		for (int i = start; i < end; i++) {
			buf.append(strings[i]);
			if (i < j) {
				buf.append(delimiter);
			}
		}
		return buf.toString();
	}
	
	public static String[] split(String str, char delimiter) {
		return split(str, delimiter, false);
	}

	public static String[] split(String str, char delimiter, boolean trim) {
		int delimIdx = 0, count = 1, pos = 0, i = 0;
		int len = str.length();
		do {
			delimIdx = str.indexOf(delimiter, pos);
			if (delimIdx == -1) {
				break;
			}
			count++;
			pos = delimIdx + 1;
		} while (pos < len);
		pos = 0;
		String[] strings = new String[count];
		if (trim) {
			do {
				delimIdx = str.indexOf(delimiter, pos);
				if (delimIdx == -1) {
					strings[i] = str.substring(pos).trim();
					break;
				}
				strings[i++] = (delimIdx - pos != 0 ? str.substring(pos, delimIdx).trim() : "");
				pos = delimIdx + 1;
			} while (pos <= len);
		} else {
			do {
				delimIdx = str.indexOf(delimiter, pos);
				if (delimIdx == -1) {
					strings[i] = str.substring(pos);
					break;
				}
				strings[i++] = (delimIdx - pos != 0 ? str.substring(pos, delimIdx) : "");
				pos = delimIdx + 1;
			} while (pos <= len);
		}
		return strings;
	}

	public static String[] split(String str, String delimiter) {
		return split(str, delimiter, false);
	}

	public static String[] split(String str, String delimiter, boolean trim) {
		int delimIdx = 0, count = 1, pos = 0, i = 0;
		int len = str.length();
		int delimLen = delimiter.length();
		do {
			delimIdx = str.indexOf(delimiter, pos);
			if (delimIdx == -1) {
				break;
			}
			count++;
			pos = delimIdx + delimLen;
		} while (pos < len);
		pos = 0;
		String[] strings = new String[count];
		if (trim) {
			do {
				delimIdx = str.indexOf(delimiter, pos);
				if (delimIdx == -1) {
					strings[i] = str.substring(pos).trim();
					break;
				}
				strings[i++] = (delimIdx - pos != 0 ? str.substring(pos, delimIdx).trim() : "");
				pos = delimIdx + delimLen;
			} while (pos <= len);
		} else {
			do {
				delimIdx = str.indexOf(delimiter, pos);
				if (delimIdx == -1) {
					strings[i] = str.substring(pos);
					break;
				}
				strings[i++] = (delimIdx - pos != 0 ? str.substring(pos, delimIdx) : "");
				pos = delimIdx + delimLen;
			} while (pos <= len);
		}
		return strings;
	}

	public static String[] split(String str, char delimiter, String[] strings) {
		int delimIdx = 0, pos = 0, i = 0;
		int len = str.length();
		do {
			delimIdx = str.indexOf(delimiter, pos);
			if (delimIdx == -1) {
				strings[i] = str.substring(pos);
				break;
			}
			strings[i++] = (delimIdx - pos != 0 ? str.substring(pos, delimIdx) : "");
			pos = delimIdx + 1;
		} while (pos <= len);

		return strings;
	}

	public static String toString(Object obj) {
		Class<?> cls = obj.getClass();
		if (cls.isArray()) {
			int len = Array.getLength(obj);
			if (len == 0) {
				return "[]";
			}
			StringBuilder sb = new StringBuilder();
			sb.append('[');
			int j = len - 1;
			for (int i = 0; i < len; i++) {
				Object o = Array.get(obj, i);
				sb.append(o != null ? o.toString() : "null");
				if (i < j) {
					sb.append(", ");
				}
			}
			return sb.append(']').toString();
		}		
		StringBuilder buf = new StringBuilder();
		buf.append(cls.getSimpleName());
		buf.append(" [");
		try {
			List<Class<?>> classes = new ArrayList<Class<?>>();
			while (cls != null) {
				classes.add(cls);
				cls = cls.getSuperclass();
			}
			Collection<Field> fields = new LinkedHashSet<Field>();
			for (int i = classes.size() - 1; i >= 0; i--) {
				Field[] fs = classes.get(i).getDeclaredFields();
				for (int j = 0; j < fs.length; j++) {
					Field f = fs[j];
					if (f.isSynthetic() || Modifier.isStatic(f.getModifiers())) {
						continue;
					}
					fields.add(f);
				}
			}
			int k = 0;
			for (Field f : fields) {
				if (k > 0) {
					buf.append(", ");
				}
				f.setAccessible(true);
				buf.append(f.getName());
				buf.append("=");
				Object v = f.get(obj);
				if (f.getType().isArray()) {
					int len = Array.getLength(v);
					if (len == 0) {
						buf.append("[]");
					} else if (len > 100) {
						buf.append("[...]");
					} else {
						buf.append('[');
						buf.append(Array.get(v, 0));
						for (int i = 1; i < len; i++) {
							buf.append(", ");
							buf.append(Array.get(v, i));
						}
						buf.append(']');
					}
				} else {
					buf.append(v);
				}
				f.setAccessible(false);
				k++;
			}
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		}
		buf.append("]");
		return buf.toString();
	}
	
	public static <T extends Comparable<T>> T max(T[] data) {
		final int len = data.length;
		if (len == 0) {
			return null;
		}
		T max = data[0];
		for (int i = 1; i < len; i++) {
			T v = data[i];
			if (v != null && v.compareTo(max) > 0) {
				max = v;
			}
		}
		return max;
	}

	public static <T extends Comparable<T>> T min(T[] data) {
		final int len = data.length;
		if (len == 0) {
			return null;
		}
		T min = data[0];
		for (int i = 1; i < len; i++) {
			T v = data[i];
			if (v != null && v.compareTo(min) < 0) {
				min = v;
			}
		}
		return min;
	}

	public static DateTime dateTimeFloor(DateTime dt, Duration millis) {
		return new DateTime(dt.getMillis() - (dt.getMillis() % millis.getMillis()));
	}

	public static DateTime dateTimeFloor(DateTime dt, Period p) {
		if (p.getYears() != 0) {
			return dt.yearOfEra().roundFloorCopy().minusYears(dt.getYearOfEra() % p.getYears());
		} else if (p.getMonths() != 0) {
			return dt.monthOfYear().roundFloorCopy().minusMonths((dt.getMonthOfYear() - 1) % p.getMonths());
		} else if (p.getWeeks() != 0) {
			return dt.weekOfWeekyear().roundFloorCopy().minusWeeks((dt.getWeekOfWeekyear() - 1) % p.getWeeks());
		} else if (p.getDays() != 0) {
			return dt.dayOfMonth().roundFloorCopy().minusDays((dt.getDayOfMonth() - 1) % p.getDays());
		} else if (p.getHours() != 0) {
			return dt.hourOfDay().roundFloorCopy().minusHours(dt.getHourOfDay() % p.getHours());
		} else if (p.getMinutes() != 0) {
			return dt.minuteOfHour().roundFloorCopy().minusMinutes(dt.getMinuteOfHour() % p.getMinutes());
		} else if (p.getSeconds() != 0) {
			return dt.secondOfMinute().roundFloorCopy().minusSeconds(dt.getSecondOfMinute() % p.getSeconds());
		}
		return dt.millisOfSecond().roundCeilingCopy().minusMillis(dt.getMillisOfSecond() % p.getMillis());
	}
	
	public static boolean isWorkingDay(DateTime dt) {
		return dt.getDayOfWeek() <= 5;
	}
	
	public static boolean isDate(DateTime dt1, DateTime dt2) {
		return dt1.getDayOfYear() == dt2.getDayOfYear() && dt1.getYear() == dt2.getYear();
	}

	public static boolean isTime(DateTime dt, LocalTime t) {
		return isTime(dt, t, 10000);
	}

	public static boolean isTime(DateTime dt, LocalTime t, long toleranceMillis) {
		return Math.abs(dt.getMillisOfDay() - t.getMillisOfDay()) <= toleranceMillis;
	}
	
	public static boolean isTime(DateTime dt1, DateTime dt2) {
		return isTime(dt1, dt2, 10000);
	}

	public static boolean isTime(DateTime dt1, DateTime dt2, long toleranceMillis) {
		return Math.abs(dt1.getMillisOfDay() - dt2.getMillisOfDay()) <= toleranceMillis;
	}

	public static boolean isTimeExactly(DateTime dt, LocalTime t) {
		return dt.getMillisOfDay() == t.getMillisOfDay();
	}
	
	public static boolean isTimeExactly(DateTime dt1, DateTime dt2) {
		return dt1.getMillisOfDay() == dt2.getMillisOfDay();
	}

	public static boolean isBetween(DateTime dt, LocalTime t1, LocalTime t2) {
		int md = dt.getMillisOfDay();
		return dt.getMillisOfDay() >= t1.getMillisOfDay() && md < t2.getMillisOfDay();
	}
	
	public static boolean isAfter(DateTime dt, LocalTime t) {
		return dt.getMillisOfDay() > t.getMillisOfDay();
	}
	
	public static boolean isAfterOrEqual(DateTime dt, LocalTime t) {
		return dt.getMillisOfDay() >= t.getMillisOfDay();
	}
	
	public static boolean isBefore(DateTime dt, LocalTime t) {
		return dt.getMillisOfDay() < t.getMillisOfDay();
	}
	
	public static boolean isBeforeOrEqual(DateTime dt, LocalTime t) {
		return dt.getMillisOfDay() <= t.getMillisOfDay();
	}

	public static boolean isWeekDay(DateTime dt) {
		return dt.getDayOfWeek() < DateTimeConstants.SATURDAY;
	}
	
	public static LocalTime toLocalTime(String time) {
		return DateTimeFormat.forPattern("HH:mm:ss").parseDateTime(time).toLocalTime();
	}

	public static DateTime toDate(String date) {
		return DateTimeFormat.forPattern("yyyyMMdd").parseDateTime(date);
	}

	public static DateTime toDateTime(String dateTime) {
		return DateTimeFormat.forPattern("yyyyMMdd HH:mm:ss").parseDateTime(dateTime);
	}

	public static double round(double d, int decimals) {
		if (d != d) {
			return Double.NaN;
		}
		double n = Math.pow(10, decimals);
		return Math.round(d * n) / n;
	}

	public static double round(double d, double fraction) {
		if (d != d) {
			return Double.NaN;
		}
		return Math.round(d * fraction) / fraction;
	}

	public static double roundSig(double d, int n) {
		if (d != d) {
			return Double.NaN;
		}
		if (d == 0) {
			return 0;
		}
		int power = n - (int) Math.ceil(Math.log10(d < 0 ? -d : d));
		double magnitude = Math.pow(10, power);
		return Math.round(d * magnitude) / magnitude;
	}
}
