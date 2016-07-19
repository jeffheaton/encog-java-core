package org.encog.ml.importance;

import org.encog.EncogError;
import org.encog.ml.data.MLDataSet;
import org.encog.neural.networks.BasicNetwork;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * A feature ranking algorithm based on the weights of a neural network.  This algorithm can only be used for neural
 * networks, and it cannot calculate importance relative to a new dataset.
 *
 * Sources:
 * Garson, D. G. (1991). Interpreting neural network connection weights.
 * Goh, A. (1995). Back-propagation neural networks for modeling complex systems. Artificial Intelligence in
 * Engineering, 9(3), 143-151.
 *
 */
public class NeuralFeatureImportanceCalc extends AbstractFeatureImportance {

    /**
     * {@inheritDoc}
     */
    @Override
    public void performRanking() {
        // Reset rankings
        for (FeatureRank rank : getFeatures()) {
            rank.setImportancePercent(0);
            rank.setTotalWeight(0);
        }

        if( ! (getModel() instanceof BasicNetwork)  ) {
            throw new EncogError("This algorithm only works for classes of type BasicNetwork");
        }

        BasicNetwork network = (BasicNetwork)getModel();

        // Sum weights for each input neuron
        for (int inputNueron = 0; inputNueron < network.getInputCount(); inputNueron++) {
            FeatureRank ranking = getFeatures().get(inputNueron);
            for (int nextNeuron = 0; nextNeuron < network.getLayerNeuronCount(1); nextNeuron++) {
                double i_h = network.getWeight(0, inputNueron, nextNeuron);
                double h_o = network.getWeight(1, nextNeuron, 0);
                ranking.addWeight(i_h * h_o);
            }

        }
        // sum total weight to input neurons.
        double max = 0;
        for (FeatureRank rank : getFeatures() ) {
            max = Math.max(max, Math.abs(rank.getTotalWeight()));
        }

        // calculate each feature's importance percent
        for (FeatureRank rank : getFeatures()) {
            rank.setImportancePercent(Math.abs(rank.getTotalWeight()) / max);
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void performRanking(MLDataSet theDataset) {
        performRanking();
    }
}
