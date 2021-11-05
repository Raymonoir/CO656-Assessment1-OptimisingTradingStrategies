import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Base {

    public static void main(String[] args) {
        double[] prices;
        try {
            prices = loadPrices("Unilever.txt");
        } catch (IOException e) {
            prices = new double[1];
            System.out.println("Unable to load prices");
        }

        double[] allSMA = Trader.calculateAllTbr(10, prices);

        for (int i = 0; i < 14; i++) {
            System.out.println(allSMA[i]);
        }
    }

    public static double[] loadPrices(String fileName) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(fileName));
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
    }

}
