/*
 * Encog Artificial Intelligence Framework v2.x
 * Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 * 
 * Copyright 2008-2010, Heaton Research Inc., and individual contributors.
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

import org.encog.neural.networks.training.propagation.TrainingContinuation;
import org.encog.parse.tags.read.ReadXML;
import org.encog.parse.tags.write.WriteXML;
import org.encog.persist.EncogPersistedCollection;
import org.encog.persist.EncogPersistedObject;
import org.encog.persist.Persistor;
import org.encog.util.csv.CSVFormat;
import org.encog.util.csv.NumberList;

/**
 * Persist a training continuation persistor.
 */
public class TrainingContinuationPersistor implements Persistor {

	/**
	 * The items tag.
	 */
	public static final String TAG_ITEMS = "items";
	
	/**
	 * An item tag.
	 */
	public static final String TAG_ITEM = "Item";
	
	/**
	 * The name attribute.
	 */
	public static final String ATTRIBUTE_NAME = "name";

	/**
	 * The current training continuation object.
	 */
	private TrainingContinuation current;

	/**
	 * Handle an item.
	 * @param in The XML input object.
	 */
	public void handleItem(final ReadXML in) {
		final String name = in.getTag().getAttributeValue(
				TrainingContinuationPersistor.ATTRIBUTE_NAME);
		final String str = in.readTextToTag();
		final double[] list = NumberList.fromList(CSVFormat.EG_FORMAT, str);
		this.current.put(name, list);

	}

	/**
	 * Handle loading the items.
	 * @param in The XML input object.
	 */
	public void handleItems(final ReadXML in) {
		while (in.readToTag()) {
			if (in.is(TrainingContinuationPersistor.TAG_ITEM, true)) {
				handleItem(in);
			} else if (in.is(TrainingContinuationPersistor.TAG_ITEM, false)) {
				break;
			}
		}
	}

	/**
	 * Load the object.
	 * @param in The XML object to load from.
	 * @return The loaded object.
	 */
	public EncogPersistedObject load(final ReadXML in) {
		this.current = new TrainingContinuation();

		final String name = in.getTag().getAttributes().get(
				EncogPersistedCollection.ATTRIBUTE_NAME);
		final String description = in.getTag().getAttributes().get(
				EncogPersistedCollection.ATTRIBUTE_DESCRIPTION);

		this.current.setName(name);
		this.current.setDescription(description);

		while (in.readToTag()) {
			if (in.is(TrainingContinuationPersistor.TAG_ITEMS, true)) {
				handleItems(in);
			} else if (in.is(
				EncogPersistedCollection.TYPE_TRAINING_CONTINUATION, false)) {
				break;
			}
		}

		return this.current;
	}

	/**
	 * Save the object.
	 * @param obj The object to save.
	 * @param out The XML output object.
	 */
	public void save(final EncogPersistedObject obj, final WriteXML out) {

		PersistorUtil.beginEncogObject(
				EncogPersistedCollection.TYPE_TRAINING_CONTINUATION, out, obj,
				true);
		this.current = (TrainingContinuation) obj;

		// save the layers
		out.beginTag(BasicNetworkPersistor.TAG_LAYERS);
		saveItems(out);
		out.endTag();

		out.endTag();
	}

	/**
	 * Save items.
	 * @param out The XML output object.
	 */
	public void saveItems(final WriteXML out) {
		for (final String key : this.current.getContents().keySet()) {
			out.addAttribute(TrainingContinuationPersistor.ATTRIBUTE_NAME, key);
			out.beginTag(TrainingContinuationPersistor.TAG_ITEM);
			final double[] value = (double[]) this.current.get(key);
			final StringBuilder result = new StringBuilder();
			NumberList.toList(CSVFormat.EG_FORMAT, result, value);
			out.addText(result.toString());
			out.endTag();
		}
	}

}
