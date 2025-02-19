import java.io.*;
import java.util.*;
// command line: https://www.geeksforgeeks.org/command-line-arguments-in-java/
// parse through data: https://www.baeldung.com/reading-file-in-java
// K-means algorithm: https://www.baeldung.com/java-k-means-clustering-algorithm
// Squared Distance: https://www.reddit.com/r/math/comments/16jsz8/calculating_distance_without_using_sqrt/?rdt=63785
// F: name of file, K: # of clusters, I: max # of iterations,
// T: convergence threshold, R: # of runs
// changed N and D to numRuns and Dimensions

/*
    Yes, empty clusters can occur in KMeans intiliazed with random centroids. It can happen if random intilaization
    places centroids in regions of data where no points are assigned. There is a posisbility that some centroids may
    be placed in empty regions of dataset where no points are closer to those centroids than to others. If not data
    point is assigned to centroid in a run, then centroid is empty and the algoirthm ill continue to iterate without
    assigning points to it.
 */

public class Main {
    public static void main(String[] args) {
        //Check if length of args array is greater than 0
        System.out.println("Command-Line Arguments:");
        if (args.length > 0) {
            //iterate args array using loop
            for (String arg : args) {
                System.out.println(arg);
            }
        } else {
            System.out.println("No command-line arguments found.");
            System.exit(1);//exit if no arg is provided
        }
        //if not given 5 arg not given, output error message
        if (args.length != 5) {
            System.err.println("Usage: java Main <F> <K> <I> <T> <R>");
            System.exit(1);
        }

        try {
            String file = args[0];//dataset file path
            int K = Integer.parseInt(args[1]);
            int maxIterations = Integer.parseInt(args[2]);
            double convergenceThreshold = Double.parseDouble(args[3]);
            int numRuns = Integer.parseInt(args[4]);

            //number of clusters need to be greater than 1, max iteration has to be 1 or higher,
            //convergence threshold needs to be a non-neg, and number of runs need to be positive, if not error message
            if (K <= 1 || maxIterations <= 0 || convergenceThreshold < 0 || numRuns <= 0) {
                System.err.println("Error: Invalid arguments. Ensure K > 1, I > 0, T >= 0, and R > 0.");
                System.exit(1);
            }

            //runs K-mean
            KMeans kMeans = new KMeans(file, K, maxIterations, convergenceThreshold, numRuns);
            kMeans.runKMeans();
        } catch (IOException e) {
            System.err.println("Error: Unable to read the file.");
            e.printStackTrace();
        } catch (NumberFormatException e) {
            System.err.println("Error: Invalid number format in arguments.");
            e.printStackTrace();
        }

    }
}