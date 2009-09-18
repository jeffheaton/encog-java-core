package org.encog.neural.data;

import org.encog.persist.persistors.PropertyDataPersistor;
import org.junit.Assert;

import junit.framework.TestCase;

public class TestPropertyData extends TestCase {
	public void testPropertyErrors() throws Exception
	{
		PropertyData data = new PropertyData();
		data.set("prop", "a");
		
		try
		{
			data.getInteger("prop");
			Assert.assertTrue(false);
		}
		catch(Exception e)
		{			
		}
		
		try
		{
			data.getDouble("prop");
			Assert.assertTrue(false);
		}
		catch(Exception e)
		{			
		}
		
		try
		{
			data.getDate("prop");
			Assert.assertTrue(false);
		}
		catch(Exception e)
		{			
		}
		
	}
	
	public void testSuccess()
	{
		PropertyData data = new PropertyData();
		data.set("int", "10");
		data.set("double", "10.5");
		data.set("date", "9/18/09");
		
		int i = data.getInteger("int");
		Assert.assertEquals(10, i);
		double d = data.getDouble("double");
		data.getDate("date");
		Assert.assertEquals(d,10.5,0.5);
		
		data.remove("date");
		Assert.assertEquals(2, data.size());
		
		Assert.assertTrue(data.createPersistor() instanceof PropertyDataPersistor);
		Assert.assertNotNull(data.getData());
	}
	
	public void testClone() throws Exception
	{
		PropertyData data = new PropertyData();
		data.setName("name");
		data.setDescription("description");
		data.set("prop", "value");
		PropertyData data2 = (PropertyData)data.clone();
		Assert.assertEquals("name", data.getName());
		Assert.assertEquals("description", data.getDescription());
		String value = data.get("prop");
		Assert.assertEquals("value", value);
		Assert.assertTrue(data2.isDefined("prop"));
	}
}
