package jtrade;

public class JTradeException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public JTradeException() {
		super();
	}

	public JTradeException(String message, Throwable cause) {
		super(message, cause);
	}

	public JTradeException(String message) {
		super(message);
	}

	public JTradeException(Throwable cause) {
		super(cause);
	}
}
