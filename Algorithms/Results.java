package Algorithms;

import java.util.ArrayList;

//Store Results for Averaging
public class Results {
	public String algorithm;
	public int numVariables;
	public int numClauses;
	public long executionTime;
	public int numUnsatisifiedClauses;
	public Double percentSatisfied;
	public int[] assignment;
	public ArrayList<Integer> rawAssignment;
	public int bestgeneration;

	// Constructor.
	public Results(String algorithm, int numVariables, int numClauses, long executionTime, int numUnsatisifiedClauses,
			double percentSatisfied, int[] assignment, int bestgeneration) {
		this.algorithm = algorithm;
		this.numVariables = numVariables;
		this.numClauses = numClauses;
		this.executionTime = executionTime;
		this.numUnsatisifiedClauses = numUnsatisifiedClauses;
		this.percentSatisfied = percentSatisfied;
		this.assignment = assignment;
		this.bestgeneration = bestgeneration;
	}
	
	
	// Constructor for hybrid use
	public Results(String algorithm, int numVariables, int numClauses, long executionTime, int numUnsatisifiedClauses,
			double percentSatisfied,  ArrayList<Integer>assignment, int bestgeneration) {
		this.algorithm = algorithm;
		this.numVariables = numVariables;
		this.numClauses = numClauses;
		this.executionTime = executionTime;
		this.numUnsatisifiedClauses = numUnsatisifiedClauses;
		this.percentSatisfied = percentSatisfied;
		this.rawAssignment = assignment;
		this.bestgeneration = bestgeneration;
	}
		
}
