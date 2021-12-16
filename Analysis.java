public class Analysis {
  // ########## TECHNICAL INDICATORS ##########

  public static double simpleMovingAverage(int periodLength, int currentTime, double[] prices) {
    double sum = 0;
    for (int i = 1; i <= periodLength; i++) {
      sum += prices[currentTime - i];
    }
    return Helpers.round(sum / periodLength, 6);
  }

  public static double momentum(int periodLength, int currentTime, double[] prices) {
    return Helpers.round(prices[currentTime] - prices[currentTime - periodLength], 6);
  }

  public static double exponentialMovingAverage(int periodLength, int currentTime, double[] prices, double smoothing,
      double[] emaValues) {

    double smoothingFactor = smoothing / (1 + (double) periodLength);
    double part1 = prices[currentTime] * smoothingFactor;
    double part2 = emaValues[currentTime - 1] * (1 - smoothingFactor);

    return Helpers.round(part1 + part2, 6);
  }

  public static double tradeBreakoutRule(int periodLength, int currentTime, double[] prices) {
    double[] periodPrices = new double[periodLength];

    for (int i = 1; i <= periodLength; i++) {
      periodPrices[i - 1] = prices[currentTime - i];
    }

    double maxPrice = periodPrices[Helpers.getMaxIndex(periodPrices)];
    return Helpers.round((prices[currentTime] - maxPrice) / (maxPrice), 6);

  }

  public static double volatility(int periodLength, int currentTime, double[] prices) {
    double[] periodPrices = new double[periodLength];

    for (int i = 1; i <= periodLength; i++) {
      periodPrices[i - 1] = prices[currentTime - i];
    }

    double standardDev = Helpers.getStandardDeviation(periodPrices);
    double sma = simpleMovingAverage(periodLength, currentTime, prices);

    return Helpers.round(standardDev / sma, 6);
  }

  // #####################################
  // ########## TRADING SIGNALS ##########

  public static int emaTradingSignal(double ema12, double ema26) {
    if (ema12 > ema26) {
      return 1;
    } else if (ema12 < ema26) {
      return 2;
    } else {
      return 0;
    }
  }

  public static int tbrTradingSignal(double tbr24) {
    if (tbr24 > -0.02) {
      return 1;
    } else if (tbr24 < -0.02) {
      return 2;
    } else {
      return 0;
    }
  }

  public static int volTradingSignal(double vol29) {
    if (vol29 > 0.02) {
      return 1;
    } else if (vol29 < 0.02) {
      return 2;
    } else {
      return 0;
    }
  }

  public static int momTradingSignal(double mom25) {
    if (mom25 > 0) {
      return 1;
    } else if (mom25 < 0) {
      return 2;
    } else {
      return 0;
    }
  }

  // ####################################
}