/*
 * Encog(tm) Core v3.2 - Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 
 * Copyright 2008-2012 Heaton Research, Inc.
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

import org.encog.ml.data.basic.BasicMLDataSet;
import org.encog.ml.ea.population.Population;
import org.encog.neural.neat.NEATPopulation;
import org.encog.neural.neat.PersistNEATPopulation;
import org.encog.neural.neat.training.NEATTraining;
import org.encog.neural.networks.training.CalculateScore;
import org.encog.neural.networks.training.TrainingSetScore;

public final class TestPersistPopulationNPE extends TestCase
{
	private static double FAKE_DATA[][] = { { 0.0, 0.0, 1.0, 0.0, 0.0, 1.0, 1.0, 1.0 } };

	public void testNPE() throws Exception
	{
		final CalculateScore score = new TrainingSetScore(new BasicMLDataSet(FAKE_DATA, FAKE_DATA));

		// create a new random population and train it
		final NEATTraining training1 = new NEATTraining(score, new NEATPopulation(FAKE_DATA[0].length, 1, 5000));
		training1.iteration();
		// enough training for now, backup current population to continue later
		final ByteArrayOutputStream serialized1 = new ByteArrayOutputStream();
		new PersistNEATPopulation().save(serialized1, training1.getPopulation());

		// reload initial backup and continue training
		final NEATTraining training2 = new NEATTraining(
			score,
			(NEATPopulation)new PersistNEATPopulation().read(new ByteArrayInputStream(serialized1.toByteArray())));
		training2.iteration();
		// enough training, backup the reloaded population to continue later
		final ByteArrayOutputStream serialized2 = new ByteArrayOutputStream();
		new PersistNEATPopulation().save(serialized2, training2.getPopulation());

		// NEATTraining.init() randomly fails with a NPE in NEATGenome.getCompatibilityScore()
		final NEATTraining training3 = new NEATTraining(
			score,
			(NEATPopulation)new PersistNEATPopulation().read(new ByteArrayInputStream(serialized2.toByteArray())));
		training3.iteration();
		final ByteArrayOutputStream serialized3 = new ByteArrayOutputStream();
		new PersistNEATPopulation().save(serialized3, training3.getPopulation());
	}

	public void testSaveRead() throws Exception
	{
		final CalculateScore score = new TrainingSetScore(new BasicMLDataSet(FAKE_DATA, FAKE_DATA));

		// create a new random population and train it
		final NEATTraining training1 = new NEATTraining(score, new NEATPopulation(FAKE_DATA[0].length, 1, 5000));
		training1.iteration();
		// enough training for now, backup current population
		final ByteArrayOutputStream serialized1 = new ByteArrayOutputStream();
		new PersistNEATPopulation().save(serialized1, training1.getPopulation());

		final Population population2 = (Population)new PersistNEATPopulation().read(new ByteArrayInputStream(
			serialized1.toByteArray()));
		final ByteArrayOutputStream serialized2 = new ByteArrayOutputStream();
		new PersistNEATPopulation().save(serialized2, population2);
		Assert.assertEquals(serialized1.toString("UTF-8"), serialized2.toString("UTF-8")); // ok, populations seems identical

		/*final NEATTraining trainingFromPersist = new NEATTraining(score, population2);
		final ByteArrayOutputStream serialized3 = new ByteArrayOutputStream();
		new PersistNEATPopulation().save(serialized3, trainingFromPersist.getPopulation());
		FileUtil.writeFileAsString(new File("e:\\test1.txt"),serialized1.toString("UTF-8"));
		FileUtil.writeFileAsString(new File("e:\\test2.txt"),serialized3.toString("UTF-8"));
		Assert.assertEquals(serialized1.toString("UTF-8"), serialized3.toString("UTF-8")); // population changed ? is this ok ?
		*/
	}
}
