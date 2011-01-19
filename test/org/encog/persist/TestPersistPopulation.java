/*
 * Encog(tm) Core v2.6 Unit Test - Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 
 * Copyright 2008-2010 Heaton Research, Inc.
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

import java.io.IOException;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.encog.engine.network.activation.ActivationStep;
import org.encog.ml.genetic.innovation.BasicInnovationList;
import org.encog.ml.genetic.innovation.InnovationList;
import org.encog.ml.genetic.population.BasicPopulation;
import org.encog.ml.genetic.population.Population;
import org.encog.ml.genetic.species.BasicSpecies;
import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.data.basic.BasicNeuralDataSet;
import org.encog.neural.neat.training.NEATGenome;
import org.encog.neural.neat.training.NEATInnovation;
import org.encog.neural.neat.training.NEATInnovationType;
import org.encog.neural.neat.training.NEATTraining;
import org.encog.neural.networks.XOR;
import org.encog.neural.networks.training.CalculateScore;
import org.encog.neural.networks.training.TrainingSetScore;
import org.encog.neural.thermal.HopfieldNetwork;
import org.encog.util.obj.SerializeObject;

public class TestPersistPopulation extends TestCase {
	
	public final String EG_FILENAME = "encogtest.eg";
	public final String EG_RESOURCE = "test";
	public final String SERIAL_FILENAME = "encogtest.ser";
	
	private Population generate()
	{
		NeuralDataSet trainingSet = new BasicNeuralDataSet(XOR.XOR_INPUT, XOR.XOR_IDEAL);
		
		CalculateScore score = new TrainingSetScore(trainingSet);
		// train the neural network
		ActivationStep step = new ActivationStep();
		step.setCenter(0.5);
		
		NEATTraining train = new NEATTraining(
				score, 2, 1, 10);
		train.setOutputActivationFunction(step);
		
		return train.getPopulation();
	}
	
	public void testPersistEG()
	{
		Population pop = generate();

		EncogMemoryCollection encog = new EncogMemoryCollection();
		encog.add(EG_RESOURCE, pop);
		encog.save(EG_FILENAME);
		
		EncogMemoryCollection encog2 = new EncogMemoryCollection();
		encog2.load(EG_FILENAME);
		BasicPopulation network2 = (BasicPopulation)encog2.find(EG_RESOURCE);
		
		validate(network2);
	}
	
	public void testPersistSerial() throws IOException, ClassNotFoundException
	{
		Population pop = generate();
		
		SerializeObject.save(SERIAL_FILENAME, pop);
		BasicPopulation pop2 = (BasicPopulation)SerializeObject.load(SERIAL_FILENAME);
		
		validate(pop2);
	}
	
	public void testPersistSerialEG() throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException
	{
		Population pop = generate();
		
		SerializeObject.saveEG(SERIAL_FILENAME, pop);
		BasicPopulation pop2 = (BasicPopulation)SerializeObject.loadEG(SERIAL_FILENAME);
				
		validate(pop2);
	}
	
	private void validate(BasicPopulation pop)
	{
		Assert.assertEquals(0.3,pop.getOldAgePenalty());
		Assert.assertEquals(50,pop.getOldAgeThreshold());
		Assert.assertEquals(10,pop.getPopulationSize());
		Assert.assertEquals(0.2,pop.getSurvivalRate());
		Assert.assertEquals(10,pop.getYoungBonusAgeThreshold());
		Assert.assertEquals(0.3,pop.getYoungScoreBonus());
		
		// see if the population can actually be used to train
		NeuralDataSet trainingSet = new BasicNeuralDataSet(XOR.XOR_INPUT, XOR.XOR_IDEAL);		
		CalculateScore score = new TrainingSetScore(trainingSet);
		NEATTraining train = new NEATTraining(score,pop);
		train.iteration();

	}
}
