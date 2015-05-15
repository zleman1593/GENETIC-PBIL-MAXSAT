package AnalysisOfResults;

// Line number in results files 
public enum LineNumber {
	// Name of MAXSAT problem.
	PROBLEM_NAME(1),
	// General info. about the problem.
	NUM_TRIALS(3),
	NUM_LITERALS(4),
	NUM_CLAUSES(5),
	// Stats for non-timed out trials.
	AVG_BEST_GENERATION(7),
	BEST_GENERATION(8),
	AVG_UNSAT_CLAUSES(9),
	FEWEST_UNSAT_CLAUSES(10),
	AVG_EXECUTION_TIME(11),
	BEST_EXECUTION_TIME(12),
	// Stats for timed out trials.
	AVG_BEST_GENERATION_TIMEOUT(14),
	BEST_GENERATION_TIMEOUT(15),
	AVG_UNSAT_CLAUSES_TIMEOUT(16),
	FEWEST_UNSAT_CLAUSES_TIMEOUT(17),
	NUM_TIMEOUTS(18),
	AVG_PERCENT_SAT(19),
	ALGORITHM_SETTING(20);
	
	private int numVal;

    LineNumber(int numVal) {
        this.numVal = numVal;
    }

    public int getNumVal() {
        return numVal;
    }
}
