/*
 * Encog(tm) Core v2.4
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
