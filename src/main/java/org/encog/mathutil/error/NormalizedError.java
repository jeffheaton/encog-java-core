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
package org.encog.mathutil.error;

import org.encog.ml.MLRegression;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.ea.exception.EARuntimeError;

/**
 * A normalized error will generally be in the approximate range between 0 and 1.  This allows normalized errors to be
 * compared across multiple datasets.  This class implements two types of normalized error calculation: Normalized
 * root-mean-square deviation (NRMSD) and  coefficient of variation of the RMSD, CV(RMSD).
 *
 * https://en.wikipedia.org/wiki/Root-mean-square_deviation
 */
public class NormalizedError {
    /**
     * The max target value in the dataset.
     */
    private double min;

    /**
     * The min target value in the dataset.
     */
    private double max;

    /**
     * The mean target value in the dataset.
     */
    private double mean;

    /**
     * The standard deviation (SD) of the target value.
     */
    private double sd;

    /**
     * The number of target values considered.
     */
    private int outputCount;

    /**
     * Construct the normalized error calculator.
     * @param theData The dataset to use.
     */
    public NormalizedError(MLDataSet theData) {
        this.min = Double.POSITIVE_INFINITY;
        this.max = Double.NEGATIVE_INFINITY;
        this.outputCount = 0;

        double sum = 0;
        for(MLDataPair pair: theData) {
            for(double d: pair.getIdealArray()) {
                this.min = Math.min(d,this.min);
                this.max = Math.max(d,this.max);
                sum += d;
                outputCount++;
            }
        }

        this.mean = sum / outputCount;

        sum = 0;
        for(MLDataPair pair: theData) {
            for(double d: pair.getIdealArray()) {
                double z = d - this.mean;
                sum += z * z;
            }
        }

        this.sd = Math.sqrt(sum/this.outputCount);
    }

    /**
     * Calculate the sum of squares of target of the target variable.
     * @param theData The dataset to use.
     * @param theModel The model to evaluate.
     * @return The sum of squares.
     */
    private double calculateSum(MLDataSet theData,MLRegression theModel) {
        double sum = 0;
        for(MLDataPair pair: theData) {
            MLData actual;
            try {
                actual = theModel.compute(pair.getInput());
            } catch (EARuntimeError e) {
                return Double.NaN;
            }
            for(int i=0;i<pair.getIdeal().size();i++) {
                double d = actual.getData(i) - pair.getIdeal().getData(i);
                d = d * d;
                sum+=d;
            }
        }
        return sum;
    }

    /**
     * Calculate the error as the coefficient of variation of the RMSD (CV(RMSD)).
     * @param theData The dataset to evaluate with.
     * @param theModel The model to evaluate.
     * @return The CV(RMSD) error.
     */
    public double calculateNormalizedMean(MLDataSet theData,MLRegression theModel) {
        double sum = calculateSum(theData,theModel);
        if( Double.isNaN(sum) || Double.isInfinite(sum) ) {
            return Double.NaN;
        }
        return Math.sqrt (sum/this.outputCount) / Math.abs(this.mean);
    }

    /**
     * Calculate the error as the Normalized root-mean-square deviation (NRMSD)
     * @param theData The dataset to evaluate with.
     * @param theModel The model to evaluate.
     * @return The NRMSD error.
     */
    public double calculateNormalizedRange(MLDataSet theData,MLRegression theModel) {
        double sum = calculateSum(theData,theModel);
        if( Double.isNaN(sum) || Double.isInfinite(sum) ) {
            return Double.NaN;
        }
        return Math.sqrt (sum/this.outputCount) / (this.max - this.min);
    }
}
