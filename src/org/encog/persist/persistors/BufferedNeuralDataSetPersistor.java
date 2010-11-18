/*
 * Encog(tm) Core v2.6 - Java Version
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
			} else if (in.is(EncogPersistedCollection.TYPE_BINARY, false)) {
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
