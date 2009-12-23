package org.encog.neural.networks.logic;

import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.data.basic.BasicNeuralDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.NetworkUtil;
import org.encog.neural.networks.XOR;
import org.encog.neural.networks.training.Train;
import org.encog.neural.networks.training.simple.TrainAdaline;
import org.encog.neural.pattern.ADALINEPattern;

import junit.framework.TestCase;

public class TestADALINE extends TestCase {

	public void testAdalineNet() throws Throwable
	{
		ADALINEPattern pattern = new ADALINEPattern();
		pattern.setInputNeurons(2);
		pattern.setOutputNeurons(1);
		BasicNetwork network = pattern.generate();
		
		// train it
		NeuralDataSet training = new BasicNeuralDataSet(XOR.XOR_INPUT,XOR.XOR_IDEAL);
		Train train = new TrainAdaline(network,training,0.01);
		NetworkUtil.testTraining(train,0.01);
	}
	
}
