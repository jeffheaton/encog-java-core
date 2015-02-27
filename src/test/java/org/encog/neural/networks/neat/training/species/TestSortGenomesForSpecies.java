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
package org.encog.neural.networks.neat.training.species;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.encog.ml.CalculateScore;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.encog.ml.ea.sort.SortGenomesForSpecies;
import org.encog.ml.ea.train.EvolutionaryAlgorithm;
import org.encog.neural.neat.NEATPopulation;
import org.encog.neural.neat.NEATUtil;
import org.encog.neural.neat.training.NEATGenome;
import org.encog.neural.networks.XOR;
import org.encog.neural.networks.training.TrainingSetScore;
import org.junit.Test;

public class TestSortGenomesForSpecies extends TestCase {
	
	@Test
	public void testSort1() {
		
		MLDataSet trainingSet = new BasicMLDataSet(XOR.XOR_INPUT,XOR.XOR_IDEAL);
		NEATPopulation pop = new NEATPopulation(2,1,100);
		pop.reset();
		CalculateScore score = new TrainingSetScore(trainingSet);
		final EvolutionaryAlgorithm train = NEATUtil.constructNEATTrainer(pop,score);
				
		NEATGenome genome1 = new NEATGenome();
		genome1.setAdjustedScore(3.0);
		NEATGenome genome2 = new NEATGenome();
		genome2.setAdjustedScore(2.0);
		NEATGenome genome3 = new NEATGenome();
		genome3.setAdjustedScore(1.0);
		
		List<NEATGenome> list = new ArrayList<NEATGenome>();
		list.add(genome1);
		list.add(genome2);
		list.add(genome3);
		Collections.sort(list,new SortGenomesForSpecies(train));
		
		Assert.assertTrue(list.get(0)==genome3);
		Assert.assertTrue(list.get(1)==genome2);
		Assert.assertTrue(list.get(2)==genome1);
	}
	
	@Test
	public void testSort2() {
		
		MLDataSet trainingSet = new BasicMLDataSet(XOR.XOR_INPUT,XOR.XOR_IDEAL);
		NEATPopulation pop = new NEATPopulation(2,1,100);
		pop.reset();
		CalculateScore score = new TrainingSetScore(trainingSet);
		final EvolutionaryAlgorithm train = NEATUtil.constructNEATTrainer(pop,score);
				
		NEATGenome genome1 = new NEATGenome();
		genome1.setAdjustedScore(3.0);
		NEATGenome genome2 = new NEATGenome();
		genome2.setAdjustedScore(2.0);
		genome2.setBirthGeneration(200);
		NEATGenome genome3 = new NEATGenome();
		genome3.setAdjustedScore(2.0);
		genome3.setBirthGeneration(100);
		
		List<NEATGenome> list = new ArrayList<NEATGenome>();
		list.add(genome1);
		list.add(genome2);
		list.add(genome3);
		Collections.sort(list,new SortGenomesForSpecies(train));
		
		Assert.assertTrue(list.get(0)==genome2);
		Assert.assertTrue(list.get(1)==genome3);
		Assert.assertTrue(list.get(2)==genome1);
	}
}
