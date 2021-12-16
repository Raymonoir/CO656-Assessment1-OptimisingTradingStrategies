public class Helpers {

    // Rounds a double to dp decimal places
    public static double round(double number, int dp) {
        double value = Double.parseDouble("1" + new String(new char[dp]).replace("\0", "0"));
        return (double) Math.round(number * value) / value;
    }

    // Returns the arithmetic mean of an array
    public static double getMean(double[] array) {
        double sum = 0;
        for (int i = 0; i < array.length; i++) {
            sum += array[i];
        }
        return sum / array.length;
    }

    // Returns standard deviation of an array
    public static double getStandardDeviation(double[] array) {
        double mean = getMean(array);
        double sumOfDiffSqrd = 0;

        for (int i = 0; i < array.length; i++) {
            sumOfDiffSqrd += Math.pow(array[i] - mean, 2);
        }

        return Math.sqrt(sumOfDiffSqrd / array.length);

    }

    // Retruns the index of the largest item in array
    public static int getMaxIndex(double[] array) {
        int maxIndex = 0;

        for (int i = 0; i < array.length; i++) {
            if (array[i] > array[maxIndex]) {
                maxIndex = i;
            }
        }
        return maxIndex;
    }

}
