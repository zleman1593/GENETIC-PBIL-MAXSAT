package Algorithms;
import java.util.*;

public class Hybrid2 extends GeneticSuper {
	
	private  double minTemp;
	private double maxTemp;
	private double decayRate;
	private ArrayList<ArrayWithFitness> sortedPop;
	private double alpha;
	private double temperature;
	private double mutateDecayRatio;

	
	// Constructor for tests
	public Hybrid2(int popSize, int literalNumber, int maxIteration, String crossOverMethod, double crossOverProb,
			ArrayList<ArrayList<Integer>> satProblem, int optimalUnSat, double maxTemp, double minTemp,double alpha) {
		
		this.satProblem = satProblem; 
		this.population = initPopulation(popSize, literalNumber);
		this.maxIteration = maxIteration;
		this.crossOverProb = crossOverProb;
		this.crossOverMethod = crossOverMethod;
		this.optimalUnSat = optimalUnSat;
		this.maxTemp = maxTemp;
		this.minTemp = minTemp;
		this.mutateProb = 1 - (1/maxIteration);
		this.alpha = alpha;
		this.temperature = maxTemp;
		this.sample = popSize/2;
		this.winners = 1;
		this.decayRate = 0.001; //1.0 / (double) population.get(0).size();
		this.mutateDecayRatio = 1.5;
	}

	public Results evolve(String selectionMethod) {
		long startTime = System.currentTimeMillis();
		long executionTime = -1;
		for (int i = 0; i < maxIteration && !foundSATSolution  && temperature >= minTemp; i++) {
			currentGeneration = i;

			tournamentSelectMod();
			
			if (foundSATSolution) {
				System.out.println("Fully Satisfied All Clauses");
				break;
			}

			
			singlePointCrossover(crossOverProb);
		
			mutateProb = 1 - ( (double) i * mutateDecayRatio / (double) maxIteration);
			
			mutate(mutateProb);

			//Replace worst two solutions
			replaceTwo();

			temperature = maxTemp * Math.exp(-1 * (i * decayRate));

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

		System.out.println("MESH SAGA  Output:");
		System.out.println("Number Of Variables: " + population.get(0).size());
		System.out.println("Number Of Clauses: " + satProblem.size());
		System.out.println("Satisfied Clauses: " + maxFitnessSoFar + " out of " + satProblem.size() + " ("
				+ (satProblem.size() - maxFitnessSoFar) + " unsatisfied clauses).");
		System.out.println("Best Variable Assignment: " + Arrays.toString(binaryToNumber(bestSolution)));
		System.out.println("Percent satisfied: " + percent + "%");
		System.out.println("Best Generation:" + bestGeneration);
		System.out.println("Total execution time: " + executionTime + " milliseconds");

		Results result = new Results("Hybrid Algorithm 2", population.get(0).size(), satProblem.size(), executionTime,
				(satProblem.size() - maxFitnessSoFar), percent, binaryToNumber(bestSolution), bestGeneration);
		return result;

	}

	private void tournamentSelectMod() {
		sortedPop =  getFitnessForAllIndividuals();
		ArrayList<ArrayList<Integer>> winnerPool = new ArrayList<ArrayList<Integer>>();
		// While the next generation pool is smaller than the population limit
		while (winnerPool.size() < 2) {

			// Get array of unique random numbers (AKA. randomly choose "sample"
			// number of individuals)
			ArrayList<Integer> randomNumbers = new ArrayList<Integer>();
			while (randomNumbers.size() < sample) {
				int number;
				do {
					number = randomGenerator.nextInt(population.size());
				} while (randomNumbers.contains(number));
				randomNumbers.add(number);
			}

			/*
			 * Evaluate each individual in the random sample, and add it with
			 * its fitness to a new object
			 */
			ArrayList<ArrayWithFitness> allIndividualsWithFitness = new ArrayList<ArrayWithFitness>();
			for (int i = 0; i < randomNumbers.size(); i++) {
				ArrayList<Integer> individual = population.get(randomNumbers.get(i));
				ArrayWithFitness memberWithFitness = new ArrayWithFitness(individual);
				memberWithFitness.fitness = evaluateCandidate(individual);
				allIndividualsWithFitness.add(memberWithFitness);
				// Update the global variable if a new individual is more fit
				// than the current best
				updateMaxFitness(memberWithFitness.fitness, individual);
			}

			// Sort the individuals by their fitness
			Collections.sort(allIndividualsWithFitness);

			// Pick top "winners" individuals and add to winnerPool until it is
			// full
			for (int i = 0; i < winners && (winnerPool.size() < population.size()); i++) {
				winnerPool.add((ArrayList<Integer>) allIndividualsWithFitness.get(i).individual.clone());
			}

		}
		population = winnerPool;// Replace current population with the
		// newly selected pool
	}

	/*Replace two.*/
	void replaceTwo(){
		

		// Sort the individuals by their fitness
		Collections.sort(sortedPop);
		ArrayWithFitness firstMemberWithFitness = new ArrayWithFitness(evaluateCandidate(population.get(0)),population.get(0));
		ArrayWithFitness secondMemberWithFitness = new ArrayWithFitness(evaluateCandidate(population.get(1)),population.get(1));
		double pickLessFitOption = randomGenerator.nextDouble();

		if (firstMemberWithFitness.fitness > sortedPop.get(0).fitness || pickLessFitOption < pickLessFitOption(firstMemberWithFitness.fitness,sortedPop.get(0).fitness)){

			sortedPop.set(0,firstMemberWithFitness);

			if (secondMemberWithFitness.fitness > sortedPop.get(1).fitness || pickLessFitOption < pickLessFitOption(secondMemberWithFitness.fitness,sortedPop.get(1).fitness)){

				sortedPop.set(1,secondMemberWithFitness);

			}

		} else if (secondMemberWithFitness.fitness > sortedPop.get(0).fitness  || pickLessFitOption < pickLessFitOption(secondMemberWithFitness.fitness,sortedPop.get(0).fitness)){

			sortedPop.set(0,secondMemberWithFitness);

			if (firstMemberWithFitness.fitness > sortedPop.get(1).fitness  || pickLessFitOption < pickLessFitOption(firstMemberWithFitness.fitness,sortedPop.get(1).fitness)){

				sortedPop.set(1,firstMemberWithFitness);

			}

		}

	
		population.clear();
		for (int i = 0; i < sortedPop.size();i++){
			population.add(sortedPop.get(i).individual);
		}


	}


	double pickLessFitOption(int fitness1, int fitness2 ){
		double dif =  fitness2 - fitness1;
	
		return Math.exp(  -1 * dif / temperature );
	}

}
