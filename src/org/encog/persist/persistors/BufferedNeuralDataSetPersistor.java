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

package org.encog.persist.persistors;

import java.io.File;

import org.encog.neural.data.NeuralDataPair;
import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.data.basic.BasicNeuralDataSet;
import org.encog.neural.data.buffer.BufferedNeuralDataSet;
import org.encog.parse.tags.read.ReadXML;
import org.encog.parse.tags.write.WriteXML;
import org.encog.persist.EncogPersistedCollection;
import org.encog.persist.EncogPersistedObject;
import org.encog.persist.Persistor;
import org.encog.util.csv.CSVFormat;
import org.encog.util.csv.NumberList;

public class BufferedNeuralDataSetPersistor implements Persistor {

	/**
	 * Tag to hold the binary file name.
	 */
	public static final String TAG_FILE = "file";
	
	/**
	 * Load the binary file.
	 * @param in Where to read the file from.
	 */
	@Override
	public EncogPersistedObject load(ReadXML in) {
		final String name = in.getTag().getAttributes().get(
				EncogPersistedCollection.ATTRIBUTE_NAME);
		final String description = in.getTag().getAttributes().get(
				EncogPersistedCollection.ATTRIBUTE_DESCRIPTION);
		File file = null;
		
		while (in.readToTag()) {
			if (in.is(BufferedNeuralDataSetPersistor.TAG_FILE, true)) {
				String str = in.readTextToTag();
				file = new File(str);
			} else if (in.is(EncogPersistedCollection.TYPE_TRAINING, false)) {
				break;
			}
		}
		
		BufferedNeuralDataSet binary = new BufferedNeuralDataSet(file);
		binary.setName(name);
		binary.setDescription(description);

		return binary;
	}

	/**
	 * Save the binary file.
	 * @param obj The object to save.
	 * @param out Where to save it to.
	 */
	@Override
	public void save(EncogPersistedObject obj, WriteXML out) {
		
		BufferedNeuralDataSet binary = (BufferedNeuralDataSet)obj;
		
		PersistorUtil.beginEncogObject(EncogPersistedCollection.TYPE_BINARY,
				out, obj, true);
		
		out.addProperty(BufferedNeuralDataSetPersistor.TAG_FILE, binary.getFile().toString());
		
		out.endTag();

	}

}
