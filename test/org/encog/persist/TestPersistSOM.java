package org.encog.persist;

import java.io.IOException;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.encog.neural.som.SOM;
import org.encog.util.obj.SerializeObject;

public class TestPersistSOM extends TestCase {
	public final String EG_FILENAME = "encogtest.eg";
	public final String EG_RESOURCE = "test";
	public final String SERIAL_FILENAME = "encogtest.ser";
	
	private SOM create()
	{
		SOM network = new SOM(4,2);
		return network;
	}
	
	public void testPersistEG()
	{
		SOM network = create();
		EncogMemoryCollection encog = new EncogMemoryCollection();
		encog.add(EG_RESOURCE, network);
		encog.save(EG_FILENAME);
		
		EncogMemoryCollection encog2 = new EncogMemoryCollection();
		encog2.load(EG_FILENAME);
		SOM network2 = (SOM)encog2.find(EG_RESOURCE);
		
		validate(network2);
	}
	
	public void testPersistSerial() throws IOException, ClassNotFoundException
	{
		SOM network = create();		
		SerializeObject.save(SERIAL_FILENAME, network);
		SOM network2 = (SOM)SerializeObject.load(SERIAL_FILENAME);
				
		validate(network2);
	}
	
	public void testPersistSerialEG() throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException
	{
		SOM network = create();		
		SerializeObject.saveEG(SERIAL_FILENAME, network);
		SOM network2 = (SOM)SerializeObject.loadEG(SERIAL_FILENAME);
				
		validate(network2);
	}
	
	private void validate(SOM network)
	{
		Assert.assertEquals(4, network.getInputNeuronCount());
		Assert.assertEquals(2, network.getOutputNeuronCount());
		Assert.assertEquals(10, network.getWeights().toPackedArray().length);
	}
}
