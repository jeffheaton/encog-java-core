/*
 * Encog(tm) Core v3.3 - Java Version
 * http://www.heatonresearch.com/encog/
 * https://github.com/encog/encog-java-core
 
 * Copyright 2008-2014 Heaton Research, Inc.
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
package org.encog.neural.data.bipolar;

import junit.framework.TestCase;

import org.encog.ml.data.specific.BiPolarNeuralData;
import org.junit.Assert;

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
