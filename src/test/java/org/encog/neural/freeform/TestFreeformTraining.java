package org.encog.neural.freeform;

import junit.framework.TestCase;

import org.encog.mathutil.randomize.RangeRandomizer;
import org.encog.ml.MLMethod;
import org.encog.ml.MethodFactory;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.encog.ml.genetic.MLMethodGeneticAlgorithm;
import org.encog.ml.train.MLTrain;
import org.encog.neural.freeform.training.FreeformBackPropagation;
import org.encog.neural.freeform.training.FreeformResilientPropagation;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.NetworkUtil;
import org.encog.neural.networks.XOR;
import org.encog.neural.networks.training.CalculateScore;
import org.encog.neural.networks.training.TrainingSetScore;
import org.encog.neural.networks.training.anneal.NeuralSimulatedAnnealing;
import org.encog.neural.networks.training.genetic.NeuralGeneticAlgorithm;
import org.junit.Test;

public class TestFreeformTraining extends TestCase {
	
	@Test
	public void testBPROP() throws Throwable
	{
		MLDataSet trainingData = new BasicMLDataSet(XOR.XOR_INPUT,XOR.XOR_IDEAL);
		
		FreeformNetwork network = NetworkUtil.createXORFreeformNetworkUntrained();

		MLTrain bprop = new FreeformBackPropagation(network, trainingData, 0.7, 0.9);
		NetworkUtil.testTraining(bprop,0.01);
	}
	
	@Test
	public void testRPROP() throws Throwable
	{
		MLDataSet trainingData = new BasicMLDataSet(XOR.XOR_INPUT,XOR.XOR_IDEAL);
		
		FreeformNetwork network = NetworkUtil.createXORFreeformNetworkUntrained();

		MLTrain bprop = new FreeformResilientPropagation(network, trainingData);
		NetworkUtil.testTraining(bprop,0.01);
	}
	
	@Test
	public void testAnneal() throws Throwable
	{
		MLDataSet trainingData = new BasicMLDataSet(XOR.XOR_INPUT,XOR.XOR_IDEAL);		
		FreeformNetwork network = NetworkUtil.createXORFreeformNetworkUntrained();
		CalculateScore score = new TrainingSetScore(trainingData);
		NeuralSimulatedAnnealing anneal = new NeuralSimulatedAnnealing(network,score,10,2,100);
		NetworkUtil.testTraining(anneal,0.01);
	}
	
	@Test
	public void testGenetic() throws Throwable
	{
		MLDataSet trainingData = new BasicMLDataSet(XOR.XOR_INPUT,XOR.XOR_IDEAL);		
		CalculateScore score = new TrainingSetScore(trainingData);
		MLMethodGeneticAlgorithm genetic = new MLMethodGeneticAlgorithm(new MethodFactory(){
			@Override
			public MLMethod factor() {
				FreeformNetwork network = NetworkUtil.createXORFreeformNetworkUntrained();
				network.reset();
				return network;
			}}, score, 500,0.1,0.25);
		NetworkUtil.testTraining(genetic,0.00001);
	}
}
