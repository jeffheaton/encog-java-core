package org.encog.neural.networks;

import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;
import org.encog.neural.pattern.ElmanPattern;
import org.encog.neural.pattern.JordanPattern;
import org.encog.util.benchmark.RandomTrainingFactory;

import junit.framework.TestCase;

public class TestSRN  extends TestCase {
	
	public void performElmanTest(int input, int hidden, int ideal)
	{
		// we are really just making sure no array out of bounds errors occur
		ElmanPattern elmanPattern = new ElmanPattern();
		elmanPattern.setInputNeurons(input);
		elmanPattern.addHiddenLayer(hidden);
		elmanPattern.setOutputNeurons(ideal);
		BasicNetwork network = (BasicNetwork)elmanPattern.generate();
		NeuralDataSet training = RandomTrainingFactory.generate(1000, 5, network.getInputCount(), network.getOutputCount(), -1, 1);
		ResilientPropagation prop = new ResilientPropagation(network,training);
		prop.iteration();
		prop.iteration();		
	}
	
	public void performJordanTest(int input, int hidden, int ideal)
	{
		// we are really just making sure no array out of bounds errors occur
		JordanPattern jordanPattern = new JordanPattern();
		jordanPattern.setInputNeurons(input);
		jordanPattern.addHiddenLayer(hidden);
		jordanPattern.setOutputNeurons(ideal);
		BasicNetwork network = (BasicNetwork)jordanPattern.generate();
		NeuralDataSet training = RandomTrainingFactory.generate(1000, 5, network.getInputCount(), network.getOutputCount(), -1, 1);
		ResilientPropagation prop = new ResilientPropagation(network,training);
		prop.iteration();
		prop.iteration();		
	}
	
	public void testElman()	
	{		
		performElmanTest(1,2,1);
		performElmanTest(1,5,1);
		performElmanTest(1,25,1);
		performElmanTest(2,2,2);
		performElmanTest(8,2,8);
	}
	
	public void testJordan()	
	{		
		performJordanTest(1,2,1);
		performJordanTest(1,5,1);
		performJordanTest(1,25,1);
		performJordanTest(2,2,2);
		performJordanTest(8,2,8);
	}
}
