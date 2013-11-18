package jtrade;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import jtrade.util.DateTimeRange;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.Months;
import org.joda.time.MutableDateTime;
import org.joda.time.ReadableDateTime;

public class SymbolFactory {
	public static List<Symbol> forex = new ArrayList<Symbol>();
	public static List<Symbol> futures = new ArrayList<Symbol>();
	public static Map<String, Symbol> symbols = new TreeMap<String, Symbol>();
	static {
		symbols.put("OMXS30-OMS-SEK-INDEX", new Symbol("OMXS30", "OMS", "SEK", "INDEX", 100, 0.25));
		symbols.put("SPX-CBOE-USD-INDEX", new Symbol("SPX-CBOE-USD-INDEX"));
		symbols.put("INDU-NYSE-USD-INDEX", new Symbol("INDU-NYSE-USD-INDEX"));
		symbols.put("COMP-NASDAQ-USD-INDEX", new Symbol("COMP-NASDAQ-USD-INDEX"));
		symbols.put("Z-LIFFE-GBP-INDEX", new Symbol("Z-LIFFE-GBP-INDEX"));
		symbols.put("DAX-DTB-EUR-INDEX", new Symbol("DAX-DTB-EUR-INDEX"));
		symbols.put("ESTX50-DTB-EUR-INDEX", new Symbol("ESTX50-DTB-EUR-INDEX"));
		symbols.put("CAC40-MONEP-EUR-INDEX", new Symbol("CAC40-MONEP-EUR-INDEX"));
		symbols.put("N225-OSE.JPN-JPY-INDEX", new Symbol("N225-OSE.JPN-JPY-INDEX"));
		symbols.put("VIX-CBOE-USD-INDEX", new Symbol("VIX-CBOE-USD-INDEX"));
		symbols.put("HSI-HKFE-HKD-INDEX", new Symbol("HSI-HKFE-HKD-INDEX"));

		futures.add(new Symbol("OMXS30", "OMS", "SEK", "FUTURE", 100, 0.25));
		futures.add(new Symbol("ES", "GLOBEX", "USD", "FUTURE", 50, 0.25));
		futures.add(new Symbol("Z", "LIFFE", "GBP", "FUTURE", 10, 0.5));
		futures.add(new Symbol("DAX", "DTB", "EUR", "FUTURE", 25, 0.5));
		futures.add(new Symbol("ESTX50", "DTB", "EUR", "FUTURE", 10, 1.0));
		futures.add(new Symbol("CAC40", "MONEP", "EUR", "FUTURE", 10, 0.5));
		futures.add(new Symbol("WTI", "IPE", "USD", "FUTURE", 1000, 0.01));
		futures.add(new Symbol("COIL", "IPE", "USD", "FUTURE", 100, 0.25));
		futures.add(new Symbol("GC", "NYMEX", "USD", "FUTURE", 100, 0.1));
		futures.add(new Symbol("CL", "NYMEX", "USD", "FUTURE", 1000, 0.01));
		for (Symbol f : futures) {
			symbols.put(f.getFullCode(), f);
		}
		for (Iterator<DateTime> dates = new DateTimeRange(new DateTime().withDayOfYear(1).minusYears(2), new DateTime().withDayOfYear(1).plusYears(2))
				.iterator(Months.ONE); dates.hasNext();) {
			DateTime dt = dates.next();
			for (Symbol f : futures) {
				Symbol s = getMostLiquidFutureSymbol(f, dt);
				symbols.put(s.getFullCode(), s);
			}
		}

		forex.add(new Symbol("EUR", "IDEALPRO", "USD", "CASH", 1, 0.0001));
		forex.add(new Symbol("USD", "IDEALPRO", "SEK", "CASH", 1, 0.0001));
		forex.add(new Symbol("USD", "IDEALPRO", "CHF", "CASH", 1, 0.0001));
		forex.add(new Symbol("USD", "IDEALPRO", "CAD", "CASH", 1, 0.0001));
		forex.add(new Symbol("USD", "IDEALPRO", "HKD", "CASH", 1, 0.0001));
		forex.add(new Symbol("USD", "IDEALPRO", "SGD", "CASH", 1, 0.0001));
		forex.add(new Symbol("USD", "IDEALPRO", "RUB", "CASH", 1, 0.0001));
		forex.add(new Symbol("USD", "IDEALPRO", "CNH", "CASH", 1, 0.0001));
		forex.add(new Symbol("USD", "IDEALPRO", "CZK", "CASH", 1, 0.0001));
		forex.add(new Symbol("USD", "IDEALPRO", "HUF", "CASH", 1, 0.0001));
		forex.add(new Symbol("USD", "IDEALPRO", "ILS", "CASH", 1, 0.0001));
		forex.add(new Symbol("USD", "IDEALPRO", "DKK", "CASH", 1, 0.0001));
		forex.add(new Symbol("USD", "IDEALPRO", "NOK", "CASH", 1, 0.0001));
		forex.add(new Symbol("USD", "IDEALPRO", "PLN", "CASH", 1, 0.0001));
		forex.add(new Symbol("USD", "IDEALPRO", "MXN", "CASH", 1, 0.0001));
		forex.add(new Symbol("USD", "IDEALPRO", "JPY", "CASH", 1, 0.0001));
		forex.add(new Symbol("EUR", "IDEALPRO", "USD", "CASH", 1, 0.0001));
		forex.add(new Symbol("EUR", "IDEALPRO", "GBP", "CASH", 1, 0.0001));
		forex.add(new Symbol("EUR", "IDEALPRO", "SEK", "CASH", 1, 0.0001));
		forex.add(new Symbol("EUR", "IDEALPRO", "CHF", "CASH", 1, 0.0001));
		forex.add(new Symbol("EUR", "IDEALPRO", "CAD", "CASH", 1, 0.0001));
		forex.add(new Symbol("EUR", "IDEALPRO", "AUD", "CASH", 1, 0.0001));
		forex.add(new Symbol("EUR", "IDEALPRO", "HKD", "CASH", 1, 0.0001));
		forex.add(new Symbol("EUR", "IDEALPRO", "SGD", "CASH", 1, 0.0001));
		forex.add(new Symbol("EUR", "IDEALPRO", "RUB", "CASH", 1, 0.0001));
		forex.add(new Symbol("EUR", "IDEALPRO", "CNH", "CASH", 1, 0.0001));
		forex.add(new Symbol("EUR", "IDEALPRO", "CZK", "CASH", 1, 0.0001));
		forex.add(new Symbol("EUR", "IDEALPRO", "HUF", "CASH", 1, 0.0001));
		forex.add(new Symbol("EUR", "IDEALPRO", "ILS", "CASH", 1, 0.0001));
		forex.add(new Symbol("EUR", "IDEALPRO", "DKK", "CASH", 1, 0.0001));
		forex.add(new Symbol("EUR", "IDEALPRO", "NOK", "CASH", 1, 0.0001));
		forex.add(new Symbol("EUR", "IDEALPRO", "PLN", "CASH", 1, 0.0001));
		forex.add(new Symbol("EUR", "IDEALPRO", "MXN", "CASH", 1, 0.0001));
		forex.add(new Symbol("EUR", "IDEALPRO", "JPY", "CASH", 1, 0.0001));
		forex.add(new Symbol("GBP", "IDEALPRO", "USD", "CASH", 1, 0.0001));
		forex.add(new Symbol("GBP", "IDEALPRO", "NZD", "CASH", 1, 0.0001));
		forex.add(new Symbol("GBP", "IDEALPRO", "SEK", "CASH", 1, 0.0001));
		forex.add(new Symbol("GBP", "IDEALPRO", "CHF", "CASH", 1, 0.0001));
		forex.add(new Symbol("GBP", "IDEALPRO", "CAD", "CASH", 1, 0.0001));
		forex.add(new Symbol("GBP", "IDEALPRO", "AUD", "CASH", 1, 0.0001));
		forex.add(new Symbol("GBP", "IDEALPRO", "HKD", "CASH", 1, 0.0001));
		forex.add(new Symbol("GBP", "IDEALPRO", "CNH", "CASH", 1, 0.0001));
		forex.add(new Symbol("GBP", "IDEALPRO", "DKK", "CASH", 1, 0.0001));
		forex.add(new Symbol("GBP", "IDEALPRO", "NOK", "CASH", 1, 0.0001));
		forex.add(new Symbol("GBP", "IDEALPRO", "MXN", "CASH", 1, 0.0001));
		forex.add(new Symbol("GBP", "IDEALPRO", "JPY", "CASH", 1, 0.0001));
		forex.add(new Symbol("CAD", "IDEALPRO", "CHF", "CASH", 1, 0.0001));
		forex.add(new Symbol("CAD", "IDEALPRO", "HKD", "CASH", 1, 0.0001));
		forex.add(new Symbol("CAD", "IDEALPRO", "JPY", "CASH", 1, 0.0001));
		forex.add(new Symbol("CAD", "IDEALPRO", "CNH", "CASH", 1, 0.0001));
		forex.add(new Symbol("AUD", "IDEALPRO", "USD", "CASH", 1, 0.0001));
		forex.add(new Symbol("AUD", "IDEALPRO", "CHF", "CASH", 1, 0.0001));
		forex.add(new Symbol("AUD", "IDEALPRO", "HKD", "CASH", 1, 0.0001));
		forex.add(new Symbol("AUD", "IDEALPRO", "JPY", "CASH", 1, 0.0001));
		forex.add(new Symbol("AUD", "IDEALPRO", "CAD", "CASH", 1, 0.0001));
		forex.add(new Symbol("AUD", "IDEALPRO", "CNH", "CASH", 1, 0.0001));
		forex.add(new Symbol("AUD", "IDEALPRO", "NZD", "CASH", 1, 0.0001));
		forex.add(new Symbol("AUD", "IDEALPRO", "SGD", "CASH", 1, 0.0001));
		forex.add(new Symbol("CHF", "IDEALPRO", "JPY", "CASH", 1, 0.0001));
		forex.add(new Symbol("CHF", "IDEALPRO", "CNH", "CASH", 1, 0.0001));
		forex.add(new Symbol("CHF", "IDEALPRO", "DKK", "CASH", 1, 0.0001));
		forex.add(new Symbol("CHF", "IDEALPRO", "NOK", "CASH", 1, 0.0001));
		forex.add(new Symbol("CHF", "IDEALPRO", "SEK", "CASH", 1, 0.0001));
		for (Symbol f : forex) {
			symbols.put(f.getFullCode(), f);
		}
	}

	public static List<Symbol> getFutures() {
		return new ArrayList<Symbol>(futures);
	}

	public static List<Symbol> getSymbols(List<String> symbols) {
		return getSymbols(symbols.toArray(new String[symbols.size()]));
	}

	public static List<Symbol> getSymbols(String... symbol) {
		List<Symbol> result = new ArrayList<Symbol>(symbol.length);
		for (String s : symbol) {
			result.add(getSymbol(s));
		}
		return result;
	}

	public static List<Symbol> getExchangeSymbols(String exchange) {
		List<Symbol> result = new ArrayList<Symbol>();
		for (Symbol s : symbols.values()) {
			if (s.getExchange().equals(exchange)) {
				result.add(s);
			}
		}
		return result;
	}

	public static Symbol getSymbol(String fullCode) {
		Symbol s = symbols.get(fullCode);
		if (s == null) {
			s = new Symbol(fullCode);
			symbols.put(fullCode, s);
		}
		return s;
	}

	public static Symbol getCash(String currency, String baseCurrency) {
		return getSymbol(new StringBuilder().append(currency).append("-IDEALPRO-").append(baseCurrency).append("-CASH").toString());
	}

	public static Symbol getSP500Index() {
		return symbols.get("SPX-CBOE-USD-INDEX");
	}

	public static Symbol getDowIndex() {
		return symbols.get("INDU-NYSE-USD-INDEX");
	}

	public static Symbol getNasdaqIndex() {
		return symbols.get("COMP-NASDAQ-USD-INDEX");
	}

	public static Symbol getNikkeiIndex() {
		return symbols.get("N225-OSE.JPN-JPY-INDEX");
	}

	public static Symbol getHangSengIndex() {
		return symbols.get("HSI-HKFE-HKD-INDEX");
	}

	public static Symbol getVixIndex() {
		return symbols.get("VIX-CBOE-USD-INDEX");
	}

	public static Symbol getDAXIndex() {
		return symbols.get("DAX-DTB-EUR-INDEX");
	}

	public static Symbol getESTX50Index() {
		return symbols.get("ESTX50-DTB-EUR-INDEX");
	}

	public static Symbol getFTSEIndex() {
		return symbols.get("Z-LIFFE-GBP-INDEX");
	}

	public static Symbol getOMXS30Index() {
		return symbols.get("OMXS30-OMS-SEK-INDEX");
	}

	public static Symbol getCAC40Index() {
		return symbols.get("CAC40-MONEP-EUR-INDEX");
	}

	public static Symbol getMostLiquidFutureSymbol(Symbol s, DateTime date) {
		if (!s.isFuture()) {
			return s;
		}
		if (s.getCode().equals("ES")) {
			return getESFutureSymbol(s, date);
		}
		if (s.getCode().equals("OMXS30")) {
			return getOMXFutureSymbol(s, date);
		}
		if (s.getCode().equals("DAX")) {
			return getDAXFutureSymbol(s, date);
		}
		if (s.getCode().equals("ESTX50")) {
			return getESTX50FutureSymbol(s, date);
		}
		if (s.getCode().equals("Z")) {
			return getZFutureSymbol(s, date);
		}
		if (s.getCode().equals("CAC40")) {
			return getCAC40FutureSymbol(s, date);
		}
		if (s.getCode().equals("VIX")) {
			return getVIXFutureSymbol(s, date);
		}
		if (s.getCode().equals("COIL")) {
			return getCOILFutureSymbol(s, date);
		}
		if (s.getCode().equals("WTI")) {
			return getWTIFutureSymbol(s, date);
		}
		if (s.getCode().equals("GC")) {
			return getGCFutureSymbol(s, date);
		}
		if (s.getCode().equals("CL")) {
			return getCLFutureSymbol(s, date);
		}
		if (s.getExchange().equals("OMX")) {
			return getOMXFutureSymbol(s, date);
		}
		if (s.getExchange().equals("ONE")) {
			return getONEFutureSymbol(s, date);
		}
		throw new IllegalArgumentException("Unsupported future: " + s.toString());
	}

	public static Symbol getOMXS30FutureSymbol(DateTime date) {
		return getOMXFutureSymbol(null, date);
	}

	public static Symbol getOMXS30FutureSymbol(Symbol s, DateTime date) {
		return getOMXFutureSymbol(s, date);
	}

	public static Symbol getOMXFutureSymbol(DateTime date) {
		return getOMXFutureSymbol(null, date);
	}

	public static Symbol getOMXFutureSymbol(Symbol s, DateTime date) {
		DateTime expiryDate = null;
		if (date.isBefore(1208383200000L)) { // 2008-04-17
			expiryDate = getMostLiquidMonthlyExpiry(date, 2, 4);
			if (expiryDate.equals(new DateTime(2007, 6, 22, 0, 0, 0, 0))) {
				expiryDate = expiryDate.minusDays(1);
			}
		} else if (date.isBefore(1293836400000L)) { // 2011-01-01
			expiryDate = getMostLiquidMonthlyExpiry(date, 2, 3);
			// Strange hack for these dates
			if (expiryDate.equals(new DateTime(2008, 6, 20, 0, 0, 0, 0))) {
				expiryDate = expiryDate.minusDays(1);
			} else if (expiryDate.equals(new DateTime(2009, 1, 16, 0, 0, 0, 0))) {
				expiryDate = expiryDate.plusDays(7);
			} else if (expiryDate.equals(new DateTime(2009, 6, 19, 0, 0, 0, 0))) {
				expiryDate = expiryDate.minusDays(1);
			} else if (expiryDate.equals(new DateTime(2010, 1, 15, 0, 0, 0, 0))) {
				expiryDate = expiryDate.plusDays(7);
			}
		} else if (date.isBefore(1371765600000L)) { // 2013-06-21
			expiryDate = getMostLiquidMonthlyExpiry(date, 2, 3);
			// Strange hack for these dates
			if (expiryDate.getMillis() == 1371765600000L) {
				expiryDate = expiryDate.minusDays(1);
			}
		} else {
			expiryDate = getMostLiquidMonthlyExpiry(date, 2, 3);
		}
		if (s != null && expiryDate.equals(s.getExpiry())) {
			return s;
		}
		// String code = "OMXS30" + (expiryDate.getYearOfCentury() % 10) +
		// (char)
		// (64 + expiryDate.getMonthOfYear());
		// return new Symbol(code, "OMS", "SEK", expiryDate);
		return new Symbol(s != null ? s.getCode() : "OMXS30", "OMS", "SEK", expiryDate, 100, 0.25);
	}

	public static Symbol getESFutureSymbol(DateTime date) {
		return getESFutureSymbol(null, date);
	}

	public static Symbol getESFutureSymbol(Symbol s, DateTime date) {
		DateTime expiryDate = getMostLiquidQuarterlyExpiry(date, 8);
		if (s != null && expiryDate.equals(s.getExpiry())) {
			return s;
		}
		return new Symbol("ES", "GLOBEX", "USD", expiryDate, 50, 0.25);
	}

	public static Symbol getDAXFutureSymbol(DateTime date) {
		return getDAXFutureSymbol(null, date);
	}

	public static Symbol getDAXFutureSymbol(Symbol s, DateTime date) {
		DateTime expiryDate = getMostLiquidQuarterlyExpiry(date, 8);
		if (s != null && expiryDate.equals(s.getExpiry())) {
			return s;
		}
		return new Symbol("DAX", "DTB", "EUR", getMostLiquidQuarterlyExpiry(date, 8), 25, 0.5);
	}

	public static Symbol getESTX50FutureSymbol(DateTime date) {
		return getESTX50FutureSymbol(null, date);
	}

	public static Symbol getESTX50FutureSymbol(Symbol s, DateTime date) {
		DateTime expiryDate = getMostLiquidQuarterlyExpiry(date, 8);
		if (s != null && expiryDate.equals(s.getExpiry())) {
			return s;
		}
		return new Symbol("ESTX50", "DTB", "EUR", getMostLiquidQuarterlyExpiry(date, 8), 10, 1.0);
	}

	public static Symbol getZFutureSymbol(DateTime date) {
		return getZFutureSymbol(null, date);
	}

	public static Symbol getZFutureSymbol(Symbol s, DateTime date) {
		DateTime expiryDate = getMostLiquidQuarterlyExpiry(date, 8);
		if (s != null && expiryDate.equals(s.getExpiry())) {
			return s;
		}
		return new Symbol("Z", "LIFFE", "GBP", expiryDate, 10, 0.5);
	}

	public static Symbol getCAC40FutureSymbol(DateTime date) {
		return getCAC40FutureSymbol(null, date);
	}

	public static Symbol getCAC40FutureSymbol(Symbol s, DateTime date) {
		DateTime expiryDate = getMostLiquidMonthlyExpiry(date, 7, 3);
		if (s != null && expiryDate.equals(s.getExpiry())) {
			return s;
		}
		return new Symbol("CAC40", "MONEP", "EUR", expiryDate, 10, 0.5);
	}

	public static Symbol getVIXFutureSymbol(DateTime date) {
		return getVIXFutureSymbol(null, date);
	}

	public static Symbol getVIXFutureSymbol(Symbol s, DateTime date) {
		DateTime expiryDate = getMostLiquidVIXMonthlyExpiry(date);
		if (s != null && expiryDate.equals(s.getExpiry())) {
			return s;
		}
		return new Symbol("VIX", "CFE", "USD", expiryDate, 1000, 0.05);
	}

	public static Symbol getCOILFutureSymbol(DateTime date) {
		return getCOILFutureSymbol(null, date);
	}

	public static Symbol getCOILFutureSymbol(Symbol s, DateTime date) {
		// Get 15th day preceding the first of the contract month
		MutableDateTime expiryDate = new MutableDateTime(date);
		expiryDate.setMillisOfDay(0);
		expiryDate.setMonthOfYear(expiryDate.getMonthOfYear() + (expiryDate.getMonthOfYear() < 12 ? 1 : 0));
		expiryDate.setDayOfMonth(1);
		expiryDate.addDays(-15);
		if (expiryDate.getDayOfWeek() == DateTimeConstants.SATURDAY) {
			expiryDate.addDays(-2);
		} else if (expiryDate.getDayOfWeek() == DateTimeConstants.SUNDAY) {
			expiryDate.addDays(-3);
		} else if (expiryDate.getDayOfWeek() == DateTimeConstants.MONDAY) {
			expiryDate.addDays(-3);
		} else {
			expiryDate.addDays(-1);
		}

		if (s != null && expiryDate.equals(s.getExpiry())) {
			return s;
		}
		return new Symbol("COIL", "IPE", "USD", expiryDate.toDateTime(), 1000, 0.01);
	}

	public static Symbol getWTIFutureSymbol(DateTime date) {
		return getWTIFutureSymbol(null, date);
	}

	public static Symbol getWTIFutureSymbol(Symbol s, DateTime date) {
		DateTime expiryDate = getMostLiquidMonthlyExpiry(date, 7, 3);
		if (s != null && expiryDate.equals(s.getExpiry())) {
			return s;
		}
		s = symbols.get(s.getFullCode());
		return new Symbol("WTI", "IPE", "USD", expiryDate, 1000, 0.01);
	}

	public static Symbol getGCFutureSymbol(DateTime date) {
		return getGCFutureSymbol(null, date);
	}

	public static Symbol getGCFutureSymbol(Symbol s, DateTime date) {
		// Get the third last business day of the delivery month.
		MutableDateTime expiryDate = new MutableDateTime(date.dayOfMonth().withMaximumValue());
		expiryDate.setMillisOfDay(0);
		int d = 3;
		while (d > 1) {
			if (isBusinessDay(expiryDate)) {
				d--;
			}
			expiryDate.addDays(-1);
		}
		if (s != null && expiryDate.equals(s.getExpiry())) {
			return s;
		}
		return new Symbol("GC", "NYMEX", "USD", expiryDate.toDateTime(), 100, 0.1);
	}

	public static Symbol getCLFutureSymbol(DateTime date) {
		return getCLFutureSymbol(null, date);
	}

	public static Symbol getCLFutureSymbol(Symbol s, DateTime date) {
		// Trading in the current delivery month shall cease on the third
		// business
		// day prior to the twenty-fifth calendar day of the
		// month preceding the delivery month.
		// If the twenty-fifth calendar day of the month is a non-business day,
		// trading shall cease on the third business day prior
		// to the last business day preceding the twenty-fifth calendar day.
		// In the event that the official Exchange holiday schedule changes
		// subsequent to the listing of a Crude Oil futures,
		// the originally listed expiration date shall remain in effect. In the
		// event that the originally listed expiration day is declared a
		// holiday,
		// expiration will move to the business day immediately prior.
		MutableDateTime expiryDate = new MutableDateTime(date);
		expiryDate.setMillisOfDay(0);
		if (expiryDate.getDayOfMonth() > 25) {
			expiryDate.addMonths(1);
		}
		expiryDate.setDayOfMonth(25);
		while (!isBusinessDay(expiryDate)) {
			expiryDate.addDays(-1);
		}
		int d = 3;
		while (d > 0) {
			if (isBusinessDay(expiryDate)) {
				d--;
			}
			expiryDate.addDays(-1);
		}
		if (s != null && expiryDate.equals(s.getExpiry())) {
			return s;
		}
		return new Symbol("CL", "NYMEX", "USD", expiryDate.toDateTime(), 1000, 0.01);
	}

	public static Symbol getONEFutureSymbol(DateTime date) {
		return getONEFutureSymbol(null, date);
	}

	public static Symbol getONEFutureSymbol(Symbol s, DateTime date) {
		DateTime expiryDate = getMostLiquidQuarterlyExpiry(date, 8);
		if (s != null && expiryDate.equals(s.getExpiry())) {
			return s;
		}
		return new Symbol(s.getCode(), "ONE", "USD", expiryDate, 100, 0.01);
	}

	public static DateTime getMostLiquidQuarterlyExpiry() {
		return getMostLiquidQuarterlyExpiry(new DateTime(), 8);
	}

	public static DateTime getMostLiquidQuarterlyExpiry(DateTime date, int daysBeforeExpiry) {
		DateTime expiryDate = getQuarterlyExpiryDate(date);
		// Volume shifts to next month about x days before expiry
		DateTime volumeShiftDate = expiryDate.minusDays(daysBeforeExpiry);

		if (date.isBefore(volumeShiftDate)) {
			return expiryDate;
		}
		return getQuarterlyExpiryDate(new DateTime(date.getMonthOfYear() == DateTimeConstants.DECEMBER ? date.getYear() + 1 : date.getYear(),
				(date.getMonthOfYear() + 1) % 12, 1, 0, 0, 0, 0));
	}

	public static DateTime getMostLiquidOMXMonthlyExpiry(DateTime date) {
		if (date == null) {
			date = new DateTime();
		} else if (date.isBefore(new DateTime(2008, 4, 17, 0, 0, 0, 0))) {
			return getMostLiquidMonthlyExpiry(date, 8, 4);
		}
		return getMostLiquidMonthlyExpiry(date, 8, 3);
	}

	public static DateTime getMostLiquidVIXMonthlyExpiry(DateTime date) {
		if (date == null) {
			date = new DateTime();
		}
		DateTime expiryDate = getNthFriday(date.plusMonths(1), 3).minusDays(31);
		DateTime volumeShiftDate = expiryDate.minusDays(2);

		if (date.isBefore(volumeShiftDate)) {
			return expiryDate;
		}
		return getNthFriday(date.plusMonths(2), 3).minusDays(31);
	}
	
	public static DateTime getMostLiquidMonthlyExpiry() {
		return getMostLiquidMonthlyExpiry(new DateTime(), 7, 3);
	}

	public static DateTime getMostLiquidMonthlyExpiry(DateTime date, int daysBeforeExpiry, int nthFriday) {
		DateTime expiryDate = getNthFriday(date, nthFriday);
		// Volume shifts to next month about x days before expiry
		DateTime volumeShiftDate = expiryDate.minusDays(daysBeforeExpiry);

		if (date.isBefore(volumeShiftDate)) {
			return expiryDate;
		}
		return getNthFriday(date.plusMonths(1), nthFriday);
	}

	public static DateTime getQuarterlyExpiryDate(DateTime date) {
		// Get third Friday
		MutableDateTime expiryDate = new MutableDateTime(date);
		expiryDate.setMillisOfDay(0);
		switch (expiryDate.getMonthOfYear()) {
		case DateTimeConstants.JANUARY:
		case DateTimeConstants.FEBRUARY:
		case DateTimeConstants.MARCH:
			expiryDate.setMonthOfYear(DateTimeConstants.MARCH);
			break;
		case DateTimeConstants.APRIL:
		case DateTimeConstants.MAY:
		case DateTimeConstants.JUNE:
			expiryDate.setMonthOfYear(DateTimeConstants.JUNE);
			break;
		case DateTimeConstants.JULY:
		case DateTimeConstants.AUGUST:
		case DateTimeConstants.SEPTEMBER:
			expiryDate.setMonthOfYear(DateTimeConstants.SEPTEMBER);
			break;
		case DateTimeConstants.OCTOBER:
		case DateTimeConstants.NOVEMBER:
		case DateTimeConstants.DECEMBER:
			expiryDate.setMonthOfYear(DateTimeConstants.DECEMBER);
			break;
		}
		expiryDate.setDayOfMonth(1);
		if (expiryDate.getDayOfWeek() > DateTimeConstants.FRIDAY) {
			expiryDate.addWeeks(3);
			expiryDate.setDayOfWeek(DateTimeConstants.FRIDAY);
		} else {
			expiryDate.addWeeks(2);
			expiryDate.setDayOfWeek(DateTimeConstants.FRIDAY);
		}
		return expiryDate.toDateTime();
	}

	private static DateTime getNthFriday(DateTime date, int nthFriday) {
		MutableDateTime expiryDate = new MutableDateTime(date);
		expiryDate.setMillisOfDay(0);
		expiryDate.setDayOfMonth(1);
		if (expiryDate.getDayOfWeek() > DateTimeConstants.FRIDAY) {
			expiryDate.addWeeks(nthFriday);
			expiryDate.setDayOfWeek(DateTimeConstants.FRIDAY);
		} else {
			expiryDate.addWeeks(nthFriday - 1);
			expiryDate.setDayOfWeek(DateTimeConstants.FRIDAY);
		}
		return expiryDate.toDateTime();
	}

	private static boolean isBusinessDay(ReadableDateTime date) {
		int dayOfWeek = date.getDayOfWeek();
		if (dayOfWeek == DateTimeConstants.SATURDAY || dayOfWeek == DateTimeConstants.SUNDAY) {
			return false;
		}
		return true;
	}
}
