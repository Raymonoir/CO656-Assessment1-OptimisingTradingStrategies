public class Trader {

    double budget;

    double portfolio;

    public Trader(double startingBudget, double initialPortfolio) {
        budget = startingBudget;
        portfolio = initialPortfolio;
    }

    // "closingPrices", "SMA12", "SMA26", "EMA12", "EMA26", "MOM25", "TBR24",
    // "VOL29", "EMASignal", "TBRSignal", "VOLSignal", "MOMSignal"
    // Carries out a trading session using the techincal indicators and signals in
    // values and a single weights individual in weights
    public double trade(double[][] values, double[] weights) {
        for (int i = 0; i < values[0].length; i++) {

            double currentPrice = values[0][i];
            double maxCount = weights[4];

            // only trade if all the signals are valid
            if (!(Double.isNaN(values[8][i]) || Double.isNaN(values[9][i]) || Double.isNaN(values[10][i])
                    || Double.isNaN(values[11][i]))) {
                double[] signals = { values[8][i], values[9][i], values[10][i], values[11][i] };

                int signal = overallSignal(signals, weights);
                // Only buy if sufficient budget
                if (signal == 1 && budget >= currentPrice * maxCount) {
                    portfolio += maxCount;
                    budget -= maxCount * currentPrice;

                    // Only sell if sufficient portfolio
                } else if (signal == 2 && portfolio >= maxCount) {
                    // SELL
                    double sellPrice = maxCount * currentPrice;
                    portfolio -= maxCount;
                    budget += sellPrice;

                } else {
                    // HODL!!!
                }
            }
        }
        budget += portfolio * values[0][values[0].length - 1];
        portfolio = 0;
        return budget;
    }

    // Get the overall buy/sell/signal weighted by their respective weights
    public static int overallSignal(double[] signals, double[] weights) {
        // Hold, Buy, Sell
        double[] count = { 0, 0, 0 };

        for (int i = 0; i < signals.length; i++) {
            if (signals[i] == 1) {
                count[1] += weights[i];
            } else if (signals[i] == 2) {
                count[2] += weights[i];
            } else {
                count[0] += weights[i];
            }
        }

        return Helpers.getMaxIndex(count);
    }

    // ########## CALCULATE TECHINCAL INDICATORS LIST ##########
    public static double[] calculateAllSma(int periodLength, double[] prices) {
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

    public static double[] calculateAllEma(int periodLength, double[] prices, double smoothing) {
        double[] emaValues = new double[prices.length];
        for (int i = 0; i < prices.length; i++) {
            if (i < periodLength) {
                emaValues[i] = Double.NaN;
            } else if (i == periodLength) {
                double smoothingFactor = smoothing / (1 + (double) periodLength);
                double part1 = prices[i] * smoothingFactor;
                double part2 = Analysis.simpleMovingAverage(periodLength, i, prices) * (1 - smoothingFactor);

                emaValues[i] = Helpers.round(part1 + part2, 6);
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

    public static double[] calculateAllVol(int periodLength, double[] prices) {
        double[] volValues = new double[prices.length];
        for (int i = 0; i < prices.length; i++) {
            if (i < periodLength) {
                volValues[i] = Double.NaN;
            } else {
                volValues[i] = Analysis.volatility(periodLength, i, prices);
            }
        }
        return volValues;
    }

    // ############################################################
    // ########## CALCULATE TRADING SIGNAL LIST ###################

    public static double[] calculateAllEmaSignals(double[] ema12List, double[] ema26List) {
        double[] signals = new double[ema12List.length];

        for (int i = 0; i < ema12List.length; i++) {
            if (Double.isNaN(ema12List[i]) || Double.isNaN(ema26List[i])) {
                signals[i] = Double.NaN;
            } else {
                signals[i] = Analysis.emaTradingSignal(ema12List[i], ema26List[i]);
            }
        }
        return signals;
    }

    public static double[] calculateAllTbrSignals(double[] tbr24List) {
        double[] signals = new double[tbr24List.length];

        for (int i = 0; i < tbr24List.length; i++) {
            if (Double.isNaN(tbr24List[i])) {
                signals[i] = Double.NaN;
            } else {
                signals[i] = Analysis.tbrTradingSignal(tbr24List[i]);
            }
        }
        return signals;
    }

    public static double[] calculateAllVolSignals(double[] vol29List) {
        double[] signals = new double[vol29List.length];

        for (int i = 0; i < vol29List.length; i++) {
            if (Double.isNaN(vol29List[i])) {
                signals[i] = Double.NaN;
            } else {
                signals[i] = Analysis.volTradingSignal(vol29List[i]);
            }
        }
        return signals;
    }

    public static double[] calculateAllMomSignals(double[] mom25List) {
        double[] signals = new double[mom25List.length];

        for (int i = 0; i < mom25List.length; i++) {
            if (Double.isNaN(mom25List[i])) {
                signals[i] = Double.NaN;
            } else {
                signals[i] = Analysis.momTradingSignal(mom25List[i]);
            }
        }
        return signals;
    }
    // ############################################################
}
