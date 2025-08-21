import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import ilog.concert.IloException;

public class Main {
    /**
     * Main entry point:
     * - Reads input files (arc capacities, demand samples, location coordinates)
     * - Computes mean and standard deviation of demands
     * - Builds distance and constraint matrices
     * - Runs optimization models for multiple quantiles (z-values)
     * - Performs simulation to validate solutions
     * - Executes Exercise 6 (average demand) and Exercise 7 (sampled demand)
     * - Prints results
     */

    public static void main(String[] args) throws IloException, FileNotFoundException {
        Scanner s = new Scanner(new File("arc_capacities.txt"));
        HashMap<String, Double> ArcCap = new HashMap<String, Double>();

        while (s.hasNext()) {
            ArcCap.put(s.nextInt() + " " + s.nextInt(), s.nextDouble());
        }
        s.close();

        Scanner s2 = new Scanner(new File("demand_samples.txt"));
        int NumberOfLocations = 10;
        int NumberOfPoints = 1000;
        double[][] demand = new double[NumberOfLocations][NumberOfPoints];

        for (int i = 0; i < NumberOfPoints; i++) {
            for (int j = 0; j < NumberOfLocations; j++) {
                demand[j][i] = s2.nextDouble();
            }
        }
        s2.close();

        double[] mean = new double[NumberOfLocations];
        double[] stDev = new double[NumberOfLocations];
        for (int i = 0; i < NumberOfLocations; i++) {
            mean[i] = getMean(demand[i]);
            stDev[i] = getStDev(demand[i]);
        }

        Scanner s3 = new Scanner(new File("location_coordinates.txt"));
        double[][] Coordinates = new double[2][NumberOfPoints];

        for (int i = 0; i < NumberOfLocations; i++) {
            Coordinates[0][i] = s3.nextDouble();
            Coordinates[1][i] = s3.nextDouble();
        }
        double[][] distances = getEuclidean(Coordinates);
        s3.close();

        HashMap<String, double[][]> Amatrices = new HashMap<String, double[][]>();
        for (int i = 0; i < NumberOfLocations; i++) {
            int sizeA = NumberOfLocations * NumberOfLocations + NumberOfLocations;
            double[][] AT = new double[NumberOfLocations][sizeA];
            for (int j = 0; j < NumberOfLocations * NumberOfLocations + NumberOfLocations; j++) {
                if (i == j) {
                    AT[0][j] = 1.0;
                }
                if (j >= (i + 1) * NumberOfLocations && j <= (i + 1) * NumberOfLocations + NumberOfLocations - 1
                        && j != (i + 1) * NumberOfLocations + i) {
                    AT[0][j] = -1.0;
                }
                for (int k = 2; k < 11; k++) {
                    if (j == k * NumberOfLocations && i != k - 1) {
                        AT[0][j + i] = 1.0;
                    }
                }
            }
            Amatrices.put(i + "", AT);
        }

        // Exercise simulation OOS
        double[] zVal = { 1.644854, 1.281552, 1.036433, 0.8416212, 0.6744898, 0.5244005, 0.3853205, 0.2533471,
                0.1256613, 0, -0.1256613, -0.2533471, -0.3853205, -0.5244005, -0.6744898, -0.8416212, -1.036433,
                -1.281552, -1.644854 };
        List<Double> averages = new ArrayList<Double>();
        List<Double> originals = new ArrayList<Double>();
        for (double z : zVal) {
            Model model = new Model(ArcCap, mean, stDev, Amatrices, distances, z);
            model.solve();
            originals.add(model.getObjVal());
            HashMap<String, Double> xVal = model.getxValues();
            model.cleanup();
            int counter = 0;
            double sum = 0;
            while (counter < 100) {
                double[] sample = new double[NumberOfLocations];
                for (int j = 0; j < NumberOfLocations; j++) {
                    sample[j] = getRandomSample(mean[j], stDev[j]);
                }
                ModelSimulation modelSim = new ModelSimulation(ArcCap, Amatrices, distances, xVal, sample);
                modelSim.solve();
                double obj = modelSim.getObjVal();
                counter++;
                sum += obj;
                modelSim.cleanup();
            }
            averages.add(sum / counter);
        }

        // Exercise 6 + 7.
        double[] sample6 = new double[NumberOfLocations];
        int counter6 = 0;
        double[] sample7 = new double[NumberOfLocations];
        double obj7 = 0;
        int amount = 2;
        while (counter6 < amount) {
            sample7 = new double[NumberOfLocations];
            for (int j = 0; j < NumberOfLocations; j++) {
                double rand = getRandomSample(mean[j], stDev[j]);
                sample7[j] = rand;
                sample6[j] += rand;
            }
            ModelExercise7 model7 = new ModelExercise7(ArcCap, sample7, Amatrices, distances);
            model7.solve();
            obj7 += model7.getObjVal();
            System.out.print(model7.getObjVal());

            model7.cleanup();
            counter6++;
        }
        for (int j = 0; j < NumberOfLocations; j++) {
            sample6[j] = sample6[j] / amount;
        }
        ModelExercise6 model6 = new ModelExercise6(ArcCap, sample6, Amatrices, distances);
        model6.solve();
        double obj6 = model6.getObjVal();
        System.out.println(obj6);

        System.out.println("Values corresponding z: " + Arrays.toString(zVal));
        System.out.println("Exercise result on originals: " + originals);
        System.out.println("Exercise simulation: " + averages);
        System.out.println("Exercise 6: " + obj6);
        System.out.println("Exercise 7: " + obj7 / amount);
    }

    private static double[][] getEuclidean(double[][] coordinates) {
        double[][] distance = new double[coordinates[0].length][coordinates[0].length];
        for (int i = 0; i < coordinates[0].length; i++) {
            for (int j = 0; j < coordinates[0].length; j++) {
                distance[i][j] = Math
                        .sqrt((coordinates[0][i] - coordinates[0][j]) * (coordinates[0][i] - coordinates[0][j])
                                + (coordinates[1][i] - coordinates[1][j]) * (coordinates[1][i] - coordinates[1][j]));
                distance[j][i] = Math
                        .sqrt((coordinates[0][i] - coordinates[0][j]) * (coordinates[0][i] - coordinates[0][j])
                                + (coordinates[1][i] - coordinates[1][j]) * (coordinates[1][i] - coordinates[1][j]));
            }
        }
        return distance;
    }

    public static double getCovariance(double[] arr1, double[] arr2, int n) {
        double mean1 = getMean(arr1);
        double mean2 = getMean(arr2);
        double sum = 0;
        for (int i = 0; i < n; i++)
            sum = sum + (arr1[i] - mean1) * (arr2[i] - mean2);
        return sum / (n - 1);
    }

    public static double getStDev(double[] arr1) {
        double sum = 0.0;
        double sd = 0.0;
        for (double num : arr1) {
            sum += num;
        }
        int size = arr1.length;
        double mean = sum / size;

        for (double num : arr1) {
            sd += Math.pow(num - mean, 2);
        }
        double answer = Math.sqrt(sd / size);
        return answer;
    }

    public static double getMean(double[] arr1) {
        double sum1 = 0;
        for (int i = 0; i < arr1.length; i++) {
            sum1 = sum1 + arr1[i];
        }
        double mean1 = sum1 / arr1.length;
        return mean1;
    }

    public static double getRandomSample(double mean, double stDev) {
        Random ran = new Random();
        double bla = ran.nextGaussian();
        return bla * stDev + mean;
    }
}
