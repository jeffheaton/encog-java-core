package org.encog.neural.networks.training;

import org.encog.neural.activation.ActivationLinear;
import org.encog.neural.data.NeuralData;
import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.data.basic.BasicNeuralData;
import org.encog.neural.data.basic.BasicNeuralDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.training.competitive.CompetitiveTraining;
import org.encog.neural.networks.training.competitive.neighborhood.NeighborhoodSingle;
import org.encog.util.logging.Logging;
import org.junit.Assert;
import org.junit.Test;

public class TestCompetitive {

	public static double SOM_INPUT[][] = { { 0.0, 0.0, 1.0, 1.0 },
			{ 1.0, 1.0, 0.0, 0.0 } };

	@Test
	public void testSOM() {
		Logging.stopConsoleLogging();

		// create the training set
		final NeuralDataSet training = new BasicNeuralDataSet(
				TestCompetitive.SOM_INPUT, null);

		// Create the neural network.
		final BasicNetwork network = new BasicNetwork();
		network.addLayer(new BasicLayer(new ActivationLinear(), false, 4));
		network.addLayer(new BasicLayer(new ActivationLinear(), false, 2));
		network.getStructure().finalizeStructure();
		network.reset();

		final CompetitiveTraining train = new CompetitiveTraining(network, 0.4,
				training, new NeighborhoodSingle());

		int iteration = 0;

		for (iteration = 0; iteration <= 100; iteration++) {
			train.iteration();
		}

		final NeuralData data1 = new BasicNeuralData(
				TestCompetitive.SOM_INPUT[0]);
		final NeuralData data2 = new BasicNeuralData(
				TestCompetitive.SOM_INPUT[1]);
		
		int result1 = network.winner(data1);
		int result2 = network.winner(data2);
		
		Assert.assertTrue(result1!=result2);

	}

}
