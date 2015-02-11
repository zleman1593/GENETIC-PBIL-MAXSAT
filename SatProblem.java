import java.io.*;
import java.util.ArrayList;


public class SatProblem {

	private ArrayList<ArrayList<Integer>> satProblem = new ArrayList<ArrayList<Integer>>();	
	private int numLiterals = 0;
	private int numClauses = 0;
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

	public void createSatProblemFromFile(String infile){
		ArrayList<ArrayList<Integer>> clauses = new ArrayList<ArrayList<Integer>>();
		ArrayList<String> choppedCNF = readFile(infile);
		for (int i = 0; i < choppedCNF.size() ;i++){
			if(!choppedCNF.get(i).startsWith("c")){
				//Number of clauses and literals
				if(choppedCNF.get(i).startsWith("p")){
					String[] values = choppedCNF.get(i).split(" ");
					this.numLiterals = Integer.parseInt(values[2]);
					this.numClauses = Integer.parseInt(values[3]);
				}else{

					ArrayList<Integer> clause = new ArrayList<Integer>();
					String[] literalsInAClause = choppedCNF.get(i).split(" ");
					for (int j = 0; j < literalsInAClause.length - 1; j++){
						clause.add(Integer.parseInt(literalsInAClause[j]));
					}
					clauses.add(clause);
				}	
			}
		}
		this.satProblem = clauses;
	}

	public ArrayList<ArrayList<Integer>> getProblem(){
		return this.satProblem;
	}

	public int getNumLiterals(){
		return this.numLiterals;
	}

	public int getNumClauses(){
		return this.numClauses;
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
