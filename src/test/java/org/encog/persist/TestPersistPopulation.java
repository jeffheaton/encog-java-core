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
package org.encog.persist;

import java.io.File;
import java.io.IOException;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.encog.engine.network.activation.ActivationStep;
import org.encog.ml.CalculateScore;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.encog.ml.ea.population.Population;
import org.encog.ml.ea.train.EvolutionaryAlgorithm;
import org.encog.neural.neat.NEATPopulation;
import org.encog.neural.neat.NEATUtil;
import org.encog.neural.networks.XOR;
import org.encog.neural.networks.training.TrainingSetScore;
import org.encog.util.TempDir;
import org.encog.util.obj.SerializeObject;

public class TestPersistPopulation extends TestCase {
	
	public final TempDir TEMP_DIR = new TempDir();
	public final File EG_FILENAME = TEMP_DIR.createFile("encogtest.eg");
	public final File SERIAL_FILENAME = TEMP_DIR.createFile("encogtest.ser");
	
	private NEATPopulation generate()
	{
		MLDataSet trainingSet = new BasicMLDataSet(XOR.XOR_INPUT, XOR.XOR_IDEAL);
		
		CalculateScore score = new TrainingSetScore(trainingSet);
		// train the neural network
		ActivationStep step = new ActivationStep();
		step.setCenter(0.5);

		EvolutionaryAlgorithm train = NEATUtil.constructNEATTrainer(
				score, 2, 1, 10);
		//train.setOutputActivationFunction(step);
		
		return (NEATPopulation)train.getPopulation();
	}
	
	public void testPersistEG()
	{
		Population pop = generate();

		EncogDirectoryPersistence.saveObject((EG_FILENAME), pop);
		NEATPopulation pop2 = (NEATPopulation)EncogDirectoryPersistence.loadObject((EG_FILENAME));
		
		validate(pop2);
	}
	
	public void testPersistSerial() throws IOException, ClassNotFoundException
	{
		NEATPopulation pop = generate();
		validate(pop);
		
		SerializeObject.save(SERIAL_FILENAME, pop);
		NEATPopulation pop2 = (NEATPopulation)SerializeObject.load(SERIAL_FILENAME);
		
		validate(pop2);
	}
	
	private void validate(NEATPopulation pop)
	{
		Assert.assertEquals(10,pop.getPopulationSize());
		Assert.assertEquals(0.2,pop.getSurvivalRate());
		
		// see if the population can actually be used to train
		MLDataSet trainingSet = new BasicMLDataSet(XOR.XOR_INPUT, XOR.XOR_IDEAL);		
		CalculateScore score = new TrainingSetScore(trainingSet);
		EvolutionaryAlgorithm train = NEATUtil.constructNEATTrainer(pop, score);
		train.iteration();

	}
	
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		TEMP_DIR.dispose();
	}
}
