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
package org.encog.ml.importance;

import org.encog.EncogError;
import org.encog.mathutil.error.ErrorCalculation;
import org.encog.mathutil.randomize.generate.GenerateRandom;
import org.encog.mathutil.randomize.generate.MersenneTwisterGenerateRandom;
import org.encog.ml.MLContext;
import org.encog.ml.MLRegression;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.ml.data.basic.BasicMLDataPair;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.encog.util.EngineArray;
import org.encog.util.simple.EncogUtility;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * perturbation  feature encoding can be used to determine the importance of features for any type of regression or
 * classification model, with any compatible dataset.  This method works by evaluating the performance of the model
 * when each of the input's corrisponding data is scrambled.  Features that are more important will result in worse
 * errors when their data are scrambled.
 *
 * Source:
 * Breiman, L. (2001). Random forests. Machine learning, 45(1), 5-32.
 */
public class PerturbationFeatureImportanceCalc extends AbstractFeatureImportance {

    /**
     * Random number generator.
     */
    private GenerateRandom rnd = new MersenneTwisterGenerateRandom();
    private double[] shuffleColumn;

    /**
     * {@inheritDoc}
     */
    @Override
    public void performRanking() {
        throw new EncogError("This algorithm requires a dataset to measure performance against, please call performRanking with a dataset.");
    }

    private double calculateRegressionError(MLDataSet dataset, int perturbFeature) {

        // init as needed
        final ErrorCalculation errorCalculation = new ErrorCalculation();
        if( getModel() instanceof MLContext)
            ((MLContext)getModel()).clearContext();

        // copy the perturb column
        for(int i=0;i<dataset.size();i++) {
            this.shuffleColumn[i] = dataset.get(i).getInput().getData(perturbFeature);
        }

        // evaluate
        MLData featureVector = new BasicMLData(dataset.getInputSize());

        try {
            int n = dataset.size();

            for(int i=0;i<n;i++) {
                // Get training element
                MLDataPair pair = dataset.get(i);
                EngineArray.arrayCopy(pair.getInput().getData(),featureVector.getData());

                // Shuffle
                if( i!=(n-1)) {
                    int j = rnd.nextInt(dataset.size() - i);
                    double t = this.shuffleColumn[i];
                    this.shuffleColumn[i] = this.shuffleColumn[j];
                    this.shuffleColumn[j] = t;
                    featureVector.setData(perturbFeature, this.shuffleColumn[i]);
                }

                // Evaluate
                final MLData actual = getModel().compute(featureVector);
                errorCalculation.updateError(actual.getData(), pair.getIdeal()
                        .getData(), pair.getSignificance());
            }
        } catch(EncogError e) {
            return Double.NaN;
        }
        return errorCalculation.calculate();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void performRanking(MLDataSet theDataset) {
        this.shuffleColumn = new double[theDataset.size()];

        double max = 0;
        for(int i=0;i<getModel().getInputCount();i++) {
            FeatureRank fr = getFeatures().get(i);
            //MLDataSet p = generatePermutation(theDataset,i);
            double e = calculateRegressionError(theDataset,i);
            fr.setTotalWeight(e);
            max = Math.max(max,e);
        }

        for(FeatureRank fr:getFeatures()) {
            fr.setImportancePercent(fr.getTotalWeight()/max);
        }
    }

    /**
     * @return The random number generator.
     */
    public GenerateRandom getRnd() {
        return rnd;
    }

    /**
     * Set the random number generator.
     * @param rnd The random number generator.
     */
    public void setRnd(GenerateRandom rnd) {
        this.rnd = rnd;
    }
}
