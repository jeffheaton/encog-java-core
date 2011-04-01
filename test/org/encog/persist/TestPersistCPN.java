package org.encog.persist;

import java.io.File;
import java.io.IOException;

import org.encog.neural.art.ART1;
import org.encog.neural.cpn.CPN;
import org.encog.util.TempDir;
import org.encog.util.obj.SerializeObject;

import junit.framework.Assert;
import junit.framework.TestCase;

public class TestPersistCPN extends TestCase {
	
	public final TempDir TEMP_DIR = new TempDir();
	public final File EG_FILENAME = TEMP_DIR.createFile("encogtest.eg");
	public final File SERIAL_FILENAME = TEMP_DIR.createFile("encogtest.ser");
	
	private CPN create() {
		CPN result = new CPN(5, 4, 3, 2);
		return result;
	}
	
	public void testPersistEG()
	{
		CPN network = create();

		EncogDirectoryPersistence.saveObject(EG_FILENAME, network);
		CPN network2 = (CPN)EncogDirectoryPersistence.loadObject(EG_FILENAME);

		validate(network2);
	}
	
	public void testPersistSerial() throws IOException, ClassNotFoundException
	{
		CPN network = create();
		
		SerializeObject.save(SERIAL_FILENAME, network);
		CPN network2 = (CPN)SerializeObject.load(SERIAL_FILENAME);
				
		validate(network2);
	}
	
	private void validate(CPN cpn) {
		Assert.assertEquals(5, cpn.getInputCount());
		Assert.assertEquals(4, cpn.getInstarCount());
		Assert.assertEquals(3, cpn.getOutputCount());
		Assert.assertEquals(3, cpn.getOutstarCount());
		Assert.assertEquals(2, cpn.getWinnerCount());
		Assert.assertEquals(5, cpn.getWeightsInputToInstar().getRows());
		Assert.assertEquals(4, cpn.getWeightsInputToInstar().getCols());
		Assert.assertEquals(4, cpn.getWeightsInstarToOutstar().getRows());
		Assert.assertEquals(3, cpn.getWeightsInstarToOutstar().getCols());
	}
	
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		TEMP_DIR.dispose();
	}
}
