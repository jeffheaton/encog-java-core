package org.encog.neural.som;

import org.encog.neural.data.NeuralData;
import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.data.basic.BasicNeuralData;
import org.encog.neural.data.basic.BasicNeuralDataSet;
import org.encog.neural.networks.som.SOMLayer;
import org.encog.neural.networks.som.TrainSelfOrganizingMap;
import org.encog.neural.networks.som.NormalizeInput.NormalizationType;
import org.encog.neural.networks.som.TrainSelfOrganizingMap.LearningMethod;

import junit.framework.TestCase;

public class TestSOM extends TestCase {
	
	public static final double MAX_ERROR = 0.05;
	
	public void testSOM()
	{
		double pattern1[] = { -0.5, -0.5, -0.5, -0.5 };
		double pattern2[] = {  0.5,  0.5,  0.5,  0.5 };
		double pattern3[] = { -0.5, -0.5, -0.5,  0.5 };
		double pattern4[] = {  0.5,  0.5,  0.5, -0.5 };
		
		NeuralData data1 = new BasicNeuralData(pattern1);
		NeuralData data2 = new BasicNeuralData(pattern2);
		NeuralData data3 = new BasicNeuralData(pattern3);
		NeuralData data4 = new BasicNeuralData(pattern4);
		
		SOMLayer som = new SOMLayer(4,2,NormalizationType.MULTIPLICATIVE);
		
		NeuralDataSet trainingSet = new BasicNeuralDataSet();
		trainingSet.add(data1);
		trainingSet.add(data2);
		
		final TrainSelfOrganizingMap train = new TrainSelfOrganizingMap(
				som, trainingSet,LearningMethod.SUBTRACTIVE,0.5);
		int tries = 1;

		do {
			train.iteration();
			System.out.println(train.getTotalError());
			tries++;
		} while ((train.getTotalError() > MAX_ERROR) && tries<1000 );
		
		TestCase.assertTrue(tries<100);
		
		int data1Neuron = som.winner(data1);
		int data2Neuron = som.winner(data2);
		
		TestCase.assertTrue(data1Neuron!=data2Neuron);
		
		int data3Neuron = som.winner(data3);
		int data4Neuron = som.winner(data4);
		
		TestCase.assertTrue(data3Neuron==data1Neuron);
		TestCase.assertTrue(data4Neuron==data2Neuron);

		
		
	}
}
