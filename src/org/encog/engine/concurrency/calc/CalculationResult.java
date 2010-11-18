/*
 * Encog(tm) Core v2.6 - Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 
 * Copyright 2008-2010 Heaton Research, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *   
 * For more information on Heaton Research copyrights, licenses 
 * and trademarks visit:
 * http://www.heatonresearch.com/copyright
 */
package org.encog.engine.concurrency.calc;

/**
 * The result from a calculation.
 * 
 */
public class CalculationResult {

	/**
	 * Was this call successful?
	 */
	private final boolean successful;

	/**
	 * Was this call even executed?
	 */
	private final boolean executed;

	/**
	 * The error.
	 */
	private double error;

	/**
	 * Construct a calculation result.
	 * @param successful What it successful?
	 * @param executed Did it execute?
	 */
	public CalculationResult(final boolean successful, final boolean executed) {
		super();
		this.successful = successful;
		this.executed = executed;
	}

	/**
	 * @return the error
	 */
	public double getError() {
		return this.error;
	}

	/**
	 * @return the executed
	 */
	public boolean isExecuted() {
		return this.executed;
	}

	/**
	 * @return the successful
	 */
	public boolean isSuccessful() {
		return this.successful;
	}

	/**
	 * @param error
	 *            the error to set
	 */
	public void setError(final double error) {
		this.error = error;
	}
}
