import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class GenerateGraphData {
	static final String GA = "GA";
	static final String PBIL = "PBIL";
	
	// All results.
	private HashMap<String, HashMap<String, String>> results_GA;
	private HashMap<String, HashMap<String, String>> results_PBIL;
	// Folder path.
	String folderPath = "Graph_Data";
	// Current graph file number.
	int graphIndex = 1; 
	/*
	 * Each ArrayList is a list of values (from all problems) for x-axis or
	 * y-axis of one graph. Indices correspond to the same file: for example,
	 * avgPercentage_GA at index 2 and numLiterals_GA at index 2 are data
	 * extracted from the same file for GA.
	 */
	ArrayList<String> numLiterals_GA = new ArrayList<String>();
	ArrayList<String> numLiterals_PBIL = new ArrayList<String>();
	ArrayList<String> numClauses_GA = new ArrayList<String>();
	ArrayList<String> numClauses_PBIL = new ArrayList<String>();
	ArrayList<String> avgNumTimeOuts_GA = new ArrayList<String>();
	ArrayList<String> avgNumTimeOuts_PBIL = new ArrayList<String>();
	ArrayList<String> bestExecutionTime_GA = new ArrayList<String>();
	ArrayList<String> bestExecutionTime_PBIL = new ArrayList<String>();
	ArrayList<String> avgExecutionTime_GA = new ArrayList<String>();
	ArrayList<String> avgExecutionTime_PBIL = new ArrayList<String>();
	ArrayList<String> bestGeneration_GA = new ArrayList<String>();
	ArrayList<String> bestGeneration_PBIL = new ArrayList<String>();
	ArrayList<String> bestGeneration_TimeOut_GA = new ArrayList<String>();
	ArrayList<String> bestGeneration_TimeOut_PBIL = new ArrayList<String>();
	ArrayList<String> avgBestGeneration_GA = new ArrayList<String>();
	ArrayList<String> avgBestGeneration_PBIL = new ArrayList<String>();
	ArrayList<String> avgBestGeneration_TimeOut_GA = new ArrayList<String>();
	ArrayList<String> avgBestGeneration_TimeOut_PBIL = new ArrayList<String>();
	ArrayList<String> bestPercentage_GA = new ArrayList<String>();
	ArrayList<String> bestPercentage_PBIL = new ArrayList<String>();
	ArrayList<String> bestPercentage_TimeOut_GA = new ArrayList<String>();
	ArrayList<String> bestPercentage_TimeOut_PBIL = new ArrayList<String>();
	ArrayList<String> avgPercentage_GA = new ArrayList<String>();
	ArrayList<String> avgPercentage_PBIL = new ArrayList<String>();
	ArrayList<String> avgPercentage_TimeOut_GA = new ArrayList<String>();
	ArrayList<String> avgPercentage_TimeOut_PBIL = new ArrayList<String>();

	public GenerateGraphData(
			HashMap<String, HashMap<String, String>> results_GA,
			HashMap<String, HashMap<String, String>> results_PBIL)
			throws IOException {
		this.results_GA = results_GA;
		this.results_PBIL = results_PBIL;
		// Initialize.
		initializeArrayList(GA);
		initializeArrayList(PBIL);
		// Write to all files.
		writeGraphData();
	}

	private void initializeArrayList(String algorithm) {
		HashMap<String, HashMap<String, String>> results;
		if (algorithm.equalsIgnoreCase(GA)) {
			results = results_GA;
		} else {
			results = results_PBIL;
		}
		// Populate the values in ArrayLists.
		for (String problem : results.keySet()) {
			HashMap<String, String> resultsValues = results.get(problem);
			
			String numLiterals = resultsValues.get(AnalyzeResults.NUM_LITERALS);
			String numClauses = resultsValues.get(AnalyzeResults.NUM_CLAUSES);
			String avgNumTimeOuts = resultsValues.get(AnalyzeResults.AVG_NUM_TIMEOUTS);
			String bestExecutionTime = resultsValues.get(AnalyzeResults.BEST_EXECUTION_TIME);
			String avgExecutionTime = resultsValues.get(AnalyzeResults.AVG_EXECUTION_TIME);
			String bestGeneration = resultsValues.get(AnalyzeResults.BEST_GENERATION);
			String bestGeneration_TimeOut = resultsValues.get(AnalyzeResults.BEST_GENERATION_TIMEOUT);
			String avgBestGeneration = resultsValues.get(AnalyzeResults.AVG_BEST_GENERATION);
			String avgBestGeneration_TimeOut = resultsValues.get(AnalyzeResults.AVG_BEST_GENERATION_TIMEOUT);
			String bestPercentage = resultsValues.get(AnalyzeResults.BEST_PERCENTAGE);
			String bestPercentage_TimeOut = resultsValues.get(AnalyzeResults.BEST_PERCENTAGE_TIMEOUT);
			String avgPercentage = resultsValues.get(AnalyzeResults.AVG_PERCENTAGE);
			String avgPercentage_TimeOut = resultsValues.get(AnalyzeResults.AVG_PERCENTAGE_TIMEOUT);
			
			if (algorithm.equalsIgnoreCase(GA)) {
				numLiterals_GA.add(numLiterals);
				numClauses_GA.add(numClauses);
				avgNumTimeOuts_GA.add(avgNumTimeOuts);
				bestExecutionTime_GA.add(bestExecutionTime);
				avgExecutionTime_GA.add(avgExecutionTime);
				bestGeneration_GA.add(bestGeneration);
				bestGeneration_TimeOut_GA.add(bestGeneration_TimeOut);
				avgBestGeneration_GA.add(avgBestGeneration);
				avgBestGeneration_TimeOut_GA.add(avgBestGeneration_TimeOut);
				bestPercentage_GA.add(bestPercentage);
				bestPercentage_TimeOut_GA.add(bestPercentage_TimeOut);
				avgPercentage_GA.add(avgPercentage);
				avgPercentage_TimeOut_GA.add(avgPercentage_TimeOut);
			} else {
				numLiterals_PBIL.add(numLiterals);
				numClauses_PBIL.add(numClauses);
				avgNumTimeOuts_PBIL.add(avgNumTimeOuts);
				bestExecutionTime_PBIL.add(bestExecutionTime);
				avgExecutionTime_PBIL.add(avgExecutionTime);
				bestGeneration_PBIL.add(bestGeneration);
				bestGeneration_TimeOut_PBIL.add(bestGeneration_TimeOut);
				avgBestGeneration_PBIL.add(avgBestGeneration);
				avgBestGeneration_TimeOut_PBIL.add(avgBestGeneration_TimeOut);
				bestPercentage_PBIL.add(bestPercentage);
				bestPercentage_TimeOut_PBIL.add(bestPercentage_TimeOut);
				avgPercentage_PBIL.add(avgPercentage);
				avgPercentage_TimeOut_PBIL.add(avgPercentage_TimeOut);
			}
		}
	}

	// Decide which graph data to write to files.
	private void writeGraphData() throws IOException {
		/* Percentage-related, for non-timed-out trials */
		// Number of literals vs.best percentage solved. 
		writeToFile(GA, AnalyzeResults.NUM_LITERALS, numLiterals_GA, 
				AnalyzeResults.BEST_PERCENTAGE, bestPercentage_GA);
		writeToFile(PBIL, AnalyzeResults.NUM_LITERALS, numLiterals_PBIL, 
				AnalyzeResults.BEST_PERCENTAGE, bestPercentage_PBIL);
		// Number of clauses vs. best percentage solved.
		writeToFile(GA, AnalyzeResults.NUM_CLAUSES, numClauses_GA, 
				AnalyzeResults.BEST_PERCENTAGE, bestPercentage_GA);
		writeToFile(PBIL, AnalyzeResults.NUM_CLAUSES, numClauses_PBIL, 
				AnalyzeResults.BEST_PERCENTAGE, bestPercentage_PBIL);
		// Number of literals vs.average percentage solved.
		writeToFile(GA, AnalyzeResults.NUM_LITERALS, numLiterals_GA, 
				AnalyzeResults.AVG_PERCENTAGE, avgPercentage_GA);
		writeToFile(PBIL, AnalyzeResults.NUM_LITERALS, numLiterals_PBIL, 
				AnalyzeResults.AVG_PERCENTAGE, avgPercentage_PBIL);
		// Number of clauses vs.average percentage solved.
		writeToFile(GA, AnalyzeResults.NUM_CLAUSES, numClauses_GA, 
				AnalyzeResults.AVG_PERCENTAGE, avgPercentage_GA);
		writeToFile(PBIL, AnalyzeResults.NUM_CLAUSES, numClauses_PBIL,
				AnalyzeResults.AVG_PERCENTAGE, avgPercentage_PBIL);
		
		/* Percentage-related, for timed-out trials */
		// Number of literals vs.best percentage solved. 
		writeToFile(GA, AnalyzeResults.NUM_LITERALS, numLiterals_GA, 
				AnalyzeResults.BEST_PERCENTAGE_TIMEOUT, bestPercentage_TimeOut_GA);
		writeToFile(PBIL, AnalyzeResults.NUM_LITERALS, numLiterals_PBIL, 
				AnalyzeResults.BEST_PERCENTAGE_TIMEOUT, bestPercentage_TimeOut_PBIL);
		// Number of clauses vs. best percentage solved.
		writeToFile(GA, AnalyzeResults.NUM_CLAUSES, numClauses_GA, 
				AnalyzeResults.BEST_PERCENTAGE_TIMEOUT, bestPercentage_TimeOut_GA);
		writeToFile(PBIL, AnalyzeResults.NUM_CLAUSES, numClauses_PBIL, 
				AnalyzeResults.BEST_PERCENTAGE_TIMEOUT, bestPercentage_TimeOut_PBIL);
		// Number of literals vs.average percentage solved.
		writeToFile(GA, AnalyzeResults.NUM_LITERALS, numLiterals_GA, 
				AnalyzeResults.AVG_PERCENTAGE_TIMEOUT, avgPercentage_TimeOut_GA);
		writeToFile(PBIL, AnalyzeResults.NUM_LITERALS, numLiterals_PBIL, 
				AnalyzeResults.AVG_PERCENTAGE_TIMEOUT, avgPercentage_TimeOut_PBIL);
		// Number of clauses vs.average percentage solved.
		writeToFile(GA, AnalyzeResults.NUM_CLAUSES, numClauses_GA, 
				AnalyzeResults.AVG_PERCENTAGE_TIMEOUT, avgPercentage_TimeOut_GA);
		writeToFile(PBIL, AnalyzeResults.NUM_CLAUSES, numClauses_PBIL,
				AnalyzeResults.AVG_PERCENTAGE_TIMEOUT, avgPercentage_TimeOut_PBIL);
		
		/* Best generation-related for non timed-out trials */
		// Number of literals vs. best generation.
		writeToFile(GA, AnalyzeResults.NUM_LITERALS, numLiterals_GA, 
				AnalyzeResults.BEST_GENERATION, bestGeneration_GA);
		writeToFile(PBIL, AnalyzeResults.NUM_LITERALS, numLiterals_PBIL, 
				AnalyzeResults.BEST_GENERATION, bestGeneration_PBIL);
		// Number of clauses vs. best generation.
		writeToFile(GA, AnalyzeResults.NUM_CLAUSES, numClauses_GA, 
				AnalyzeResults.BEST_GENERATION, bestGeneration_GA);
		writeToFile(PBIL, AnalyzeResults.NUM_CLAUSES, numClauses_PBIL, 
				AnalyzeResults.BEST_GENERATION, bestGeneration_PBIL);
		// Number of literals vs. average best generation.
		writeToFile(GA, AnalyzeResults.NUM_LITERALS, numLiterals_GA, 
				AnalyzeResults.AVG_BEST_GENERATION, avgBestGeneration_GA);
		writeToFile(PBIL, AnalyzeResults.NUM_LITERALS, numLiterals_PBIL, 
				AnalyzeResults.AVG_BEST_GENERATION, avgBestGeneration_PBIL);
		// Number of clauses vs. average best generation.
		writeToFile(GA, AnalyzeResults.NUM_CLAUSES, numClauses_GA, 
				AnalyzeResults.AVG_BEST_GENERATION, avgBestGeneration_GA);
		writeToFile(PBIL, AnalyzeResults.NUM_CLAUSES, numClauses_PBIL, 
				AnalyzeResults.AVG_BEST_GENERATION, avgBestGeneration_PBIL);
		
		/* Best generation-related for timed-out trials */
		// Number of literals vs. best generation.
		writeToFile(GA, AnalyzeResults.NUM_LITERALS, numLiterals_GA, 
				AnalyzeResults.BEST_GENERATION_TIMEOUT, bestGeneration_TimeOut_GA);
		writeToFile(PBIL, AnalyzeResults.NUM_LITERALS, numLiterals_PBIL, 
				AnalyzeResults.BEST_GENERATION_TIMEOUT, bestGeneration_TimeOut_PBIL);
		// Number of clauses vs. best generation.
		writeToFile(GA, AnalyzeResults.NUM_CLAUSES, numClauses_GA, 
				AnalyzeResults.BEST_GENERATION_TIMEOUT, bestGeneration_TimeOut_GA);
		writeToFile(PBIL, AnalyzeResults.NUM_CLAUSES, numClauses_PBIL, 
				AnalyzeResults.BEST_GENERATION_TIMEOUT, bestGeneration_TimeOut_PBIL);
		// Number of literals vs. average best generation.
		writeToFile(GA, AnalyzeResults.NUM_LITERALS, numLiterals_GA, 
				AnalyzeResults.AVG_BEST_GENERATION_TIMEOUT, avgBestGeneration_TimeOut_GA);
		writeToFile(PBIL, AnalyzeResults.NUM_LITERALS, numLiterals_PBIL, 
				AnalyzeResults.AVG_BEST_GENERATION_TIMEOUT, avgBestGeneration_TimeOut_PBIL);
		// Number of clauses vs. average best generation.
		writeToFile(GA, AnalyzeResults.NUM_CLAUSES, numClauses_GA, 
				AnalyzeResults.AVG_BEST_GENERATION_TIMEOUT, avgBestGeneration_TimeOut_GA);
		writeToFile(PBIL, AnalyzeResults.NUM_CLAUSES, numClauses_PBIL, 
				AnalyzeResults.AVG_BEST_GENERATION_TIMEOUT, avgBestGeneration_TimeOut_PBIL);
		
		/* Time-related */
		// Number of literals vs. average number of time outs. 
		writeToFile(GA, AnalyzeResults.NUM_LITERALS, numLiterals_GA, 
				AnalyzeResults.AVG_NUM_TIMEOUTS, avgNumTimeOuts_GA);
		writeToFile(PBIL, AnalyzeResults.NUM_LITERALS, numLiterals_PBIL, 
				AnalyzeResults.AVG_NUM_TIMEOUTS, avgNumTimeOuts_PBIL);
		// Number of clauses vs. average number of time outs.
		writeToFile(GA, AnalyzeResults.NUM_CLAUSES, numClauses_GA, 
				AnalyzeResults.AVG_NUM_TIMEOUTS, avgNumTimeOuts_GA);
		writeToFile(PBIL, AnalyzeResults.NUM_CLAUSES, numClauses_PBIL, 
				AnalyzeResults.AVG_NUM_TIMEOUTS, avgNumTimeOuts_PBIL);
		// Number of literals vs. best execution time.
		writeToFile(GA, AnalyzeResults.NUM_LITERALS, numLiterals_GA, 
				AnalyzeResults.BEST_EXECUTION_TIME, bestExecutionTime_GA);
		writeToFile(PBIL, AnalyzeResults.NUM_LITERALS, numLiterals_PBIL, 
				AnalyzeResults.BEST_EXECUTION_TIME, bestExecutionTime_PBIL);
		// Number of clauses vs. best execution time.
		writeToFile(GA, AnalyzeResults.NUM_CLAUSES, numClauses_GA, 
				AnalyzeResults.BEST_EXECUTION_TIME, bestExecutionTime_GA);
		writeToFile(PBIL, AnalyzeResults.NUM_CLAUSES, numClauses_PBIL, 
				AnalyzeResults.BEST_EXECUTION_TIME, bestExecutionTime_PBIL);
		// Number of literals vs. average execution time.
		writeToFile(GA, AnalyzeResults.NUM_LITERALS, numLiterals_GA, 
				AnalyzeResults.AVG_EXECUTION_TIME, avgExecutionTime_GA);
		writeToFile(PBIL, AnalyzeResults.NUM_LITERALS, numLiterals_PBIL, 
				AnalyzeResults.AVG_EXECUTION_TIME, avgExecutionTime_PBIL);
		// Number of clauses vs. average execution time.
		writeToFile(GA, AnalyzeResults.NUM_CLAUSES, numClauses_GA, 
				AnalyzeResults.AVG_EXECUTION_TIME, avgExecutionTime_GA);
		writeToFile(PBIL, AnalyzeResults.NUM_CLAUSES, numClauses_PBIL, 
				AnalyzeResults.AVG_EXECUTION_TIME, avgExecutionTime_PBIL);		
	}

	// Write the specified data to the specified file.
	private void writeToFile(String algorithm, String x_name, ArrayList<String> x_data, 
			String y_name, ArrayList<String> y_data)
			throws IOException {
		File file = new File(getFilePath(graphIndex));
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
	private String getFilePath(int n) {
		return folderPath + "/graph " + String.valueOf(n) + ".txt";
	}
}
