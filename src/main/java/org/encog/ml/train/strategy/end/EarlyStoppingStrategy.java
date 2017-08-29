/*
 * Encog(tm) Core v3.4 - Java Version
 * http://www.heatonresearch.com/encog/
 * https://github.com/encog/encog-java-core
 
 * Copyright 2008-2017 Heaton Research, Inc.
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

import org.encog.Encog;
import org.encog.ml.MLRegression;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.train.MLTrain;
import org.encog.ml.train.strategy.end.EndTrainingStrategy;
import org.encog.util.obj.SerializeObject;
import org.encog.util.simple.EncogUtility;

import java.io.Serializable;

/**
 * A simple early stopping strategy that halts training when the validation set no longer improves.
 */
public class EarlyStoppingStrategy implements EndTrainingStrategy {
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
	 * The model that is being trained.
	 */
	private MLRegression model;

	/**
	 * The frequency to check the validation set.
	 */
	private int checkFrequency;

	/**
	 * How many iterations since the validation set was last checked.
	 */
	private int lastCheck;

	/**
	 * The number of iterations that the validation is allowed to remain stagnant/degrading for.
	 */
	private int allowedStagnantIterations;

	private int stagnantIterations;

	/**
	 * The best model so far.
	 */
	private MLRegression bestModel;

	private boolean saveBest;

    private double bestValidationError;

	private double minimumImprovement = Encog.DEFAULT_DOUBLE_EQUAL;

	public EarlyStoppingStrategy(MLDataSet theValidationSet) {
		this(theValidationSet, 5, 50);
	}


	public EarlyStoppingStrategy(MLDataSet theValidationSet,
										  int theCheckFrequency, int theAllowedStagnantIterations) {
		this.validationSet = theValidationSet;
		this.checkFrequency = theCheckFrequency;
		this.allowedStagnantIterations = theAllowedStagnantIterations;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void init(MLTrain theTrain) {
		this.train = theTrain;
		this.model = (MLRegression) train.getMethod();
		this.stop = false;
		this.lastCheck = 0;
		this.lastValidationError = Double.POSITIVE_INFINITY;
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
			double currentValidationError = EncogUtility.calculateRegressionError(this.model, this.validationSet);
            double improve = this.bestValidationError-currentValidationError;
            improve = Math.max(improve,0);

			if( Double.isInfinite(currentValidationError) || Double.isNaN(currentValidationError) ) {
				stop = true;
			} else if( this.bestValidationError<=currentValidationError
                    && !Double.isInfinite(this.lastValidationError)
                    && improve<this.minimumImprovement) {
				// No improvement
				this.stagnantIterations+=this.lastCheck;
				if(this.stagnantIterations>this.allowedStagnantIterations) {
					stop = true;
				}
			} else {
                // Improvement
				if( this.saveBest ) {
					this.bestModel = (MLRegression) SerializeObject.serializeClone((Serializable) this.model);
				}
				this.bestValidationError = currentValidationError;
				this.stagnantIterations=0;
			}

			this.lastValidationError = currentValidationError;
			this.lastCheck = 0;
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

	public int getStagnantIterations() {
		return stagnantIterations;
	}

	public void setStagnantIterations(int stagnantIterations) {
		this.stagnantIterations = stagnantIterations;
	}

	public int getAllowedStagnantIterations() {
		return allowedStagnantIterations;
	}

	public void setAllowedStagnantIterations(int allowedStagnantIterations) {
		this.allowedStagnantIterations = allowedStagnantIterations;
	}

	public boolean isSaveBest() {
		return saveBest;
	}

	public void setSaveBest(boolean saveBest) {
		this.saveBest = saveBest;
	}

	public MLRegression getBestModel() {
		return bestModel;
	}

    public double getBestValidationError() {
        return bestValidationError;
    }

    public double getMinimumImprovement() {
        return minimumImprovement;
    }

    public void setMinimumImprovement(double minimumImprovement) {
        this.minimumImprovement = minimumImprovement;
    }
}
