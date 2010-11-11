package org.encog.engine.concurrency.calc;

public class CalculationResult {
	
	private final boolean successful;
	private final boolean executed;
	private double error;
		
	public CalculationResult(boolean successful, boolean executed) {
		super();
		this.successful = successful;
		this.executed = executed;
	}
	
	/**
	 * @return the successful
	 */
	public boolean isSuccessful() {
		return successful;
	}
	/**
	 * @return the executed
	 */
	public boolean isExecuted() {
		return executed;
	}

	/**
	 * @return the error
	 */
	public double getError() {
		return error;
	}

	/**
	 * @param error the error to set
	 */
	public void setError(double error) {
		this.error = error;
	}	
}
