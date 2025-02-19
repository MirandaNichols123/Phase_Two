import java.io.*;
import java.util.*;

public class Iris {
    public static void main(String[] args) throws IOException {
        String inputFile = "iris_bezdek.txt";
        String outputFile = "iris_bezdek_mod.txt";

        BufferedReader br = new BufferedReader(new FileReader(inputFile));
        List<String> lines = new ArrayList<>();
        String line;

        // Read all lines from the input file
        while ((line = br.readLine()) != null) {
            lines.add(line);
        }
        br.close();

        List<String> modifiedData = new ArrayList<>();

        // Copy every 5th data point 10 times
        for (int i = 0; i < lines.size(); i++) {
            modifiedData.add(lines.get(i));  // Add original data point
            if ((i + 1) % 5 == 0) {  // Every 5th point
                for (int j = 1; j < 10; j++) {
                    modifiedData.add(lines.get(i));  // Add duplicate
                }
            }
        }

        // Write the modified data to the output file
        PrintWriter writer = new PrintWriter(new FileWriter(outputFile));
        for (String modifiedLine : modifiedData) {
            writer.println(modifiedLine);
        }
        writer.close();
    }
}
