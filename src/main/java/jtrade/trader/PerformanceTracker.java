package jtrade.trader;

import java.util.ArrayList;
import java.util.List;

import jtrade.Symbol;
import jtrade.marketfeed.MarketFeed;
import jtrade.timeseries.TimeSeries;
import jtrade.timeseries.TimeSeriesArray;
import jtrade.timeseries.TimeSeriesMap;
import jtrade.timeseries.TimeSeriesValuePair;
import jtrade.util.Configurable;
import jtrade.util.Util;

import org.joda.time.DateTime;

public class PerformanceTracker {
	public final Configurable<Integer> BUSINESS_DAYS_PER_YEAR = new Configurable<Integer>("BUSINESS_DAYS_PER_YEAR", 252);
	public final Configurable<Double> RISKFREE_RATE = new Configurable<Double>("RISKFREE_RATE", 0.0);

	MarketFeed marketFeed;
	DateTime start;
	DateTime end;
	TimeSeries portfolioByDate;
	List<Trade> trades;
	
	boolean needsUpdate;
	TimeSeries pnlByDate;
	TimeSeries returnsByDate;
	TimeSeries cumPnlByDate;
	TimeSeries cumReturnsByDate;
	int numTrades;
	double tradesPerDay;
	double profitLoss;
	double avgReturn;
	double stdReturn;
	double annualizedReturn;
	double performanceIndex;
	double sharpeRatio;
	double profitFactor;
	double maxDrawDown;
	double winRate;
	double longShort;
	double expectancy;


	public PerformanceTracker(MarketFeed marketFeed) {
		this.marketFeed = marketFeed;
		portfolioByDate = new TimeSeriesMap();
		trades = new ArrayList<Trade>(128);
		needsUpdate = true;
		pnlByDate = new TimeSeriesArray();
		returnsByDate = new TimeSeriesArray();
		cumPnlByDate = new TimeSeriesArray();
		cumReturnsByDate = new TimeSeriesArray();
		numTrades = 0;
		tradesPerDay = 0;
		profitLoss = Double.NaN;
		avgReturn = Double.NaN;
		stdReturn = Double.NaN;
		annualizedReturn = Double.NaN;
		performanceIndex = Double.NaN;
		sharpeRatio = Double.NaN;
		profitFactor = Double.NaN;
		maxDrawDown = Double.NaN;
		winRate = Double.NaN;
		longShort = Double.NaN;
		expectancy = Double.NaN;
	}

	public void updatePortfolio(DateTime date, Portfolio portfolio) {
		date = date.withTimeAtStartOfDay();
		if (start == null || date.isBefore(start)) {
			start = date;
		}
		if (end == null || date.isAfter(end)) {
			end = date;
		}
		portfolioByDate.set(date, portfolio.getPortfolioValue());
		needsUpdate = true;
	}
	
	public void updateTrades(DateTime date, Symbol symbol, int quantity, double costBasis, double price, double profitLoss) {
		trades.add(new Trade(date, symbol, quantity, costBasis, price, profitLoss));
		needsUpdate = true;
	}

	public void updateMetrics() {
		if (!needsUpdate || portfolioByDate.size() <= 1) {
			return;
		}
		pnlByDate = new TimeSeriesArray(portfolioByDate.diff(1));
		returnsByDate = portfolioByDate.arithReturn(1);
		cumPnlByDate = pnlByDate.cumsum();
		returnsByDate = returnsByDate.cumsum();
		double days = ((double)portfolioByDate.size());
		double annualRiskFreeRate = RISKFREE_RATE.get();
		double businessDaysPerYear = BUSINESS_DAYS_PER_YEAR.get();
		double profit = 0.0;
		double loss = 0.0;
		double cumProfitLoss = 0.0;
		double profitSquared = 0.0;
		double peak = Double.NEGATIVE_INFINITY;
		double mdd = Double.NEGATIVE_INFINITY;
		for (TimeSeriesValuePair vp : pnlByDate) {
			double pl = vp.getValue();
			if (pl >= 0) {
				profit += pl;
			} else {
				loss -= pl;
			}
			cumProfitLoss += pl;
			profitSquared += pl > 0 ? pl * pl : 0;
			if (cumProfitLoss != 0.0) {
				peak = Math.max(peak, cumProfitLoss);
				mdd = Math.max(mdd, peak - cumProfitLoss);
			}
		}
		int l = 0;
		int w = 0;
		for (Trade t : trades) {
			if (t.getQuantity() > 0) {
				l++;
			}
			if (t.getProfitLoss() >= 0) {
				w++;
			}
		}
		
		numTrades = trades.size();
		tradesPerDay = numTrades / days;
		profitLoss = cumProfitLoss;
		avgReturn = returnsByDate.mean();
		stdReturn = returnsByDate.std();
		annualizedReturn = ((portfolioByDate.last() - portfolioByDate.first()) / portfolioByDate.first()) / (days / businessDaysPerYear);
		performanceIndex = numTrades != 0 ? (Math.sqrt(numTrades) * (cumProfitLoss / numTrades)) / (Math.sqrt(numTrades * profitSquared - cumProfitLoss * cumProfitLoss) / numTrades) : Double.NaN;
		profitFactor = loss != 0.0 ? profit / loss : Double.POSITIVE_INFINITY;
		sharpeRatio = ((avgReturn - (annualRiskFreeRate / businessDaysPerYear)) / stdReturn) * Math.sqrt(businessDaysPerYear);
		maxDrawDown = -mdd;
		winRate = ((double)w) / numTrades;
		longShort = numTrades > 0 ? ((double)l) / numTrades : 1.0;
		expectancy = (winRate * (profit / w)) - ((1 - winRate) * (loss / (numTrades - w)));
		
		needsUpdate = false;
	}
	
	public MarketFeed getMarketFeed() {
		return marketFeed;
	}

	public DateTime getStart() {
		return start;
	}

	public DateTime getEnd() {
		return end;
	}

	public List<Trade> getTrades() {
		return trades;
	}

	public TimeSeries getPortfolioByDate() {
		updateMetrics();
		return portfolioByDate;
	}

	public TimeSeries getPnlByDate() {
		updateMetrics();
		return pnlByDate;
	}

	public TimeSeries getReturnsByDate() {
		updateMetrics();
		return returnsByDate;
	}

	public TimeSeries getCumPnlByDate() {
		updateMetrics();
		return cumPnlByDate;
	}

	public TimeSeries getCumReturnsByDate() {
		updateMetrics();
		return cumReturnsByDate;
	}

	public int getNumTrades() {
		updateMetrics();
		return numTrades;
	}
	
	public double getTradesPerDay() {
		updateMetrics();
		return tradesPerDay;
	}

	public double getProfitLoss() {
		updateMetrics();
		return profitLoss;
	}

	public double getAvgReturn() {
		updateMetrics();
		return avgReturn;
	}

	public double getStdReturn() {
		updateMetrics();
		return stdReturn;
	}

	public double getAnnualizedReturn() {
		updateMetrics();
		return annualizedReturn;
	}

	public double getPerformanceIndex() {
		updateMetrics();
		return performanceIndex;
	}

	public double getSharpeRatio() {
		updateMetrics();
		return sharpeRatio;
	}

	public double getProfitFactor() {
		updateMetrics();
		return profitFactor;
	}

	public double getMaxDrawDown() {
		updateMetrics();
		return maxDrawDown;
	}

	public double getWinRate() {
		updateMetrics();
		return winRate;
	}

	public double getLongShort() {
		updateMetrics();
		return longShort;
	}
	
	public double getExpectancy() {
		updateMetrics();
		return expectancy;
	}

	@Override
	public String toString() {
		updateMetrics();
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		sb.append("start=");
		sb.append(start != null ? start.toString("yyyy-MM-dd") : "");
		sb.append(", ");
		sb.append("end=");
		sb.append(end != null ? end.toString("yyyy-MM-dd") : "");
		sb.append(", trades=");
		sb.append(trades.size());
		sb.append(", trades/day=");
		sb.append(Util.round(tradesPerDay, 2));
		sb.append(", pnl=");
		sb.append(Util.round(profitLoss, 2));
		sb.append(", return/year=");
		sb.append(Util.round(annualizedReturn, 2));
		sb.append(", pi=");
		sb.append(Util.round(performanceIndex, 2));
		sb.append(", sr=");
		sb.append(Util.round(sharpeRatio, 2));
		sb.append(", pf=");
		sb.append(Util.round(profitFactor, 2));
		sb.append(", mdd=");
		sb.append(Util.round(maxDrawDown, 2));
		sb.append(", win ratio=");
		sb.append(Util.round(winRate, 2));
		sb.append(", long/short ratio=");
		sb.append(Util.round(longShort, 2));
		sb.append("]");
		return sb.toString();
	}

}
