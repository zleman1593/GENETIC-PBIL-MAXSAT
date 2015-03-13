
// Line number in results files 
public enum LineNumber {
	// Name of MAXSAT problem.
	PROBLEM_NAME(1),
	// General info. about the problem.
	NUM_TRIALS(6),
	NUM_VARS(7),
	NUM_CLAUSES(8),
	// Stats for non-timed out trials.
	AVG_BEST_GENERATION(10),
	BEST_GENERATION(11),
	AVG_UNSAT_CLAUSES(12),
	FEWEST_UNSAT_CLAUSES(13),
	AVG_EXECUTION_TIME(14),
	BEST_EXECUTION_TIME(15),
	// Stats for timed out trials.
	AVG_BEST_GENERATION_TIMEOUT(17),
	BEST_GENERATION_TIMEOUT(18),
	AVG_UNSAT_CLAUSES_TIMEOUT(19),
	FEWEST_UNSAT_CLAUSES_TIMEOUT(20),
	NUM_TIMEOUTS(21),
	// For both algorithms.
	POP_SIZE(24);
	
	private int numVal;

    LineNumber(int numVal) {
        this.numVal = numVal;
    }

    public int getNumVal() {
        return numVal;
    }
}
