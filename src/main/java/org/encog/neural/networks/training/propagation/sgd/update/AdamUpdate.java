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
