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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.encog.ml.CalculateScore;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.encog.ml.ea.population.Population;
import org.encog.ml.ea.train.EvolutionaryAlgorithm;
import org.encog.neural.neat.NEATPopulation;
import org.encog.neural.neat.NEATUtil;
import org.encog.neural.neat.PersistNEATPopulation;
import org.encog.neural.networks.training.TrainingSetScore;

public final class TestPersistPopulationNPE extends TestCase
{
	private static double FAKE_DATA[][] = { { 0.0, 0.0, 1.0, 0.0, 0.0, 1.0, 1.0, 1.0 } };

	public void testNPE() throws Exception
	{
		final CalculateScore score = new TrainingSetScore(new BasicMLDataSet(FAKE_DATA, FAKE_DATA));

		// create a new random population and train it
		NEATPopulation pop = new NEATPopulation(FAKE_DATA[0].length, 1, 50);
		pop.reset();
		EvolutionaryAlgorithm training1 = NEATUtil.constructNEATTrainer(pop, score);
		training1.iteration();
		// enough training for now, backup current population to continue later
		final ByteArrayOutputStream serialized1 = new ByteArrayOutputStream();
		new PersistNEATPopulation().save(serialized1, training1.getPopulation());

		// reload initial backup and continue training
		EvolutionaryAlgorithm training2 = NEATUtil.constructNEATTrainer(
			(NEATPopulation)new PersistNEATPopulation().read(new ByteArrayInputStream(serialized1.toByteArray())),
			score);
		training2.iteration();
		// enough training, backup the reloaded population to continue later
		final ByteArrayOutputStream serialized2 = new ByteArrayOutputStream();
		new PersistNEATPopulation().save(serialized2, training2.getPopulation());

		// NEATTraining.init() randomly fails with a NPE in NEATGenome.getCompatibilityScore()
		EvolutionaryAlgorithm training3 = NEATUtil.constructNEATTrainer(
			(NEATPopulation)new PersistNEATPopulation().read(new ByteArrayInputStream(serialized2.toByteArray())),
			score);
		training3.iteration();
		final ByteArrayOutputStream serialized3 = new ByteArrayOutputStream();
		new PersistNEATPopulation().save(serialized3, training3.getPopulation());
	}

	public void testSaveRead() throws Exception
	{
		final CalculateScore score = new TrainingSetScore(new BasicMLDataSet(FAKE_DATA, FAKE_DATA));
		NEATPopulation pop = new NEATPopulation(FAKE_DATA[0].length, 1, 50);
		pop.reset();
		// create a new random population and train it
		EvolutionaryAlgorithm training1 = NEATUtil.constructNEATTrainer(pop, score);
		training1.iteration();
		// enough training for now, backup current population
		final ByteArrayOutputStream serialized1 = new ByteArrayOutputStream();
		new PersistNEATPopulation().save(serialized1, training1.getPopulation());

		final Population population2 = (Population)new PersistNEATPopulation().read(new ByteArrayInputStream(
			serialized1.toByteArray()));
		final ByteArrayOutputStream serialized2 = new ByteArrayOutputStream();
		new PersistNEATPopulation().save(serialized2, population2);
		Assert.assertEquals(serialized1.size(), serialized2.size());		
	}
}
