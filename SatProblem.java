import java.util.ArrayList;


public class SatProblem {

	private ArrayList<ArrayList<Integer>> satProblem = new ArrayList<ArrayList<Integer>>();

	public void readSatFile(){
		ArrayList<ArrayList<Integer>> clauses = new ArrayList<ArrayList<Integer>>();
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
