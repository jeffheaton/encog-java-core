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

import org.encog.EncogError;
import org.encog.neural.activation.ActivationFunction;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.parse.tags.read.ReadXML;
import org.encog.parse.tags.write.WriteXML;
import org.encog.persist.EncogPersistedCollection;
import org.encog.persist.EncogPersistedObject;
import org.encog.persist.Persistor;
import org.encog.util.ReflectionUtil;
import org.encog.util.csv.CSVFormat;
import org.encog.util.csv.NumberList;
import org.encog.util.csv.ReadCSV;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides basic functions that many of the persistors will need.
 *
 *
 * @author jheaton
 */
public class BasicLayerPersistor implements Persistor {

	/**
	 * The activation function tag.
	 */
	public static final String TAG_ACTIVATION = "activation";

	/**
	 * The neurons property.
	 */
	public static final String PROPERTY_NEURONS = "neurons";

	/**
	 * The bias property, stores the bias weights.
	 */
	public static final String PROPERTY_THRESHOLD = "threshold";

	/**
	 * The bias activation.
	 */
	public static final String PROPERTY_BIAS_ACTIVATION = "biasActivation";

	/**
	 * The x-coordinate to place this object at.
	 */
	public static final String PROPERTY_X = "x";

	/**
	 * The y-coordinate to place this object at.
	 */
	public static final String PROPERTY_Y = "y";

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
		double biasActivation = 1;
		int neuronCount = 0;
		int x = 0;
		int y = 0;
		String threshold = null;
		ActivationFunction activation = null;
		final String end = in.getTag().getName();

		while (in.readToTag()) {
			if (in.is(BasicLayerPersistor.TAG_ACTIVATION, true)) {
				in.readToTag();
				final String type = in.getTag().getName();
				activation = loadActivation(type,in);
			} else if (in.is(BasicLayerPersistor.PROPERTY_NEURONS, true)) {
				neuronCount = in.readIntToTag();
			} else if (in.is(BasicLayerPersistor.PROPERTY_THRESHOLD, true)) {
				threshold = in.readTextToTag();
			} else if (in.is(BasicLayerPersistor.PROPERTY_X, true)) {
				x = in.readIntToTag();
			} else if (in.is(BasicLayerPersistor.PROPERTY_Y, true)) {
				y = in.readIntToTag();
			} else if (in.is(BasicLayerPersistor.PROPERTY_BIAS_ACTIVATION, true)) {
				biasActivation = Double.parseDouble(in.readTextToTag());
			} else if (in.is(end, false)) {
				break;
			}
		}

		if (neuronCount > 0) {
			BasicLayer layer;

			if (threshold == null) {
				layer = new BasicLayer(activation, false, neuronCount);
			} else {
				final double[] t = NumberList.fromList(CSVFormat.EG_FORMAT,
						threshold);
				layer = new BasicLayer(activation, true, neuronCount);
				for (int i = 0; i < t.length; i++) {
					layer.setBiasWeight(i, t[i]);
				}
				layer.setBiasActivation(biasActivation);
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
				EncogPersistedCollection.TYPE_BASIC_LAYER, out, obj, false);
		final BasicLayer layer = (BasicLayer) obj;

		out.addProperty(BasicLayerPersistor.PROPERTY_NEURONS, layer
				.getNeuronCount());
		out.addProperty(BasicLayerPersistor.PROPERTY_X, layer.getX());
		out.addProperty(BasicLayerPersistor.PROPERTY_Y, layer.getY());

		if (layer.hasBias()) {
			final StringBuilder result = new StringBuilder();
			NumberList
					.toList(CSVFormat.EG_FORMAT, result, layer.getBiasWeights());
			out.addProperty(BasicLayerPersistor.PROPERTY_THRESHOLD, result
					.toString());
		}

		out.addProperty(BasicLayerPersistor.PROPERTY_BIAS_ACTIVATION, layer.getBiasActivation());
		
		
		saveActivationFunction(layer.getActivationFunction(),out);
		

		out.endTag();
	}

	public static void saveActivationFunction(ActivationFunction activationFunction,
			WriteXML out) {
		if( activationFunction!=null ) {
			out.beginTag(BasicLayerPersistor.TAG_ACTIVATION);
			out.beginTag(activationFunction.getClass().getSimpleName());
			String[] names = activationFunction.getParamNames();
			for(int i=0;i<names.length;i++)
			{
				String str = names[i];
				double d = activationFunction.getParams()[i];
				out.addAttribute(str, ""+CSVFormat.EG_FORMAT.format(d, 10) );
			}
			out.endTag();
			out.endTag();
		}
	}

	public static ActivationFunction loadActivation(String type, ReadXML in) {
		
		try {
			Class<?> clazz = ReflectionUtil.resolveEncogClass(type);
			ActivationFunction result = (ActivationFunction)clazz.newInstance();
			
			for( String key : in.getTag().getAttributes().keySet() )
			{
				int index = -1;
				
				for(int i=0;i<result.getParamNames().length;i++)
				{
					if( key.equalsIgnoreCase(result.getParamNames()[i]))
					{
						index = i;
						break;
					}
					
					if( index!=-1 )
					{
						String str = in.getTag().getAttributeValue(key);
						double d = CSVFormat.EG_FORMAT.parse(str);
						result.setParam(index, d);
					}
				}
			}
			
			return result;
		} catch (InstantiationException e) {
			throw new EncogError(e);
		} catch (IllegalAccessException e) {
			throw new EncogError(e);
		}
	}

}
