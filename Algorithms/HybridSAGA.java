package Algorithms;

import java.util.*;

public class HybridSAGA extends GeneticSuper {
	private int fitnessOfPop1;
	private int fitnessOfPop2;
	private ArrayList<Integer> firstMember = null;
	private ArrayList<Integer> secondMember = null;
	private int literalNumber;
	
	//Make params
	private  double minTemp;
	private double maxTemp;
	private int populationSize = 60;
	private int numberOfGAIterationsWithoutImprovement = 100;

	
	// Constructor for preview
	public HybridSAGA( int literalNumber, int maxIteration, double crossOverProb,
			double mutateProb, ArrayList<ArrayList<Integer>> satProblem, double minTemp, double maxTemp, int optimalUnSat) {
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
	}

	// Constructor for tests
	public HybridSAGA( int literalNumber, int maxIteration, double crossOverProb,
			double mutateProb, ArrayList<ArrayList<Integer>> satProblem, int optimalUnSat) {
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

	private void generateChildren(int popSize) {
		Results resultOfSecondAnneal = new Results();
		for (int i = 0; i < popSize/2; i++) {
			// Pick cross over location
			int crossOverLocation = randomGenerator.nextInt(literalNumber);

			ArrayList<Integer> newSibling1 =  (ArrayList<Integer>) firstMember.clone();
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
	
	
	
//	private void generateChildren(int popSize) {
//		for (int i = 0; i < popSize; i++) {
//	
//			ArrayList<Integer> newSibling1 =  (ArrayList<Integer>) firstMember.clone();
//
//			population.add(newSibling1);
//	
//		}
//
//	}
//	
	



	void sa(double minTemp, double maxTemp){

		int fitnessOfFirstMember = 0;

		boolean increment = true;
		int numberOfConsecutiveNoImprovements = 0;

		while (numberOfConsecutiveNoImprovements <= 10 ){
			
			int currentUnsat = satProblem.size() - maxFitnessSoFar;
			if (currentUnsat <= optimalUnSat) {
				foundSATSolution = true;
				break;
			}

			boolean backwards;
			if( randomGenerator.nextDouble() <= 0.5) {backwards = true;} else { backwards = true;} 
			SimulatedAnnealing anneal1 = new SimulatedAnnealing(literalNumber,satProblem,2,minTemp,maxTemp,firstMember,backwards);
			Results resultOfFirstAnneal = anneal1.anneal();
			int fitnessOfFirstResult = (resultOfFirstAnneal.numClauses - resultOfFirstAnneal.numUnsatisifiedClauses);

			if (fitnessOfFirstResult > fitnessOfFirstMember) {
				firstMember =  resultOfFirstAnneal.rawAssignment;
				increment = false;
				fitnessOfFirstMember = fitnessOfFirstResult;

			} 
			
			if (increment) {numberOfConsecutiveNoImprovements++;}
			increment = true;
			updateMaxFitness(fitnessOfFirstMember, firstMember);
		}


	
		fitnessOfPop1 = fitnessOfFirstMember;
		
	}



	void ga(){
		
		int counter = 0;
		population = new ArrayList< ArrayList<Integer>>();
		generateChildren(populationSize);
		while(counter <= numberOfGAIterationsWithoutImprovement){

			if (foundSATSolution) {
				System.out.println("Fully Satisfied All Clauses");
				break;
			}
			mutate(mutateProb);
			if (population.size() < populationSize){
				population.add((ArrayList<Integer>) population.get(0).clone());
			}//Debug
			singlePointCrossover(crossOverProb);


			tournamentSelect();

			//Make this more efficient
			ArrayList<ArrayWithFitness> allIndividualsWithFitness = getFitnessForAllIndividuals();
			fitnessOfPop2 = allIndividualsWithFitness.get(allIndividualsWithFitness.size() - 1).fitness;
			if (fitnessOfPop2 > fitnessOfPop1){
				//update seeds for SA
				secondMember =  allIndividualsWithFitness.get(allIndividualsWithFitness.size() - 1).individual;
				firstMember = allIndividualsWithFitness.get(allIndividualsWithFitness.size() - 1).individual;
				//reset counter
				counter = 0;
				fitnessOfPop1 = fitnessOfPop2;
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
