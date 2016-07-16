package org.encog.neural.networks.training.propagation.sgd.update;

import org.encog.neural.networks.training.propagation.sgd.StochasticGradientDescent;

/**
 * Created by jeffh on 7/15/2016.
 */
public class MomentumUpdate implements UpdateRule {
    private StochasticGradientDescent training;
    private double[] lastDelta;

    @Override
    public void init(StochasticGradientDescent theTraining) {
        this.training = theTraining;
        this.lastDelta = new double[theTraining.getFlat().getWeights().length];
    }

    @Override
    public void update(double[] gradients, double[] weights) {
        for(int i=0;i<weights.length;i++) {
            final double delta = (this.training.getLearningRate()*gradients[i]) + (this.training.getMomentum()*this.lastDelta[i]);
            weights[i] += delta;
            this.lastDelta[i] = delta;
        }
    }
}
