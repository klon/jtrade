package jtrade;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

public class Symbol implements Comparable<Symbol> {

	public enum SymbolType {
		STOCK, FUTURE, INDEX, OPTION, CASH, CFD
	}

	public enum OptionRight {
		CALL, PUT
	}

	protected String code;
	protected String exchange;
	protected String currency;
	protected SymbolType type;
	protected DateTime expiry;
	protected OptionRight right;
	protected double strike;
	protected String fullCode;
	protected int multiplier;
	protected double minTick;
	private int hash;

	public Symbol(String code, String exchange, String currency, SymbolType type) {
		this(code, exchange, currency, type, null, 1, 0.0, null, 0.01);
	}

	public Symbol(String code, String exchange, String currency, String type) {
		this(code, exchange, currency, SymbolType.valueOf(type), null, 1, 0.0, null, 0.01);
	}

	public Symbol(String code, String exchange, String currency, String type, int multiplier, double minTick) {
		this(code, exchange, currency, SymbolType.valueOf(type), null, multiplier, 0.0, null, minTick);
	}

	public Symbol(String code, String exchange, String currency, DateTime expiry, int multiplier, double minTick) {
		this(code, exchange, currency, SymbolType.FUTURE, expiry, multiplier, 0.0, null, minTick);
	}

	public Symbol(String code, String exchange, String currency, SymbolType type, DateTime expiry, int multiplier, double strike, OptionRight right,
			double minTick) {
		this.code = code;
		this.exchange = exchange;
		this.currency = currency;
		this.type = type;
		this.expiry = expiry;
		this.multiplier = multiplier;
		this.strike = strike;
		this.right = right;
		this.minTick = minTick;

		StringBuilder sb = new StringBuilder();
		sb.append(code);
		if (exchange != null) {
			sb.append('-');
			sb.append(exchange);
		}
		if (currency != null) {
			sb.append('-');
			sb.append(currency);
		}
		if (type != null) {
			sb.append('-');
			sb.append(type);
		}
		if (expiry != null) {
			sb.append('-');
			sb.append(expiry.toString("yyyyMMdd"));
		}
		if (strike > 0) {
			sb.append('-');
			sb.append(strike);
			sb.append('-');
			sb.append(right);
		}
		fullCode = sb.toString();
		hash = fullCode.hashCode();
	}

	/**
	 * <code>-<exchange>-<currency>-<type>-<expiry>-<strike>-<right>
	 * 
	 * e.g. ES-GLOBEX-USD-OPTION-201012-1080-CALL
	 * 
	 * @parameter fullCode
	 */
	public Symbol(String fullCode) {
		try {
			this.multiplier = 1;

			String[] parts = fullCode.split("-");
			int i = 0;
			int j = 0;
			if (parts[1].equals(parts[2]) || parts[0].equals("TICK")) {
				this.code = parts[i++].concat("-").concat(parts[i++]);
				j++;
			} else {
				this.code = parts[i++];
			}
			this.exchange = parts[i++];
			if (parts.length > 2 + j) {
				this.currency = parts[i++];
			}
			if (parts.length > 3 + j) {
				this.type = SymbolType.valueOf(parts[i++]);
			}
			if (parts.length > 4 + j) {
				if (parts[4].length() <= 6) {
					this.expiry = DateTimeFormat.forPattern("yyyyMM").parseDateTime(parts[4]);
				} else {
					this.expiry = DateTimeFormat.forPattern("yyyyMMdd").parseDateTime(parts[4]);
				}
			}
			if (parts.length > 5 + j) {
				this.strike = Double.parseDouble(parts[5]);
				this.multiplier = 100; // We assume 100 for options
			}
			if (parts.length > 6 + j) {
				this.right = OptionRight.valueOf(parts[6]);
			}
			if (SymbolType.OPTION.equals(type) && strike <= 0) {
				throw new IllegalArgumentException("Symbol code does not contain strike: " + fullCode);
			}
			if (SymbolType.OPTION.equals(type) && right == null) {
				throw new IllegalArgumentException("Symbol code does not contain right: " + fullCode);
			}
			this.fullCode = fullCode;
			hash = fullCode.hashCode();
		} catch (Exception e) {
			throw new IllegalArgumentException("Cannot parse symbol code: " + fullCode, e);
		}
	}

	public String getCode() {
		return code;
	}

	public String getExchange() {
		return exchange;
	}

	public String getCurrency() {
		return currency;
	}

	public DateTime getExpiry() {
		return expiry;
	}

	public int getMultiplier() {
		return multiplier;
	}

	public double getStrike() {
		return strike;
	}

	public SymbolType getType() {
		return type;
	}

	public OptionRight getOptionRight() {
		return right;
	}

	public double getMinTick() {
		return minTick;
	}

	public boolean isUnknown() {
		return type == null;
	}

	public boolean isStock() {
		return type == SymbolType.STOCK;
	}

	public boolean isFuture() {
		return type == SymbolType.FUTURE;
	}

	public boolean isIndex() {
		return type == SymbolType.INDEX;
	}

	public boolean isOption() {
		return type == SymbolType.OPTION;
	}

	public boolean isCash() {
		return type == SymbolType.CASH;
	}

	public boolean isTickIndex() {
		return type == SymbolType.INDEX && code.startsWith("TICK");
	}

	public String getFullCode() {
		return fullCode;
	}

	@Override
	public int hashCode() {
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Symbol)) {
			return false;
		}
		Symbol other = (Symbol) obj;
		if (hash != other.hash) {
			return false;
		}
		return fullCode.equals(other.fullCode);
	}

	@Override
	public String toString() {
		return fullCode;
	}

	@Override
	public int compareTo(Symbol other) {
		int c = exchange.compareTo(other.exchange);
		if (c != 0) {
			return c;
		}
		return fullCode.compareTo(other.fullCode);
	}
}
