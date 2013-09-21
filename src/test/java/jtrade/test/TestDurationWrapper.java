package jtrade.test;

import org.testng.IInvokedMethod;
import org.testng.IInvokedMethodListener;
import org.testng.ITestResult;

public class TestDurationWrapper implements IInvokedMethodListener {
	long start;

	@Override
	public void beforeInvocation(IInvokedMethod method, ITestResult testResult) {
		start = System.currentTimeMillis();
	}

	@Override
	public void afterInvocation(IInvokedMethod method, ITestResult testResult) {
		if (method.isTestMethod()) {
			System.out.printf("%s: %d ms\n", method.getTestMethod().getMethodName(), System.currentTimeMillis() - start);
		}
	}

}
