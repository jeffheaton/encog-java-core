package org.encog.neural.networks.training.propagation.sgd.update;

import org.encog.neural.networks.training.propagation.sgd.StochasticGradientDescent;

/**
 * Created by jeffh on 7/15/2016.
 */
public class RMSPropUpdate implements UpdateRule {
    private StochasticGradientDescent training;
    private double[] cache;
    private double eps = 1e-8;
    private double decayRate = 0.99;

    @Override
    public void init(StochasticGradientDescent theTraining) {
        this.training = theTraining;
        this.cache = new double[theTraining.getFlat().getWeights().length];
    }

    @Override
    public void update(double[] gradients, double[] weights) {
        for(int i=0;i<weights.length;i++) {
            this.cache[i] += gradients[i]*gradients[i];
            this.cache[i] = this.decayRate * cache[i] + (1 - this.decayRate) * gradients[i] * gradients[i];
            final double delta = (this.training.getLearningRate()*gradients[i])/(Math.sqrt(cache[i])+this.eps);
            weights[i] += delta;
        }
    }

    public double getEps() {
        return eps;
    }

    public void setEps(double eps) {
        this.eps = eps;
    }

    public double getDecayRate() {
        return decayRate;
    }

    public void setDecayRate(double decayRate) {
        this.decayRate = decayRate;
    }
}
