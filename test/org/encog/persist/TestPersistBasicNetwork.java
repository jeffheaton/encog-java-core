package org.encog.persist;

import java.io.File;
import java.io.IOException;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.encog.neural.art.ART1;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.XOR;
import org.encog.util.obj.SerializeObject;

public class TestPersistBasicNetwork extends TestCase {
	
	public final String EG_FILENAME = "encogtest.eg";
	public final String EG_RESOURCE = "test";
	public final String SERIAL_FILENAME = "encogtest.ser";
	
	
	public BasicNetwork create()
	{
		BasicNetwork network = XOR.createTrainedXOR();
		XOR.verifyXOR(network, 0.1);
		
		network.setProperty("test", "test2");
		network.setDescription("desc");
		
		return network;
	}
	
	public void validate(BasicNetwork network)
	{
		network.clearContext();
		XOR.verifyXOR(network, 0.1);
	}
	
	public void testPersistEG()
	{
		BasicNetwork network = create();

		EncogDirectoryPersistence.saveObject(new File(EG_FILENAME), network);
		BasicNetwork network2 = (BasicNetwork)EncogDirectoryPersistence.loadObject(new File(EG_FILENAME));

		validate(network2);
	}
	
	public void testPersistSerial() throws IOException, ClassNotFoundException
	{
		BasicNetwork network = create();
		
		SerializeObject.save(SERIAL_FILENAME, network);
		BasicNetwork network2 = (BasicNetwork)SerializeObject.load(SERIAL_FILENAME);
				
		validate(network2);
	}

	
}
