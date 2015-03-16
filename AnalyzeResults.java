import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java. util.HashMap;

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
	int[] MAXSATSolutions = TestController.maxValues;		// Unsatisfied clauses from currently known best algorithm.
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
		for (String problem : filesGroupedByProblem_GA.keySet()) {
			analyzeResults(problem, "GA");
		}
		for (String problem : filesGroupedByProblem_PBIL.keySet()) {
			analyzeResults(problem, "PBIL");
		}
	}
	
	// Initialize with problem names.
	private void initializeHashMaps() {
		for (String problem : filesGroupedByProblem_GA.keySet()) {
			parsedResults_GA.put(problem, new HashMap<String, String>());
		}
		for (String problem : filesGroupedByProblem_PBIL.keySet()) {
			parsedResults_PBIL.put(problem, new HashMap<String, String>());
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
	
	// Helper method: Strip the "File:" and get the actual name of file.
	private String getProblemName(String line) {
		int fileNameStartIndex = line.indexOf(":") + 1;
		String problemFileName = (line.substring(fileNameStartIndex));
		return problemFileName.trim();
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
		int numExperiments;
		if (algorithm.equalsIgnoreCase("GA")) {
			files = filesGroupedByProblem_GA.get(prob);
			numExperiments = filesGroupedByProblem_GA.keySet().size();
		} else if (algorithm.equalsIgnoreCase("PBIL")) {
			files = filesGroupedByProblem_PBIL.get(prob);
			numExperiments = filesGroupedByProblem_PBIL.keySet().size();
		} else {
			throw new InvalidParameterException("Invalid algorithm name.");
		}

		// Factors we care about.
		int totalNumTimeOuts = 0;
		int numLiterals = 0;
		int numClauses = 0;
		long bestExecutionTime =  Long.MAX_VALUE;
		long totalExecutionTime = 0;
		int bestGeneration = Integer.MAX_VALUE;
		int bestGeneration_TimeOut = Integer.MAX_VALUE;
		int totalBestGeneration = 0;
		int totalBestGeneration_TimeOut = 0;
		double totalUnsatClauses = 0.0;
		double totalUnsatClauses_TimeOut = 0.0;
		double fewestUnsatClauses = Double.MAX_VALUE;
		double fewestUnsatClauses_TimeOut = Double.MAX_VALUE;
		String parameterSettings = null;
		
		// Iterate through all files associated with this problem.
		for (int i = 0; i < files.size(); i++) {
			String filePath = files.get(i);
			try {
				BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath));
				String line;
				int lineNum = 1;
				
				while ((line = bufferedReader.readLine()) != null) {
					if (lineNum == LineNumber.NUM_LITERALS.getNumVal()) {
						numLiterals = Integer.parseInt(line);
					} else if (lineNum == LineNumber.NUM_CLAUSES.getNumVal()) {
						numClauses = Integer.parseInt(line);
					} else if (lineNum == LineNumber.AVG_BEST_GENERATION.getNumVal()) {
						int data = Integer.parseInt(line);
						if (data != NO_DATA) {
							totalBestGeneration += data;
						}
					} else if (lineNum == LineNumber.AVG_BEST_GENERATION_TIMEOUT.getNumVal()) {
						int data = Integer.parseInt(line);
						if (data != NO_DATA) {
							totalBestGeneration_TimeOut += data;
						}
					} else if (lineNum == LineNumber.BEST_GENERATION.getNumVal()) {
						int current = Integer.parseInt(line);
						if (current != NO_DATA && current < bestGeneration) {
							bestGeneration = current;
						}
					} else if (lineNum == LineNumber.BEST_GENERATION_TIMEOUT.getNumVal()) {
						int current = Integer.parseInt(line);
						if (current != NO_DATA && current < bestGeneration_TimeOut) {
							bestGeneration_TimeOut = current;
						} 
					} else if (lineNum == LineNumber.AVG_UNSAT_CLAUSES.getNumVal()) {
						int current = Integer.parseInt(line);
						if (current != NO_DATA) {
							totalUnsatClauses += current;
						}
					} else if (lineNum == LineNumber.AVG_UNSAT_CLAUSES_TIMEOUT.getNumVal()) {
						int current = Integer.parseInt(line);
						if (current != NO_DATA) {
							totalUnsatClauses_TimeOut += current;
						}
					} else if (lineNum == LineNumber.FEWEST_UNSAT_CLAUSES.getNumVal()) {
						int current = Integer.parseInt(line);
						if (current != NO_DATA && current < fewestUnsatClauses) {
							fewestUnsatClauses = current;
						}
					} else if (lineNum == LineNumber.FEWEST_UNSAT_CLAUSES_TIMEOUT.getNumVal()) {
						int current = Integer.parseInt(line);
						if (current != NO_DATA && current < fewestUnsatClauses_TimeOut) {
							fewestUnsatClauses_TimeOut = current;
						}
					}  else if (lineNum == LineNumber.AVG_EXECUTION_TIME.getNumVal()) {
						long current = Long.parseLong(line);
						if ((int)current != NO_DATA) {
							totalExecutionTime += Long.parseLong(line);
						}
					} else if (lineNum == LineNumber.BEST_EXECUTION_TIME.getNumVal()) {
						long current = Long.parseLong(line);
						if ((int)current != NO_DATA && current < bestExecutionTime) {
							bestExecutionTime = current; 
						}
					} else if (lineNum == LineNumber.NUM_TIMEOUTS.getNumVal()) {
						totalNumTimeOuts += LineNumber.NUM_TIMEOUTS.getNumVal();
					}
					
					if (algorithm.equalsIgnoreCase("GA")) {
						// GA parameter settings.
						if (lineNum == LineNumberGA.POP_SIZE.getNumVal() ||
								lineNum == LineNumberGA.SELECTION_TYPE.getNumVal() ||
								lineNum == LineNumberGA.CROSSOVER_TYPE.getNumVal() ||
								lineNum == LineNumberGA.CROSSOVER_PROB.getNumVal() ||
								lineNum == LineNumberGA.MUTATION_PROB.getNumVal()) {
							parameterSettings += line += ",";
						}
					} else {
						// PBIL parameter settings.
						if (lineNum == LineNumberPBIL.POP_SIZE.getNumVal() ||
								lineNum == LineNumberPBIL.LEARNING_RATE.getNumVal() ||
								lineNum == LineNumberPBIL.NEG_LEARNING_RATE.getNumVal() ||
								lineNum == LineNumberPBIL.MUTATION_PROB.getNumVal() ||
								lineNum == LineNumberPBIL.MUTATION_SHIFT.getNumVal()) {
							parameterSettings += line += ",";
						}
					}
					
					lineNum++;
				}
				bufferedReader.close();
				
				// Get values and other info.
				int totalNumNonTimeOutTrials = LineNumber.NUM_TRIALS.getNumVal() * numExperiments - totalNumTimeOuts;
				int index = Arrays.asList(MAXSATProblems).indexOf(prob);
				int bestKnownNumUnsatClauses = MAXSATSolutions[index];
				int avgNumTimeOuts = totalNumTimeOuts / numExperiments;
				long avgExecutionTime = totalExecutionTime / (long) numExperiments;
				// Values to be calculated.
				int avgBestGeneration = NO_DATA;
				int avgBestGeneration_TimeOut = NO_DATA; 
				double avgPercentage = NO_DATA;
				double avgPercentage_TimeOut = NO_DATA;
				double bestPercentage = NO_DATA;
				double bestPercentage_TimeOut = NO_DATA;
				// Calculations.
				if (totalNumNonTimeOutTrials > 0) {
					avgBestGeneration = totalBestGeneration / totalNumNonTimeOutTrials;
				}
				if (totalNumTimeOuts > 0) {
					avgBestGeneration_TimeOut = totalBestGeneration_TimeOut / totalNumTimeOuts;
				}
				
				if (bestKnownNumUnsatClauses > 0) {
					avgPercentage = (double)totalUnsatClauses / (double)totalNumNonTimeOutTrials / (double)bestKnownNumUnsatClauses;
					avgPercentage_TimeOut = (double)totalUnsatClauses_TimeOut / (double)totalNumTimeOuts / (double)bestKnownNumUnsatClauses;
					bestPercentage = (double)fewestUnsatClauses / (double)bestKnownNumUnsatClauses;
					bestPercentage_TimeOut = (double)fewestUnsatClauses_TimeOut / (double)bestKnownNumUnsatClauses;
				}
				
				// Push values to HashMap.
				HashMap<String, String> values = parsedResults_GA.get(prob);
				values = new HashMap<String, String>();
				
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
				values.put(PARAMETER_SETTINGS, parameterSettings);
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


