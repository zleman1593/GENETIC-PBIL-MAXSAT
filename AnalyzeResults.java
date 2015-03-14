import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java. util.HashMap;
import java.util.Map;

public class AnalyzeResults {
	
	/* The factors we are considering.*/
	private static final String NUM_LITERALS = "number of literals";
	private static final String NUM_CLAUSES = "nuber of clauses";
	private static final String NUM_EXPERIMENTS = "number of experiments"; // The number of experiments run on this problem.
	private static final String NUM_TIMEOUTS = "total number of timeouts"; // The total number of timeouts on this problem. 
	private static final String FASTEST_TIME = "fastest time"; // The fastest time the algorithm took to solve this problem.
	private static final String AVG_TIME = "average time"; // The average time the algorithm took to solve this problem.
	private static final String BEST_GENERATION = "best generation"; 
	private static final String AVG_GENERATION = "average best generation";
	private static final String BEST_PERCENTAGE = "best percentage"; // The best percentage of: clauses solved/clauses solved by best known algorithm.
	private static final String AVG_PERCENTAGE = "average percentage"; // Percentage defined as above, but the average percentage.
	private static final String PARAMETER_SETTINGS = "parameter settings"; // A list of parameter settings, comma-separated
	
	
	private int numLiterals;
	private int numClauses;
	// GA
	private int numExperiments_GA;
	private int numTimeOuts_GA;
	private long fastestTime_GA;
	private long avgTime_GA;
	private int bestGeneration_GA;
	private int avgGeneration_GA;
	private double bestPercentage_GA;
	private double avgPercentage_GA;
	private String parameterSettings_GA;
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
	
	
	// A list of the names of MAXSAT problems.
	String[] MAXSATProblems = TestController.files;
	// Path of source folder that contains all the results.
	String folderPath = "Combined_Results";
	// A list of all the result files.
	File[] resultsFiles = new File(folderPath).listFiles();
	
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
		groupFilesByProblemName();
		analyzeResults_GA();
		analyzeResults_PBIL();
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
	private void analyzeResults_GA() throws IOException {
		// Initialize HashMap value for all MAXSAT problems.
		for (int i = 0; i < MAXSATProblems.length; i++) {
			String problem = MAXSATProblems[i];
			parsedResults_GA.put(problem, new HashMap<String, String>());
			
			HashMap<String, String> values = parsedResults_GA.get(problem);
			values.put(NUM_LITERALS, String.valueOf(numLiterals));
			values.put(NUM_CLAUSES, String.valueOf(numClauses));
			values.put(NUM_EXPERIMENTS, String.valueOf(numExperiments_GA));
			values.put(FASTEST_TIME, String.valueOf(fastestTime_GA));
			values.put(AVG_TIME, String.valueOf(avgTime_GA));
		}
	}
	
	// Run the analysis for PBIL.
	private void analyzeResults_PBIL() {
		
	}
	
	// Return the number of literals for this problem.
//	private void getNumOfLiteralsAndClauses(String prob) throws IOException {
//		String filePath = filesGroupedByProblem.get(prob).get(0);
//		try {
//			BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath));
//			for (int i = 0; i < LineNumber.NUM_VARS.getNumVal(); i++) {
//				bufferedReader.readLine();
//			}
//			numLiterals = bufferedReader.readLine();
//			numClauses = bufferedReader.readLine();
//			bufferedReader.close();
//			
//		}
//		catch (FileNotFoundException e) {
//			printFileNotFound(filePath);
//		}
//	}	
	// The number of experiments run on this problem
	private void initializeValues_GA(String prob) throws IOException {
		ArrayList<String> files = filesGroupedByProblem.get(prob);
		for (int i = 0; i< files.size(); i++) {
			String filePath = files.get(i);
			try {
				BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath));
				String line;
				int lineNum = 1;
				while ((line = bufferedReader.readLine()) != null) {
					if (lineNum == LineNumber.NUM_VARS.getNumVal()) {
						numLiterals = Integer.parseInt(line);
					} else if (lineNum == LineNumber.NUM_CLAUSES.getNumVal()) {
						numClauses = Integer.parseInt(line);
					} else if (lineNum == LineNumber.AVG_BEST_GENERATION.getNumVal()) {
						avgGeneration_GA += Integer.parseInt(line);
					} else if (lineNum == LineNumber.BEST_GENERATION.getNumVal()) {
						int currentBestGeneration = Integer.parseInt(line);
						if (currentBestGeneration < bestGeneration_GA) {
							bestGeneration_GA = currentBestGeneration;
						}
					} else if (lineNum == LineNumber.AVG_UNSAT_CLAUSES.getNumVal()) {
						// TODO 
					} else if (lineNum == LineNumber.FEWEST_UNSAT_CLAUSES.getNumVal()) {
						// TODO
					} else if (lineNum == LineNumber.AVG_EXECUTION_TIME.getNumVal()) {
						avgTime_GA +=
					}
					lineNum++;
				}
			}
			catch(FileNotFoundException e) {
				printFileNotFound(filePath);
			}
		}
	}
	
	private String getNumOfExperiments_PBIL(String prob) {
		int num = 0;
		return String.valueOf(num);
	}
	
	// 
	private String getFastestTime_GA(String prob) {
		long fastest = Long.MAX_VALUE;
		ArrayList<String> filePaths = filesGroupedByProblem.get(prob);
		for(String filePath : filePaths) {
			BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath));
			for (int i = 0; i < )
			if () {
				
			}
		}
		
		
		return String.valueOf(fastest);
	}
	
	private String getFastestTime_PBIL(String prob) {
		long fastest = Long.MAX_VALUE;
		
		return String.valueOf(fastest);
	}
	
	private String getAverageTime_GA(String prob) {
		long average = 0;
		
		return String.valueOf(average);
	}
	
	private String getAverageTime_PBIL(String prob) {
		long average = 0;
		
		return String.valueOf(average);
	}
}
