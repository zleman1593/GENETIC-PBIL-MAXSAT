import java.io.*;
import java.util.ArrayList;


public class SatProblem {

	private ArrayList<ArrayList<Integer>> satProblem = new ArrayList<ArrayList<Integer>>();	
	private ArrayList<String> readFile(String filename)
	{
		ArrayList<String> records = new ArrayList<String>();
	  try
	  {
	    BufferedReader reader = new BufferedReader(new FileReader(filename));
	    String line;
	    while ((line = reader.readLine()) != null)
	    {
	      records.add(line);
	    }
	    reader.close();
	    return records;
	  }
	  catch (Exception e)
	  {
	    System.err.format("Exception occurred trying to read '%s'.", filename);
	    e.printStackTrace();
	    return null;
	  }
	}
	
	public void readSatFile(String infile){
		ArrayList<ArrayList<Integer>> clauses = new ArrayList<ArrayList<Integer>>();
		ArrayList<String> choppedCNF = readFile(infile);
		for (int i = 0; i < choppedCNF.size() ;i++){
			
			if(!choppedCNF.get(i).startsWith("c")){
				//Number of clauses and literals
				if(choppedCNF.get(i).startsWith("p")){
					String[] values = choppedCNF.get(i).split(" ");
					int h = 0;
				}
			}	
		}
		
		
		
		
		

		int numberOfClauses = 5;
		for (int i = 0; i < numberOfClauses ;i++){
			int clauseLength = 10;
			ArrayList<Integer> clause = new ArrayList<Integer>();
			for (int j = 0; j < clauseLength ;j++){
				Integer test = 10;
				clause.add(test);
			}
			clauses.add(clause);
		}
		this.satProblem = clauses;
	}

	public ArrayList<ArrayList<Integer>> getProblem(){
		return this.satProblem;
	}


	public int  evaluateCandidate(ArrayList<Integer> values){
		int fitness = 0;
		//Look at every clause
		for (int i = 0; i < satProblem.size() ;i++){
			//Look at every literal
			for (int j = 0; j < satProblem.size() ;j++){
				//If 
				int literalTruth = satProblem.get(i).get(j);
				if(((literalTruth < 0) && values.get(Math.abs(literalTruth)) == 0)   || ((literalTruth >= 0) && values.get(Math.abs(literalTruth)) == 1) ){
					//Count clause as satisfied
					fitness++;
					break;
				} 
			}
		}
		return fitness;
	}
}
