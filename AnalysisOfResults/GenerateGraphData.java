package AnalysisOfResults;
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
	String folderPath = "Graph_Data";
	// Current graph file number.
	int graphIndex = 1; 
	// Whether we are graphing for parameters.
	boolean isParameterGraph;
	String parameterName;
	String parameterVal;
	
	/*
	 * Each ArrayList is a list of values (from all problems) for x-axis or
	 * y-axis of one graph. Indices correspond to the same file: for example,
	 * avgPercentage at index 2 and numLiterals at index 2 are data
	 * extracted from the same file.
	 */
	ArrayList<String> numLiterals = new ArrayList<String>();
	ArrayList<String> numClauses = new ArrayList<String>();
	ArrayList<String> avgNumTimeOuts = new ArrayList<String>();
	ArrayList<String> bestExecutionTime = new ArrayList<String>();
	ArrayList<String> avgExecutionTime = new ArrayList<String>();
	ArrayList<String> bestGeneration = new ArrayList<String>();
	ArrayList<String> bestGeneration_TimeOut = new ArrayList<String>();
	ArrayList<String> avgBestGeneration = new ArrayList<String>();
	ArrayList<String> avgBestGeneration_TimeOut = new ArrayList<String>();
	ArrayList<String> bestPercentage = new ArrayList<String>();
	ArrayList<String> bestPercentage_TimeOut = new ArrayList<String>();
	ArrayList<String> avgPercentage = new ArrayList<String>();
	ArrayList<String> avgPercentage_TimeOut = new ArrayList<String>();

	public GenerateGraphData(HashMap<String, HashMap<String, String>> results, 
			String algorithm, boolean isParameterGraph, String parameterName,
			String parameterVal) throws IOException {
		this.results = results;
		this.isParameterGraph = isParameterGraph;
		this.parameterName = parameterName;
		this.parameterVal = parameterVal;

		// x-axis is the number of literals.
		initializeArrayList(algorithm, AnalyzeResults.NUM_LITERALS);
		writeGraphData_sortedByLiterals(algorithm);

		if (!isParameterGraph) {
			// x-axis is the number of clauses.		
			initializeArrayList(algorithm, AnalyzeResults.NUM_CLAUSES);
			writeGraphData_sortedByClauses(algorithm);
		}
	}

	private void initializeArrayList(String algorithm, String sortedKey) {
		// Clear the ArrayLists.
		clearArrayLists();
		
		// Populate the values in ArrayLists.
		for (String problem : results.keySet()) {
			HashMap<String, String> resultsValues = results.get(problem);
			
			String numLiterals_val = resultsValues.get(AnalyzeResults.NUM_LITERALS);
			String numClauses_val = resultsValues.get(AnalyzeResults.NUM_CLAUSES);
			String avgNumTimeOuts_val = resultsValues.get(AnalyzeResults.AVG_NUM_TIMEOUTS);
			String bestExecutionTime_val = resultsValues.get(AnalyzeResults.BEST_EXECUTION_TIME);
			String avgExecutionTime_val = resultsValues.get(AnalyzeResults.AVG_EXECUTION_TIME);
			String bestGeneration_val = resultsValues.get(AnalyzeResults.BEST_GENERATION);
			String bestGeneration_TimeOut_val = resultsValues.get(AnalyzeResults.BEST_GENERATION_TIMEOUT);
			String avgBestGeneration_val = resultsValues.get(AnalyzeResults.AVG_BEST_GENERATION);
			String avgBestGeneration_TimeOut_val = resultsValues.get(AnalyzeResults.AVG_BEST_GENERATION_TIMEOUT);
			String bestPercentage_val = resultsValues.get(AnalyzeResults.BEST_PERCENTAGE);
			String bestPercentage_TimeOut_val = resultsValues.get(AnalyzeResults.BEST_PERCENTAGE_TIMEOUT);
			String avgPercentage_val = resultsValues.get(AnalyzeResults.AVG_PERCENTAGE);
			String avgPercentage_TimeOut_val = resultsValues.get(AnalyzeResults.AVG_PERCENTAGE_TIMEOUT);
			
			numLiterals.add(numLiterals_val);
			numClauses.add(numClauses_val);
			avgNumTimeOuts.add(avgNumTimeOuts_val);
			bestExecutionTime.add(bestExecutionTime_val);
			avgExecutionTime.add(avgExecutionTime_val);
			bestGeneration.add(bestGeneration_val);
			bestGeneration_TimeOut.add(bestGeneration_TimeOut_val);
			avgBestGeneration.add(avgBestGeneration_val);
			avgBestGeneration_TimeOut.add(avgBestGeneration_TimeOut_val);
			bestPercentage.add(bestPercentage_val);
			bestPercentage_TimeOut.add(bestPercentage_TimeOut_val);
			avgPercentage.add(avgPercentage_val);
			avgPercentage_TimeOut.add(avgPercentage_TimeOut_val);
		}
	}
	
	// Clear ArrayList entries.
	private void clearArrayLists() {
		numLiterals.clear();
		numClauses.clear();
		avgNumTimeOuts.clear();
		bestExecutionTime.clear();
		avgExecutionTime.clear();
		bestGeneration.clear();
		bestGeneration_TimeOut.clear();
		avgBestGeneration.clear();
		avgBestGeneration_TimeOut.clear();
		bestPercentage.clear();
		bestPercentage_TimeOut.clear();
		avgPercentage.clear();
		avgPercentage_TimeOut.clear();
	}
	
	// Graph data to write to files, with problems sorted by number of literals.
	private void writeGraphData_sortedByLiterals(String algorithm) throws IOException {
		if (isParameterGraph) {
			writeToFile(algorithm, parameterName, numLiterals, 
					parameterVal, avgPercentage_TimeOut);
		} else {
			/* Percentage-related, for non-timed-out trials */
			// Number of literals vs.best percentage solved. 
			writeToFile(algorithm, AnalyzeResults.NUM_LITERALS, numLiterals, 
					AnalyzeResults.BEST_PERCENTAGE, bestPercentage);
			// Number of literals vs.average percentage solved.
			writeToFile(algorithm, AnalyzeResults.NUM_LITERALS, numLiterals, 
					AnalyzeResults.AVG_PERCENTAGE, avgPercentage);
			
			/* Percentage-related, for timed-out trials */
			// Number of literals vs.best percentage solved. 
			writeToFile(algorithm, AnalyzeResults.NUM_LITERALS, numLiterals, 
					AnalyzeResults.BEST_PERCENTAGE_TIMEOUT, bestPercentage_TimeOut);
			// Number of literals vs.average percentage solved.
			writeToFile(algorithm, AnalyzeResults.NUM_LITERALS, numLiterals, 
					AnalyzeResults.AVG_PERCENTAGE_TIMEOUT, avgPercentage_TimeOut);
			
			/* Best generation-related for non timed-out trials */
			// Number of literals vs. best generation.
			writeToFile(algorithm, AnalyzeResults.NUM_LITERALS, numLiterals, 
					AnalyzeResults.BEST_GENERATION, bestGeneration);
			// Number of literals vs. average best generation.
			writeToFile(algorithm, AnalyzeResults.NUM_LITERALS, numLiterals, 
					AnalyzeResults.AVG_BEST_GENERATION, avgBestGeneration);
		
			/* Best generation-related for timed-out trials */
			// Number of literals vs. best generation.
			writeToFile(algorithm, AnalyzeResults.NUM_LITERALS, numLiterals, 
					AnalyzeResults.BEST_GENERATION_TIMEOUT, bestGeneration_TimeOut);
			// Number of literals vs. average best generation.
			writeToFile(algorithm, AnalyzeResults.NUM_LITERALS, numLiterals, 
					AnalyzeResults.AVG_BEST_GENERATION_TIMEOUT, avgBestGeneration_TimeOut);
			
			/* Time-related */
			// Number of literals vs. average number of time outs. 
			writeToFile(algorithm, AnalyzeResults.NUM_LITERALS, numLiterals, 
					AnalyzeResults.AVG_NUM_TIMEOUTS, avgNumTimeOuts);
			// Number of literals vs. best execution time.
			writeToFile(algorithm, AnalyzeResults.NUM_LITERALS, numLiterals, 
					AnalyzeResults.BEST_EXECUTION_TIME, bestExecutionTime);
			// Number of literals vs. average execution time.
			writeToFile(algorithm, AnalyzeResults.NUM_LITERALS, numLiterals, 
					AnalyzeResults.AVG_EXECUTION_TIME, avgExecutionTime);
		}
	}

	// Graph data to write to files, with problems sorted by number of clauses.
	private void writeGraphData_sortedByClauses(String algorithm) throws IOException {
		/* Percentage-related, for non-timed-out trials */
		// Number of clauses vs. best percentage solved.
		writeToFile(algorithm, AnalyzeResults.NUM_CLAUSES, numClauses, 
				AnalyzeResults.BEST_PERCENTAGE, bestPercentage);
		// Number of clauses vs.average percentage solved.
		writeToFile(algorithm, AnalyzeResults.NUM_CLAUSES, numClauses, 
				AnalyzeResults.AVG_PERCENTAGE, avgPercentage);
		
		/* Percentage-related, for timed-out trials */
		// Number of clauses vs. best percentage solved.
		writeToFile(algorithm, AnalyzeResults.NUM_CLAUSES, numClauses, 
				AnalyzeResults.BEST_PERCENTAGE_TIMEOUT, bestPercentage_TimeOut);
		// Number of clauses vs.average percentage solved.
		writeToFile(algorithm, AnalyzeResults.NUM_CLAUSES, numClauses, 
				AnalyzeResults.AVG_PERCENTAGE_TIMEOUT, avgPercentage_TimeOut);
		
		/* Best generation-related for non timed-out trials */
		// Number of clauses vs. best generation.
		writeToFile(algorithm, AnalyzeResults.NUM_CLAUSES, numClauses, 
				AnalyzeResults.BEST_GENERATION, bestGeneration);
		// Number of clauses vs. average best generation.
		writeToFile(algorithm, AnalyzeResults.NUM_CLAUSES, numClauses, 
				AnalyzeResults.AVG_BEST_GENERATION, avgBestGeneration);
		
		/* Best generation-related for timed-out trials */
		// Number of clauses vs. best generation.
		writeToFile(algorithm, AnalyzeResults.NUM_CLAUSES, numClauses, 
				AnalyzeResults.BEST_GENERATION_TIMEOUT, bestGeneration_TimeOut);
		// Number of clauses vs. average best generation.
		writeToFile(algorithm, AnalyzeResults.NUM_CLAUSES, numClauses, 
				AnalyzeResults.AVG_BEST_GENERATION_TIMEOUT, avgBestGeneration_TimeOut);
		
		/* Time-related */
		// Number of clauses vs. average number of time outs.
		writeToFile(algorithm, AnalyzeResults.NUM_CLAUSES, numClauses, 
				AnalyzeResults.AVG_NUM_TIMEOUTS, avgNumTimeOuts);
		// Number of clauses vs. best execution time.
		writeToFile(algorithm, AnalyzeResults.NUM_CLAUSES, numClauses, 
				AnalyzeResults.BEST_EXECUTION_TIME, bestExecutionTime);
		// Number of clauses vs. average execution time.
		writeToFile(algorithm, AnalyzeResults.NUM_CLAUSES, numClauses, 
				AnalyzeResults.AVG_EXECUTION_TIME, avgExecutionTime);
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