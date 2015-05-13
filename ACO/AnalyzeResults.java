package ACO;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java. util.HashMap;

public class AnalyzeResults {
	// When the algorithm reached its maximum iteration.
	static final int MAX_ITERATION = Integer.MAX_VALUE;
	static final String AVG_LENGTH = "Average Path Length";
	
	/* Files */
	// Path of source folder that contains all the results.
	String folderPath = "Results_ACO"; 
	// A list of all the result files.
	File[] resultsFiles = new File(folderPath).listFiles(); 
	
	/* HashMap<String, HashMap> to store information on each problem for quick lookup.
	 * KEY - String: Name of MAXSAT problem.
	 * VALUE - HashMap<String, String>: A HashMap where 
	 * 				KEY - String: a factor we're analyzing/considering (e.g. fastest time to solve the problem).
	 * 				VALUE - String: the value for this factor, after searching through all files.
	 * 
	 * NOTE: These maps may have different lengths because (after ~40 hours, on two computer, with multi-threading) 
	 * 		not all experiments were finished, so some problems were not used for some experiments on one algorithm. 
	 * */
	private HashMap<String, HashMap<String, String>> parsedResults_ACO_EAS = new HashMap<String, HashMap<String, String>>(); 
	private HashMap<String, HashMap<String, String>> parsedResults_ACO_ACS = new HashMap<String, HashMap<String, String>>(); 
	
	/* HashMap that groups the different files by problem name to allow faster operation for the above HashMaps.
	 * KEY - String: Name of MAXSAT problem.
	 * VALUE - ArrayList<String>: An ArrayList of file paths for this problem.
	 * */
	private HashMap<String, ArrayList<String>> filesGroupedByProblem_ACO_ACS = new HashMap<String, ArrayList<String>>();
	private HashMap<String, ArrayList<String>> filesGroupedByProblem_ACO_EAS = new HashMap<String, ArrayList<String>>();

	// Constructor.
	public AnalyzeResults(String algorithm, int paramLn, String paramVal) throws IOException {
		// Sort files by problem name for each algorithm.
		groupFilesByProblem();
		// Initialize parsed results.
		initializeHashMaps();
		
		if (algorithm.equalsIgnoreCase("ACS")) {
			for (String problem : filesGroupedByProblem_ACO_ACS.keySet()) {
				analyzeResults(problem, algorithm, paramLn, paramVal);
			}
		} else {
			for (String problem : filesGroupedByProblem_ACO_EAS.keySet()) {
				analyzeResults(problem, algorithm, paramLn, paramVal);
			}
		}
	}
	
	// Getter method: Return parsed results for PBIL in a HashMap.
	public HashMap<String, HashMap<String, String>>  getParsedResults_ACO_ACS() {
		return parsedResults_ACO_ACS;
	}
	
	// Getter method: Return parsed results for PBIL in a HashMap.
	public HashMap<String, HashMap<String, String>>  getParsedResults_ACO_EAS() {
		return parsedResults_ACO_EAS;
	}
	
	// Initialize with problem names.
	private void initializeHashMaps() {		
		for (String problem : filesGroupedByProblem_ACO_ACS.keySet()) {
			parsedResults_ACO_ACS.put(problem, new HashMap<String, String>());
		}
		
		for (String problem : filesGroupedByProblem_ACO_EAS.keySet()) {
			parsedResults_ACO_EAS.put(problem, new HashMap<String, String>());
		}
	}
	
	// Helper method: Return path to specified file.
	private String getFilePath(File file) {
		return folderPath + "/" + file.getName();
	}
	
	// Helper method: Print file not found error.
	private void printFileNotFound(String path) {
		System.out.println("Unable to open file '" + path + "'");
	}
			
	// Group all the result file paths by problem name for fast look up.
	private void groupFilesByProblem() throws IOException {
		for (File file : resultsFiles) {
			if (!file.getName().trim().equalsIgnoreCase(".DS_Store")) {
				String filePath = getFilePath(file);
				
				boolean isACS = true;
				try {
					// Read problem name.
					BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath));
					String problemName = bufferedReader.readLine();
					String algorithm = bufferedReader.readLine();
					
					isACS = algorithm.equalsIgnoreCase("ACS") ? true : false;
					bufferedReader.close();
					
					HashMap<String, ArrayList<String>> listOfProblems = isACS ?  
							filesGroupedByProblem_ACO_ACS : filesGroupedByProblem_ACO_EAS; 
					ArrayList<String> listOfFiles;
					if (listOfProblems.containsKey(problemName)) {
						listOfFiles = listOfProblems.get(problemName);
					} else {
						listOfFiles = new ArrayList<String>();
					}
					// Push to map.
					listOfFiles.add(filePath);
					listOfProblems.put(problemName, listOfFiles);
				} catch (FileNotFoundException e) {
					printFileNotFound(filePath);
				}
			}
		}
	}
	
	// Run analysis and fill in values for the HashMaps.
	private void analyzeResults(String prob, String algorithm, 
			int paramLineNum, String targetValue) throws IOException {
		ArrayList<String> files;
		if (algorithm.equalsIgnoreCase("ACS")) {
			files = filesGroupedByProblem_ACO_ACS.get(prob);
		} else {
			files = filesGroupedByProblem_ACO_EAS.get(prob);
		}
		
		/* Handling parameter queries:
		 * If we want to examine how the parameters affect performance,
		 * we only look at one experiment per problem (other parameters are fix.)
		 * Now need to loop through the experiments to find the one we need.
		 */
		if (paramLineNum != AnalyzeResultsController.NO_DATA) {
			boolean foundExperiment = false;
			for (int i = 0; i < files.size(); i++) {
				String file = files.get(i);
				try {
					BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
					String line = "";
					
					/* Handling results for default experiment. 
					 * We need to make sure we match values for all fields (not just one),
					 * because each field represents a fixed value for all other experiments.
					 * So we cannot use the paramLineNum - targetValue matching here. 
					 */
					if (paramLineNum == AnalyzeResultsController.DEFAULT) {
						// See if rho matches default value.
						for (int l = 0; l < LineNumberACO.RHO.getNumVal(); l++) {
							line = bufferedReader.readLine();
						}
						String defaultRho = String.valueOf(0.7);
						if (line.equalsIgnoreCase(defaultRho)) {
							// See if Q0 matches.
							line = bufferedReader.readLine();
							String defaultQ0 = String.valueOf(0.9);
							if (line.equalsIgnoreCase(defaultQ0) || 
									algorithm.equalsIgnoreCase("EAS")) {
								// See if alpha matches.
								line = bufferedReader.readLine();
								String defaultAlpha = String.valueOf(1.0);
								if (line.equalsIgnoreCase(defaultAlpha)) {
									// See if beta matches.
									line = bufferedReader.readLine();
									String defaultBeta = String.valueOf(2.0);
									if (line.equalsIgnoreCase(defaultBeta)) {
										// See if mutation probability matches.
										bufferedReader.readLine();
										bufferedReader.readLine();
										bufferedReader.readLine();
										line = bufferedReader.readLine();
										String defaultAntNum = String.valueOf(10);
										
										if (line.equalsIgnoreCase(defaultAntNum)) {
											// Finally we found the experiment with all default values.
											foundExperiment = true;
											files.clear();
											files.add(file);
											break;
										}
									}
								}
							}
						}
					} else {
						// Look for the experiment we care about.
						for (int l = 0; l < paramLineNum; l++) {
							line = bufferedReader.readLine();
						}
						
						// Handling two-parameter alpha-beta ratio.
						boolean abRatioMatch = false;
						if (paramLineNum == LineNumberACO.ALPHA.getNumVal()) {
							String betaLine = bufferedReader.readLine();
							if (Double.parseDouble(line) / Double.parseDouble(betaLine) == 
									Double.parseDouble(targetValue)) {
								abRatioMatch = true;
							}
						}
						
						if (line.trim().equalsIgnoreCase(targetValue) || abRatioMatch) {
							// Now the files ArrayList contains only the experiment we care about.
							foundExperiment = true;
							files.clear();
							files.add(file);
							break;
						}
					}
					bufferedReader.close();
				} 
				catch(FileNotFoundException e) {
					printFileNotFound(file); 
				}
			}
			
			if (!foundExperiment) {
				System.out.println("Didn't find experiment.");
				System.out.println("Algorithm: " + algorithm);
				System.out.println("Parameter Line Number: " + paramLineNum);
				System.out.println("Target Value: " + targetValue);
				return;
			}
		} 

		// Factors we are considering.
		double avgPathLength = 0;
		
		// Iterate through all files associated with this problem.
		for (int i = 0; i < files.size(); i++) {
			String file = files.get(i);
			try {
				BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
				String line;
				int lineNum = 1;
				while ((line = bufferedReader.readLine()) != null) {
					if (lineNum == LineNumberACO.AVG_LENGTH.getNumVal()) {
						avgPathLength = Double.parseDouble(line);
						
						
					}
					lineNum++;
				}
				bufferedReader.close();
				
				// Push values to HashMap.
				HashMap<String, String> results = new HashMap<String, String>();
				results.put(AVG_LENGTH, String.valueOf(avgPathLength));
				
				// Push HashMaps to parsed results as a value.
				if (algorithm.equalsIgnoreCase("ACS")) {
					parsedResults_ACO_ACS.put(prob, results);
				} else {
					parsedResults_ACO_EAS.put(prob, results);
				}
			}
			catch(FileNotFoundException e) {
				printFileNotFound(file);
			}
		}
	}
}
