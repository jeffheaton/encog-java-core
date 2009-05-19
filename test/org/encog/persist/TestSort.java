package org.encog.persist;

import java.util.Set;

import junit.framework.Assert;

import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.data.basic.BasicNeuralDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.CreateNetwork;
import org.encog.neural.networks.XOR;
import org.junit.Test;


public class TestSort {
	
	public static final String NAME_DATA1 = "data1";
	public static final String NAME_DATA2 = "data2";
	public static final String NAME_DATA3 = "data3";
	public static final String NAME_NETWORK1 = "network1";
	public static final String NAME_NETWORK2 = "network2";
	public static final String NAME_NETWORK3 = "network3";
	
	@Test
	public void testSort()
	{
		EncogPersistedCollection encog = 
			new EncogPersistedCollection("encogtest.eg");
		encog.create();
		
		BasicNeuralDataSet data1 = 
			new BasicNeuralDataSet(XOR.XOR_INPUT,XOR.XOR_IDEAL);
		BasicNeuralDataSet data2 = 
			new BasicNeuralDataSet(XOR.XOR_INPUT,XOR.XOR_IDEAL);
		BasicNeuralDataSet data3 = 
			new BasicNeuralDataSet(XOR.XOR_INPUT,XOR.XOR_IDEAL);
		
		BasicNetwork network1 = CreateNetwork.createXORNetworkUntrained();
		BasicNetwork network2 = CreateNetwork.createXORNetworkUntrained();	
		BasicNetwork network3 = CreateNetwork.createXORNetworkUntrained();
		
		encog.add(NAME_NETWORK3, network3);
		encog.add(NAME_NETWORK1, network1);
		encog.add(NAME_DATA3, data3);
		encog.add(NAME_DATA1, data1);
		encog.add(NAME_DATA2, data2);
		encog.add(NAME_NETWORK2, network2);
		
		encog.buildDirectory();
		Set<DirectoryEntry> dir = encog.getDirectory();
		Object[] dir2 = dir.toArray();
		Assert.assertEquals(NAME_DATA1, 
				((DirectoryEntry)dir2[0]).getName() );
		Assert.assertEquals(NAME_DATA2, 
				((DirectoryEntry)dir2[1]).getName() );
		Assert.assertEquals(NAME_DATA3, 
				((DirectoryEntry)dir2[2]).getName() );
		
		Assert.assertEquals(NAME_NETWORK1, 
				((DirectoryEntry)dir2[3]).getName() );
		Assert.assertEquals(NAME_NETWORK2, 
				((DirectoryEntry)dir2[4]).getName() );
		Assert.assertEquals(NAME_NETWORK3, 
				((DirectoryEntry)dir2[5]).getName() );
		
		
	}
}
