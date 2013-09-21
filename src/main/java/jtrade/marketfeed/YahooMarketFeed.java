package jtrade.marketfeed;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import jtrade.JTradeException;
import jtrade.Symbol;
import jtrade.util.Util;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class YahooMarketFeed extends AbstractMarketFeed implements MarketFeed {
	private static final int POLL_INITIAL_DELAY_SECONDS = 1;
	private static final double POLL_INTERVAL_FUZZINESS = 0.2;

	private static final String YAHOO_BASE_URL = "http://download.finance.yahoo.com/d/quotes.csv?s=%s&f=%s";
	private static final String ASK = "a";
	private static final String ASK_SIZE = "a5";
	private static final String BID = "b";
	private static final String BID_SIZE = "b6";
	private static final String LOW = "g";
	private static final String HIGH = "h";
	private static final String LAST_PRICE = "l1";
	private static final String LAST_TRADE_SIZE = "k3";
	private static final String OPEN = "o";
	private static final String PREV_CLOSE = "p";
	private static final String VOLUME = "v";
	private static final String LAST_TRADE_DATE = "d1";
	private static final String LAST_TRADE_TIME = "t1";

	private static final String YAHOO_HISTORICAL_URL = "http://chartapi.finance.yahoo.com/instrument/1.0/%s/chartdata;type=quote;ys=%s;yz=%s/csv/";

//	private static final String HISTORICAL_OPEN = "Open";
//	private static final String HISTORICAL_CLOSE = "Close";
//	private static final String HISTORICAL_PREV_CLOSE = "Adj Close";
//	private static final String HISTORICAL_HIGH = "High";
//	private static final String HISTORICAL_LOW = "Low";
//	private static final String HISTORICAL_VOLUME = "Volume";
//	private static final String HISTORICAL_DATE = "Date";

	private static final DateTimeFormatter dateFormatter = DateTimeFormat.forPattern("\"MM/dd/yyyy\"\"hh:mma\"");

	private static final Logger logger = LoggerFactory.getLogger(YahooMarketFeed.class);

	private static String translateToYahoo(Symbol symbol) {
		if (symbol.isIndex() && symbol.getCode().equals("OMXS30")) {
			return "^OMX";
		}
		if (symbol.isIndex() && symbol.getCode().equals("SPX")) {
			return "^GSPC";
		}
		if (symbol.isIndex() && symbol.getCode().equals("COMP")) {
			return "^IXIC";
		}
		if (symbol.isIndex() && symbol.getCode().equals("INDU")) {
			return "^DJI";
		}
		if (symbol.isIndex() && symbol.getCode().equals("Z")) {
			return "^FTSE";
		}
		if (symbol.isIndex() && symbol.getCode().equals("DAX")) {
			return "^GDAXI";
		}
		if (symbol.isIndex()) {
			return "^".concat(symbol.getCode());
		}
		if (symbol.isCash()) {
			return symbol.getCode().concat(symbol.getCurrency()).concat("=X");
		}
		if (symbol.isStock() && symbol.getCode().equals("HUS")) {
			return "HUSQ-B.ST";
		}
		if (symbol.isStock() && symbol.getCode().equals("HUSQA")) {
			return "HUSQ-A.ST";
		}
		if (symbol.isStock() && symbol.getCode().equals("LUMI")) {
			return "LUMI-SDB.ST";
		}
		if (symbol.isStock() && symbol.getCode().equals("NDA")) {
			return "NDA-SEK.ST";
		}
		if (symbol.isStock() && symbol.getCode().equals("SWEDA")) {
			return "SWED-A.ST";
		}
		if (symbol.isStock() && symbol.getCode().equals("SWEDPREF")) {
			return "SWED-PREF.ST";
		}
		if (symbol.isStock() && symbol.getCode().equals("TTEB")) {
			return "TIEN.ST";
		}
		if (symbol.isStock() && symbol.getCode().equals("WSIB")) {
			return "AOIL-SDB.ST";
		}
		if (symbol.isStock() && symbol.getExchange().equals("SFB")) {
			return symbol.getCode().replace('.', '-').concat(".ST");
		}
		if (symbol.isStock() && symbol.getExchange().equals("ARCA")) {
			return symbol.getCode().replace('.', '-');
		}
		throw new IllegalArgumentException("Untranslated symbol: " + symbol);
	}

	private ScheduledThreadPoolExecutor executor;
	private boolean connected;
	private Map<Symbol, YahooTickPoller> pollersBySymbol;
	private Map<Symbol, Bar> lastBarBySymbol;

	public YahooMarketFeed() {
		this(new File("~/marketdata/yahoo"));
	}

	public YahooMarketFeed(File dataDir) {
		super(dataDir);
		this.pollersBySymbol = new HashMap<Symbol, YahooTickPoller>();
		this.lastBarBySymbol = new HashMap<Symbol, Bar>();
	}

	public void connect() {
		connected = true;
	}

	public void disconnect() {
		connected = false;
	}

	public boolean isConnected() {
		return connected;
	}

	public void removeAllListeners() {
		for (YahooTickPoller t : pollersBySymbol.values()) {
			t.listeners.clear();
		}
	}
	
	private String urlEncode(String s) {
		try {
			return URLEncoder.encode(s, "UTF-8");
		} catch (UnsupportedEncodingException e) {
		}
		return s;
	}

	private String makeHttpGet(String url) {
		HttpURLConnection connection = null;
		try {
			connection = (HttpURLConnection) new URL(url).openConnection();
			connection.setRequestMethod("GET");
			connection.setRequestProperty("Accept-Charset", "utf-8");
			connection.setRequestProperty("User-Agent",	"Mozilla/5.0 (Windows NT 6.2; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/32.0.1667.0 Safari/537.36");

			int status = connection.getResponseCode();
			logger.debug("GET {} {}", url, status);
			
			InputStream is = connection.getInputStream();
			String contentType = connection.getHeaderField("Content-Type");
			String charset = "utf-8";
			for (String param : contentType.replace(" ", "").split(";")) {
				if (param.startsWith("charset=")) {
					charset = param.split("=", 2)[1];
					break;
				}
			}
			BufferedReader reader = new BufferedReader(new InputStreamReader(is, charset));
			StringBuilder response = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				response.append(line);
				response.append('\n');
			}
			reader.close();
			
			if (!String.valueOf(status).startsWith("2")) {
				throw new JTradeException(String.format("%s: %s", status, response.toString()));
			}
			return response.toString();
		} catch (IOException e) {
			throw new JTradeException(e);
		} finally {
			if (connection != null) {
				connection.disconnect();
			}
		}
	}

	private String makeHistoricalRequest(String symbol, DateTime from, DateTime to) {
		String url = String.format(YAHOO_HISTORICAL_URL, urlEncode(symbol), from.getYear(), 0);
		
		try {
			Thread.sleep((long) (4000 * Math.random()));
		} catch (InterruptedException e) {
		}
		String response = null;
		int errorCount = 0;
		while (true) {
			try {
				response = makeHttpGet(url);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				if (++errorCount > 3) {
					throw new JTradeException("HTTP request failed too many times, aborting.");
				}
				try {
					Thread.sleep(50 * errorCount * errorCount);
				} catch (InterruptedException e2) {
				}
				continue;
			}
			break;
		}
		return response;
	}

	@Override
	public NavigableMap<DateTime, Bar> fetchHistoricalData(Symbol symbol, DateTime from, DateTime to, int barSizeSeconds) {
		logger.info("YahooMarketFeed fetching historical data for symbol {} for {} - {}", new Object[] { symbol, from, to });

		String symbolExchange = translateToYahoo(symbol);
		String response = makeHistoricalRequest(symbolExchange, from, to);
		String[] lines = Util.split(response, '\n', true);
		if (lines.length == 0) {
			throw new IllegalArgumentException(String.format("No data found for symbol %s between %s and %s: %s", symbol, from, to, response));
		} else if (lines[2].startsWith("errorid:3")) {
			return new TreeMap<DateTime, Bar>();
		} else if (lines[2].startsWith("errorid:")) {
			throw new IllegalArgumentException(
					String.format("Historical data for symbol %s could not be fetched: %s", symbol, lines.length > 3 ? lines[3] : response));
		}
		NavigableMap<DateTime, Bar> data = new TreeMap<DateTime, Bar>();
		DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyyMMdd");
		Duration d = Duration.standardDays(1);
		// Date,close,high,low,open,volume
		for (int i = lines.length - 1; i > 0; i--) {
			if (lines[i].length() == 0) {
				continue;
			}
			String[] cols = Util.split(lines[i], ',', true);
			try {
				DateTime date = formatter.parseDateTime(cols[0]);
				double open = Double.parseDouble(cols[4]);
				double high = Double.parseDouble(cols[2]);
				double low = Double.parseDouble(cols[3]);
				double close = Double.parseDouble(cols[1]);
				long volume = Long.parseLong(cols[5]);
				Bar bar = new Bar(d, symbol, date, open, high, low, close, Util.round((open + close) / 2, 2), volume, 1);
				data.put(bar.dateTime, bar);
			} catch (Exception e) {
				break;
			}
		}
		return data.subMap(from, true, to, false);
	}

	public Tick getLastTick(Symbol symbol) {
		Bar bar = getLastBar(symbol);
		if (bar == null) {
			return null;
		}
		Tick tick = new Tick(symbol);
		tick.dateTime = bar.getDateTime();
		tick.price = (bar.getOpen() + bar.getClose()) / 2;
		tick.ask = bar.getHigh();
		tick.bid = bar.getLow();
		tick.askSize = 1;
		tick.bidSize = 1;
		tick.lastSize = 1;
		return tick;
	}

	public Bar getLastBar(Symbol symbol) {
		return lastBarBySymbol.get(symbol);
	}

	@Override
	public void addTickListener(TickListener listener) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void removeTickListener(TickListener listener) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void addBarListener(BarListener listener) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void removeBarListener(BarListener listener) {
		throw new UnsupportedOperationException();
	}

	public synchronized void addBarListener(Symbol symbol, BarListener listener) {
		addBarListener(symbol, listener, 60, new MedianCleaner(60 * 60 * 8, 8));
	}

	public synchronized void addBarListener(Symbol symbol, BarListener listener, int barSizeSeconds, Cleaner cleaner) {
		YahooTickPoller poller = pollersBySymbol.get(symbol);
		if (poller == null) {
			poller = new YahooTickPoller(symbol, barSizeSeconds);
			executor.scheduleAtFixedRate(poller, POLL_INITIAL_DELAY_SECONDS, barSizeSeconds, TimeUnit.SECONDS);
			pollersBySymbol.put(symbol, poller);
		}
		poller.listeners.add(listener);
	}

	public synchronized void removeBarListener(Symbol symbol, BarListener listener) {
		YahooTickPoller poller = pollersBySymbol.get(symbol);
		if (poller != null) {
			poller.listeners.remove(listener);
		}
		if (poller.listeners.isEmpty()) {
			pollersBySymbol.remove(symbol);
			executor.remove(poller);
		}
	}

	@Override
	public void addMarketListener(MarketListener listener) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void removeMarketListener(MarketListener listener) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void addTickListener(Symbol symbol, TickListener listener) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void addTickListener(Symbol symbol, TickListener listener, boolean marketDepth, Cleaner cleaner) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void removeTickListener(Symbol symbol, TickListener listener) {
		throw new UnsupportedOperationException();
	}

	private void tick(YahooTickPoller poller, double ask, double bid, double last, double open, double close, double high, double low, int askSize, int bidSize,
			int lastSize, int volume, DateTime lastTrade) {
		if (poller.listeners.isEmpty()) {
			executor.remove(poller);
			return;
		}
		Bar bar = new Bar(null, poller.symbol, lastTrade, open, high, low, close, (open + close) / 2, volume, 1);
		if (bar.isComplete()) {
			lastBarBySymbol.put(poller.symbol, bar);
			for (BarListener listener : poller.listeners) {
				try {
					listener.onBar(bar);
				} catch (Throwable t) {
					logger.error(t.getMessage(), t);
				}
			}
		}
	}

	private String makeRequest(String symbol, String... keys) {
		String url = String.format(YAHOO_BASE_URL, symbol, Util.join(keys, ""));
		try {
			Thread.sleep((long) (4000 * Math.random()));
		} catch (InterruptedException e) {
		}
		String response = null;
		int internalErrorCount = 0;
		while (true) {
			try {
				response = makeHttpGet(url);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				if (++internalErrorCount > 3) {
					throw new JTradeException("HTTP request failed too many times, aborting.");
				}
				try {
					Thread.sleep(50 * internalErrorCount * internalErrorCount);
				} catch (InterruptedException e2) {
				}
				continue;
			}
			break;
		}
		return response;
	}

	private Map<String, String> getData(String symbol, String... keys) {
		String[] values = Util.split(makeRequest(symbol, keys), ',');
		Map<String, String> data = new HashMap<String, String>(keys.length);
		for (int i = 0; i < keys.length; i++) {
			data.put(keys[i], values[i]);
		}
		return data;
	}

	private double parseDouble(String amount) {
		if (amount == null) {
			return 0.0;
		}
		int l = amount.length() - 1;
		if (amount.charAt(l) == 'B') {
			return Double.parseDouble(amount.substring(0, l)) * 1000000000;
		}
		if (amount.charAt(l) == 'M') {
			return Double.parseDouble(amount.substring(0, l)) * 1000000;
		}
		if (amount.charAt(l) == '%') {
			return Double.parseDouble(amount.substring(0, l)) / 100;
		}
		return Double.parseDouble(amount);
	}

	private int parseInt(String amount) {
		if (amount == null) {
			return 0;
		}
		int l = amount.length() - 1;
		if (amount.charAt(l) == 'B') {
			return Integer.parseInt(amount.substring(0, l)) * 1000000000;
		}
		if (amount.charAt(l) == 'M') {
			return Integer.parseInt(amount.substring(0, l)) * 1000000;
		}
		if (amount.charAt(l) == '%') {
			return Integer.parseInt(amount.substring(0, l)) / 100;
		}
		return Integer.parseInt(amount);
	}

	private DateTime parseDateTime(String date, String time) {
		return dateFormatter.parseDateTime(date.concat(time));
	}

	final class YahooTickPoller implements Runnable {
		Symbol symbol;
		int barSizeSeconds;
		String symbolExchange;
		Map<String, String> lastData;
		List<BarListener> listeners;

		YahooTickPoller(Symbol symbol, int barSizeSeconds) {
			this.symbol = symbol;
			this.barSizeSeconds = barSizeSeconds;
			this.symbolExchange = new StringBuilder(symbol.getCode()).append('.').append(symbol.getExchange()).toString();
			this.listeners = new ArrayList<BarListener>();
		}

		public void run() {
			try {
				Thread.sleep((long) (barSizeSeconds * POLL_INTERVAL_FUZZINESS * 1000 * Math.random()));
				Map<String, String> data = getData(symbolExchange, ASK, BID, LAST_PRICE, OPEN, PREV_CLOSE, HIGH, LOW, VOLUME, LAST_TRADE_DATE, LAST_TRADE_TIME);
				if (lastData == null || !lastData.equals(data)) {
					YahooMarketFeed.this.tick(this, parseDouble(data.get(ASK)), parseDouble(data.get(BID)), parseDouble(data.get(LAST_PRICE)),
							parseDouble(data.get(OPEN)), parseDouble(data.get(PREV_CLOSE)), parseDouble(data.get(HIGH)), parseDouble(data.get(LOW)),
							parseInt(data.get(ASK_SIZE)), parseInt(data.get(BID_SIZE)), parseInt(data.get(LAST_TRADE_SIZE)), (lastData != null ? parseInt(data.get(VOLUME))
									- parseInt(lastData.get(VOLUME)) : 0), parseDateTime(data.get(LAST_TRADE_DATE), data.get(LAST_TRADE_TIME)));
				}
				lastData = data;
			} catch (Throwable t) {
				logger.error(t.getMessage(), t);
			}
		}
	}
}
