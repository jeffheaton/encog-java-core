package org.encog.neural.networks.training.propagation.sgd.update;

import org.encog.neural.flat.FlatNetwork;
import org.encog.neural.networks.training.propagation.sgd.StochasticGradientDescent;

/**
 * Created by jeffh on 7/15/2016.
 */
public interface UpdateRule {
    void init(StochasticGradientDescent training);
    void update(double[] gradients, double[] weights);
}
