import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java. util.HashMap;
import java.util.Map;

public class AnalyzeResults {
	/* Names of factors we are considering. */
	private static final String NUM_LITERALS = "number of literals";
	private static final String NUM_CLAUSES = "nuber of clauses";
	private static final String NUM_EXPERIMENTS = "number of experiments"; // The number of different parameters used for this problem.
	private static final String AVG_NUM_TIMEOUTS = "average number of timeouts"; // Average number of timeouts per experiment. 	
	private static final String BEST_EXECUTION_TIME = "best execution time"; // The fastest time the algorithm took to solve this problem.
	private static final String AVG_EXECUTION_TIME = "average execution time"; // The average time the algorithm took to solve this problem.
	private static final String BEST_GENERATION = "best generation"; // The generation the algorithm found the best solution.    
	private static final String AVG_BEST_GENERATION = "average best generation";  // Average of best generations over all non-timed out trials.
	private static final String BEST_GENERATION_TIMEOUT = "best generation timeout"; // Same as above but for timed out trials.
	private static final String AVG_BEST_GENERATION_TIMEOUT = "average best generation timeout"; // Same as above but for timed out trials.
	/* NOTE: 
	 * Percentage defined as: clauses solved/clauses solved by best known algorithm. */
	private static final String BEST_PERCENTAGE = "best percentage"; 
	private static final String AVG_PERCENTAGE = "average percentage"; 
	/* A list of parameter settings, comma-separated. 
	 * Order - GA:
	 * Order - PBIL:
	 * */
	private static final String PARAMETER_SETTINGS = "parameter settings"; 
	private static final int NO_DATA = -1;
	
	/* Actual values of factors we are considering. */
	private int numLiterals;
	private int numClauses;
	// GA
	
	// PBIL
	private int numExperiments_PBIL;
	private int numTimeOuts_PBIL;
	private long fastestTime_PBIL;
	private long avgTime_PBIL;
	private int bestGeneration_PBIL;
	private int avgGeneration_PBIL;
	private double bestPercentage_PBIL;
	private double avgPercentage_PBIL;
	private String parameterSettings_PBIL;
	
	/* Files */
	String[] MAXSATProblems = TestController.files; 		// A list of the names of MAXSAT problems.
	String folderPath = "Combined_Results"; 				// Path of source folder that contains all the results.
	File[] resultsFiles = new File(folderPath).listFiles(); // A list of all the result files.
	
	/* HashMap<String, HashMap> for GA to store information on each problem for quick lookup.
	 * KEY- String: Name of MAXSAT problem.
	 * VALUE - HashMap<String, String>: A HashMap where 
	 * 				KEY - String: a factor we're analyzing/considering (e.g. fastest time to solve the problem).
	 * 				VALUE - String: the value for this factor, after searching through all files.
	 * */
	HashMap<String, HashMap<String, String>> parsedResults_GA = new HashMap<String, HashMap<String, String>>();
	// Same as above, for PBIL.
	HashMap<String, HashMap<String, String>> parsedResults_PBIL = new HashMap<String, HashMap<String, String>>(); 
	
	/* HashMap that groups the different files by problem name to allow faster operation for the above HashMaps.
	 * KEY - String: Name of MAXSAT problem.
	 * VALUE - ArrayList<String>: An ArrayList of file paths for this problem.
	 * */
	HashMap<String, ArrayList<String>> filesGroupedByProblem = new HashMap<String, ArrayList<String>>();

	// Constructor.
	public AnalyzeResults() throws IOException {
		// Sort the files.
		groupFilesByProblemName();
		// Initialize.
		initializeHashMaps();
		// Fill in HashMap values.
		for (int i = 0; i < MAXSATProblems.length; i++) {
			analyzeResults_GA(MAXSATProblems[i]);
			analyzeResults_PBIL(MAXSATProblems[i]);
		}
	}
	
	// Getter method: Return parsed results for GA in a HashMap.
	public HashMap<String, HashMap<String, String>>  getParsedResults_GA() {
		return parsedResults_GA;
	}
	
	// Getter method: Return parsed results for PBIL in a HashMap.
	public HashMap<String, HashMap<String, String>>  getParsedResults_PBIL() {
		return parsedResults_PBIL;
	}
	
	// Find out which problems are unused, if any, because of some unfinished experiments.
	public void printUnusedProblems() throws IOException {
		HashSet<String> setOfUsedProblems = new HashSet<String>();
				
		for (File file : resultsFiles) {
			String resultFilePath = getFilePath(file);
			
			if (!file.getName().equalsIgnoreCase(".DS_Store")) {
				try {
					BufferedReader bufferReader = new BufferedReader(new FileReader(resultFilePath));
					String problemFileName = getProblemName(bufferReader.readLine());
					// Add to set.
					setOfUsedProblems.add(problemFileName.trim());
					bufferReader.close();
				} 
				catch(FileNotFoundException e) {
					System.out.println("Unable to open file '" + resultFilePath + "'");
				}
			} 
		}
		
		boolean hasUnused = false;
		for (int i = 0; i < MAXSATProblems.length; i++) {
			String problem = MAXSATProblems[i];
			if (!setOfUsedProblems.contains(problem)) {
				hasUnused = true;
				System.out.println(problem + " is unused");
			}
		}
		if (!hasUnused) {
			System.out.println("All MAXSAT problems are used.");
		}
	}
	
	private void initializeHashMaps() {
		for (int i = 0; i < MAXSATProblems.length; i++) {
			String prob = MAXSATProblems[i];
			parsedResults_GA.put(prob, new HashMap<String, String>());
			parsedResults_PBIL.put(prob, new HashMap<String, String>());
		}
	}
	
	// Helper method: Strip the "File:" and get the actual name of file.
	private String getProblemName(String line) {
		int fileNameStartIndex = line.indexOf(":") + 1;
		String problemFileName = (line.substring(fileNameStartIndex));
		return problemFileName;
	}
	
	// Helper method: Return path to specified file.
	private String getFilePath(File file) {
		return folderPath + "/" + file.getName();
	}
	
	// Helper method: File not found error.
	private void printFileNotFound(String path) {
		System.out.println("Unable to open file '" + path + "'");
	}
		
	// Return the number of experiments run on this problem for GA.
	private String getNumOfExperiments_GA(String prob) {
		int num = 0;
		return String.valueOf(num);
	}
	 
	// Return the number of experiments run on this problem for PBIL.
	private String getNumOfExperiments_PBIL(String prob) {
		int num = 0;
		return String.valueOf(num);
	}
	
	// Group all the results file paths by problem name to allow fast look up.
	private void groupFilesByProblemName() throws IOException {
		for (File file : resultsFiles) {
			String filePath = getFilePath(file);
			try {
				// Read problem name.
				BufferedReader bufferReader = new BufferedReader(new FileReader(filePath));
				String problemName = getProblemName(bufferReader.readLine());
				bufferReader.close();
				
				// Add file to HashMap.
				ArrayList<String> listOfFiles;
				if (filesGroupedByProblem.containsKey(problemName)) {
					listOfFiles = filesGroupedByProblem.get(problemName);
					listOfFiles.add(filePath);
				} else {
					listOfFiles = new ArrayList<String>();
				}
				filesGroupedByProblem.put(problemName, listOfFiles);
				
			} catch (FileNotFoundException e) {
				printFileNotFound(filePath);
			}
		}
	}
	
	// Run the analysis for GA.
	private void analyzeResults_GA(String prob) throws IOException {
		ArrayList<String> files = filesGroupedByProblem.get(prob);
		int totalNumExperiments = files.size();
		
		// Iterate through all files associated with this problem.
		for (int i = 0; i< totalNumExperiments; i++) {
			String filePath = files.get(i);
			int numNonTimeOutExperiments = totalNumExperiments;
			int totalNumTimeouts = 0;
			
			try {
				BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath));
				String line;
				int lineNum = 1;
				while ((line = bufferedReader.readLine()) != null) {
					int lineNum_AvgBestGeneration = LineNumber.AVG_BEST_GENERATION.getNumVal();
					int lineNum_AvgBestGeneration_Timeout = LineNumber.AVG_BEST_GENERATION_TIMEOUT.getNumVal();					

					if (lineNum == lineNum_AvgBestGeneration) {
						// TODO: divide by number of files.
						if (lineNum_AvgBestGeneration != NO_DATA) {
							avgBestGeneration_GA += Integer.parseInt(line);
						} else {
							numNonTimeOutExperiments--;
							totalNumTimeouts++;
						}
					} else if (lineNum == lineNum_AvgBestGeneration_Timeout) {
						if (lineNum_AvgBestGeneration_Timeout != NO_DATA) {
							lineNum_AvgBestGeneration_Timeout += Integer.parseInt(line);
						}
					} else if (lineNum == LineNumber.BEST_GENERATION.getNumVal()) {
						int currentBestGeneration = Integer.parseInt(line);
						if (currentBestGeneration < bestGeneration_GA) {
							bestGeneration_GA = currentBestGeneration;
						}
					} else if (lineNum == LineNumber.BEST_GENERATION_TIMEOUT.getNumVal()) {
						int currentBestGeneration = Integer.parseInt(line);
						if (currentBestGeneration < bestGeneration_TimeOut_GA) {
							bestGeneration_TimeOut_GA = currentBestGeneration;
						} 
					} else if (lineNum == LineNumber.AVG_UNSAT_CLAUSES.getNumVal()) {
						// TODO 
					} else if (lineNum == LineNumber.FEWEST_UNSAT_CLAUSES.getNumVal()) {
						// TODO
					} else if (lineNum == LineNumber.AVG_EXECUTION_TIME.getNumVal()) {
						// TODO: divide by number of files
						avgExecutionTime_GA += Long.parseLong(line);
					} else if (lineNum == LineNumber.BEST_EXECUTION_TIME.getNumVal()) {
						long currentExecutionTime = Long.parseLong(line);
						if (currentExecutionTime < bestExecutionTime_GA) {
							bestExecutionTime_GA = currentExecutionTime; 
						}
					}
					lineNum++;
				}
				bufferedReader.close();
				
				avgBestGeneration_GA = avgBestGeneration_GA / numNonTimeOutExperiments;
				avgBestGeneration_TimeOut_GA = avgBestGeneration_TimeOut_GA / (totalNumExperiments - numNonTimeOutExperiments);
				totalNumTimeouts = totalNumTimeouts / totalNumExperiments;
			}
			catch(FileNotFoundException e) {
				printFileNotFound(filePath);
			}
		}
		
		HashMap<String, String> values = parsedResults_GA.get(prob);
		values.put(NUM_LITERALS, String.valueOf());
		values.put(NUM_CLAUSES, getNumOfClauses(prob));
		values.put(NUM_EXPERIMENTS, String.valueOf(getNumOfExperiments_GA(prob)));
		values.put(BEST_EXECUTION_TIME, String.valueOf(bestExecutionTime_GA));
		values.put(AVG_EXECUTION_TIME, String.valueOf(avgExecutionTime_GA));
	}
	
	private void initializeValues_PBIL(String prob) throws IOException {
	}
	
	/* 
	private int numTimeOuts_GA;
	private double bestPercentage_GA;
	private double avgPercentage_GA;
	private String parameterSettings_GA;
	 */
}
