import java.io.*;
import java.util.*;

public class KMeans
{
    private int numClusters; //number of clusters
    private int maxIterations; //max number of iterations
    private double threshold; //convergence threshold
    private int numRuns; // number of runs to determine best cluster
    private double[][] data; // data points
    private int numPoints; // number of data points
    private int dimension; // number of dim
    private Random random; // random number generator for centroids
    private String inputFileName; // input file name

    public KMeans(String fileName, int k, int maxIters, double t, int r) throws IOException
    {
        //setters
        this.numClusters = k;
        this.maxIterations = maxIters;
        this.threshold = t;
        this.numRuns = r;
        this.random = new Random();
        this.inputFileName = fileName;
        loadData(fileName); //load data from file
    }

    //load data from input file
    private void loadData(String fileName) throws IOException
    {
        BufferedReader br = new BufferedReader(new FileReader(fileName));
        //read the first line of data set, (\\s+ means one or more whitespaces)
        String[] firstLine = br.readLine().split("\\s+");
        numPoints = Integer.parseInt(firstLine[0]); //number of points
        dimension = Integer.parseInt(firstLine[1]); //dimensionality of each point
        //turns int into a double, into an array
        data = new double[numPoints][dimension];

        //read all other data points
        for (int i = 0; i < numPoints; i++)
        {
            String[] pointStr = br.readLine().split("\\s+");
            for (int j = 0; j < dimension; j++)
            {
                //store data points
                data[i][j] = Double.parseDouble(pointStr[j]);
            }
        }
        //closes file reader
        br.close();
    }

    //runs KMeans algorithm multiple times and finds best cluster and gives the output of algorithm
    public void runKMeans()
    {
        double bestSSE = Double.MAX_VALUE; //initialize the best SSE to a large value
        int bestRun = -1; //best run initialize to -1
        String outputFileName = inputFileName.substring(0, inputFileName.lastIndexOf('.')) + "_output.txt";//output file name

        try (PrintWriter writer = new PrintWriter(outputFileName))
        {
            writer.println("Running K-Means on: " + inputFileName); //write start message to output file
            for (int run = 1; run <= numRuns; run++) //runs KMeans for a number of runs
            {
                //displays run number and runs
                writer.println("\nRun " + run + " -----");
                System.out.println("\nRun " + run + " -----");
                double sse = executeKMeans(writer); //gets SSE
                //checks to see SSE runs and updates bestSSE and bestRun
                if (sse < bestSSE)
                {
                    bestSSE = sse;
                    bestRun = run;
                }
            }
            //prints best run
            System.out.println("\nBest Run: " + bestRun + ": SSE = " + bestSSE);
            writer.println("\nBest Run: " + bestRun + ": SSE = " + bestSSE);
        }
        //file write error
        catch (IOException e)
        {
            System.err.println("Error writing to file: " + e.getMessage());
        }
    }

    //execute KMeans algorithm for single run
    private double executeKMeans(PrintWriter writer)
    {
        double[][] centroids = initializeCentroids();
        double prevSSE = Double.POSITIVE_INFINITY;
        double sse = 0;
        //array to store point assignments to clusters
        int[] assignments = new int[numPoints];

        //iterate through specified number of iterations
        for (int iter = 1; iter <= maxIterations; iter++)
        {
            //assign points to nearest centroid and calculate SSE
            sse = assignPointsToClusters(centroids, assignments);

            //write SSE for each iteration to the file and console
            writer.println("Iteration " + iter + ": SSE = " + sse);
            System.out.println("Iteration " + iter + ": SSE = " + sse);

            //check for convergence, if change in SSE is smaller than  threshold
            if ((prevSSE - sse) / prevSSE < threshold)
            {
                writer.println("Converged on iteration " + iter);
                break;
            }

            //updates previous SSE
            prevSSE = sse;
            updateCentroids(centroids, assignments); // updates centroids based on point assignments
        }
        //return final SSE for this run
        return sse;
    }

    private double[][] initializeCentroids()
    {
        double[][] centroids = new double[numClusters][dimension]; // array stores centroids
        Set<Integer> chosenIndices = new HashSet<>(); // stores indices of chosen centroids

        //randomly selects unique points as centroids
        for (int i = 0; i < numClusters; i++)
        {
            int index;
            do
            {
                index = random.nextInt(numPoints); // select random index
            }
            while (chosenIndices.contains(index));
            //added index to set chosen centroids
            chosenIndices.add(index);
            //set centroids to chosen point
            centroids[i] = Arrays.copyOf(data[index], dimension);
        }
        return centroids;
    }

    //assigns each data point to the nearest cluster and calculate SSE
    private double assignPointsToClusters(double[][] centroids, int[] assignments)
    {
        double sse = 0;

        //iterate through all data points
        for (int i = 0; i < numPoints; i++)
        {
            double minDist = Double.MAX_VALUE; // initialize min distance to large value
            int bestCluster = 0;

            //iterate through all centroids to find the nearest
            for (int j = 0; j < numClusters; j++)
            {
                //calculate squared distance to centroid
                double dist = squaredDistance(data[i], centroids[j]);
                //updates to best cluster
                if (dist < minDist)
                {
                    minDist = dist;
                    bestCluster = j;
                }
            }
            //assigns point to best cluster and add distance to SSE
            assignments[i] = bestCluster;
            sse += minDist;
        }
        return sse;
    }

    //update centroids by recalcualting the mean of points assigned to each cluster
    private void updateCentroids(double[][] centroids, int[] assignments)
    {
        double[][] newCentroids = new double[numClusters][dimension]; // array stores new centroids
        int[] clusterSizes = new int[numClusters]; // array to store size of each cluster

        //iterate through all data points to sum up coordinates for each cluster
        for (int i = 0; i < numPoints; i++)
        {
            int cluster = assignments[i];
            for (int j = 0; j < dimension; j++)
            {
                newCentroids[cluster][j] += data[i][j]; // sum up coordinates for cluster
            }
            clusterSizes[cluster]++; // increment size of cluster
        }
        //calculate mean for each cluster
        for (int i = 0; i < numClusters; i++)
        {
            //avoids division by 0
            if (clusterSizes[i] > 0)
            {
                for (int j = 0; j < dimension; j++)
                {
                    centroids[i][j] = newCentroids[i][j] / clusterSizes[i]; // calculates means and updates centroids
                }
            }
        }
    }

    //calculate sqaured Euclidean distance between 2 points
    private double squaredDistance(double[] point, double[] centroid)
    {
        double sum = 0;
        for (int i = 0; i < dimension; i++)
        {
            double diff = point[i] - centroid[i]; // calculate difference between coordinates
            sum += diff * diff; // add squared difference to sum
        }
        return sum;
    }
}
