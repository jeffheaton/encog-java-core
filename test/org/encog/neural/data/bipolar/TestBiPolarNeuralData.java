/*
 * Encog(tm) Artificial Intelligence Framework v2.3
 * Java Version, Unit Tests
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

package org.encog.neural.data.bipolar;

import org.junit.Assert;

import junit.framework.TestCase;

public class TestBiPolarNeuralData extends TestCase {
	public void testConstruct()
	{
		boolean[] d = { true, false };
		BiPolarNeuralData data = new BiPolarNeuralData(d);
		Assert.assertEquals("[T,F]",data.toString());
		Assert.assertEquals(1,data.getData(0),0.5);
		Assert.assertEquals(-1,data.getData(1),0.5);
		Assert.assertEquals(true, data.getBoolean(0));
		Assert.assertEquals(false, data.getBoolean(1));
		Assert.assertEquals(data.getData().length,2);
	}
	
	public void testClone()
	{
		boolean[] d = { true, false };
		BiPolarNeuralData data2 = new BiPolarNeuralData(d);
		BiPolarNeuralData data = (BiPolarNeuralData)data2.clone();
		Assert.assertEquals("[T,F]",data.toString());
		Assert.assertEquals(1,data.getData(0),0.5);
		Assert.assertEquals(-1,data.getData(1),0.5);
		Assert.assertEquals(true, data.getBoolean(0));
		Assert.assertEquals(false, data.getBoolean(1));
		Assert.assertEquals(data.getData().length,2);
	}
	
	public void testError()
	{
		BiPolarNeuralData data = new BiPolarNeuralData(2);
		Assert.assertEquals(2, data.size());
		
		try
		{
			data.add(0, 0);
			Assert.assertTrue(false);
		}
		catch(Exception e)
		{
		}
	}
	
	public void testClear()
	{
		double[] d = {1,1};
		BiPolarNeuralData data = new BiPolarNeuralData(2);
		data.setData(d);
		data.clear();
		Assert.assertEquals(-1,data.getData(0),0.5);
		data.setData(0,true);
		Assert.assertEquals(true,data.getBoolean(0));
	}
		

}
