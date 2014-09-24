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
package org.encog.neural.networks.training.competitive;

import junit.framework.TestCase;

import org.encog.mathutil.matrices.Matrix;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.encog.neural.som.SOM;
import org.encog.neural.som.training.basic.BasicTrainSOM;
import org.encog.neural.som.training.basic.neighborhood.NeighborhoodSingle;
import org.junit.Assert;
import org.junit.Test;

public class TestCompetitive extends TestCase  {

	public static double SOM_INPUT[][] = { { 0.0, 0.0, 1.0, 1.0 },
			{ 1.0, 1.0, 0.0, 0.0 } };
	
	// Just a random starting matrix, but it gives us a constant starting point
	public static final double[][] MATRIX_ARRAY = {
			{0.9950675732277183, -0.09315692732658198,0.9840257865083011,0.5032129897356723}, 
			{-0.8738960119753589, -0.48043680531294997,-0.9455207768842442, -0.8612565984447569}
			};
	
	@Test
	public void testSOM() {

		// create the training set
		final MLDataSet training = new BasicMLDataSet(
				TestCompetitive.SOM_INPUT, null);

		// Create the neural network.
		SOM network = new SOM(4,2);		
		network.setWeights(new Matrix(MATRIX_ARRAY));

		final BasicTrainSOM train = new BasicTrainSOM(network, 0.4,
				training, new NeighborhoodSingle());
		train.setForceWinner(true);
		int iteration = 0;

		for (iteration = 0; iteration <= 100; iteration++) {
			train.iteration();
		}

		final MLData data1 = new BasicMLData(
				TestCompetitive.SOM_INPUT[0]);
		final MLData data2 = new BasicMLData(
				TestCompetitive.SOM_INPUT[1]);
		
		int result1 = network.classify(data1);
		int result2 = network.classify(data2);
		
		Assert.assertTrue(result1!=result2);

	}

}
