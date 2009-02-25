package org.encog.neural.networks.layers;

import org.encog.matrix.Matrix;
import org.encog.neural.data.NeuralData;
import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.data.basic.BasicNeuralData;
import org.encog.neural.data.basic.BasicNeuralDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.layers.SOMLayer;
import org.encog.neural.networks.training.som.TrainSelfOrganizingMap;
import org.encog.neural.networks.training.som.TrainSelfOrganizingMap.LearningMethod;
import org.encog.util.NormalizeInput.NormalizationType;

import junit.framework.TestCase;

public class TestSOM extends TestCase {
	
	public static final double MAX_ERROR = 0.05;
	
	public static final double untrainedData[][] = 
	{{0.6882551270881396, -0.9291471917702279, 0.9631574105879768, -0.6847023846227012, 0.6445001219615334},
	{0.2596121773799609, 0.20903647997830488, 0.44901788840545387, -0.8991254913211779, -0.4440569188207164}};
	
	public static final double trainedData[][] = {{0.48746847854732106, 0.5010119462167667, 0.5167202966276256, 0.4943294925693857, 0.0},
		{-0.49856939758003643, -0.5027761724685629, -0.5061504393638588, -0.49239862807111295, 0.0}};
				
	public void testTrainSOM()
	{
		Matrix matrix = new Matrix(TestSOM.untrainedData);
		double pattern1[] = { -0.5, -0.5, -0.5, -0.5 };
		double pattern2[] = {  0.5,  0.5,  0.5,  0.5 };
		
		NeuralData data1 = new BasicNeuralData(pattern1);
		NeuralData data2 = new BasicNeuralData(pattern2);
		
		SOMLayer layer;
		BasicNetwork network = new BasicNetwork();
		network.addLayer(layer = new SOMLayer(4,NormalizationType.MULTIPLICATIVE));
		network.addLayer(new BasicLayer(2));
		
		layer.getSynapse().setMatrix(matrix);
		
		NeuralDataSet trainingSet = new BasicNeuralDataSet();
		trainingSet.add(data1);
		trainingSet.add(data2);
		
		final TrainSelfOrganizingMap train = new TrainSelfOrganizingMap(
				network, trainingSet,LearningMethod.SUBTRACTIVE,0.5);
		

		train.iteration();
		double error1 = train.getTotalError();
		train.iteration();
		double error2 = train.getTotalError();
		
		double diff = (error2-error1)/error1;
		
		TestCase.assertTrue(diff<=0.0);		
	}
	
	public void testRunSOM()
	{
		Matrix matrix = new Matrix(TestSOM.trainedData);
		
		double pattern1[] = { -0.5, -0.5, -0.5, -0.5 };
		double pattern2[] = {  0.5,  0.5,  0.5,  0.5 };
		double pattern3[] = { -0.5, -0.5, -0.5,  0.5 };
		double pattern4[] = {  0.5,  0.5,  0.5, -0.5 };
		
		NeuralData data1 = new BasicNeuralData(pattern1);
		NeuralData data2 = new BasicNeuralData(pattern2);
		NeuralData data3 = new BasicNeuralData(pattern3);
		NeuralData data4 = new BasicNeuralData(pattern4);
		
		SOMLayer layer;
		BasicNetwork network = new BasicNetwork();
		network.addLayer(layer = new SOMLayer(4,NormalizationType.MULTIPLICATIVE));
		network.addLayer(new BasicLayer(2));
		
		layer.getSynapse().setMatrix(matrix);
		
		int data1Neuron = network.winner(data1);
		int data2Neuron = network.winner(data2);
		
		TestCase.assertTrue(data1Neuron!=data2Neuron);
		
		int data3Neuron = network.winner(data3);
		int data4Neuron = network.winner(data4);
		
		TestCase.assertTrue(data3Neuron==data1Neuron);
		TestCase.assertTrue(data4Neuron==data2Neuron);		
	}
}
