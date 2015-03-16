import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.security.InvalidParameterException;
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
	private static final String BEST_GENERATION_TIMEOUT = "best generation for timeouts"; // Same as above but for timed out trials.
	private static final String AVG_BEST_GENERATION_TIMEOUT = "average best generation for timeout"; // Same as above but for timed out trials.
	/* NOTE: 
	 * Percentage defined as: clauses solved/clauses solved by best known algorithm. */
	private static final String BEST_PERCENTAGE = "best percentage"; 
	private static final String BEST_PERCENTAGE_TIMEOUT = "best percentage for timeouts"; 
	private static final String AVG_PERCENTAGE = "average percentage"; 
	private static final String AVG_PERCENTAGE_TIMEOUT = "average percentage for timeouts"; 
	/* A list of parameter settings, comma-separated. 
	 * Order - GA:
	 * Order - PBIL:
	 * */
	private static final String PARAMETER_SETTINGS = "parameter settings"; 
	private static final int NO_DATA = -1;
	
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
	HashMap<String, ArrayList<String>> filesGroupedByProblem_GA = new HashMap<String, ArrayList<String>>();
	HashMap<String, ArrayList<String>> filesGroupedByProblem_PBIL = new HashMap<String, ArrayList<String>>();

	// Constructor.
	public AnalyzeResults() throws IOException {
		// Sort the files.
		groupFilesByProblem();
		// Initialize.
		initializeHashMaps();
		// Fill in HashMap values.
		for (int i = 0; i < MAXSATProblems.length; i++) {
			analyzeResults(MAXSATProblems[i], "GA");
			analyzeResults(MAXSATProblems[i], "PBIL");
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
	
	// Initialize with problem names.
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
			
	// Group all the results file paths by problem name to allow fast look up.
	private void groupFilesByProblem() throws IOException {
		for (File file : resultsFiles) {
			String filePath = getFilePath(file);
			boolean isGA = true;
			try {
				// Read problem name.
				BufferedReader bufferReader = new BufferedReader(new FileReader(filePath));
				String problemName = getProblemName(bufferReader.readLine());
				
				for (int i = 2; i < LineNumber.ALGORITHM_SETTING.getNumVal(); i++) {
					bufferReader.readLine();
				}
				String algorithm = bufferReader.readLine();
				isGA  = algorithm.endsWith("GA") ? true : false;
				bufferReader.close();
				
				// Add file to HashMap.
				ArrayList<String> listOfFiles;
				if (isGA) { 
					// Add to the list of files for GA.
					if (filesGroupedByProblem_GA.containsKey(problemName)) {
						listOfFiles = filesGroupedByProblem_GA.get(problemName);
						listOfFiles.add(filePath);
					} else {
						listOfFiles = new ArrayList<String>();
					}
					filesGroupedByProblem_GA.put(problemName, listOfFiles);
				} else {
					// Add to the list of files for PBIL.
					if (filesGroupedByProblem_PBIL.containsKey(problemName)) {
						listOfFiles = filesGroupedByProblem_PBIL.get(problemName);
						listOfFiles.add(filePath);
					} else {
						listOfFiles = new ArrayList<String>();
					}
					filesGroupedByProblem_PBIL.put(problemName, listOfFiles);
				}
			} catch (FileNotFoundException e) {
				printFileNotFound(filePath);
			}
		}
	}
	
	// Run analysis and fill in values for the HashMaps.
	private void analyzeResults(String prob, String algorithm) throws IOException {
		ArrayList<String> files;
		if (algorithm.equalsIgnoreCase("GA")) {
			files = filesGroupedByProblem_GA.get(prob);
		} else if (algorithm.equalsIgnoreCase("PBIL")) {
			files = filesGroupedByProblem_GA.get(prob);
		} else {
			throw new InvalidParameterException("Invalid algorithm name.");
		}

		// Stats that help.
		int numExperiments = files.size();
		int totalNumTimeOuts = 0;
		// Factors we care about.
		int numLiterals = 0;
		int numClauses = 0;
		int avgNumTimeOuts = 0; // per file/experiment
		long bestExecutionTime =  Long.MAX_VALUE;
		long avgExecutionTime = 0;
		int bestGeneration = Integer.MAX_VALUE;
		int bestGeneration_TimeOut = Integer.MAX_VALUE;
		int avgBestGeneration = 0;
		int avgBestGeneration_TimeOut = 0;
		double bestPercentage = Double.MAX_VALUE;
		double bestPercentage_TimeOut = Double.MAX_VALUE;
		double avgPercentage = 0.0;
		double avgPercentage_TimeOut = 0.0;
		String parameterSettings;
		
		// Iterate through all files associated with this problem.
		for (int i = 0; i< numExperiments; i++) {
			String filePath = files.get(i);
			try {
				BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath));
				String line;
				int lineNum = 1;
				
				while ((line = bufferedReader.readLine()) != null) {
					int lineNum_AvgBestGeneration = LineNumber.AVG_BEST_GENERATION.getNumVal();
					int lineNum_AvgBestGeneration_Timeout = LineNumber.AVG_BEST_GENERATION_TIMEOUT.getNumVal();					

					if (lineNum == LineNumber.NUM_LITERALS.getNumVal()) {
						numLiterals = Integer.parseInt(line);
					} else if (lineNum == LineNumber.NUM_CLAUSES.getNumVal()) {
						numClauses = Integer.parseInt(line);
					} else if (lineNum == lineNum_AvgBestGeneration) {
						if (lineNum_AvgBestGeneration != NO_DATA) {
							avgBestGeneration += Integer.parseInt(line);
						}
					} else if (lineNum == lineNum_AvgBestGeneration_Timeout) {
						if (lineNum_AvgBestGeneration_Timeout != NO_DATA) {
							lineNum_AvgBestGeneration_Timeout += Integer.parseInt(line);
						}
					} else if (lineNum == LineNumber.BEST_GENERATION.getNumVal()) {
						int currentBestGeneration = Integer.parseInt(line);
						if (currentBestGeneration < bestGeneration) {
							bestGeneration = currentBestGeneration;
						}
					} else if (lineNum == LineNumber.BEST_GENERATION_TIMEOUT.getNumVal()) {
						int currentBestGeneration = Integer.parseInt(line);
						if (currentBestGeneration < bestGeneration_TimeOut) {
							bestGeneration_TimeOut = currentBestGeneration;
						} 
					} else if (lineNum == LineNumber.AVG_UNSAT_CLAUSES.getNumVal()) {
						// TODO 
					} else if (lineNum == LineNumber.FEWEST_UNSAT_CLAUSES.getNumVal()) {
						// TODO
					} else if (lineNum == LineNumber.AVG_EXECUTION_TIME.getNumVal()) {
						avgExecutionTime += Long.parseLong(line);
					} else if (lineNum == LineNumber.BEST_EXECUTION_TIME.getNumVal()) {
						long currentExecutionTime = Long.parseLong(line);
						if (currentExecutionTime < bestExecutionTime) {
							bestExecutionTime = currentExecutionTime; 
						}
					} else if (lineNum == LineNumber.NUM_TIMEOUTS.getNumVal()) {
						totalNumTimeOuts += LineNumber.NUM_TIMEOUTS.getNumVal();
					}
					lineNum++;
				}
				bufferedReader.close();
				
				int totalNumNonTimeOutTrials = LineNumber.NUM_TRIALS.getNumVal() * numExperiments - totalNumTimeOuts;
				// Divide by number of files to get the average.
				avgBestGeneration = avgBestGeneration / totalNumNonTimeOutTrials;
				avgBestGeneration_TimeOut = avgBestGeneration_TimeOut / totalNumTimeOuts;
				avgNumTimeOuts = totalNumTimeOuts / numExperiments;
				avgPercentage = avgPercentage / (double) totalNumNonTimeOutTrials;
				avgPercentage_TimeOut = avgPercentage_TimeOut / (double) totalNumTimeOuts;
				
				HashMap<String, String> values = parsedResults_GA.get(prob);
				values.put(NUM_LITERALS, String.valueOf(numLiterals));
				values.put(NUM_CLAUSES, String.valueOf(numClauses));
				values.put(NUM_EXPERIMENTS, String.valueOf(numExperiments));
				values.put(AVG_NUM_TIMEOUTS, String.valueOf(avgNumTimeOuts));
				values.put(BEST_EXECUTION_TIME, String.valueOf(bestExecutionTime));
				values.put(AVG_EXECUTION_TIME, String.valueOf(avgExecutionTime));
				values.put(BEST_GENERATION, String.valueOf(bestGeneration));
				values.put(BEST_GENERATION_TIMEOUT, String.valueOf(bestGeneration_TimeOut));
				values.put(AVG_BEST_GENERATION, String.valueOf(avgBestGeneration));
				values.put(AVG_BEST_GENERATION_TIMEOUT, String.valueOf(avgBestGeneration_TimeOut));
				values.put(BEST_PERCENTAGE, String.valueOf(bestPercentage));
				values.put(BEST_PERCENTAGE_TIMEOUT, String.valueOf(bestPercentage_TimeOut));
				values.put(AVG_PERCENTAGE, String.valueOf(avgPercentage));
				values.put(AVG_PERCENTAGE_TIMEOUT, String.valueOf(avgPercentage_TimeOut));
				
				/* String parameterSettings; */
			}
			catch(FileNotFoundException e) {
				printFileNotFound(filePath);
			}
		}
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
}
