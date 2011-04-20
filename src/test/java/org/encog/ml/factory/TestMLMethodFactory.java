package org.encog.ml.factory;

import org.encog.EncogError;
import org.encog.ml.svm.SVM;
import org.encog.neural.activation.ActivationLinear;
import org.encog.neural.activation.ActivationTANH;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.rbf.RBFNetwork;

import junit.framework.Assert;
import junit.framework.TestCase;

public class TestMLMethodFactory extends TestCase {

	public static final String TYPE_FEEDFORWARD = "feedforward";
	public static final String TYPE_RBFNETWORK = "rbfnetwork";
	public static final String TYPE_SVM = "svm";
	public static final String TYPE_SOM = "som";
	
	public void testFactoryFeedforward() {
		String architecture = "?:B->TANH->3->LINEAR->?:B";
		MLMethodFactory factory = new MLMethodFactory();
		BasicNetwork network = (BasicNetwork)factory.create(MLMethodFactory.TYPE_FEEDFORWARD, architecture, 1, 4);
		Assert.assertTrue(network.isLayerBiased(0));
		Assert.assertFalse(network.isLayerBiased(1));
		Assert.assertTrue(network.isLayerBiased(2));
		Assert.assertEquals(3, network.getLayerCount());
		Assert.assertTrue(network.getActivation(0) instanceof ActivationTANH );
		Assert.assertTrue(network.getActivation(1) instanceof ActivationLinear );
		Assert.assertTrue(network.getActivation(2) instanceof ActivationLinear );
		Assert.assertEquals(18,network.encodedArrayLength());
		Assert.assertEquals(1,network.getLayerNeuronCount(0));
		Assert.assertEquals(3,network.getLayerNeuronCount(1));
		Assert.assertEquals(4,network.getLayerNeuronCount(2));
	}
	
	private void expectError(String t, String a) {
		MLMethodFactory factory = new MLMethodFactory();
		try {
			factory.create(t, a, 2, 1);
			Assert.assertTrue(false);
		} catch(EncogError e) {
			// good
		}
	}
	
	public void testFactoryFeedforwardError() {
		String ARC1 = "?->3->ERROR->?";
		String ARC2 = "?->?->?";
		String ARC3 = "?->3->0->?";
		
		expectError(MLMethodFactory.TYPE_FEEDFORWARD, ARC1);
		expectError(MLMethodFactory.TYPE_FEEDFORWARD, ARC2);
		expectError(MLMethodFactory.TYPE_FEEDFORWARD, ARC3);

	}
	
	public void testFactoryRBF() {
		String architecture = "?->GAUSSIAN(c=4)->?";
		MLMethodFactory factory = new MLMethodFactory();
		RBFNetwork network = (RBFNetwork)factory.create(MLMethodFactory.TYPE_RBFNETWORK, architecture, 1, 4);
		Assert.assertEquals(1,network.getInputCount());
		Assert.assertEquals(4,network.getOutputCount());
		Assert.assertEquals(4,network.getRBF().length);	
	}
	
	public void testFactorySVM() {
		String architecture = "?->C(KERNEL=RBF,TYPE=NEW)->?";
		MLMethodFactory factory = new MLMethodFactory();
		SVM network = (SVM)factory.create(MLMethodFactory.TYPE_SVM, architecture, 4, 1);
		Assert.assertEquals(4,network.getInputCount());
		Assert.assertEquals(1,network.getOutputCount());
	}
	
	public void testFactorySOM() {
		
	}
	
}
