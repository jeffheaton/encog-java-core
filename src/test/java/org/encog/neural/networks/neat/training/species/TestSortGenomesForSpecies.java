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
