/*
 * Encog Artificial Intelligence Framework v2.x
 * Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 * 
 * Copyright 2008-2009, Heaton Research Inc., and individual contributors.
 * See the copyright.txt in the distribution for a full listing of 
 * individual contributors.
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
 */

package org.encog.persist.persistors;

import java.util.Map;

import org.encog.neural.data.NeuralData;
import org.encog.neural.data.NeuralDataPair;
import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.data.basic.BasicNeuralData;
import org.encog.neural.data.basic.BasicNeuralDataPair;
import org.encog.neural.data.basic.BasicNeuralDataSet;
import org.encog.parse.tags.read.ReadXML;
import org.encog.parse.tags.write.WriteXML;
import org.encog.persist.EncogPersistedCollection;
import org.encog.persist.EncogPersistedObject;
import org.encog.persist.Persistor;
import org.encog.util.csv.CSVFormat;
import org.encog.util.csv.NumberList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Encog persistor used to persist the ActivationBiPolar class.
 * 
 * @author jheaton
 */
public class BasicNeuralDataSetPersistor implements Persistor {

	/**
	 * The item tag.
	 */
	public static final String TAG_ITEM = "Item";

	/**
	 * The input tag.
	 */
	public static final String TAG_INPUT = "Input";

	/**
	 * THe ideal tag.
	 */
	public static final String TAG_IDEAL = "Ideal";

	/**
	 * The current data set being loaded.
	 */
	private BasicNeuralDataSet currentDataSet;

	/**
	 * The logging object.
	 */
	@SuppressWarnings("unused")
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * Handle reading an item tag.
	 * 
	 * @param in
	 *            The XML reader.
	 */
	private void handleItem(final ReadXML in) {
		final Map<String, String> properties = in.readPropertyBlock();
		NeuralDataPair pair = null;
		final NeuralData input = new BasicNeuralData(NumberList.fromList(
				CSVFormat.EG_FORMAT, properties
						.get(BasicNeuralDataSetPersistor.TAG_INPUT)));

		if (properties.containsKey(BasicNeuralDataSetPersistor.TAG_IDEAL)) {
			// supervised
			final NeuralData ideal = new BasicNeuralData(NumberList.fromList(
					CSVFormat.EG_FORMAT, properties
							.get(BasicNeuralDataSetPersistor.TAG_IDEAL)));
			pair = new BasicNeuralDataPair(input, ideal);
		} else {
			// unsupervised
			pair = new BasicNeuralDataPair(input);
		}

		this.currentDataSet.add(pair);
	}

	/**
	 * Load the specified Encog object from an XML reader.
	 * 
	 * @param in
	 *            The XML reader to use.
	 * @return The loaded object.
	 */
	public EncogPersistedObject load(final ReadXML in) {

		final String name = in.getTag().getAttributes().get(
				EncogPersistedCollection.ATTRIBUTE_NAME);
		final String description = in.getTag().getAttributes().get(
				EncogPersistedCollection.ATTRIBUTE_DESCRIPTION);

		this.currentDataSet = new BasicNeuralDataSet();
		this.currentDataSet.setName(name);
		this.currentDataSet.setDescription(description);

		while (in.readToTag()) {
			if (in.is(BasicNeuralDataSetPersistor.TAG_ITEM, true)) {
				handleItem(in);
			} else if (in.is(EncogPersistedCollection.TYPE_TRAINING, false)) {
				break;
			}

		}

		return this.currentDataSet;
	}

	/**
	 * Save the specified Encog object to an XML writer.
	 * 
	 * @param obj
	 *            The object to save.
	 * @param out
	 *            The XML writer to save to.
	 */
	public void save(final EncogPersistedObject obj, final WriteXML out) {
		PersistorUtil.beginEncogObject(EncogPersistedCollection.TYPE_TRAINING,
				out, obj, true);
		final NeuralDataSet set = (NeuralDataSet) obj;
		final StringBuilder builder = new StringBuilder();

		for (final NeuralDataPair pair : set) {
			out.beginTag(BasicNeuralDataSetPersistor.TAG_ITEM);

			NumberList.toList(CSVFormat.EG_FORMAT, builder, pair.getInput()
					.getData());
			out.addProperty(BasicNeuralDataSetPersistor.TAG_INPUT, builder
					.toString());

			if (pair.getIdeal() != null) {
				NumberList.toList(CSVFormat.EG_FORMAT, builder, pair.getIdeal()
						.getData());
				out.addProperty(BasicNeuralDataSetPersistor.TAG_IDEAL, builder
						.toString());
			}
			out.endTag();
		}
		out.endTag();

	}

}
