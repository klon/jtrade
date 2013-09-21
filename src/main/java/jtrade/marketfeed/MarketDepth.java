package jtrade.marketfeed;

public class MarketDepth {
	double[] askPrices;
	double[] bidPrices;
	int[] askSizes;
	int[] bidSizes;
	int askSizeSum;
	int bidSizeSum;
	double balance;
	double weightedMidPoint;

	public MarketDepth(int levels) {
		this.askPrices = new double[levels];
		this.bidPrices = new double[levels];
		this.askSizes = new int[levels];
		this.bidSizes = new int[levels];
		askSizeSum = -1;
		bidSizeSum = -1;
		balance = Double.NaN;
		weightedMidPoint = Double.NaN;
	}

	public MarketDepth(MarketDepth marketDepth) {
		this.askPrices = marketDepth.askPrices.clone();
		this.bidPrices = marketDepth.bidPrices.clone();
		this.askSizes = marketDepth.askSizes.clone();
		this.bidSizes = marketDepth.bidSizes.clone();
		balance = Double.NaN;
		weightedMidPoint = Double.NaN;
	}

	public double[] getAskPrices() {
		return askPrices;
	}

	public double[] getBidPrices() {
		return bidPrices;
	}

	public int[] getAskSizes() {
		return askSizes;
	}

	public int[] getBidSizes() {
		return bidSizes;
	}

	public double getMidPoint() {
		return (askPrices[0] + bidPrices[0]) / 2;
	}

	public double getBalance() {
		if (balance == balance) {
			return balance;
		}
		int cumAskSize = 0;
		int cumBidSize = 0;
		for (int i = 0; i < askSizes.length; i++) {
			cumAskSize += askSizes[i];
			cumBidSize += bidSizes[i];
		}
		int totalSize = cumAskSize + cumBidSize;
		if (totalSize != 0) {
			return 100.0 * (cumBidSize - cumAskSize) / totalSize;
		}
		return balance;
	}

	public double getBalance(int maxLevel) {
		int cumAskSize = 0;
		int cumBidSize = 0;
		for (int i = 0; i < maxLevel; i++) {
			cumAskSize += askSizes[i];
			cumBidSize += bidSizes[i];
		}
		int totalSize = cumAskSize + cumBidSize;
		if (totalSize != 0) {
			return 100.0 * (cumBidSize - cumAskSize) / totalSize;
		}
		return Double.NaN;
	}

	public double getWeightedBalance() {
		if (balance == balance) {
			return balance;
		}
		int cumAskSize = 0;
		int cumBidSize = 0;
		for (int i = 0, w = 1; i < askSizes.length; i++, w++) {
			cumAskSize += askSizes[i] / w;
			cumBidSize += bidSizes[i] / w;
		}
		int totalSize = cumAskSize + cumBidSize;
		if (totalSize != 0) {
			balance = 100.0 * (cumBidSize - cumAskSize) / totalSize;
		}
		return balance;
	}

	public int getLevels() {
		return askPrices.length;
	}

	public double getAskSizeSum() {
		if (askSizeSum > -1) {
			return askSizeSum;
		}
		for (int i = 0; i < askSizes.length; i++) {
			askSizeSum += askSizes[i];
			bidSizeSum += bidSizes[i];
		}
		return askSizeSum;
	}

	public double getBidSizeSum() {
		if (bidSizeSum > -1) {
			return bidSizeSum;
		}
		for (int i = 0; i < askSizes.length; i++) {
			askSizeSum += askSizes[i];
			bidSizeSum += bidSizes[i];
		}
		return bidSizeSum;
	}

	public double getWeightedAskPrice() {
		double weightedAsk = 0.0;
		int askSizeSum = 0;
		for (int i = 0; i < askSizes.length; i++) {
			weightedAsk += askPrices[i] * askSizes[i];
			askSizeSum += askSizes[i];
		}
		return weightedAsk / askSizeSum;
	}

	public double getWeightedBidPrice() {
		double weightedBid = 0.0;
		int bidSizeSum = 0;
		for (int i = 0; i < bidSizes.length; i++) {
			weightedBid += bidPrices[i] * bidSizes[i];
			bidSizeSum += bidSizes[i];
		}
		return weightedBid / bidSizeSum;
	}

	public double getWeightedMidPoint() {
		if (weightedMidPoint == weightedMidPoint) {
			return weightedMidPoint;
		}
		double weightedBid = 0.0;
		double weightedAsk = 0.0;
		int bidSizeSum = 0;
		int askSizeSum = 0;
		for (int i = 0; i < bidSizes.length; i++) {
			weightedBid += bidPrices[i] * bidSizes[i];
			bidSizeSum += bidSizes[i];
			weightedAsk += askPrices[i] * askSizes[i];
			askSizeSum += askSizes[i];
		}
		return weightedMidPoint = (weightedBid / bidSizeSum + weightedAsk / askSizeSum) / 2;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("MarketDepthImpl [");
		if (bidPrices != null) {
			sb.append("bids=[");
			for (int i = 0; i < bidPrices.length; i++) {
				sb.append(bidSizes[i]);
				sb.append('@');
				sb.append(bidPrices[i]);
				if (i + 1 < bidPrices.length) {
					sb.append(", ");
				}
			}
			sb.append(", ");
		}
		if (askPrices != null) {
			sb.append("asks=[");
			for (int i = 0; i < askPrices.length; i++) {
				sb.append(askSizes[i]);
				sb.append('@');
				sb.append(askPrices[i]);
				if (i + 1 < askPrices.length) {
					sb.append(", ");
				}
			}
			sb.append(", ");
		}
		sb.append(getBalance());
		sb.append(", weightedMidPoint=");
		sb.append(getWeightedMidPoint());
		sb.append("]");
		return sb.toString();
	}
}