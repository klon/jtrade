package jtrade.marketfeed;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.Timer;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;

import jtrade.JTradeException;
import jtrade.Symbol;
import jtrade.Symbol.SymbolType;
import jtrade.SymbolFactory;
import jtrade.util.Configurable;
import jtrade.util.Util;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Duration;
import org.joda.time.Interval;
import org.joda.time.format.DateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ib.client.CommissionReport;
import com.ib.client.Contract;
import com.ib.client.ContractDetails;
import com.ib.client.EClientSocket;
import com.ib.client.EWrapper;
import com.ib.client.Execution;
import com.ib.client.Order;
import com.ib.client.OrderState;
import com.ib.client.ScannerSubscription;
import com.ib.client.TickType;
import com.ib.client.UnderComp;

/**
 * @author jonkle
 * 
 */
public class IBMarketFeed extends AbstractMarketFeed implements EWrapper {
	private static final Logger logger = LoggerFactory.getLogger(IBMarketFeed.class);
	private static final int historicalRequestTimeOutMillis = 30000;
	private static final int maxBarsPerHistoricalRequest = 2000;
	private static final double maxHistoricalRequestsPerSecond = 0.067;
	private static final int maxHistoricalRequestRetries = 2;
	private static final Duration realTimeBarSize = new Duration(5000);
	private static final int defaultPort = 4000;

	public final Configurable<String> SERVER_HOSTS = new Configurable<String>("SERVER_HOST", "localhost:4000");
	public final Configurable<Integer> CLIENT_ID = new Configurable<Integer>("CLIENT_ID", 1);

	protected String[] hosts;
	protected int[] ports;
	protected int currHost;
	protected EClientSocket socket;
	protected int serverVersion;
	protected boolean connect;
	protected int clientId;
	protected String accountCode;
	protected AtomicInteger nextValidReqId;
	protected String lastMessage;
	protected Map<Integer, IBTickRequest> tickRequestsById;
	protected Map<Integer, IBBarRequest> barRequestsById;
	protected Map<Integer, IBHistoricalRequest> historicalRequestsById;
	protected Map<Integer, IBScannerRequest> scannerRequestsById;
	protected Map<Integer, IBContractRequest> contractRequestsById;
	protected String marketScannerParams;
	protected Timer timer;
	protected long lastHistoricalRequestMillis;
	protected List<BarListener> barListeners;
	protected List<TickListener> tickListeners;

	public IBMarketFeed() {
		this(null, -1, (File) null);
	}

	public IBMarketFeed(int clientId) {
		this(null, clientId, (File) null);
	}

	public IBMarketFeed(String hosts, int clientId) {
		this(hosts, clientId, (File) null);
	}

	public IBMarketFeed(String hosts, int clientId, String dataDir) {
		this(hosts, clientId, new File(dataDir));
	}

	public IBMarketFeed(String hosts, int clientId, File dataDir) {
		super(dataDir);
		if (hosts == null) {
			hosts = SERVER_HOSTS.get();
		}
		if (clientId < 0) {
			clientId = CLIENT_ID.get();
		}
		String[] tmp = Util.split(hosts, ',');
		this.hosts = new String[tmp.length];
		this.ports = new int[tmp.length];
		for (int i = 0; i < tmp.length; i++) {
			String[] hp = Util.split(tmp[i].trim().toLowerCase(), ':');
			if (hp.length == 1) {
				this.hosts[i] = hp[0].trim();
				this.ports[i] = defaultPort;
			} else {
				this.hosts[i] = hp[0].trim();
				this.ports[i] = Integer.parseInt(hp[1]);
			}
		}
		this.clientId = clientId;
		nextValidReqId = new AtomicInteger();
		tickRequestsById = new HashMap<Integer, IBTickRequest>();
		barRequestsById = new HashMap<Integer, IBBarRequest>();
		historicalRequestsById = new HashMap<Integer, IBHistoricalRequest>();
		scannerRequestsById = new HashMap<Integer, IBScannerRequest>();
		contractRequestsById = new HashMap<Integer, IBContractRequest>();
		barListeners = new ArrayList<BarListener>();
		tickListeners = new ArrayList<TickListener>();
	}

	@Override
	protected void finalize() throws Throwable {
		disconnect();
	}

	@Override
	public synchronized void connect() {
		if (isConnected()) {
			return;
		}
		connect = true;
		try {
			doConnect();
		} catch (JTradeException e) {
			logger.warn(e.getMessage());
		} catch (Throwable t) {
			logger.error(t.getMessage(), t);
		}

		timer = new Timer(true);
		timer.scheduleAtFixedRate(new java.util.TimerTask() {
			@Override
			public void run() {
				try {
					if (connect && (socket == null || !socket.isConnected())) {
						doConnect();
					}
				} catch (JTradeException e) {
					logger.warn(e.getMessage());
				} catch (Throwable t) {
					logger.error(t.getMessage(), t);
				}
			}
		}, 15000, 15000);
		timer.scheduleAtFixedRate(new java.util.TimerTask() {
			@Override
			public void run() {
				DateTime now = new DateTime();
				if (now.getSecondOfMinute() == 0) {
					fireMinuteEvent(now);
					if (now.getMinuteOfHour() == 0) {
						fireHourEvent(now);
						if (now.getHourOfDay() == 0) {
							fireDayEvent(now);
						}
					}
					// Sometimes ticks stop being received, this might
					// workaround that.
					refreshTickRequests();
				}
				if (tickRequestsById.size() > 0) {
					for (IBTickRequest req : tickRequestsById.values()) {
						if (req.barListeners.size() > 0) {
							buildBar(req, -1, -1, -1, null);
						}
					}
				}
			}
		}, new DateTime().millisOfSecond().roundCeilingCopy().toDate(), 1000);
	}

	protected synchronized void doConnect() {
		socket = new EClientSocket(this);
		socket.eConnect(hosts[currHost], ports[currHost], clientId);
		if (!socket.isConnected()) {
			int failedHost = currHost;
			currHost = (currHost + 1) % hosts.length;
			throw new JTradeException(String.format("Could not connect to TWS using %s@%s:%s", clientId, hosts[failedHost], ports[failedHost]));
		}
		// IB Log levels: 1=SYSTEM 2=ERROR 3=WARNING 4=INFORMATION 5=DETAIL
		socket.setServerLogLevel(3);
		socket.reqNewsBulletins(true);
		serverVersion = socket.serverVersion();

		logger.info("Connected to TWS v{} using {}@{}:{}, data folder is {}",
				new Object[] { serverVersion, clientId, hosts[currHost], ports[currHost], dataDir.getAbsolutePath() });
	}

	@Override
	public synchronized void disconnect() {
		connect = false;
		if (timer != null) {
			timer.cancel();
			timer = null;
		}
		if (socket == null || !socket.isConnected()) {
			return;
		}

		removeAllListeners();

		if (socket != null && socket.isConnected()) {
			socket.cancelNewsBulletins();
			socket.eDisconnect();
		}
		socket = null;
		logger.info("Disconnected from TWS v{} using {}@{}:{}", new Object[] { serverVersion, clientId, hosts[currHost], hosts[currHost] });
	}

	@Override
	public boolean isConnected() {
		return socket != null && socket.isConnected();
	}

	protected void checkConnected() {
		if (!isConnected()) {
			throw new JTradeException(String.format("%s is not connected to TWS at %s@%s", getClass().getSimpleName(), clientId, SERVER_HOSTS.get()));
		}
	}

	private synchronized void refreshTickRequests() {
		long now = System.currentTimeMillis();
		for (IBTickRequest req : new ArrayList<IBTickRequest>(tickRequestsById.values())) {
			if (req.timestamp + 30000 > now) {
				continue;
			}
			logger.debug("Refreshing, removing {}", req);
			tickRequestsById.remove(req.id);
			if (req.marketDepth) {
				socket.cancelMktDepth(req.id);
			}
			socket.cancelMktData(req.id);

			Contract contract = makeContract(req.symbol);
			req.id = nextValidReqId.incrementAndGet();
			logger.debug("Refreshing, adding {}", req);
			socket.reqMktData(req.id, contract, "", false);
			if (req.marketDepth) {
				socket.reqMktDepth(req.id, contract, 10);
			}
			tickRequestsById.put(req.id, req);
		}
	}

	protected Contract makeContract(Symbol symbol) {
		String code = symbol.getCode();
		String exchange = symbol.getExchange();
		String currency = symbol.getCurrency();
		String securityType = null;
		String expiry = null;
		String multiplier = String.valueOf(symbol.getMultiplier());
		double strike = 0.0;
		String right = null;
		int conId = 0;
		boolean includeExpired = false;
		if (symbol.isStock()) {
			securityType = "STK";
		} else if (symbol.isFuture()) {
			securityType = "FUT";
			expiry = symbol.getExpiry() != null ? symbol.getExpiry().toString("yyyyMMdd") : null;
			includeExpired = true;
		} else if (symbol.isIndex()) {
			securityType = "IND";
		} else if (symbol.isOption()) {
			securityType = "OPT";
			expiry = symbol.getExpiry() != null ? symbol.getExpiry().toString("yyyyMMdd") : null;
			strike = symbol.getStrike();
			right = symbol.getOptionRight().toString();
			includeExpired = true;
		} else if (symbol.isCash()) {
			securityType = "CASH";
		}
		return makeContract(code, exchange, currency, securityType, expiry, multiplier, strike, right, conId, includeExpired);
	}

	protected Contract makeContract(String symbol, String exchange, String currency, String securityType, String expiry, String multiplier, double strike,
			String right, int conId, boolean includeExpired) {
		Contract contract = new Contract();
		contract.m_symbol = symbol;
		contract.m_exchange = exchange;
		contract.m_currency = currency;
		contract.m_secType = securityType;
		contract.m_expiry = expiry;
		contract.m_multiplier = multiplier;
		contract.m_strike = strike;
		contract.m_right = right;
		contract.m_conId = conId;
		contract.m_includeExpired = includeExpired;
		return contract;
	}

	protected Symbol toSymbol(Contract contract) {
		return toSymbol(contract, null);
	}

	protected Symbol toSymbol(Contract contract, ContractDetails details) {
		StringBuilder fullCode = new StringBuilder(contract.m_symbol).append('-')
				.append(contract.m_exchange != null ? contract.m_exchange : contract.m_primaryExch).append('-').append(contract.m_currency).append('-');
		if ("FUT".equals(contract.m_secType)) {
			fullCode.append("FUTURE");
		} else if ("STK".equals(contract.m_secType)) {
			fullCode.append("STOCK");
		} else if ("IND".equals(contract.m_secType)) {
			fullCode.append("INDEX");
		} else if ("OPT".equals(contract.m_secType)) {
			fullCode.append("OPTION");
		} else {
			fullCode.append("CASH");
		}
		if (contract.m_expiry != null) {
			fullCode.append('-').append(contract.m_expiry);
		}
		if (contract.m_strike > 0) {
			if (contract.m_strike - (long) contract.m_strike != 0.0) {
				fullCode.append('-').append(String.format("%.2f", contract.m_strike)).append('-').append(contract.m_right.toUpperCase());
			} else {
				fullCode.append('-').append((long) contract.m_strike).append('-').append(contract.m_right.toUpperCase());
			}
		}
		if (contract.m_right != null) {
			if (contract.m_right.equals("C") || contract.m_right.equals("CALL")) {
				fullCode.append('-').append("CALL");
			} else if (contract.m_right.equals("P") || contract.m_right.equals("PUT")) {
				fullCode.append('-').append("PUT");
			}
		}
		Symbol s = SymbolFactory.getSymbol(fullCode.toString());
		// s.multiplier = contract.m_multiplier != null ?
		// Integer.parseInt(contract.m_multiplier) : s.multiplier;
		// s.minTick = details != null ? details.m_minTick : s.minTick;
		return s;
	}

	@Override
	protected NavigableMap<DateTime, Bar> fetchHistoricalData(Symbol symbol, DateTime fromDate, DateTime toDate, int barSizeSeconds) {
		NavigableMap<DateTime, Bar> result = new TreeMap<DateTime, Bar>();

		DateTime start = toDate.isAfterNow() ? new DateTime(toDate.getZone()) : toDate;
		if (symbol.getExpiry() != null && start.isAfter(symbol.getExpiry())) {
			start = symbol.getExpiry();
		}
		DateTime end = null;

		logger.info("Fetching historical data for symbol {} {}-{}", new Object[] { symbol, fromDate, start });

		int retries = 0;
		while (true) {
			end = start;
			if (barSizeSeconds < 86400) {
				start = end.minusSeconds(Math.min(maxBarsPerHistoricalRequest * barSizeSeconds, 86400));
			} else {
				start = end.minusSeconds(maxBarsPerHistoricalRequest * barSizeSeconds);
			}
			if (start.isBefore(fromDate)) {
				start = fromDate;
			}

			while (1000.0 / (System.currentTimeMillis() - lastHistoricalRequestMillis) >= maxHistoricalRequestsPerSecond) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
				}
			}

			lastHistoricalRequestMillis = System.currentTimeMillis();

			IBHistoricalRequest req = requestHistoricalData(symbol, start, end, barSizeSeconds);
			if (req.failed) {
				throw new JTradeException(String.format("Historical data for symbol %s could not be fetched: %s", symbol, req.error));
			}
			for (Bar bar : req.bars.values()) {
				if ((bar.getDateTime().isEqual(fromDate) || bar.getDateTime().isAfter(fromDate)) && bar.getDateTime().isBefore(toDate)) {
					result.put(bar.getDateTime(), bar);
				}
			}
			if (req.bars.isEmpty()) {
				retries++;
				start = end;
			} else {
				start = req.bars.firstKey();
			}

			if (start.isBefore(fromDate) || start.isEqual(fromDate) || retries > maxHistoricalRequestRetries) {
				break;
			}
		}
		return result;
	}

	public IBHistoricalRequest requestHistoricalData(Symbol symbol, DateTime fromDate, DateTime toDate, int barSizeSeconds) {
		checkConnected();
		if (historicalRequestsById.size() > 0) {
			throw new IllegalStateException(String.format("%s historical request already in progress.", getClass().getSimpleName()));
		}
		Interval interval = new Interval(fromDate, toDate);
		if ((interval.toDurationMillis() / 1000) / barSizeSeconds > maxBarsPerHistoricalRequest) {
			throw new IllegalStateException(String.format("%s cannot request %s > %s ticks historical data at once.", getClass().getSimpleName(),
					(interval.toDurationMillis() / 1000) / barSizeSeconds, maxBarsPerHistoricalRequest));
		}

		String endDateStr = toDate.toString("yyyyMMdd HH:mm:ss");
		String durationStr = null;
		String barSizeSetting = null;
		switch (barSizeSeconds) {
		case 1:
			durationStr = (interval.toDurationMillis() / 1000) + " S";
			barSizeSetting = "1 secs";
			break;
		case 5:
			durationStr = (interval.toDurationMillis() / 1000) + " S";
			barSizeSetting = "5 secs";
			break;
		case 15:
			durationStr = (interval.toDurationMillis() / 1000) + " S";
			barSizeSetting = "15 secs";
			break;
		case 30:
			durationStr = (interval.toDurationMillis() / 1000) + " S";
			barSizeSetting = "30 secs";
			break;
		case 60:
			durationStr = (interval.toDurationMillis() / 1000) + " S";
			barSizeSetting = "1 min";
			break;
		case 120:
			durationStr = (interval.toDurationMillis() / 1000) + " S";
			barSizeSetting = "2 mins";
			break;
		case 180:
			durationStr = (interval.toDurationMillis() / 1000) + " S";
			barSizeSetting = "3 mins";
			break;
		case 300:
			durationStr = (interval.toDurationMillis() / 1000) + " S";
			barSizeSetting = "5 mins";
			break;
		case 900:
			durationStr = (interval.toDurationMillis() / 1000) + " S";
			barSizeSetting = "15 mins";
			break;
		case 1800:
			durationStr = (interval.toDurationMillis() / 1000) + " S";
			barSizeSetting = "30 mins";
			break;
		case 3600:
			durationStr = (interval.toDurationMillis() / 1000) + " S";
			barSizeSetting = "1 hour";
			break;
		case 86400:
			durationStr = (Days.daysIn(interval).getDays()) + " D";
			barSizeSetting = "1 day";
			break;
		default:
			throw new IllegalArgumentException("Invalid bar size: " + barSizeSeconds);
		}
		if (logger.isDebugEnabled())
			logger.debug("Requesting historical data: {} {} {} {}", new Object[] { symbol, endDateStr, durationStr, barSizeSetting });

		// Fetch TRADES
		int requestId = nextValidReqId.incrementAndGet();
		IBHistoricalRequest req = new IBHistoricalRequest(requestId, symbol, Duration.standardSeconds(barSizeSeconds));
		historicalRequestsById.put(requestId, req);
		socket.reqHistoricalData(requestId, makeContract(symbol), endDateStr, durationStr, barSizeSetting, symbol.isCash() ? "MIDPOINT" : "TRADES", 0, 2);
		long timeOut = System.currentTimeMillis() + historicalRequestTimeOutMillis;
		try {
			while (!req.done && !req.failed && timeOut > System.currentTimeMillis()) {
				Thread.sleep(500);
			}
		} catch (InterruptedException e) {
		}
		historicalRequestsById.remove(requestId);
		return req;
	}

	@Override
	public Tick getLastTick(Symbol symbol) {
		for (Iterator<Entry<Integer, IBTickRequest>> entries = tickRequestsById.entrySet().iterator(); entries.hasNext();) {
			Map.Entry<Integer, IBTickRequest> entry = entries.next();
			if (entry.getValue().symbol.equals(symbol)) {
				return entry.getValue().prevTick;
			}
		}
		return null;
	}

	@Override
	public Bar getLastBar(Symbol symbol) {
		for (Iterator<Entry<Integer, IBTickRequest>> entries = tickRequestsById.entrySet().iterator(); entries.hasNext();) {
			Map.Entry<Integer, IBTickRequest> entry = entries.next();
			if (entry.getValue().symbol.equals(symbol)) {
				return entry.getValue().prevBar;
			}
		}
		return null;
	}

	@Override
	public synchronized void addTickListener(TickListener listener) {
		logger.info("Adding TickListener {} for all symbols", listener.getClass().getSimpleName());
		tickListeners.add(listener);
	}

	@Override
	public synchronized void removeTickListener(TickListener listener) {
		boolean removed = tickListeners.remove(listener);
		if (removed) {
			logger.info("Removed TickListener {} for all symbols", listener.getClass().getSimpleName());
		}
		removeTickListener(null, listener);
	}

	@Override
	public void addTickListener(Symbol symbol, TickListener listener) {
		addTickListener(symbol, listener, false, null);
	}

	@Override
	public synchronized void addTickListener(Symbol symbol, TickListener listener, boolean marketDepth, Cleaner cleaner) {
		checkConnected();
		marketDepth = marketDepth && !symbol.isIndex();
		Integer reqId = null;
		IBTickRequest req = null;
		for (Iterator<Entry<Integer, IBTickRequest>> entries = tickRequestsById.entrySet().iterator(); entries.hasNext();) {
			Map.Entry<Integer, IBTickRequest> entry = entries.next();
			if (entry.getValue().symbol.equals(symbol)) {
				reqId = entry.getKey();
				req = entry.getValue();
				if (req.tickListeners.contains(listener)) {
					return;
				}
			}
		}
		if (marketDepth) {
			logger.info("Adding TickListener {} with marketdepth for symbol {} using cleaner {}",
					new Object[] { listener.getClass().getSimpleName(), symbol, cleaner });
		} else {
			logger.info("Adding TickListener {} for symbol {} using cleaner {}", new Object[] { listener.getClass().getSimpleName(), symbol, cleaner });
		}
		if (reqId == null) {
			Contract contract = makeContract(symbol);

			reqId = nextValidReqId.incrementAndGet();
			socket.reqContractDetails(reqId, contract);
			contractRequestsById.put(reqId, new IBContractRequest(reqId));

			reqId = nextValidReqId.incrementAndGet();
			socket.reqMktData(reqId, contract, "", false);
			if (marketDepth) {
				socket.reqMktDepth(reqId, contract, 10);
			}
			req = new IBTickRequest(reqId, symbol, marketDepth, cleaner);
			tickRequestsById.put(reqId, req);
		}
		req.tickListeners.add(listener);
	}

	@Override
	public synchronized void removeTickListener(Symbol symbol, TickListener listener) {
		for (Iterator<Entry<Integer, IBTickRequest>> entries = tickRequestsById.entrySet().iterator(); entries.hasNext();) {
			Map.Entry<Integer, IBTickRequest> entry = entries.next();
			if (symbol == null || entry.getValue().symbol.equals(symbol)) {
				boolean removed = entry.getValue().tickListeners.remove(listener);
				if (removed) {
					logger.info("Removed TickListener {} for symbol {}", listener.getClass().getSimpleName(), entry.getValue().symbol);
				}
				if (entry.getValue().tickListeners.isEmpty() && entry.getValue().barListeners.isEmpty()) {
					logger.info("Removed last TickListener for symbol {}, cancelling market data request {}", entry.getValue().symbol, entry.getValue());
					entries.remove();
					if (isConnected()) {
						if (entry.getValue().marketDepth) {
							socket.cancelMktDepth(entry.getKey());
						}
						socket.cancelMktData(entry.getKey());
					}
				}
			}
		}
	}

	@Override
	public synchronized void addBarListener(BarListener listener) {
		logger.info("Adding BarListener {} for all symbols", listener.getClass().getSimpleName());
		barListeners.add(listener);
	}

	@Override
	public synchronized void removeBarListener(BarListener listener) {
		boolean removed = barListeners.remove(listener);
		if (removed) {
			logger.info("Removed BarListener {} for all symbols", listener.getClass().getSimpleName());
		}
		removeBarListener(null, listener);
	}

	@Override
	public void addBarListener(Symbol symbol, BarListener listener) {
		addBarListener(symbol, listener, 60, null);
	}

	@Override
	public synchronized void addBarListener(Symbol symbol, BarListener listener, int barSize, Cleaner cleaner) {
		checkConnected();
		Integer reqId = null;
		IBTickRequest req = null;

		for (Iterator<Entry<Integer, IBTickRequest>> entries = tickRequestsById.entrySet().iterator(); entries.hasNext();) {
			Map.Entry<Integer, IBTickRequest> entry = entries.next();
			if (entry.getValue().symbol.equals(symbol)) {
				reqId = entry.getKey();
				req = entry.getValue();
				if (req.barSize != null && req.barSize.getStandardSeconds() != barSize) {
					throw new IllegalArgumentException("Existing listener uses different bar size: " + req.barSize.getStandardSeconds());
				}
				if (req.barListeners.contains(listener)) {
					return;
				}
			}
		}

		logger.info("Adding BarListener {} for symbol {}", listener.getClass().getSimpleName(), symbol);

		if (reqId == null) {
			Contract contract = makeContract(symbol);

			reqId = nextValidReqId.incrementAndGet();
			socket.reqContractDetails(reqId, contract);
			contractRequestsById.put(reqId, new IBContractRequest(reqId));

			reqId = nextValidReqId.incrementAndGet();
			// socket.reqRealTimeBars(reqId, contract, 5, "TRADES", false);
			socket.reqMktData(reqId, contract, "", false);
			req = new IBTickRequest(reqId, symbol, barSize, cleaner);

			tickRequestsById.put(reqId, req);
		} else {
			req.setBarSize(barSize);
		}
		req.barListeners.add(listener);
	}

	@Override
	public synchronized void removeBarListener(Symbol symbol, BarListener listener) {
		for (Iterator<Entry<Integer, IBTickRequest>> entries = tickRequestsById.entrySet().iterator(); entries.hasNext();) {
			Map.Entry<Integer, IBTickRequest> entry = entries.next();
			if (symbol == null || entry.getValue().symbol.equals(symbol)) {
				boolean removed = entry.getValue().barListeners.remove(listener);
				if (removed) {
					logger.info("Removed BarListener {} for symbol {}", listener.getClass().getSimpleName(), entry.getValue().symbol);
				}
				if (entry.getValue().tickListeners.isEmpty() && entry.getValue().barListeners.isEmpty()) {
					logger.info("Removed last BarListener for symbol {}, cancelling market data request {}", entry.getValue().symbol, entry.getValue());
					entries.remove();
					if (isConnected()) {
						socket.cancelMktData(entry.getKey());
					}
				}
			}
		}
	}

	@Override
	public synchronized void removeAllListeners() {
		super.removeAllListeners();
		barListeners.clear();
		tickListeners.clear();
		for (IBTickRequest req : new ArrayList<IBTickRequest>(tickRequestsById.values())) {
			for (BarListener listener : new ArrayList<BarListener>(req.barListeners)) {
				removeBarListener(null, listener);
			}
			for (TickListener listener : new ArrayList<TickListener>(req.tickListeners)) {
				removeTickListener(null, listener);
			}
		}
	}

	@Override
	public void historicalData(int reqId, String date, double open, double high, double low, double close, int volume, int count, double wap, boolean hasGaps) {
		try {
			if (logger.isDebugEnabled())
				logger.debug("historicalData: {} {} {} {} {} {} {} {} {} {}", new Object[] { reqId, date, open, low, high, close, volume, count, wap, hasGaps });

			IBHistoricalRequest req = historicalRequestsById.get(reqId);
			if (req == null) {
				socket.cancelHistoricalData(reqId);
				return;
			}
			req.timestamp = System.currentTimeMillis();

			if (date.startsWith("finished")) {
				req.done = true;
				return;
			}
			DateTime dateTime = null;
			if (req.barSize.getMillis() == 86400000) {
				dateTime = DateTimeFormat.forPattern("yyyyMMdd").parseDateTime(date);
			} else {
				dateTime = new DateTime(Long.parseLong(date) * 1000);
			}
			Bar bar = new Bar(req.barSize, req.symbol, dateTime, open, high, low, close, wap, volume, count);
			req.bars.put(bar.dateTime, bar);
		} catch (Throwable t) {
			// Do not allow exceptions come back to the socket -- it will cause
			// disconnects
			logger.error(t.getMessage(), t);
		}
	}

	@Override
	public void tickPrice(int reqId, int tickType, double price, int canAutoExecute) {
		tick(reqId, tickType, price, -1, null);
	}

	@Override
	public void tickSize(int reqId, int tickType, int size) {
		tick(reqId, tickType, -1, size, null);
	}

	@Override
	public void tickString(int reqId, int tickType, String value) {
		tick(reqId, tickType, -1, -1, value);
	}

	@Override
	public void tickGeneric(int reqId, int tickType, double value) {
		tick(reqId, tickType, value, -1, null);
	}

	private void tick(int reqId, int tickType, double doubleValue, int intValue, String stringValue) {
		try {
			if (logger.isDebugEnabled())
				logger.debug("tick: reqId: {}, tickType: {}, doubleValue: {}, intValue: {}, stringValue: {}", new Object[] { reqId, tickType, doubleValue, intValue,
						stringValue });

			IBTickRequest req = tickRequestsById.get(reqId);
			if (req != null) {
				buildTick(req, tickType, doubleValue, intValue, stringValue);
				if (req.barListeners.size() > 0) {
					buildBar(req, tickType, doubleValue, intValue, stringValue);
				}
			}
			if (req == null) {
				logger.warn("No matching request for tick event, cancelling market data: reqId: {}, tickType: {}, doubleValue: {}, intValue: {}, stringValue: {}",
						new Object[] { reqId, tickType, doubleValue, intValue, stringValue });
				socket.cancelMktData(reqId);
			}

		} catch (Throwable t) {
			// Do not allow exceptions come back to the socket -- it will cause
			// disconnects
			logger.error(t.getMessage(), t);
		}
	}

	private void buildBar(IBTickRequest req, int tickType, double doubleValue, int intValue, String stringValue) {
		try {
			synchronized (req) {
				Bar bar = req.currBar;
				long now = System.currentTimeMillis();
				if (bar == null) {
					bar = req.currBar = new Bar(req.barSize, req.symbol, new DateTime(now - (now % req.barSizeMillis)));
				} else if (bar.dateTime.getMillis() + req.barSizeMillis <= now) {
					boolean valid = isValidBar(req);
					if (!valid && req.prevBar != null) {
						bar.open = bar.high = bar.low = bar.close = bar.wap = req.prevBar.close;
						bar.volume = bar.trades = 0;
						valid = true;
					}
					if (valid) {
						int len = req.barListeners.size();
						for (int i = 0; i < len; i++) {
							BarListener listener = req.barListeners.get(i);
							try {
								listener.onBar(bar);
							} catch (Throwable t) {
								logger.error(t.getMessage(), t);
							}
						}
						len = barListeners.size();
						for (int i = 0; i < len; i++) {
							BarListener listener = barListeners.get(i);
							try {
								listener.onBar(bar);
							} catch (Throwable t) {
								logger.error(t.getMessage(), t);
							}
						}
						req.prevBar = bar;
						bar = req.currBar = new Bar(req.barSize, req.symbol, new DateTime(now - (now % req.barSizeMillis)));
					}
				}

				switch (tickType) {
				case TickType.BID:
				case TickType.ASK:
					// If currency then only bids and asks are received
					if (!req.symbol.isCash()) {
						break;
					}
				case TickType.LAST:
					if (bar.open == 0.0) {
						bar.open = doubleValue;
						bar.high = Double.NEGATIVE_INFINITY;
						bar.low = Double.POSITIVE_INFINITY;
					}
					bar.high = Math.max(bar.high, doubleValue);
					bar.low = Math.min(bar.low, doubleValue);
					bar.close = doubleValue;
					req.lastPrice = doubleValue;
					break;
				case TickType.VOLUME:
					if (intValue > 0) {
						// If new volume is lower than previous, reset has
						// occured.
						if (intValue < req.barVolume) {
							bar.volume = intValue;
							bar.wap = req.lastPrice;
						} else if (req.barVolume > 0) {
							int lastSize = (intValue - req.barVolume);
							bar.wap = Util.round((bar.wap * bar.volume + req.lastPrice * lastSize) / (bar.volume + lastSize), 2);
							bar.volume += lastSize;
							bar.trades++;
						} else {
							bar.volume = 0;
							bar.wap = req.lastPrice;
						}
						req.barVolume = intValue;
					}
					break;
				case TickType.LAST_TIMESTAMP:
					if (logger.isDebugEnabled()) {
						logger.debug("Tick timestamp {}", new DateTime(Long.parseLong(stringValue) * 1000));
					}
					// bar.dateTime = new DateTime(Long.parseLong(stringValue) *
					// 1000);
					break;
				case TickType.BID_SIZE:
				case TickType.ASK_SIZE:
				case TickType.OPEN:
				case TickType.CLOSE:
				case TickType.HIGH:
				case TickType.LOW:
				case TickType.LAST_SIZE:
				default:
					break;
				}
			}
		} catch (Throwable t) {
			// Do not allow exceptions come back to the socket -- it will cause
			// disconnects
			logger.error(t.getMessage(), t);
		}
	}

	private void buildTick(IBTickRequest req, int tickType, double doubleValue, int intValue, String stringValue) {
		try {
			// Ignore last size since it is not consistent, use volume change
			// instead.
			if (tickType == TickType.LAST_SIZE) {
				return;
			}
			req.timestamp = System.currentTimeMillis();

			Tick currTick = req.currTick;
			if (currTick == null) {
				currTick = req.currTick = new Tick(req.symbol);
			}
			currTick.dateTime = new DateTime(req.timestamp);
			currTick.lastSize = 0;

			switch (tickType) {
			case TickType.ASK:
				currTick.ask = doubleValue;
				break;
			case TickType.BID:
				currTick.bid = doubleValue;
				break;
			case TickType.LAST:
				currTick.price = doubleValue;
				break;
			// case TickType.OPEN:
			// break;
			// case TickType.CLOSE:
			// break;
			// case TickType.HIGH:
			// break;
			// case TickType.LOW:
			// break;
			case TickType.ASK_SIZE:
				currTick.askSize = intValue;
				break;
			case TickType.BID_SIZE:
				currTick.bidSize = intValue;
				break;
			// case TickType.LAST_SIZE:
			// Ignore last size since it is not consistent, use volume change
			// instead.
			// tick.lastSize = intValue;
			// break;
			case TickType.VOLUME:
				// If new volume is lower than previous, reset has occured.
				if (intValue > 0) {
					if (intValue < currTick.volume) {
						currTick.lastSize = intValue;
						currTick.volume = intValue;
					} else if (currTick.volume > 0) {
						currTick.lastSize = intValue - currTick.volume;
						currTick.volume = intValue;
					} else {
						currTick.lastSize = 0;
						currTick.volume = intValue;
					}
				}
				break;
			case TickType.LAST_TIMESTAMP:
				if (logger.isDebugEnabled()) {
					logger.debug("Tick timestamp {}", new DateTime(Long.parseLong(stringValue) * 1000));
				}
				// tick.dateTime = new DateTime(Long.parseLong(stringValue) *
				// 1000);
				break;
			}

			if (isValidTick(req)) {
				int len = req.tickListeners.size();
				for (int i = 0; i < len; i++) {
					TickListener listener = req.tickListeners.get(i);
					try {
						listener.onTick(currTick);
					} catch (Throwable t) {
						logger.error(t.getMessage(), t);
					}
				}
				len = tickListeners.size();
				for (int i = 0; i < len; i++) {
					TickListener listener = tickListeners.get(i);
					try {
						listener.onTick(currTick);
					} catch (Throwable t) {
						logger.error(t.getMessage(), t);
					}
				}
				req.prevTick = currTick;
				req.currTick = new Tick(currTick);
			}
		} catch (Throwable t) {
			// Do not allow exceptions come back to the socket -- it will cause
			// disconnects
			logger.error(t.getMessage(), t);
		}
	}

	private boolean isValidBar(IBBarRequest req) {
		Bar curr = req.currBar;
		if (curr.close <= 0 || curr.high <= 0 || curr.low <= 0 || curr.close <= 0) {
			return false;
		}
		if (curr.high < curr.low) {
			return false;
		}
		if (req.cleaner != null && Double.isNaN(req.cleaner.update(curr.getDateTime(), curr.getPrice()))) {
			return false;
		}
		return true;
	}

	private boolean isValidBar(IBTickRequest req) {
		Bar curr = req.currBar;
		if (curr.close <= 0 || curr.high <= 0 || curr.low <= 0 || curr.close <= 0) {
			return false;
		}
		if (curr.high < curr.low) {
			return false;
		}
		if (req.barCleaner != null && Double.isNaN(req.barCleaner.update(curr.getDateTime(), curr.getPrice()))) {
			return false;
		}
		return true;
	}

	private boolean isValidTick(IBTickRequest req) {
		Tick currTick = req.currTick;
		Tick prevTick = req.prevTick;
		if (req.marketDepth && currTick.marketDepth == null) {
			return false;
		}

		if (currTick.getSymbol().isIndex()) {
			if (currTick.price <= 0 && !currTick.getSymbol().isTickIndex()) {
				return false;
			}
			if (prevTick != null) {
				if (currTick.price == prevTick.price) {
					return false;
				}
			}
			return true;
		}
		if (currTick.askSize <= 0 || currTick.bidSize <= 0) {
			return false;
		}
		if (currTick.ask <= 0 || currTick.bid <= 0 || currTick.ask == currTick.bid) {
			return false;
		}
		if (prevTick != null) {
			if (currTick.ask == prevTick.ask && currTick.askSize == prevTick.askSize && currTick.bid == prevTick.bid && currTick.bidSize == prevTick.bidSize
					&& currTick.price == prevTick.price && currTick.lastSize == prevTick.lastSize && currTick.volume == prevTick.volume) {
				return false;
			}
		}
		if (req.tickCleaner != null && Double.isNaN(req.tickCleaner.update(currTick.dateTime, currTick.price))) {
			return false;
		}
		return true;
	}

	@Override
	public void updateMktDepth(int reqId, int position, int operation, int side, double price, int size) {
		try {
			if (logger.isDebugEnabled())
				logger.debug("reqId: {}, position: {}, operation: {}, side: {}, price: {}, size: {}", new Object[] { reqId, position, operation, side, price, size });

			if (price < 0) {
				return;
			}

			IBTickRequest req = tickRequestsById.get(reqId);
			if (req == null) {
				logger.warn(
						"No matching request for market depth event, cancelling market data: reqId: {}, position: {}, operation: {}, side: {}, price: {}, size: {}",
						new Object[] { reqId, position, operation, side, price, size });
				socket.cancelMktData(reqId);
				return;
			}
			if (req.currTick == null) {
				return;
			}

			MarketDepth md = req.currTick.marketDepth;
			if (md == null) {
				md = req.currTick.marketDepth = new MarketDepth(10);
			}
			double[] prices = (side == 0 ? md.askPrices : md.bidPrices); // ASK
			// 0,BID
			// 1
			int[] sizes = (side == 0 ? md.askSizes : md.bidSizes); // ASK 0,BID
			// 1

			switch (operation) {
			case 0: // INSERT
			case 1: // UPDATE
				prices[position] = price;
				sizes[position] = size;
				break;
			case 2: // DELETE
				prices[position] = 0;
				sizes[position] = 0;
				break;
			}
		} catch (Throwable t) {
			// Do not allow exceptions come back to the socket -- it will cause
			// disconnects
			logger.error(t.getMessage(), t);
		}
	}

	@Override
	public void realtimeBar(int reqId, long time, double open, double high, double low, double close, long volume, double wap, int count) {
		try {
			if (logger.isDebugEnabled())
				logger.debug("realtimeBar: reqId: {}, time: {}, open: {}, high: {}, low: {}, close: {}, volume: {}, wap: {}, count: {}", new Object[] { reqId, time,
						open, high, low, close, volume, wap, count });

			IBBarRequest req = barRequestsById.get(reqId);
			if (req == null) {
				socket.cancelRealTimeBars(reqId);
				barRequestsById.remove(reqId);
				return;
			}
			req.timestamp = System.currentTimeMillis();

			Bar bar = new Bar(realTimeBarSize, req.symbol, new DateTime(time * 1000), open, high, low, close, wap, (int) volume, count);
			if (isValidBar(req)) {
				for (BarListener listener : req.listeners) {
					try {
						listener.onBar(bar);
					} catch (Throwable t) {
						logger.error(t.getMessage(), t);
					}
				}
			}
			req.prevBar = bar;

		} catch (Throwable t) {
			// Do not allow exceptions come back to the socket -- it will cause
			// disconnects
			logger.error(t.getMessage(), t);
		}
	}

	@Override
	public void error(Exception e) {
		if (!connect && e.getMessage() != null && e.getMessage().startsWith("Socket closed")) {
			return;
		}
		logger.error(e.getMessage(), e);
	}

	@Override
	public void error(String error) {
		logger.error(error);
	}

	@Override
	public void error(int reqId, int errorCode, String errorMsg) {
		try {
			Object req = barRequestsById.get(reqId);
			if (req == null) {
				req = tickRequestsById.get(reqId);
			}
			if (req == null) {
				req = historicalRequestsById.get(reqId);
			}
			if (req == null) {
				req = scannerRequestsById.get(reqId);
			}
			if (req == null) {
				req = reqId;
			}
			String message = errorCode + ": " + errorMsg;
			lastMessage = message;

			if (req instanceof IBRequest) {
				((IBRequest) req).error = message;
			}

			switch (errorCode) {
			case 162:
				// Historical data request returned no data or pacing violation
				if (req instanceof IBHistoricalRequest) {
					logger.warn("Received error for request {}: {}", req, message);
					((IBHistoricalRequest) req).done = true;
				} else if (req instanceof IBScannerRequest) {
					logger.debug("Received error for request {}: {}", req, message);
					((IBScannerRequest) req).done = true;
				}
				break;
			case 200: // 200: bad contract
				logger.warn("Received error for request {}: {}", req, message);
				if (req instanceof IBHistoricalRequest) {
					((IBHistoricalRequest) req).failed = true;
				} else if (req instanceof IBScannerRequest) {
					((IBScannerRequest) req).done = true;
				}
				break;
			case 202: // 202: Cancelled order
				break;
			case 321: // 321: server error when validating an API client request
				logger.warn("Received error for request {}: {}", req, message);
				if (req instanceof IBHistoricalRequest) {
					((IBHistoricalRequest) req).failed = true;
				} else if (req instanceof IBScannerRequest) {
					((IBScannerRequest) req).done = true;
				}
				break;
			case 309: // 309: market depth requested for more than 3 symbols
				logger.warn("Received error for request {}: {}", req, message);
				// socket.cancelMktDepth(reqId);
				break;
			case 317: // 317: Market depth data has been reset
				logger.warn("Received error for request {}: {}", req, message);
				break;
			case 326:
				logger.info("Unable connect as the client id is already in use. Retry with a unique client id: {}", message);
				disconnect();
				break;
			case 399:
				logger.info("Received order message for request {}: {}", req, message);
				break;
			case 504:
				// Not connected
				logger.warn("Not connected: {}", message);
			case 1100:
				// Connectivity between IB and TWS has been lost.
				logger.warn("Connectivity between IB and TWS has been lost: {}", message);
				break;
			case 1101:
			case 1102:
			case 2100:
				logger.info("Received account message: {}", message);
				break;
			case 2104: // Market data farm connection is OK
			case 2108: // Market data farm connection is inactive but should be available upon demand
			case 2119: // Market data farm is connecting
				//logger.info("Connectivity: {}", message);
				break;
			default:
				logger.warn("Received error for request {}: {}", req, message);
			}
		} catch (Throwable t) {
			// Do not allow exceptions come back to the socket -- it will cause
			// disconnects
			logger.error(t.getMessage(), t);
		}
	}

	public String getMarketScannerParameters() {
		// if (logger.isDebugEnabled())
		// logger.debug(String.format("Requesting historical data: %s %s %s %s",
		// symbol, endDateStr, durationStr, barSizeSetting));
		marketScannerParams = null;
		socket.reqScannerParameters();

		try {
			while (marketScannerParams == null) {
				Thread.sleep(500);
			}
		} catch (InterruptedException e) {
		}
		return marketScannerParams;
	}

	@Override
	public void scannerParameters(String xml) {
		marketScannerParams = xml;
	}

	public List<Symbol> scanMarket(ScannerType type, ScannerInstrument instrument, ScannerLocation location) {
		return scanMarket(type, instrument, location, false);
	}

	public List<Symbol> scanMarket(ScannerType type, ScannerInstrument instrument, ScannerLocation location, boolean exhaustive) {
		List<Symbol> result = new ArrayList<Symbol>();
		if (!exhaustive) {
			IBScannerRequest data = requestScannerSubscription(type, instrument, location, Double.MAX_VALUE, Double.MAX_VALUE);
			result.addAll(data.symbols);
			return result;
		}
		double increment = 1.0;
		double toPrice = 0;
		double fromPrice = 0;
		double maxPrice = 100000;
		while (true) {
			fromPrice = toPrice;
			toPrice = fromPrice + increment;
			IBScannerRequest data = requestScannerSubscription(type, instrument, location, fromPrice, toPrice);
			if (data.symbols.size() >= 50) {
				toPrice = fromPrice;
				increment /= 2;
				continue;
			}
			result.addAll(data.symbols);
			if (toPrice > maxPrice) {
				break;
			}
			if (data.symbols.size() < 25) {
				increment *= 2;
			}
		}
		return result;
	}

	public IBScannerRequest requestScannerSubscription(ScannerType type, ScannerInstrument instrument, ScannerLocation location, double minPrice, double maxPrice) {
		checkConnected();
		if (logger.isDebugEnabled())
			logger.debug("requestScannerSubscription: {} {} {} {} {}", new Object[] { type, instrument, location, minPrice, maxPrice });

		ScannerSubscription params = new ScannerSubscription();
		// params.scanCode("MOST_ACTIVE");
		// params.instrument("STOCK.EU");
		// params.locationCode("STK.EU.SFB");
		// params.scanCode("TOP_PERC_GAIN");
		// params.instrument("IND.US");
		// params.locationCode("IND.US");
		params.scanCode(type.toString());
		params.instrument(instrument.toString());
		params.locationCode(location.toString());
		params.abovePrice(minPrice);
		params.belowPrice(maxPrice);
		params.aboveVolume(0);
		params.averageOptionVolumeAbove(0);
		params.marketCapAbove(0);
		params.marketCapBelow(1.0E100);
		params.stockTypeFilter("ALL");

		int requestId = nextValidReqId.incrementAndGet();
		IBScannerRequest req = new IBScannerRequest(requestId);
		scannerRequestsById.put(requestId, req);
		socket.reqScannerSubscription(requestId, params);
		long timeOut = System.currentTimeMillis() + historicalRequestTimeOutMillis;
		try {
			while (!req.done && timeOut > System.currentTimeMillis()) {
				Thread.sleep(500);
			}
		} catch (InterruptedException e) {
		}
		scannerRequestsById.remove(requestId);
		return req;
	}

	@Override
	public void scannerData(int reqId, int rank, ContractDetails details, String distance, String benchmark, String projection, String legsStr) {
		if (logger.isDebugEnabled())
			logger.debug("scannerData: {} {} {} {} {} {} {}", new Object[] { reqId, rank, Util.toString(details.m_summary), Util.toString(details),
					distance, benchmark, projection, legsStr });
		try {
			IBScannerRequest req = scannerRequestsById.get(reqId);
			req.symbols.add(toSymbol(details.m_summary, details));
		} catch (Throwable t) {
			// Do not allow exceptions come back to the socket -- it will cause
			// disconnects
			logger.error(t.getMessage(), t);
		}
	}

	@Override
	public void scannerDataEnd(int reqId) {
		if (logger.isDebugEnabled())
			logger.debug("scannerDataEnd: {}", reqId);
		try {
			socket.cancelScannerSubscription(reqId);

			// IBScannerRequest req = scannerRequestsById.get(reqId);
			// req.done = true;

		} catch (Throwable t) {
			// Do not allow exceptions come back to the socket -- it will cause
			// disconnects
			logger.error(t.getMessage(), t);
		}
	}

	public List<Symbol> findSymbols(String code, String exchange, String currency, SymbolType type) {
		checkConnected();
		Contract contract = makeContract(new Symbol(code, exchange, currency, type));

		int reqId = nextValidReqId.incrementAndGet();
		IBContractRequest req = new IBContractRequest(reqId);
		contractRequestsById.put(reqId, req);

		socket.reqContractDetails(reqId, contract);

		long timeOut = System.currentTimeMillis() + historicalRequestTimeOutMillis;
		try {
			while (!req.done && timeOut > System.currentTimeMillis()) {
				Thread.sleep(500);
			}
		} catch (InterruptedException e) {
		}
		contractRequestsById.remove(reqId);
		return req.symbols;
	}

	@Override
	public void contractDetails(int reqId, ContractDetails contractDetails) {
		try {
			if (logger.isDebugEnabled())
				logger.debug("contractDetails: {} {}", reqId, Util.toString(contractDetails));

			IBContractRequest req = contractRequestsById.get(reqId);
			if (req == null) {
				return;
			}
			req.symbols.add(toSymbol(contractDetails.m_summary, contractDetails));
		} catch (Throwable t) {
			logger.error(t.getMessage(), t);
		}
	}

	@Override
	public void contractDetailsEnd(int reqId) {
		try {
			if (logger.isDebugEnabled())
				logger.debug("contractDetailsEnd: {}", reqId);

			IBContractRequest req = contractRequestsById.get(reqId);
			if (req == null) {
				return;
			}
			req.done = true;

		} catch (Throwable t) {
			logger.error(t.getMessage(), t);
		}
	}

	class IBRequest {
		int id;
		Symbol symbol;
		String error;
		long timestamp;

		IBRequest(int id, Symbol symbol) {
			this.id = id;
			this.symbol = symbol;
			this.timestamp = System.currentTimeMillis();
		}

		@Override
		public int hashCode() {
			return id;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			return obj != null && obj instanceof IBRequest && id == ((IBRequest) obj).id;
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append(getClass().getSimpleName());
			sb.append(" [id=");
			sb.append(id);
			sb.append(", ");
			if (symbol != null) {
				sb.append("symbol=");
				sb.append(symbol);
			}
			sb.append(", ts=");
			sb.append(new DateTime(timestamp));
			sb.append("]");
			return sb.toString();
		}
	}

	class IBTickRequest extends IBRequest {
		boolean marketDepth;
		Tick prevTick;
		Tick currTick;
		Cleaner tickCleaner;
		List<TickListener> tickListeners;

		long barSizeMillis;
		Duration barSize;
		int barVolume;
		double lastPrice;
		Bar prevBar;
		Bar currBar;
		Cleaner barCleaner;
		List<BarListener> barListeners;

		public IBTickRequest(int id, Symbol symbol, boolean marketDepth, Cleaner cleaner) {
			super(id, symbol);
			this.marketDepth = marketDepth;
			this.tickListeners = new ArrayList<TickListener>(4);
			this.barListeners = new ArrayList<BarListener>(4);
			this.tickCleaner = cleaner;
		}

		public IBTickRequest(int id, Symbol symbol, int barSizeSeconds, Cleaner cleaner) {
			super(id, symbol);
			this.barSizeMillis = barSizeSeconds * 1000;
			this.barSize = new Duration(barSizeMillis);
			this.tickListeners = new ArrayList<TickListener>(4);
			this.barListeners = new ArrayList<BarListener>(4);
			this.barCleaner = cleaner;
		}

		public void setBarSize(int barSize) {
			this.barSizeMillis = barSize * 1000;
			this.barSize = new Duration(barSizeMillis);
		}
	}

	class IBBarRequest extends IBRequest {
		Bar prevBar;
		Bar currBar;
		List<BarListener> listeners;
		Cleaner cleaner;

		public IBBarRequest(int id, Symbol symbol) {
			super(id, symbol);
			this.listeners = new ArrayList<BarListener>();
		}
	}

	class IBHistoricalRequest extends IBRequest {
		boolean failed;
		boolean done;
		NavigableMap<DateTime, Bar> bars;
		Duration barSize;

		public IBHistoricalRequest(int id, Symbol symbol, Duration barSize) {
			super(id, symbol);
			this.bars = new TreeMap<DateTime, Bar>();
			this.barSize = barSize;
		}
	}

	class IBScannerRequest {
		int id;
		boolean done;
		List<Symbol> symbols;
		int lastSize;

		public IBScannerRequest(int id) {
			this.id = id;
			this.symbols = new ArrayList<Symbol>();
		}

		@Override
		public int hashCode() {
			return id;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			return obj != null && obj instanceof IBScannerRequest && id == ((IBScannerRequest) obj).id;
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append(getClass().getSimpleName());
			sb.append(" [id=");
			sb.append(id);
			sb.append("]");
			return sb.toString();
		}
	}

	class IBContractRequest {
		int id;
		boolean done;
		List<Symbol> symbols;
		int lastSize;

		public IBContractRequest(int id) {
			this.id = id;
			this.symbols = new ArrayList<Symbol>();
		}

		@Override
		public int hashCode() {
			return id;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			return obj != null && obj instanceof IBContractRequest && id == ((IBContractRequest) obj).id;
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append(getClass().getSimpleName());
			sb.append(" [id=");
			sb.append(id);
			sb.append("]");
			return sb.toString();
		}
	}

	@Override
	public void connectionClosed() {
	}

	@Override
	public void tickEFP(int tickerId, int tickType, double basisPoints, String formattedBasisPoints, double impliedFuture, int holdDays, String futureExpiry,
			double dividendImpact, double dividendsToExpiry) {
	}

	@Override
	public void orderStatus(int orderId, String status, int filled, int remaining, double avgFillPrice, int permId, int parentId, double lastFillPrice,
			int clientId, String whyHeld) {
	}

	@Override
	public void openOrder(int orderId, Contract contract, Order order, OrderState orderState) {
	}

	@Override
	public void openOrderEnd() {
	}

	@Override
	public void updateAccountValue(String key, String value, String currency, String accountName) {
	}

	@Override
	public void updatePortfolio(Contract contract, int position, double marketPrice, double marketValue, double averageCost, double unrealizedPNL,
			double realizedPNL, String accountName) {
	}

	@Override
	public void updateAccountTime(String timeStamp) {
	}

	@Override
	public void accountDownloadEnd(String accountName) {
	}

	@Override
	public void nextValidId(int orderId) {
	}

	@Override
	public void bondContractDetails(int reqId, ContractDetails contractDetails) {
	}

	@Override
	public void execDetails(int reqId, Contract contract, Execution execution) {
	}

	@Override
	public void execDetailsEnd(int reqId) {
	}

	@Override
	public void updateMktDepthL2(int tickerId, int position, String marketMaker, int operation, int side, double price, int size) {
	}

	@Override
	public void updateNewsBulletin(int msgId, int msgType, String message, String origExchange) {
	}

	@Override
	public void managedAccounts(String accountsList) {
	}

	@Override
	public void receiveFA(int faDataType, String xml) {
	}

	@Override
	public void currentTime(long time) {
	}

	@Override
	public void fundamentalData(int reqId, String data) {
	}

	@Override
	public void deltaNeutralValidation(int reqId, UnderComp underComp) {
	}

	public enum ScannerType {
		TOP_PERC_GAIN, TOP_PERC_LOSE, MOST_ACTIVE, MOST_ACTIVE_USD, MOST_ACTIVE_AVG_USD, HALTED, HIGH_DIVIDEND_YIELD_IB, TOP_TRADE_COUNT, TOP_TRADE_RATE
	}

	public enum ScannerInstrument {
		STK, STOCK_EU, STOCK_HK, IND_EU, IND_US, IND_HK, FUT_EU, FUT_US, FUT_HK;

		@Override
		public String toString() {
			return super.toString().replace('_', '.');
		}
	}

	public enum ScannerLocation {
		IND_US, IND_EU, IND_HK, STK_US, STK_US_MAJOR, STK_NYSE, STK_NASDAQ, STK_AMEX, STK_ARCA, FUT_US, FUT_GLOBEX, FUT_ECBOT, FUT_IPE, FUT_NYBOT, FUT_NYMEX, FUT_NYSELIFFE, STK_EU, STK_EU_AEB, STK_EU_IBIS, STK_EU_LSE, STK_EU_SBF, STK_EU_SFB, STK_EU_VIRTX, FUT_EU, FUT_EU_BELFOX, FUT_EU_DTB, FUT_EU_FTA, FUT_EU_IDEM, FUT_EU_LIFFE, FUT_EU_MEFFRV, FUT_EU_MONEP;

		@Override
		public String toString() {
			return super.toString().replace('_', '.');
		}
	}

	@Override
	public void tickOptionComputation(int tickerId, int field, double impliedVol, double delta, double optPrice, double pvDividend, double gamma, double vega,
			double theta, double undPrice) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void tickSnapshotEnd(int reqId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void marketDataType(int reqId, int marketDataType) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void commissionReport(CommissionReport commissionReport) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void position(String account, Contract contract, int pos, double avgCost) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void positionEnd() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void accountSummary(int reqId, String account, String tag, String value, String currency) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void accountSummaryEnd(int reqId) {
		// TODO Auto-generated method stub
		
	}
}
