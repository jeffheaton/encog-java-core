package org.encog.neural.som;

import org.encog.neural.data.NeuralData;
import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.data.basic.BasicNeuralData;
import org.encog.neural.data.basic.BasicNeuralDataSet;
import org.encog.neural.networks.som.SelfOrganizingMap;
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
		
		SelfOrganizingMap som = new SelfOrganizingMap(4,2,NormalizationType.MULTIPLICATIVE);
		
		NeuralDataSet trainingSet = new BasicNeuralDataSet();
		trainingSet.add(data1);
		trainingSet.add(data2);
		
		final TrainSelfOrganizingMap train = new TrainSelfOrganizingMap(
				som, trainingSet,LearningMethod.SUBTRACTIVE,0.5);
		int tries = 1;

		do {
			train.iteration();
			System.out.println( train.getTotalError() );
		} while ((train.getTotalError() > MAX_ERROR) );

		
	}
}
