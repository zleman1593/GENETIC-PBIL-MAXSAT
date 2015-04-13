package Algorithms;
import java.util.ArrayList;
//Class extends Arraylist to allow solution stored in ArrayList to have associated fitness which will be used to select solutions in the two algorithms
public class ArrayWithFitness implements Comparable<ArrayWithFitness> {
	public int fitness;
	public ArrayList<Integer> individual;
	
public ArrayWithFitness(int fitness,ArrayList<Integer> individual){
	this.fitness = fitness;
	this.individual = individual;
}

public ArrayWithFitness(ArrayList<Integer> individual){
	this.individual = individual;
}


public int compareTo(ArrayWithFitness compareObject) {
	 
	int compareQuantity = ((ArrayWithFitness) compareObject).fitness; 

	//descending order
	return compareQuantity - this.fitness;

}

}