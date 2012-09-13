package org.encog.persist;

import java.io.File;
import java.io.IOException;

import junit.framework.TestCase;

import org.encog.neural.freeform.FreeformNetwork;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.XOR;
import org.encog.util.TempDir;
import org.encog.util.obj.SerializeObject;
import org.junit.Test;

public class TestPersistFreeform extends TestCase {

	public final TempDir TEMP_DIR = new TempDir();
	public final File EG_FILENAME = TEMP_DIR.createFile("encogtest.eg");
	public final File SERIAL_FILENAME = TEMP_DIR.createFile("encogtest.ser");

	public FreeformNetwork create()
	{
		FreeformNetwork network = XOR.createTrainedFreeformXOR();
		XOR.verifyXOR(network, 0.1);
		
		network.setProperty("test", "test2");

		return network;
	}
	
	public void validate(FreeformNetwork network)
	{
		network.clearContext();
		XOR.verifyXOR(network, 0.1);
	}
	
	@Test
	public void testPersistSerial() throws IOException, ClassNotFoundException
	{
		FreeformNetwork network = create();
		
		SerializeObject.save(SERIAL_FILENAME, network);
		FreeformNetwork network2 = (FreeformNetwork)SerializeObject.load(SERIAL_FILENAME);
				
		validate(network2);
	}
}
