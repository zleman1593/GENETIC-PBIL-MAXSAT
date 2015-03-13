import java.io.IOException;
import java.util.ArrayList;

public class AnalyzeResultsController {
	static String[] MAXSATProblems = TestController.files;
	static String folderPath = "Combined_Results";
	
	/* For all the array lists below: 
	 * Index 0 stores a list of what's described by the variable name
	 * Index 1 stores a list of the corresponding MAXSAT problem names
	 * Index 2 stores an array list of array list of parameter settings 
	 * Sorry about the complication. */ 
	ArrayList<ArrayList<String>> fastestTimeGA = new ArrayList<ArrayList<String>>();
	ArrayList<ArrayList<String>> fastestTimePBIL = new ArrayList<ArrayList<String>>();
	
	
	public static void main(String[] args) throws IOException {		
		AnalyzeResults analyzeResults = new AnalyzeResults();
		
		// Check if any problem is unused.
		analyzeResults.printUnusedProblems();
		
	}

}
