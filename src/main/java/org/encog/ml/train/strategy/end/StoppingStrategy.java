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
 * A simple early stopping strategy that halts training when the training set no longer improves.
 */
public class StoppingStrategy implements EndTrainingStrategy {

    /**
     * The trainer.
     */
    private MLTrain train;

    /**
     * Has training stopped.
     */
    private boolean stop;

    /**
     * Current validation error.
     */
    private double lastError;

    /**
     * The model that is being trained.
     */
    private MLRegression model;

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

    private double bestError;

    private double minimumImprovement = Encog.DEFAULT_DOUBLE_EQUAL;

    public StoppingStrategy(MLDataSet theValidationSet) {
        this(50);
    }


    public StoppingStrategy(int theAllowedStagnantIterations) {
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
        this.lastError = Double.POSITIVE_INFINITY;
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
        double trainingError = this.train.getError();
        double improve = this.bestError-trainingError;
        improve = Math.max(improve,0);

        if( Double.isInfinite(trainingError) || Double.isNaN(trainingError) ) {
            stop = true;
        } else if( this.bestError<=trainingError
                && !Double.isInfinite(this.lastError)
                && improve<this.minimumImprovement) {
            // No improvement
            this.stagnantIterations++;
            if(this.stagnantIterations>this.allowedStagnantIterations) {
                stop = true;
            }
        } else {
            // Improvement
            if( this.saveBest ) {
                this.bestModel = (MLRegression) SerializeObject.serializeClone((Serializable) this.model);
            }
            this.bestError = trainingError;
            this.stagnantIterations=0;
        }

        this.lastError = trainingError;
    }

    /**
     * @return Returns true if we should stop.
     */
    @Override
    public boolean shouldStop() {
        return stop;
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

    public double getMinimumImprovement() {
        return minimumImprovement;
    }

    public void setMinimumImprovement(double minimumImprovement) {
        this.minimumImprovement = minimumImprovement;
    }

}
