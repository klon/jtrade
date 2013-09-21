package jtrade.trader;

import org.joda.time.DateTime;

public interface ExecutionMethod {

	public void processExecution(Trader trader, DateTime dt);

}
