public class Analysis {

  public static double getMean(double[] prices) {
    double sum = 0;
    for (int i = 0; i < prices.length; i++) {
      sum += prices[i];
    }
    return sum / prices.length;
  }

  public static double getStandardDeviation(double[] prices) {
    double mean = getMean(prices);
    double sumOfDiffSqrd = 0;

    for (int i = 0; i < prices.length; i++) {
      sumOfDiffSqrd += Math.pow(prices[i] - mean, 2);
    }

    return Math.sqrt(sumOfDiffSqrd / prices.length);

  }

  public static int getMaxIndex(double[] prices) {
    int maxIndex = 0;

    for (int i = 0; i < prices.length; i++) {
      if (prices[i] > prices[maxIndex]) {
        maxIndex = i;
      }
    }
    return maxIndex;
  }

  public static double simpleMovingAverage(int periodLength, int currentTime, double[] prices) {
    double sum = 0;
    for (int i = 1; i <= periodLength; i++) {
      sum += prices[currentTime - i];
    }
    return sum / periodLength;
  }

  public static double momentum(int periodLength, int currentTime, double[] prices) {
    return prices[currentTime] - prices[currentTime - periodLength];
  }

  public static double exponentialMovingAverage(int periodLength, int currentTime, double[] prices, double smoothing,
      double[] emaValues) {

    double smoothingFactor = smoothing / (1 + (double) periodLength);
    double part1 = prices[currentTime] * smoothingFactor;
    double part2 = emaValues[currentTime - 1] * (1 - smoothingFactor);

    return part1 + part2;
  }

  public static double tradeBreakoutRule(int periodLength, int currentTime, double[] prices) {
    double[] periodPrices = new double[periodLength];

    for (int i = 1; i <= periodLength; i++) {
      periodPrices[i - 1] = prices[currentTime - i];
    }

    double maxPrice = periodPrices[getMaxIndex(periodPrices)];
    return (prices[currentTime] - maxPrice) / (maxPrice);

  }

  public static double volatility(int periodLength, int currentTime, double[] prices) {
    double[] periodPrices = new double[periodLength];

    for (int i = 1; i <= periodLength; i++) {
      periodPrices[i - 1] = prices[currentTime - i];
    }

    double standardDev = getStandardDeviation(periodPrices);
    double sma = simpleMovingAverage(periodLength, currentTime, prices);

    return standardDev / sma;
  }
}