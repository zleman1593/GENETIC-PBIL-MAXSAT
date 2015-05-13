package ACO;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class GenerateGraphData {
	// Results where the problems are sorted by the number of literals, in increasing order.
	private HashMap<String, HashMap<String, String>> results;
	// Folder path.
	String folderPath = AnalyzeResultsController.folderPath;
	// Current graph file number.
	int graphIndex = 1; 
	// Whether we are graphing for parameters.
	boolean isParameterGraph;
	String parameterName;
	String parameterVal;
	
	/* Each ArrayList is a list of values (from all problems) for x-axis or
	 * y-axis of one graph. Indices correspond to the same file: for example,
	 * avgPercentage at index 2 and numLiterals at index 2 are data
	 * extracted from the same file.
	 */
	ArrayList<String> avgPathLengthPercentage = new ArrayList<String>();

	public GenerateGraphData(HashMap<String, HashMap<String, String>> results, 
			String algorithm, boolean isParameterGraph, String parameterName,
			String parameterVal) throws IOException {
		this.results = results;
		this.isParameterGraph = isParameterGraph;
		this.parameterName = parameterName;
		this.parameterVal = parameterVal;

		// x-axis is the number of literals.
		initializeArrayList(algorithm);
		writeGraphData_sortedByLiterals(algorithm);
	}

	private void initializeArrayList(String algorithm) {		
		// Populate the values in ArrayLists.
		for (String problem : results.keySet()) {
			HashMap<String, String> resultsValues = results.get(problem);
			String length = resultsValues.get(AnalyzeResults.AVG_LENGTH);
			
			double avgLength = 0.0;
			if (length != null) {
				avgLength =  Double.parseDouble(length);
			}
			
			double percent = 0;
			if (problem.equalsIgnoreCase("d2103")) {
				percent = avgLength / 80450;
			} else if (problem.equalsIgnoreCase("eil76")) {
				percent = avgLength / 538;
			}
			
			avgPathLengthPercentage.add(String.valueOf(percent));
		}
	}
	
	// Graph data to write to files, with problems sorted by number of literals.
	private void writeGraphData_sortedByLiterals(String algorithm) throws IOException {
		if (isParameterGraph) {
			writeToFile(algorithm, parameterVal, new ArrayList<String>(results.keySet()), 
					parameterName, avgPathLengthPercentage);
		} 
	}

	// Write the specified data to the specified file.
	private void writeToFile(String algorithm, String x_name, ArrayList<String> x_data, 
			String y_name, ArrayList<String> y_data)
			throws IOException {
		File file = new File(getFilePath(y_name + ", " + x_name + ", " + algorithm));
		if (!file.exists()) {
			file.createNewFile();
		}
		try {
			BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));
			bufferedWriter.write(algorithm);
			bufferedWriter.newLine();
			// Data for x-axis.
			bufferedWriter.write(x_name);
			bufferedWriter.newLine();
			for (String data : x_data) { 
				if (data != null) {
					bufferedWriter.write(data);
					bufferedWriter.newLine();
				}
			}
			bufferedWriter.newLine();
			
			// Data for y-axis.
			bufferedWriter.write(y_name);
			bufferedWriter.newLine();
			for (String data : y_data) {
				if (data != null) {
					bufferedWriter.write(data);
					bufferedWriter.newLine();
				}
			}
			
			graphIndex++;
			bufferedWriter.flush();
			bufferedWriter.close();
		} 
		catch (FileNotFoundException e) {
			System.out.println("Unable to open or create file '" + file.getName() + "'");
		}
	}

	// Helper method: Return path to specified file that ends with n.
	private String getFilePath(String name) {
		return folderPath + "/" + name + ".txt";
	}
}