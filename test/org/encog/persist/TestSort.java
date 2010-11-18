/*
 * Encog(tm) Core v2.6 Unit Test - Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 
 * Copyright 2008-2010 Heaton Research, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *   
 * For more information on Heaton Research copyrights, licenses 
 * and trademarks visit:
 * http://www.heatonresearch.com/copyright
 */
package org.encog.persist;

import java.util.Collection;
import java.util.Set;

import junit.framework.Assert;

import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.data.basic.BasicNeuralDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.NetworkUtil;
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
		
		BasicNetwork network1 = NetworkUtil.createXORNetworkUntrained();
		BasicNetwork network2 = NetworkUtil.createXORNetworkUntrained();	
		BasicNetwork network3 = NetworkUtil.createXORNetworkUntrained();
		
		encog.add(NAME_NETWORK3, network3);
		encog.add(NAME_NETWORK1, network1);
		encog.add(NAME_DATA3, data3);
		encog.add(NAME_DATA1, data1);
		encog.add(NAME_DATA2, data2);
		encog.add(NAME_NETWORK2, network2);
		
		encog.buildDirectory();
		Collection<DirectoryEntry> dir = encog.getDirectory();
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
