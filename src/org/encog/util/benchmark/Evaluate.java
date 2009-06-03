package org.encog.util.benchmark;

import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.training.Train;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;

public class Evaluate {
	public static double evaluateTrain(BasicNetwork network, NeuralDataSet training)
	{
		// train the neural network
		final Train train = new ResilientPropagation(network, training);
		long result=Long.MAX_VALUE;
		
		for(int i=1;i<10;i++) {
			long start = System.currentTimeMillis();
			train.iteration();
			long time = System.currentTimeMillis()-start;
			if( time<result)
				result = time;
		} 
		return result/1000.0;
	}
	
	public static double evaluateNetwork(BasicNetwork network, NeuralDataSet training)
	{
		// train the neural network
		long result=Long.MAX_VALUE;
		
		for(int i=1;i<10;i++) {
			long start = System.currentTimeMillis();
			network.calculateError(training);
			long time = System.currentTimeMillis()-start;
			if( time<result)
				result = time;
		} 
		
		return result/1000.0;
	}
}
