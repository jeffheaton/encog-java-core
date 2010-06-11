/*
 * Encog(tm) Core v2.5 
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 * 
 * Copyright 2008-2010 by Heaton Research Inc.
 * 
 * Released under the LGPL.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 * 
 * Encog and Heaton Research are Trademarks of Heaton Research, Inc.
 * For information on Heaton Research trademarks, visit:
 * 
 * http://www.heatonresearch.com/copyright.html
 */

package org.encog.persist;

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
