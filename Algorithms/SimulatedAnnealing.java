package Algorithms;

import java.util.*;

public class SimulatedAnnealing extends EvolAlgorithms {//Just inherits the fitness function and some MAXSAT output conversion functions



	// Maximum fitness found so far in any generation
	private int maxFitnessSoFar = 0;
	// Individual Solution with Maximum fitness found so far in any generation
	private ArrayList<Integer> bestSolution;
	//Current Candidate 
	private ArrayList<Integer>currentCandidate ;
	// Keeps track of whether a full solution has been found
	private boolean foundSATSolution = false;
	private int iteration = 0;
	private long endTime;
	private double maxTemp;
	private double minTemp;
	private double decayRate;
	private int optimalUnSat = Integer.MAX_VALUE;

	// Constructor
	public SimulatedAnnealing(int literalNumber, ArrayList<ArrayList<Integer>> satProblem, int optimalUnSat,double minTemp, double maxTemp) {
		this.satProblem = satProblem;
		this.currentCandidate = initStartingCandidate(literalNumber);
		this.optimalUnSat = optimalUnSat;
		this.minTemp = minTemp;
		this.maxTemp = maxTemp;
		this.decayRate = 1.0 / currentCandidate.size();
	}


	public Results anneal() {
		long startTime = System.currentTimeMillis();
		long executionTime = -1;
		double  temperature = maxTemp; 
		while ( !foundSATSolution && temperature >= minTemp /*&& iteration < 100*/) {
			iteration++;
			temperature = maxTemp * Math.exp(-1 * (iteration * decayRate));
			mutate(temperature);


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

		System.out.println("Simulated Annealing Output:");
		System.out.println("Number Of Variables: " + currentCandidate.size());
		System.out.println("Number Of Clauses: " + satProblem.size());
		System.out.println("Satisfied Clauses: " + maxFitnessSoFar + " out of " + satProblem.size() + " ("
				+ (satProblem.size() - maxFitnessSoFar) + " unsatisfied clauses).");
		System.out.println("Best Variable Assignment: " + Arrays.toString(binaryToNumber(bestSolution)));
		System.out.println("Percent satisfied: " + percent + "%");
		System.out.println("Best Iteration:" + bestGeneration);
		System.out.println("Total execution time: " + executionTime + " milliseconds");

		Results result = new Results("Simulated Annealing",currentCandidate.size(), satProblem.size(), executionTime,
				(satProblem.size() - maxFitnessSoFar), percent, bestSolution/*binaryToNumber(bestSolution)*/, bestGeneration);
		return result;

	}



	// Update the max fitness encountered so far
	private void updateMaxFitness(int fitness) {	
		if (fitness > maxFitnessSoFar) {
			bestGeneration = iteration;
			maxFitnessSoFar = fitness;
			bestSolution = new ArrayList<Integer>();
			bestSolution.clear();
			bestSolution.addAll(currentCandidate);
			// If all clauses are satisfied
			if (fitness == satProblem.size()) {
				foundSATSolution = true;
			}
			endTime = System.currentTimeMillis();//Time to find best solution

		}

	}

	private ArrayList<Integer> initStartingCandidate(int literalNumber){

		ArrayList<Integer> individual = new ArrayList<Integer>();

		for (int j = 0; j < literalNumber; j++) {
			Random rand = new Random();
			Integer value = rand.nextInt(2);
			individual.add(value);
		}
		return individual;
	}

	private void mutate(double temperature) {
		boolean flippedToOne = true;
		boolean accept = true;

		for (int j = 0; j < currentCandidate.size(); j++) {
			if (currentCandidate.get(j) == 1) {
				currentCandidate.set(j, 0);
				flippedToOne = false;
			} else if (currentCandidate.get(j) == 0) {
				currentCandidate.set(j, 1);
				flippedToOne = true;
			}

			//Check fitness
			int fitness = evaluateCandidate(currentCandidate);
			double increase =  fitness - maxFitnessSoFar;
			double mutateProb =   1.0 / ( 1 + Math.exp(-1 * (increase / temperature)));
			accept = randomGenerator.nextDouble() < mutateProb;
			updateMaxFitness(fitness);



			if ( !accept && flippedToOne) {
				//revert change
				currentCandidate.set(j, 0);
			} else if  ( !accept && !flippedToOne){
				//revert change
				currentCandidate.set(j, 1);
			}

		}

	}



}
