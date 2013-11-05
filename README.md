# jtrade
A framework for developing, backtesting and deploying automated trading strategies.

### Features
* Event based architecture supporting both bar and tick data.
* Backtesting engine with performance metrics
* Market data feed with support for streaming data from disk or Interactive Brokers
* Historical data download support for Yahoo and Interactive Brokers
* Order and account integration with Interactive Brokers
* TimeSeries classes inspired by Pandas

### License
Apache License (2.0)

### Requirements
jtrade needs JDK 7 and Ant/Ivy installed in order to build.

### Installation
Use `ant fetchdeps` to fetch dependencies using Ivy and then `ant jar` to build and `ant test` to run tests.
