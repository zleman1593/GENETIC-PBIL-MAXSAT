package ACO;
import java.io.*;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.HashMap;

public class AnalyzeResultsController {
	// Parameters.
	public static final int NO_DATA = -1;
	public static final int DEFAULT = -2;
	// Folder name.
	static final String folderPath = "Graph_Data";
	// Store the results.
	private static HashMap<String, HashMap<String, String>> results_Parameters_ACO_ACS;
	private static HashMap<String, HashMap<String, String>> results_Parameters_ACO_EAS;
	// Store the parameters and their respective values.
	private static HashMap<Integer, ArrayList<String>> parameters_ACO_ACS;
	private static HashMap<Integer, ArrayList<String>> parameters_ACO_EAS;
	
	public static void main(String[] args) throws IOException {
		// Delete all files in the directory.
		File folder = new File(folderPath);
		for (File file : folder.listFiles()) {
			try {
			    file.delete();
			} catch (Exception e) {
				System.out.println("Cannot delete file: " + file.getName());
			}
		}
		
		/* Run parameter analysis. */
		// GA
		initializeParameters_ACO_ACS();
		// Get graph data for the experiment with all default parameters.
		AnalyzeResults analyzeResultsDefaultParameters_GA = new AnalyzeResults("ACS", DEFAULT, "parameter values");
		results_Parameters_ACO_ACS = analyzeResultsDefaultParameters_GA.getParsedResults_ACO_ACS();
		new GenerateGraphData(results_Parameters_ACO_ACS, "ACS", true, "Default", "parameter values");

		// Get graph data for the rest of the experiments.
		for (Integer lineNum : parameters_ACO_ACS.keySet()) {
			for (String value: parameters_ACO_ACS.get(lineNum)) {
				AnalyzeResults analyzeResultsParameters_ACO_ACS = new AnalyzeResults("ACS", lineNum, value);
				results_Parameters_ACO_ACS = analyzeResultsParameters_ACO_ACS.getParsedResults_ACO_ACS();
				String param = getParameterName("ACO", lineNum);
				new GenerateGraphData(results_Parameters_ACO_ACS, "ACS", true, param, value);
			}
		}
		
		initializeParameters_ACO_EAS();
		AnalyzeResults analyzeResultsDefaultParameters_ACO_EAS = new AnalyzeResults("EAS", DEFAULT, "parameter values");
		results_Parameters_ACO_EAS = analyzeResultsDefaultParameters_ACO_EAS.getParsedResults_ACO_EAS();
		new GenerateGraphData(results_Parameters_ACO_EAS, "EAS", true, "Default", "parameter values");

		// Get graph data for the rest of the experiments.
		for (Integer lineNum : parameters_ACO_EAS.keySet()) {
			for (String value: parameters_ACO_EAS.get(lineNum)) {
				AnalyzeResults analyzeResultsParameters_ACO_EAS = new AnalyzeResults("EAS", lineNum, value);
				results_Parameters_ACO_EAS = analyzeResultsParameters_ACO_EAS.getParsedResults_ACO_EAS();
				String param = getParameterName("EAS", lineNum);
				new GenerateGraphData(results_Parameters_ACO_EAS, "EAS", true, param, value);
			}
		}
		
		System.out.println("Finished running analysis and generating all graph data.");
		System.out.println("Please refresh the Graph_Data folder to see the results.");
		/* End parameter analysis*/
	}
	
	public static void initializeParameters_ACO_ACS() {
		parameters_ACO_ACS = new HashMap<Integer, ArrayList<String>>();
		initializeParameters_ACO(parameters_ACO_ACS);
		
		// Varying q0.
		ArrayList<String> listOfParamValsQ0 = new ArrayList<String>();
		listOfParamValsQ0.add(String.valueOf(0.1));
		listOfParamValsQ0.add(String.valueOf(0.3));
		listOfParamValsQ0.add(String.valueOf(0.5));		
		listOfParamValsQ0.add(String.valueOf(1.0));		
		parameters_ACO_ACS.put(LineNumberACO.Q0.getNumVal(), listOfParamValsQ0);
	}
	
	public static void initializeParameters_ACO_EAS() {
		parameters_ACO_EAS = new HashMap<Integer, ArrayList<String>>();
		initializeParameters_ACO(parameters_ACO_EAS);
	}
	
	public static void initializeParameters_ACO(HashMap<Integer, ArrayList<String>> map) {
		ArrayList<String> listOfParamValsRho = new ArrayList<String>();
		ArrayList<String> listOfParamValsAntNum = new ArrayList<String>();
		ArrayList<String> listOfParamValsABRatio = new ArrayList<String>();
		
		// Varying rho.
		listOfParamValsRho.add(String.valueOf(0.01));
		listOfParamValsRho.add(String.valueOf(0.3));
		listOfParamValsRho.add(String.valueOf(1.0));		
		map.put(LineNumberACO.RHO.getNumVal(), listOfParamValsRho);
		
		// Varying Alpha-beta ratio.
		listOfParamValsABRatio.add(String.valueOf(1.0));
		listOfParamValsABRatio.add(String.valueOf(2.0));
		listOfParamValsABRatio.add(String.valueOf(0.25));
		map.put(LineNumberACO.ALPHA.getNumVal(), listOfParamValsABRatio);
		
		// Varying number of ants.
		listOfParamValsAntNum.add(String.valueOf(5));
		listOfParamValsAntNum.add(String.valueOf(25));
		listOfParamValsAntNum.add(String.valueOf(50));		
		listOfParamValsAntNum.add(String.valueOf(100));		
		map.put(LineNumberACO.NUM_ANTS.getNumVal(), listOfParamValsAntNum);
	}

	// Returns the name of the parameter given the algorithm and its line number in results file.
	public static String getParameterName(String algorithm, int line) 
		throws InvalidParameterException {
		if (line == LineNumberACO.RHO.getNumVal()) {
			return "Rho";
		} else if (line == LineNumberACO.Q0.getNumVal()) {
			return "Q0";
		} else if (line == LineNumberACO.ALPHA.getNumVal()) {
			return "Alpha:Beta Ratio";
		} else if (line == LineNumberACO.BETA.getNumVal()) {
			return "Beta";
		} else if (line == LineNumberACO.NUM_ANTS.getNumVal()) {
			return "Number of Ants";
		} else {
			throw new InvalidParameterException("Invalid line number for " + algorithm);
		}
	}
	
	// Print out the fields in the results objects.
	public static void test(HashMap<String, HashMap<String, String>> results) {
		for (String problem : results.keySet()) {
			HashMap<String, String> map = results.get(problem);
			for (String key : map.keySet()) {
				System.out.println("KEY " + key);
				System.out.println("VALUE " + map.get(key));
			}
		}
	}
}