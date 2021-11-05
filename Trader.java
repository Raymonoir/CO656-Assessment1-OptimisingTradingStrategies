public class Trader {

    public static double[] calculateAllSMA(int periodLength, double[] prices) {
        double[] smaValues = new double[prices.length];
        for (int i = 0; i < prices.length; i++) {
            if (i < periodLength) {
                smaValues[i] = Double.NaN;
            } else {
                smaValues[i] = Analysis.simpleMovingAverage(periodLength, i, prices);
            }
        }

        return smaValues;
    }

    public static double[] calculateAllEMA(int periodLength, double[] prices, double smoothing) {
        double[] emaValues = new double[prices.length];
        for (int i = 0; i < prices.length; i++) {
            if (i < periodLength) {
                emaValues[i] = Double.NaN;
            } else if (i == periodLength) {
                double smoothingFactor = smoothing / (1 + (double) periodLength);
                double part1 = prices[i] * smoothingFactor;
                double part2 = Analysis.simpleMovingAverage(periodLength, i, prices) * (1 - smoothingFactor);

                emaValues[i] = part1 + part2;
            } else {
                emaValues[i] = Analysis.exponentialMovingAverage(periodLength, i, prices, smoothing, emaValues);
            }
        }

        return emaValues;
    }

    public static double[] calculateAllMom(int periodLength, double[] prices) {
        double[] momValues = new double[prices.length];
        for (int i = 0; i < prices.length; i++) {
            if (i < periodLength) {
                momValues[i] = Double.NaN;
            } else {
                momValues[i] = Analysis.momentum(periodLength, i, prices);
            }
        }

        return momValues;
    }

    public static double[] calculateAllTbr(int periodLength, double[] prices) {
        double[] tbrValues = new double[prices.length];
        for (int i = 0; i < prices.length; i++) {
            if (i < periodLength) {
                tbrValues[i] = Double.NaN;
            } else {
                tbrValues[i] = Analysis.tradeBreakoutRule(periodLength, i, prices);
            }
        }

        return tbrValues;
    }

}
