import java.util.ArrayList;


public class GAAlgorithms {
	
	/*Fitness Function*/
	public int evaluateCandidate(ArrayList<ArrayList<Integer>> satProblem, 
			ArrayList<Integer> values) {
		int fitness = 0;
		//Look at every clause
		for (int i = 0; i < satProblem.size(); i++){
			//Look at every literal
			for (int j = 0; j <  satProblem.get(i).size();j++){
				int literalTruth = satProblem.get(i).get(j);
				if(((literalTruth < 0) && values.get(Math.abs(literalTruth) -1 ) == 0)  || ((literalTruth > 0) && values.get(Math.abs(literalTruth) -1 ) == 1) ){
					//Count clause as satisfied
					fitness++;
					break;
				} 
			}
		}
		return fitness;
	}
}
