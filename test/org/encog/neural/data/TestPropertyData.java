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
