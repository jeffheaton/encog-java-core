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
			} else if (in.is(TrainingContinuationPersistor.TAG_ITEMS, false)) {
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
		out.beginTag(TrainingContinuationPersistor.TAG_ITEMS);
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
