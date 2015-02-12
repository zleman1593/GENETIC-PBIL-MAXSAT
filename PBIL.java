import java.util.ArrayList;

public class PBIL {
	private int samples;
	private double learningRate;
	private double negLearningRate;
	private int length;
	private double mutProb;
	private double mutShift;
	private int maxIterations;
	private int iterations = 0;
	private double[] probVector;
	private ArrayList<double[]> sampleVectors = new ArrayList<double[]>();
			
	public PBIL (int s, double lr, double neglr, int l, double mProb, double mShift, int i) {
		samples = s;
		learningRate = lr;
		negLearningRate = neglr;
		length = l;
		mutProb = mProb;
		mutShift = mShift;
		maxIterations = i;
		probVector = new double[l];
	}
	
	public void initProbVector() {
		for (int i = 0; i < probVector.length; i++) {
			probVector[i] = 0.5;
		}
	}
	
	public double[] iteratePBIL() {
		while (iterations < maxIterations) {
			for (int i = 0; i < samples; i++) {
				double[] vector = generateSampleVector(probVector);
				sampleVectors.add(vector);
				
			}
		}	
		
		return probVector;
	}
	
	private double[] generateSampleVector(double[] prob) {
		double[] v = new double[length];
		// Generate according to probability
		
		return v;
	}
}
