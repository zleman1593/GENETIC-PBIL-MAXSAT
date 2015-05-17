package Algorithms;

import java.util.*;

public class HybridSAGA extends GeneticSuper {
	private ArrayList<Integer> saSeed = null;
	private int literalNumber;

	private  double minTemp;
	private double maxTemp;
	private int populationSize;//defaulted 60
	private int numberOfGAIterationsWithoutImprovement = 100;


	// Constructor for preview
	public HybridSAGA( int literalNumber, int maxIteration, double crossOverProb,
			double mutateProb, ArrayList<ArrayList<Integer>> satProblem, double minTemp, double maxTemp, int optimalUnSat,int populationSize, int numberOfGAIterationsWithoutImprovement) {
		this.satProblem = satProblem;
		this.population = new ArrayList<ArrayList<Integer>>(); 
		this.maxIteration = maxIteration;
		this.crossOverProb = crossOverProb;
		this.mutateProb = mutateProb;

		this.optimalUnSat = optimalUnSat;
		this.literalNumber = literalNumber;
		this.sample =  populationSize / 2;
		this.winners = 1;
		this.minTemp = minTemp;
		this.maxTemp = maxTemp;
		this.populationSize = populationSize;
	}

	// Constructor for tests
	public HybridSAGA( int literalNumber, int maxIteration, double crossOverProb,
			double mutateProb, ArrayList<ArrayList<Integer>> satProblem, int optimalUnSat, int populationSize,int numberOfGAIterationsWithoutImprovement) {
		this.satProblem = satProblem;
		this.population = new ArrayList<ArrayList<Integer>>(); 
		this.maxIteration = maxIteration;
		this.crossOverProb = crossOverProb;
		this.mutateProb = mutateProb;
		this.optimalUnSat = optimalUnSat;
		this.literalNumber = literalNumber;
		this.sample =  populationSize / 2;
		this.winners = 1;
		this.minTemp = 0.0001;
		this.maxTemp = 0.5;
		this.populationSize = populationSize;
		this.numberOfGAIterationsWithoutImprovement = numberOfGAIterationsWithoutImprovement;
	}

	public Results solve() {
		long startTime = System.currentTimeMillis();
		long executionTime = -1;

		for (int i = 0; i < maxIteration && !foundSATSolution; i++) {
			currentGeneration = i;
			//SA cycle
			sa(minTemp,maxTemp);

			//GA cycle
			ga();

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
			i++;
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

	//Generates a population by crossing over the best result of SA with a random individual
	private void generateChildren(int popSize) {
		Results resultOfSecondAnneal = new Results();
		for (int i = 0; i < popSize/2; i++) {
			// Pick cross over location
			int crossOverLocation = randomGenerator.nextInt(literalNumber);

			ArrayList<Integer> newSibling1 =  (ArrayList<Integer>) saSeed.clone();
			ArrayList<Integer> newSibling2 =   initStartingCandidate();

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


	//The SA part of the algorithm
	void sa(double minTemp, double maxTemp){

		int maxFitness = 0;
		boolean increment = true;
		int numberOfConsecutiveNoImprovements = 0;

		while (numberOfConsecutiveNoImprovements <= 10 ){

			int currentUnsat = satProblem.size() - maxFitnessSoFar;
			//Update best solution 
			if (currentUnsat <= optimalUnSat) {
				foundSATSolution = true;
				break;
			}

			boolean backwards;
			backwards =  randomGenerator.nextDouble() <= 0.5 ?  true : false; 
			
			
			//Run SA with last and Best result from SA run
			SimulatedAnnealing anneal1 = new SimulatedAnnealing(literalNumber,satProblem,2,minTemp,maxTemp,saSeed,backwards);
			Results resultOfFirstAnneal = anneal1.anneal();
			
			int fitnessOfResult = (resultOfFirstAnneal.numClauses - resultOfFirstAnneal.numUnsatisifiedClauses);

			if (fitnessOfResult > maxFitness) {
				saSeed =  resultOfFirstAnneal.rawAssignment;
				increment = false;
				maxFitness = fitnessOfResult;
			} 

			if (increment) {numberOfConsecutiveNoImprovements++;}
			increment = true;
			updateMaxFitness(maxFitness, saSeed);
		}



	}



	void ga(){

		int counter = 0;
		population = new ArrayList< ArrayList<Integer>>();
		//Get a population based of the best solution so far
		generateChildren(populationSize);
		//While a certain number of generations have not gone by without an improvement 
		while(counter <= numberOfGAIterationsWithoutImprovement){

			if (foundSATSolution) {
				System.out.println("Fully Satisfied All Clauses");
				break;
			}
			//Mutate
			mutate(mutateProb);
			//Crossover members of the population
			singlePointCrossover(crossOverProb);
			//Extreme case of tournament select where half the pop is put into tournament each time and a single member is selected as a winner for each tournament 
			tournamentSelect();

			//Get most fit individual
			ArrayList<ArrayWithFitness> allIndividualsWithFitness = getFitnessForAllIndividuals();
			int fitnessOfNewPop = allIndividualsWithFitness.get(allIndividualsWithFitness.size() - 1).fitness;
			
			if (fitnessOfNewPop > maxFitnessSoFar){
				//update seeds for SA
				saSeed = allIndividualsWithFitness.get(allIndividualsWithFitness.size() - 1).individual;
				//reset counter
				counter = 0;
				maxFitnessSoFar = fitnessOfNewPop;
			} else{
				//increment the iteration number
				counter++;

			}

		}
	}



	private ArrayList<Integer> initStartingCandidate(){

		ArrayList<Integer> individual = new ArrayList<Integer>();

		for (int j = 0; j < literalNumber; j++) {
			Random rand = new Random();
			Integer value = rand.nextInt(2);
			individual.add(value);
		}
		return individual;
	}

}
