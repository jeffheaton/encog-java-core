package org.encog;
import org.encog.engine.network.activation.ActivationLinear;
import org.encog.engine.network.activation.ActivationReLU;
import org.encog.engine.network.activation.ActivationSigmoid;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;

/**
 * XOR: This example is essentially the "Hello World" of neural network
 * programming.  This example shows how to construct an Encog neural
 * network to predict the output from the XOR operator.  This example
 * uses backpropagation to train the neural network.
 *
 * This example attempts to use a minimum of Encog features to create and
 * train the neural network.  This allows you to see exactly what is going
 * on.  For a more advanced example, that uses Encog factories, refer to
 * the XORFactory example.
 *
 */
public class Test {

    /**
     * The input necessary for XOR.
     */
    public static double XOR_INPUT[][] = { { 0.0, 0.0 }, { 1.0, 0.0 },
            { 0.0, 1.0 }, { 1.0, 1.0 } };

    /**
     * The ideal data necessary for XOR.
     */
    public static double XOR_IDEAL[][] = { { 0.0 }, { 1.0 }, { 1.0 }, { 0.0 } };

    /**
     * The main method.
     * @param args No arguments are used.
     */
    public static void main(final String args[]) {

        // create a neural network, without using a factory
        BasicNetwork network = new BasicNetwork();
        network.addLayer(new BasicLayer(new ActivationReLU(),true,2));
        network.addLayer(new BasicLayer(new ActivationSigmoid(),true,3));
        network.addLayer(new BasicLayer(new ActivationLinear(),false,1));
        network.getStructure().finalizeStructure();
        network.reset();

        // create training data
        MLDataSet trainingSet = new BasicMLDataSet(XOR_INPUT, XOR_IDEAL);

        // train the neural network
        final ResilientPropagation train = new ResilientPropagation(network, trainingSet);

        int epoch = 1;

        do {
            train.iteration();
            System.out.println("Epoch #" + epoch + " Error:" + train.getError());
            epoch++;
        } while(train.getError() > 0.01);
        train.finishTraining();

        // test the neural network
        System.out.println("Neural Network Results:");
        for(MLDataPair pair: trainingSet ) {
            final MLData output = network.compute(pair.getInput());
            System.out.println(pair.getInput().getData(0) + "," + pair.getInput().getData(1)
                    + ", actual=" + output.getData(0) + ",ideal=" + pair.getIdeal().getData(0));
        }

        Encog.getInstance().shutdown();
    }
}