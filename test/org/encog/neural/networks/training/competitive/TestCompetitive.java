/*
 * Encog(tm) Artificial Intelligence Framework v2.3
 * Java Version, Unit Tests
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 * 
 * Copyright 2008-2010 by Heaton Research Inc.
 * 
 * Released under the LGPL.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 * 
 * Encog and Heaton Research are Trademarks of Heaton Research, Inc.
 * For information on Heaton Research trademarks, visit:
 * 
 * http://www.heatonresearch.com/copyright.html
 */

package org.encog.neural.networks.training.competitive;

import junit.framework.TestCase;

import org.encog.matrix.Matrix;
import org.encog.neural.activation.ActivationLinear;
import org.encog.neural.data.NeuralData;
import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.data.basic.BasicNeuralData;
import org.encog.neural.data.basic.BasicNeuralDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.layers.Layer;
import org.encog.neural.networks.synapse.Synapse;
import org.encog.neural.networks.training.competitive.CompetitiveTraining;
import org.encog.neural.networks.training.competitive.neighborhood.NeighborhoodSingle;
import org.encog.neural.pattern.SOMPattern;
import org.encog.util.logging.Logging;
import org.junit.Assert;
import org.junit.Test;

public class TestCompetitive extends TestCase  {

	public static double SOM_INPUT[][] = { { 0.0, 0.0, 1.0, 1.0 },
			{ 1.0, 1.0, 0.0, 0.0 } };
	
	// Just a random starting matrix, but it gives us a constant starting point
	public static final double[][] MATRIX_ARRAY = {
			{0.9950675732277183, -0.09315692732658198}, 
			{0.9840257865083011, 0.5032129897356723}, 
			{-0.8738960119753589, -0.48043680531294997}, 
			{-0.9455207768842442, -0.8612565984447569}
			};

	private Synapse findSynapse(BasicNetwork network)
	{
		Layer input = network.getLayer(BasicNetwork.TAG_INPUT);
		return input.getNext().get(0);
	}
	
	@Test
	public void testSOM() {
		Logging.stopConsoleLogging();

		// create the training set
		final NeuralDataSet training = new BasicNeuralDataSet(
				TestCompetitive.SOM_INPUT, null);

		// Create the neural network.
		SOMPattern pattern = new SOMPattern();
		pattern.setInputNeurons(4);
		pattern.setOutputNeurons(2);
		BasicNetwork network = pattern.generate();
		
		Synapse synapse = findSynapse(network);
		synapse.setMatrix(new Matrix(MATRIX_ARRAY));

		final CompetitiveTraining train = new CompetitiveTraining(network, 0.4,
				training, new NeighborhoodSingle());
		train.setForceWinner(true);
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
