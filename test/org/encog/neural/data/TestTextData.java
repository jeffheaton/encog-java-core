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

package org.encog.neural.data;

import org.encog.persist.persistors.PropertyDataPersistor;
import org.encog.persist.persistors.TextDataPersistor;

import junit.framework.Assert;
import junit.framework.TestCase;

public class TestTextData extends TestCase {

	public void testClone() throws Exception
	{
		TextData data = new TextData();
		data.setName("name");
		data.setDescription("description");
		data.setText("text");
		TextData data2 = (TextData)data.clone();
		Assert.assertEquals("name",data2.getName());
		Assert.assertEquals("description",data2.getDescription());
		Assert.assertEquals("text",data2.getText());
		Assert.assertTrue(data.createPersistor() instanceof TextDataPersistor);
	}
}
