/*
 * Encog(tm) Core v2.5 - Java Version
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

import org.encog.neural.networks.synapse.WeightedSynapse;
import org.encog.parse.tags.read.ReadXML;
import org.encog.parse.tags.write.WriteXML;
import org.encog.persist.EncogPersistedCollection;
import org.encog.persist.EncogPersistedObject;
import org.encog.persist.Persistor;

/**
 * The Encog persistor used to persist the WeightedSynapse class.
 * 
 * @author jheaton
 */
public class WeightedSynapsePersistor implements Persistor {

	/**
	 * The weights tag.
	 */
	public static final String TAG_WEIGHTS = "weights";

	/**
	 * Load the specified Encog object from an XML reader.
	 * 
	 * @param in
	 *            The XML reader to use.
	 * @return The loaded object.
	 */
	public EncogPersistedObject load(final ReadXML in) {
		final WeightedSynapse synapse = new WeightedSynapse();

		final String end = in.getTag().getName();

		while (in.readToTag()) {

			if (in.is(WeightedSynapsePersistor.TAG_WEIGHTS, true)) {
				in.readToTag();
				synapse.setMatrix(PersistorUtil.loadMatrix(in));
			}
			if (in.is(end, false)) {
				break;
			}
		}

		return synapse;
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
		PersistorUtil
				.beginEncogObject(
						EncogPersistedCollection.TYPE_WEIGHTED_SYNAPSE, out,
						obj, false);
		final WeightedSynapse synapse = (WeightedSynapse) obj;

		out.beginTag(WeightedSynapsePersistor.TAG_WEIGHTS);
		PersistorUtil.saveMatrix(synapse.getMatrix(), out);
		out.endTag();

		out.endTag();
	}

}
