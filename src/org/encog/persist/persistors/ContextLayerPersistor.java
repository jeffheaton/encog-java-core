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

import org.encog.engine.network.activation.ActivationFunction;
import org.encog.neural.networks.layers.ContextLayer;
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
 * The Encog persistor used to persist the ContextLayer class.
 * 
 * @author jheaton
 */
public class ContextLayerPersistor implements Persistor {

	public final String PROPERTY_CONTEXT = "context";

	/**
	 * The logging object.
	 */
	@SuppressWarnings("unused")
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * Load the specified Encog object from an XML reader.
	 * 
	 * @param in
	 *            The XML reader to use.
	 * @return The loaded object.
	 */
	public EncogPersistedObject load(final ReadXML in) {

		int neuronCount = 0;
		int x = 0;
		int y = 0;
		String threshold = null;
		String context = null;
		ActivationFunction activation = null;
		final String end = in.getTag().getName();
		double biasActivation = 1;

		while (in.readToTag()) {
			if (in.is(BasicLayerPersistor.TAG_ACTIVATION, true)) {
				in.readToTag();
				final String type = in.getTag().getName();
				activation = BasicLayerPersistor.loadActivation(type, in);
			} else if (in.is(BasicLayerPersistor.PROPERTY_NEURONS, true)) {
				neuronCount = in.readIntToTag();
			} else if (in.is(BasicLayerPersistor.PROPERTY_X, true)) {
				x = in.readIntToTag();
			} else if (in.is(BasicLayerPersistor.PROPERTY_Y, true)) {
				y = in.readIntToTag();
			} else if (in.is(BasicLayerPersistor.PROPERTY_THRESHOLD, true)) {
				threshold = in.readTextToTag();
			} else if (in.is(PROPERTY_CONTEXT, true)) {
				context = in.readTextToTag();
			} else if (in
					.is(BasicLayerPersistor.PROPERTY_BIAS_ACTIVATION, true)) {
				biasActivation = Double.parseDouble(in.readTextToTag());
			} else if (in.is(end, false)) {
				break;
			}
		}

		if (neuronCount > 0) {
			ContextLayer layer;

			layer = new ContextLayer(activation, neuronCount);

			if (context != null) {
				final double[] t = NumberList.fromList(CSVFormat.EG_FORMAT,
						context);

				for (int i = 0; i < t.length; i++) {
					layer.getContext().setData(i, t[i]);
				}
			}

			layer.setX(x);
			layer.setY(y);

			return layer;
		}
		return null;
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
		PersistorUtil.beginEncogObject(
				EncogPersistedCollection.TYPE_CONTEXT_LAYER, out, obj, false);
		final ContextLayer layer = (ContextLayer) obj;

		out.addProperty(BasicLayerPersistor.PROPERTY_NEURONS, layer
				.getNeuronCount());
		out.addProperty(BasicLayerPersistor.PROPERTY_X, layer.getX());
		out.addProperty(BasicLayerPersistor.PROPERTY_Y, layer.getY());

		if (layer.hasBias()) {
			final StringBuilder result = new StringBuilder();
			NumberList.toList(CSVFormat.EG_FORMAT, result, layer
					.getBiasWeights());
			out.addProperty(BasicLayerPersistor.PROPERTY_THRESHOLD, result
					.toString());
		}

		StringBuilder ctx = new StringBuilder();
		NumberList.toList(CSVFormat.EG_FORMAT, ctx, layer.getContext()
				.getData());
		out.addProperty(PROPERTY_CONTEXT, ctx.toString());

		out.addProperty(BasicLayerPersistor.PROPERTY_BIAS_ACTIVATION, layer
				.getBiasActivation());

		BasicLayerPersistor.saveActivationFunction(layer
				.getActivationFunction(), out);

		out.endTag();
	}

}
