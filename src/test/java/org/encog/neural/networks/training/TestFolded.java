package org.encog.neural.networks.training;

import junit.framework.TestCase;

import org.encog.ml.MLRegression;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.folded.FoldedDataSet;
import org.encog.ml.train.MLTrain;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.NetworkUtil;
import org.encog.neural.networks.XOR;
import org.encog.neural.networks.training.cross.CrossValidationKFold;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;
import org.encog.util.simple.EncogUtility;
import org.junit.Test;

public class TestFolded extends TestCase {
	@Test
	public void testRPROP() throws Throwable
	{
		MLDataSet trainingData = XOR.createNoisyXORDataSet(10);
		
		BasicNetwork network = NetworkUtil.createXORNetworkUntrained();
		
		final FoldedDataSet folded = new FoldedDataSet(trainingData); 
		final MLTrain train = new ResilientPropagation(network, folded);
		final CrossValidationKFold trainFolded = new CrossValidationKFold(train,4);
		
		EncogUtility.trainToError(trainFolded, 0.2);
		
		XOR.verifyXOR((MLRegression)trainFolded.getMethod(), 0.2);
		
	}
}
