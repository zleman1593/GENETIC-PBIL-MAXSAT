package Algorithms;

import java.util.*;

public class HybridSAGA extends GeneticSuper {
	private ArrayList<ArrayList<Integer>> twoMembers;
	private int fitnessOfPop1;
	// Constructor.
	public HybridSAGA(int literalNumber, int maxIteration, String crossOverMethod, double crossOverProb,
			double mutateProb, ArrayList<ArrayList<Integer>> satProblem) {
		this.satProblem = satProblem;
		this.population = new ArrayList<ArrayList<Integer>>(); //initPopulation(2, literalNumber);
		this.maxIteration = maxIteration;
		this.crossOverProb = crossOverProb;
		this.mutateProb = mutateProb;
		this.crossOverMethod = crossOverMethod;
		this.timeout = Long.MAX_VALUE;
		this.twoMembers = new ArrayList<ArrayList<Integer>>();
	}

	// Constructor for tests
	public HybridSAGA( int literalNumber, int maxIteration, String crossOverMethod, double crossOverProb,
			double mutateProb, ArrayList<ArrayList<Integer>> satProblem, int optimalUnSat) {
		this.satProblem = satProblem;
		this.population = new ArrayList<ArrayList<Integer>>(); //initPopulation(2, literalNumber);
		this.maxIteration = maxIteration;
		this.crossOverProb = crossOverProb;
		this.mutateProb = mutateProb;
		this.crossOverMethod = crossOverMethod;
		this.optimalUnSat = optimalUnSat;
	}



	public Results solve() {
		long startTime = System.currentTimeMillis();
		long executionTime = -1;

		double minTemp = 0.0001;
		double maxTemp = 0.5;

			//SA cycle
			sa(minTemp,maxTemp);
		
			//GA cycle
			ga();


			// If time out or reached optimal number of clauses satisfied, return result.
			int currentUnsat = satProblem.size() - maxFitnessSoFar;
			if (System.currentTimeMillis() - startTime >= timeout) {
				foundSATSolution = true;
				//break;
			} else if (currentUnsat <= optimalUnSat) {
				// If finished all iterations, calculate time.
				executionTime = endTime - startTime;
				foundSATSolution = true;
				//break;
			}
		

		double percent = ((double) maxFitnessSoFar * 100 / (double) satProblem.size());

		System.out.println("SAGA Output:");
		System.out.println("Number Of Variables: " + population.get(0).size());
		System.out.println("Number Of Clauses: " + satProblem.size());
		System.out.println("Satisfied Clauses: " + maxFitnessSoFar + " out of " + satProblem.size() + " ("
				+ (satProblem.size() - maxFitnessSoFar) + " unsatisfied clauses).");
		System.out.println("Best Variable Assignment: " + Arrays.toString(binaryToNumber(bestSolution)));
		System.out.println("Percent satisfied: " + percent + "%");
		System.out.println("Best Generation:" + bestGeneration);
		System.out.println("Total execution time: " + executionTime + " milliseconds");

		Results result = new Results("SAGA", population.get(0).size(), satProblem.size(), executionTime,
				(satProblem.size() - maxFitnessSoFar), percent, binaryToNumber(bestSolution), bestGeneration);
		return result;

	}

	/*
	 *  Crosses over two individuals using a single  random crossover point
	 */
	private void singlePointCrossover(int popSize) {
			for (int i = 0; i < popSize/2; i++) {
					// Pick cross over location
					int crossOverLocation = randomGenerator.nextInt(population.get(i).size());

					ArrayList<Integer> newSibling1 =  (ArrayList<Integer>) twoMembers.get(0).clone();
					ArrayList<Integer> newSibling2 =  (ArrayList<Integer>) twoMembers.get(1).clone();

					// Copy first part of A into C
					List<Integer> c = new ArrayList<Integer>(newSibling1.subList(0, crossOverLocation));
					// Replace first part of A with First part of B
					for (int j = 0; j < crossOverLocation; j++) {
						int value = newSibling2.get(j);
						newSibling1.set(j, value);
					}
					// Replace first part of B with C
					for (int j = 0; j < c.size(); j++) {
						int value = c.get(j);
						newSibling2.set(j, value);
					}
				
					population.add(newSibling1);
					population.add(newSibling2);
			}
	
	}
	
	
	void sa(double minTemp, double maxTemp){
		int fitnessOfFirstMember = 0;
		ArrayList<Integer> firstMember;
		int fitnessOfSecondMember = 0;
		ArrayList<Integer> secondMember;

		for (int i = 0; i < maxIteration && !foundSATSolution; i++) {
			currentGeneration = i + 1;


			boolean increment = true;
			int numberOfConsecutiveNoImprovements = 0;

			while (numberOfConsecutiveNoImprovements <= 10){


				SimulatedAnnealing anneal1 = new SimulatedAnnealing(population.get(0).size(),satProblem,2,minTemp,maxTemp);
				Results resultOfFirstAnneal = anneal1.anneal();
				int fitnessOfFirstResult = (resultOfFirstAnneal.numClauses - resultOfFirstAnneal.numUnsatisifiedClauses);

				if (fitnessOfFirstResult > fitnessOfFirstMember) {
					firstMember =  resultOfFirstAnneal.rawAssignment;
					increment = false;

				} else if (fitnessOfFirstResult > fitnessOfSecondMember) {

					secondMember =  resultOfFirstAnneal.rawAssignment;
					increment = false;
				}

				SimulatedAnnealing anneal2 = new SimulatedAnnealing(population.get(0).size(),satProblem,2,minTemp,maxTemp);
				Results resultOfSecondAnneal = anneal2.anneal();
				int fitnessOfSecondResult = (resultOfSecondAnneal.numClauses - resultOfSecondAnneal.numUnsatisifiedClauses);

				if (fitnessOfSecondResult > fitnessOfFirstMember) {
					firstMember =  resultOfSecondAnneal.rawAssignment;
					increment = false;
					fitnessOfFirstMember = fitnessOfFirstResult;

				} else if (fitnessOfSecondResult> fitnessOfSecondMember) {

					secondMember =  resultOfSecondAnneal.rawAssignment;
					increment = false;
					fitnessOfSecondMember = fitnessOfSecondResult;
				}


				if (increment) {numberOfConsecutiveNoImprovements++; increment = true;}

			}

			twoMembers.add(firstMember);
			twoMembers.add(secondMember);
			
		}
		fitnessOfPop1 = Math.max(fitnessOfFirstMember,fitnessOfSecondMember);
	}

	
	
	void ga(){
		int counter = 0;
		int fitnessOfPop2;
		while(counter <= 10){

			if (foundSATSolution) {
				System.out.println("Fully Satisfied All Clauses");
				break;
			}

			singlePointCrossover(60);
			mutate(mutateProb);

			
			ArrayList<ArrayWithFitness> allIndividualsWithFitness = getFitnessForAllIndividuals();
			fitnessOfPop2 = allIndividualsWithFitness.get(0).fitness;
			if (fitnessOfPop2 < fitnessOfPop1){
				//reset couter
				counter = 0;

				//population1 =  2;
			} else{
				//increment the iteration number
				counter++;

			}

		}
	}


}
