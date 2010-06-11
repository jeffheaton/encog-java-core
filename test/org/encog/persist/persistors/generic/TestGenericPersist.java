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

package org.encog.persist.persistors.generic;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import junit.framework.TestCase;

import org.encog.parse.tags.read.ReadXML;
import org.encog.parse.tags.write.WriteXML;
import org.encog.persist.EncogPersistedObject;
import org.junit.Assert;
import org.junit.Test;

public class TestGenericPersist extends TestCase {

	private EncogPersistedObject roundTrip(final EncogPersistedObject obj)
			throws IOException, ClassNotFoundException {
		final ByteArrayOutputStream memory = new ByteArrayOutputStream();
		final WriteXML writeXML = new WriteXML(memory);
		final Object2XML obj2xml = new Object2XML();
		writeXML.beginDocument();
		obj2xml.save(obj, writeXML);
		writeXML.endDocument();
		memory.close();

		final ByteArrayInputStream memoryInput = new ByteArrayInputStream(
				memory.toByteArray());
		final ReadXML readXML = new ReadXML(memoryInput);
		final XML2Object xml2obj = new XML2Object();
		final SimpleObjectToPersist result = new SimpleObjectToPersist();
		advance(readXML);
		xml2obj.load(readXML, result);
		memoryInput.close();
		return result;
	}
	
	private void advance(ReadXML in)
	{
		while( !in.is(""+SimpleObjectToPersist.class.getSimpleName(), true)) {
			// read the next tag, if we hit EOF, then return.
			if(!in.readToTag())
				return;
		}
	}

	@Test
	public void testGenericPersist() throws Exception {
		/*SimpleObjectToPersist simple = new SimpleObjectToPersist();
		simple.setFirst("first");
		simple.setSecond("second");
		simple.setName("name");
		simple.setDescription("description");
		simple.setNumber(10);
		simple = (SimpleObjectToPersist) roundTrip(simple);
		Assert.assertEquals(simple.getName(), "name");
		Assert.assertEquals(simple.getDescription(), "description");
		Assert.assertEquals("first",simple.getFirst());
		Assert.assertEquals("second",simple.getSecond());
		Assert.assertEquals(10,simple.getNumber(),0.01);*/

	}
}
