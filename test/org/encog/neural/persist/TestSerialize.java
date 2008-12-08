package org.encog.neural.persist;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.encog.matrix.Matrix;
import org.encog.neural.data.NeuralData;
import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.data.basic.BasicNeuralData;
import org.encog.neural.data.basic.BasicNeuralDataSet;
import org.encog.neural.data.bipolar.BiPolarNeuralData;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.Train;
import org.encog.neural.networks.XOR;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.layers.FeedforwardLayer;
import org.encog.neural.networks.layers.HopfieldLayer;
import org.encog.neural.networks.layers.SOMLayer;
import org.encog.neural.networks.training.backpropagation.Backpropagation;
import org.encog.neural.networks.training.hopfield.TrainHopfield;
import org.encog.util.NormalizeInput.NormalizationType;

import junit.framework.TestCase;

public class TestSerialize extends TestCase {
	
	public static final double MAX_ERROR = 0.05;
	
	public static final double trainedData[][] = {{0.48746847854732106, 0.5010119462167667, 0.5167202966276256, 0.4943294925693857, 0.0},
		{-0.49856939758003643, -0.5027761724685629, -0.5061504393638588, -0.49239862807111295, 0.0}};
				
	
	private BasicNetwork createNetwork() 
	{
		BasicNetwork network = new BasicNetwork();
		network.addLayer(new FeedforwardLayer(2));
		network.addLayer(new FeedforwardLayer(3));
		network.addLayer(new FeedforwardLayer(1));
		network.reset();
		return network;
	}
	
	private BasicNetwork roundTrip(BasicNetwork starting) throws IOException, ClassNotFoundException
	{
		ByteArrayOutputStream memory = new ByteArrayOutputStream();
		ObjectOutputStream objectOut = new ObjectOutputStream(memory);
		objectOut.writeObject(starting);
		objectOut.close();
		memory.close();
		ByteArrayInputStream memoryInput = new ByteArrayInputStream(memory.toByteArray());
		ObjectInputStream objectIn = new ObjectInputStream(memoryInput);
		BasicNetwork result = (BasicNetwork)objectIn.readObject();	
		objectIn.close();
		return result;
	}
	
	public void testFeedforwardPersist() throws Throwable
	{
		NeuralDataSet trainingData = new BasicNeuralDataSet(XOR.XOR_INPUT,XOR.XOR_IDEAL);
		
		BasicNetwork network = createNetwork();
		Train train = new Backpropagation(network, trainingData, 0.7, 0.9); 	

		for (int i = 0; i < 5000; i++) 
		{
			train.iteration();
			network = (BasicNetwork) train.getNetwork();
		}
		
		TestCase.assertTrue("Error too high for backpropagation",train.getError()<0.1);
		TestCase.assertTrue("XOR outputs not correct",XOR.verifyXOR(network, 0.1));
		
		BasicNetwork n = roundTrip(network);
		
		TestCase.assertTrue("Error too high for load",n.calculateError(trainingData)<0.1);

	}
	
	public void testHopfieldPersist() throws Exception
	{
		boolean input[] = { true, false, true, false };
		
		BasicNetwork network = new BasicNetwork();
		network.addLayer(new HopfieldLayer(4));
		
		NeuralData data = new BiPolarNeuralData(input);
		Train train = new TrainHopfield(data,network);
		train.iteration();
		
		BasicNetwork network2 = roundTrip(network);
		
		BiPolarNeuralData output = (BiPolarNeuralData) network2.compute(new BiPolarNeuralData(input));
		TestCase.assertTrue(output.getBoolean(0));
		TestCase.assertFalse(output.getBoolean(1));
		TestCase.assertTrue(output.getBoolean(2));
		TestCase.assertFalse(output.getBoolean(3));
	}
	
	public void testSOMPersist() throws Exception
	{
		Matrix matrix = new Matrix(TestSerialize.trainedData);
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
		layer.setMatrix(matrix);
		
		BasicNetwork network2 = roundTrip(network);
		
		int data1Neuron = network2.winner(data1);
		int data2Neuron = network2.winner(data2);
		
		TestCase.assertTrue(data1Neuron!=data2Neuron);
		
		int data3Neuron = network2.winner(data3);
		int data4Neuron = network2.winner(data4);
		
		TestCase.assertTrue(data3Neuron==data1Neuron);
		TestCase.assertTrue(data4Neuron==data2Neuron);
	}
}
