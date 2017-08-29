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
package org.encog.neural.networks.training.propagation.sgd.update;

import org.encog.neural.networks.training.propagation.sgd.StochasticGradientDescent;

/**
 * Created by jeffh on 7/15/2016.
 */
public class AdamUpdate implements UpdateRule {
    private StochasticGradientDescent training;
    private double[] m;
    private double[] v;
    private double beta1 = 0.9;
    private double beta2 = 0.999;
    private double eps = 1e-8;

    @Override
    public void init(StochasticGradientDescent theTraining) {
        this.training = theTraining;
        this.m = new double[theTraining.getFlat().getWeights().length];
        this.v = new double[theTraining.getFlat().getWeights().length];
    }

    @Override
    public void update(double[] gradients, double[] weights) {

        for(int i=0;i<weights.length;i++) {

            m[i] = (this.beta1*m[i])+(1-this.beta1)*gradients[i];
            v[i] = (this.beta2*v[i])+(1-this.beta2)*gradients[i]*gradients[i];

            double mCorrect = m[i]/(1-Math.pow(this.beta1,this.training.getIteration()));
            double vCorrect = v[i]/(1-Math.pow(this.beta2,this.training.getIteration()));

            final double delta = (training.getLearningRate()*mCorrect)/(Math.sqrt(vCorrect)+this.eps);
            weights[i] += delta;
        }
    }
}
