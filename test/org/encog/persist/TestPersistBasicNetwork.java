package org.encog.persist;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.encog.mathutil.rbf.RBFEnum;
import org.encog.neural.art.ART1;
import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.XOR;
import org.encog.neural.rbf.RBFNetwork;
import org.encog.neural.rbf.training.SVDTraining;

public class TestPersistBasicNetwork extends TestCase {
	
	public final String EG_FILENAME = "encogtest.eg";
	public final String EG_RESOURCE = "test";
	public final String SERIAL_FILENAME = "encogtest.ser";
	
	
	public void testPersistNetwork()
	{
		BasicNetwork network = XOR.createTrainedXOR();
		XOR.verifyXOR(network, 0.1);
		
		network.setProperty("test", "test2");
		network.setDescription("desc");
		
		EncogMemoryCollection encog = new EncogMemoryCollection();
		encog.add(EG_RESOURCE, network);
		encog.save(EG_FILENAME);
		
		EncogMemoryCollection encog2 = new EncogMemoryCollection();
		encog2.load(EG_FILENAME);
		BasicNetwork network2 = (BasicNetwork)encog2.find(EG_RESOURCE);
		Assert.assertEquals("desc", network2.getDescription());
		network2.clearContext();
		XOR.verifyXOR(network2, 0.1);
		Assert.assertEquals("test2", network2.getPropertyString("test"));
	}
	
}
