import java.util.ArrayList;
import java.util.Random;

public class EvolAlgorithms {
	//The MAXSAT problem
	protected ArrayList<ArrayList<Integer>> satProblem;
	//Random number generator
	protected Random randomGenerator = new Random();
	protected int bestGeneration;
	protected int currentGeneration;
	protected int targetPercentSolved;
	
	/* Fitness Function */
	public int evaluateCandidate(ArrayList<ArrayList<Integer>> satProblem, ArrayList<Integer> values) {
		int fitness = 0;
		// Look at every clause
		for (int i = 0; i < satProblem.size(); i++) {
			// Look at every literal
			for (int j = 0; j < satProblem.get(i).size(); j++) {
				int literalTruth = satProblem.get(i).get(j);
				if (((literalTruth < 0) && values.get(Math.abs(literalTruth) - 1) == 0)
						|| ((literalTruth > 0) && values.get(Math.abs(literalTruth) - 1) == 1)) {
					// Count clause as satisfied
					fitness++;
					break;
				}
			}
		}
		return fitness;
	}
}
