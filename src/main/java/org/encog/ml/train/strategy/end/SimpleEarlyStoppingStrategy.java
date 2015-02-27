/*
 * Encog(tm) Core v3.3 - Java Version
 * http://www.heatonresearch.com/encog/
 * https://github.com/encog/encog-java-core
 
 * Copyright 2008-2014 Heaton Research, Inc.
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
package org.encog.ml.train.strategy.end;

import org.encog.ml.MLError;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.train.MLTrain;

/**
 * A simple early stopping strategy that halts training when the validation set no longer improves.
 */
public class SimpleEarlyStoppingStrategy implements EndTrainingStrategy {
	/**
	 * The validation set.
	 */
	private MLDataSet validationSet;

	/**
	 * The trainer.
	 */
	private MLTrain train;

	/**
	 * Has training stopped.
	 */
	private boolean stop;

	/**
	 * Current training error.
	 */
	private double trainingError;

	/**
	 * Current validation error.
	 */
	private double lastValidationError;

	/**
	 * The error calculation.
	 */
	private MLError calc;

	private int checkFrequency;
	
	private int lastCheck;
	
	private double lastError;
	
	
	
	public SimpleEarlyStoppingStrategy(MLDataSet theValidationSet) {
		this(theValidationSet, 5);
	}


	public SimpleEarlyStoppingStrategy(MLDataSet theValidationSet,
			int theCheckFrequency) {
		this.validationSet = theValidationSet;
		this.checkFrequency = theCheckFrequency;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void init(MLTrain theTrain) {
		this.train = theTrain;
		this.calc = (MLError) train.getMethod();
		this.stop = false;
		this.lastCheck = 0;
		this.lastValidationError = this.calc.calculateError(this.validationSet);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void preIteration() {

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void postIteration() {
		this.lastCheck++;
		this.trainingError = this.train.getError();

		if( this.lastCheck>this.checkFrequency || Double.isInfinite(this.lastValidationError) ) {
			this.lastCheck = 0;
			
			double currentValidationError = this.calc.calculateError(this.validationSet);

			if( currentValidationError>=this.lastValidationError ) {
				stop = true;
			}
			
			this.lastValidationError = currentValidationError;
			
		}
	}

	/**
	 * @return Returns true if we should stop.
	 */
	@Override
	public boolean shouldStop() {
		return stop;
	}

	/**
	 * @return the trainingError
	 */
	public double getTrainingError() {
		return trainingError;
	}


	/**
	 * @return The validation error.
	 */
	public double getValidationError() {
		return this.lastValidationError;
	}
}
