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
package org.encog.neural.networks.training;

import org.encog.mathutil.error.ErrorCalculation;
import org.encog.mathutil.randomize.generate.GenerateRandom;
import org.encog.mathutil.randomize.generate.MersenneTwisterGenerateRandom;
import org.encog.ml.CalculateScore;
import org.encog.ml.MLMethod;
import org.encog.ml.MLRegression;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.buffer.BufferedMLDataSet;
import org.encog.util.error.CalculateRegressionError;

/**
 * Calculate a score based on a training set. This class allows simulated
 * annealing or genetic algorithms just as you would any other training set
 * based training method.  The method must support regression (MLRegression).
 * A random sample is used to score the model.
 */
public class StochasticTrainingSetScore implements CalculateScore {

    /**
     * The training set.
     */
    private final MLDataSet training;

    /**
     * The sample size.
     */
    private final int size;

    /**
     * Random number generator.
     */
    private GenerateRandom rnd = new MersenneTwisterGenerateRandom();

    /**
     * Construct a training set score calculation.
     *
     * @param training
     *            The training data to use.
     */
    public StochasticTrainingSetScore(final MLDataSet training, final int theSize)
    {
        this.training = training;
        this.size = theSize;
    }

    /**
     * Calculate the score for the network.
     * @param method The network to calculate for.
     * @return The score.
     */
    public double calculateScore(final MLMethod method) {
        ErrorCalculation error = new ErrorCalculation();

        for(int i=0;i<this.size;i++) {
            int idx = this.rnd.nextInt(this.training.size());
            MLDataPair pair = this.training.get(idx);
            MLData output = ((MLRegression)method).compute(pair.getInput());
            error.updateError(output.getData(),pair.getIdealArray(),1.0);
        }

        return error.calculate();
    }

    /**
     * A training set based score should always seek to lower the error,
     * as a result, this method always returns true.
     * @return Returns true.
     */
    public boolean shouldMinimize() {
        return true;
    }

    @Override
    public boolean requireSingleThreaded() {
        if( this.training instanceof BufferedMLDataSet ) {
            return true;
        }
        return false;
    }

    public MLDataSet getTraining() {
        return training;
    }

    public int getSize() {
        return size;
    }

    public GenerateRandom getRnd() {
        return rnd;
    }

    public void setRnd(GenerateRandom rnd) {
        this.rnd = rnd;
    }
}
