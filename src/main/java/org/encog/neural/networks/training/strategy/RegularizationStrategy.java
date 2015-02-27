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
package org.encog.neural.networks.training.strategy;

import org.encog.EncogError;
import org.encog.ml.MLEncodable;
import org.encog.ml.train.MLTrain;
import org.encog.ml.train.strategy.Strategy;
import org.encog.util.EngineArray;

public class RegularizationStrategy implements Strategy {

    private double lambda; // Weight decay
    private MLTrain train;
    private double[] weights;
    private double[] newWeights;
    private MLEncodable encodable;

    public RegularizationStrategy(double lambda) {
        this.lambda = lambda;
    }

    @Override
    public void init(MLTrain train) {
        this.train = train;
        if( !(train.getMethod() instanceof MLEncodable) ) {
        	throw new EncogError("Method must implement MLEncodable to be used with regularization.");
        }
        this.encodable = ((MLEncodable)train.getMethod());
        this.weights = new double[this.encodable.encodedArrayLength()];
        this.newWeights = new double[this.encodable.encodedArrayLength()];
    }

    @Override
    public void preIteration() {
    	((MLEncodable)train.getMethod()).encodeToArray(weights);
    }

    @Override
	public void postIteration() {

		this.encodable.encodeToArray(newWeights);

		for (int i = 0; i < newWeights.length; i++) {
			newWeights[i] -= lambda * weights[i];
		}

		this.encodable.decodeFromArray(newWeights);
		EngineArray.arrayCopy(newWeights, weights);
	}
}
