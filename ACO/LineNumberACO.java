package ACO;
// Line number in results files specific to PBIL setting
public enum LineNumberACO {
	PROBLEM(1),
	ALGORITHM(2),
	RHO(3),
	Q0(4),
	ALPHA(5),
	BETA(6),
	EPSILON(7),
	ITERATIONS(8),
	NUM_ANTS(10),
	ELITISM(11),
	AVG_LENGTH(13);
	
	private int numVal;

    LineNumberACO(int numVal) {
        this.numVal = numVal;
    }

    public int getNumVal() {
        return numVal;
    }
}
