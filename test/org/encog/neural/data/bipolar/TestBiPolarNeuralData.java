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
