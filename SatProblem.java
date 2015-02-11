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


	}

	public ArrayList<ArrayList<Integer>> getPopulation(){

		return this.satProblem;
	}

}
