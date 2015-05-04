package Algorithms;
import java.util.ArrayList;
import java.util.Random;

public class EvolAlgorithms {
	//The MAXSAT problem
	protected ArrayList<ArrayList<Integer>> satProblem = new ArrayList<ArrayList<Integer>>();
	//Random number generator
	protected Random randomGenerator = new Random();
	protected int bestGeneration;
	protected int currentGeneration;
	protected int targetPercentSolved;
	protected long timeout = 180000;
	
	/* Fitness Function */
	protected int evaluateCandidate(ArrayList<Integer> values) {
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
	
	
	/* Makes the solution more human readable */
	protected int[] binaryToNumber(ArrayList<Integer> solution) {
		int[] display = new int[solution.size()];
		for (int i = 0; i < solution.size(); i++) {
			if (solution.get(i) < 1) {
				display[i] = -1 * (i + 1);
			} else {
				display[i] = (i + 1);
			}
		}
		return display;
	}
	
	
}
