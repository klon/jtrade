package jtrade.trader;


public class Commission {
	double commissionPerShare;
	double commissionRate;
	double maxCommissionRate;
	double minCommissionOrder;
	double maxCommissionOrder;

	public Commission(double commissionPerShare, double commissionRate, double maxCommissionRate, double minCommissionOrder, double maxCommissionOrder) {
		this.commissionPerShare = commissionPerShare;
		this.commissionRate = commissionRate;
		this.maxCommissionRate = maxCommissionRate;
		this.minCommissionOrder = minCommissionOrder;
		this.maxCommissionOrder = maxCommissionOrder;
	}
	
	public double calculate(int quantity, double price) {
		double sum = 0.0;
		if (commissionPerShare > 0.0) {
			sum += commissionPerShare * quantity;
		}
		if (commissionRate > 0.0) {
			sum += commissionRate * quantity * price;
		}
		if (maxCommissionRate > 0.0 && sum / (quantity * price) > maxCommissionRate) {
			sum = maxCommissionRate * quantity * price;
		}
		if (maxCommissionOrder > 0.0 && sum > maxCommissionOrder) {
			sum = maxCommissionOrder;
		}
		if (minCommissionOrder > 0.0 && sum < minCommissionOrder) {
			sum = minCommissionOrder;
		}
		return sum;
	}

	public double getCommissionPerShare() {
		return commissionPerShare;
	}

	public double getMinCommissionOrder() {
		return minCommissionOrder;
	}

	public double getCommissionRate() {
		return commissionRate;
	}

	public double getMaxCommissionOrder() {
		return maxCommissionOrder;
	}

	public double getMaxCommissionRate() {
		return maxCommissionRate;
	}

}
