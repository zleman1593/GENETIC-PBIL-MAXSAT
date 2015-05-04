package Algorithms;
import java.util.*;

public class Genetic extends GeneticSuper {



	// Constructor.
	public Genetic(int popSize, int literalNumber, int maxIteration, String crossOverMethod, double crossOverProb,
			double mutateProb, ArrayList<ArrayList<Integer>> satProblem) {
		this.satProblem = satProblem;
		this.population = initPopulation(popSize, literalNumber);
		this.maxIteration = maxIteration;
		this.crossOverProb = crossOverProb;
		this.mutateProb = mutateProb;
		this.crossOverMethod = crossOverMethod;
		this.winners = 2;
		this.sample = 5;
		this.timeout = Long.MAX_VALUE;
	}
	
	// Constructor for tests
	public Genetic(int popSize, int literalNumber, int maxIteration, String crossOverMethod, double crossOverProb,
			double mutateProb, ArrayList<ArrayList<Integer>> satProblem, int optimalUnSat) {
		this.satProblem = satProblem;
		this.population = initPopulation(popSize, literalNumber);
		this.maxIteration = maxIteration;
		this.crossOverProb = crossOverProb;
		this.mutateProb = mutateProb;
		this.crossOverMethod = crossOverMethod;
		this.winners = 2;
		this.sample = 5;
		this.optimalUnSat = optimalUnSat;
	}

	public Results evolve(String selectionMethod) {
		long startTime = System.currentTimeMillis();
		long executionTime = -1;
		for (int i = 0; i < maxIteration && !foundSATSolution; i++) {
			currentGeneration = i + 1;
			if (selectionMethod.equalsIgnoreCase("rs") || selectionMethod.equalsIgnoreCase("bs")) {
				rankBoltzSelect(selectionMethod);
			} else {
				tournamentSelect();
			}

			if (foundSATSolution) {
				System.out.println("Fully Satisfied All Clauses");
				break;
			}

			if (crossOverMethod.equalsIgnoreCase("1c")) {
				singlePointCrossover(crossOverProb);
			} else {
				uniformCrossover(crossOverProb);
			}

			mutate(mutateProb);
			// eightCore(mutateProb, population.size());
			
			// If time out or reached optimal number of clauses satisfied, return result.
			int currentUnsat = satProblem.size() - maxFitnessSoFar;
			if (System.currentTimeMillis() - startTime >= timeout) {
				foundSATSolution = true;
				break;
			} else if (currentUnsat <= optimalUnSat) {
				// If finished all iterations, calculate time.
				executionTime = endTime - startTime;
				foundSATSolution = true;
				break;
			}
		}
		
		double percent = ((double) maxFitnessSoFar * 100 / (double) satProblem.size());

		System.out.println("Genetic Algorithm Output:");
		System.out.println("Number Of Variables: " + population.get(0).size());
		System.out.println("Number Of Clauses: " + satProblem.size());
		System.out.println("Satisfied Clauses: " + maxFitnessSoFar + " out of " + satProblem.size() + " ("
				+ (satProblem.size() - maxFitnessSoFar) + " unsatisfied clauses).");
		System.out.println("Best Variable Assignment: " + Arrays.toString(binaryToNumber(bestSolution)));
		System.out.println("Percent satisfied: " + percent + "%");
		System.out.println("Best Generation:" + bestGeneration);
		System.out.println("Total execution time: " + executionTime + " milliseconds");

		Results result = new Results("Genetic Algorithm", population.get(0).size(), satProblem.size(), executionTime,
				(satProblem.size() - maxFitnessSoFar), percent, binaryToNumber(bestSolution), bestGeneration);
		return result;

	}



}
