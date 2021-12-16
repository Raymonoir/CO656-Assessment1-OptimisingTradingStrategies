import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Base {

    public static void main(String[] args) {

        int populationSize = 750;
        double mutationChance = 0.25;
        double crossoverChance = 0.75;
        double eliteProportion = 0.01;
        int tournamentK = 8;
        int generations = 350;
        double initalBudget = 3000;
        double initialPortfolio = 0;
        double[][] values = loadAllValues();

        for (int i = 0; i < 50; i++) {
            Genetics genetics = new Genetics(populationSize, mutationChance, crossoverChance, eliteProportion,
                    tournamentK, generations);

            genetics.runSimulation(initalBudget, initialPortfolio, values);
        }

    }

    // Calculates all indicators and signals using the provided prices file
    public static double[][] loadAllValues() {
        double[] prices = loadPrices("Unilever.txt");
        double[] sma12List = Trader.calculateAllSma(12, prices);
        double[] sma26List = Trader.calculateAllSma(26, prices);
        double[] ema12List = Trader.calculateAllEma(12, prices, 2);
        double[] ema26List = Trader.calculateAllEma(26, prices, 2);
        double[] mom25List = Trader.calculateAllMom(25, prices);
        double[] tbr24List = Trader.calculateAllTbr(24, prices);
        double[] vol29List = Trader.calculateAllVol(29, prices);
        double[] emaSignals = Trader.calculateAllEmaSignals(ema12List, ema26List);
        double[] tbrSignals = Trader.calculateAllTbrSignals(tbr24List);
        double[] volSignals = Trader.calculateAllVolSignals(vol29List);
        double[] momSignals = Trader.calculateAllMomSignals(mom25List);

        double[][] values = { prices, sma12List, sma26List, ema12List, ema26List, mom25List, tbr24List, vol29List,
                emaSignals, tbrSignals, volSignals, momSignals };

        return values;

    }

    // Saves the best results from a single simulation into provided file
    public static void saveResult(double fitness, double[] weights, int iteration, String filename) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(filename, true));
            writer.write(String.format("%-30s", Helpers.round(fitness, 3)));
            String weightsString = "";

            for (int i = 0; i < weights.length; i++) {
                weightsString += weights[i] + " ";
            }
            writer.write(String.format("%-30s", weightsString));
            writer.write(String.format("%-30s", iteration));
            writer.newLine();

            writer.close();
        } catch (IOException e) {

        }
    }

    // Loads all provided prices from provided file
    public static double[] loadPrices(String filename) {

        try {
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            ArrayList<Double> pricesArrayList = new ArrayList<>();
            String price;

            while ((price = reader.readLine()) != null) {
                pricesArrayList.add(Double.parseDouble(price));
            }

            double[] prices = new double[pricesArrayList.size()];
            for (int i = 0; i < pricesArrayList.size(); i++) {
                prices[i] = pricesArrayList.get(i);
            }

            reader.close();

            return prices;

        } catch (IOException e) {
            System.err.println("Unable to load prices from file: " + filename);
            return null;
        }

    }

    // Writes all calculated values along with price into specified file
    public static void writeAllValues(double[][] values, String filename) {

        String[] headings = { "closingPrices", "SMA12", "SMA26", "EMA12", "EMA26", "MOM25", "TBR24", "VOL29",
                "EMASignal", "TBRSignal", "VOLSignal", "MOMSignal" };

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(filename));

            for (int i = 0; i < headings.length; i++) {
                String result = String.format("%-15s", headings[i]);
                writer.write(result);
            }
            writer.newLine();

            for (int i = 0; i < values[0].length; i++) {
                for (int j = 0; j < headings.length; j++) {
                    String line = String.valueOf(values[j][i]);
                    line = line == "NaN" ? "N/A" : line;
                    String result = String.format("%-15s", line);
                    writer.write(result);
                }
                writer.newLine();
            }

            writer.close();
        } catch (IOException e) {

            System.err.println("Unable to write values to file: " + filename);
        } finally {

        }
    }

}
