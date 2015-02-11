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
				//If literal is not same value as truth condition then...   (i.e true false, false true)
				if(satProblem.get(i).get(j) != values.get(j)){
					//Don't count clause as satisfied
					break;
				} else if ((j == satProblem.size() -1) && satProblem.get(i).get(j) != values.get(j)){
					//If at last literal in clause and it is true true or false false then count clauses as satisfied
					fitness++;
				}
			}
		}
		return fitness;
	}
}
