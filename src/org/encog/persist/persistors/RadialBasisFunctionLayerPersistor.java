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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.encog.engine.network.activation.ActivationFunction;
import org.encog.engine.network.rbf.RadialBasisFunction;
import org.encog.mathutil.rbf.GaussianFunction;
import org.encog.neural.NeuralNetworkError;
import org.encog.neural.networks.layers.RadialBasisFunctionLayer;
import org.encog.parse.tags.read.ReadXML;
import org.encog.parse.tags.write.WriteXML;
import org.encog.persist.EncogPersistedCollection;
import org.encog.persist.EncogPersistedObject;
import org.encog.persist.PersistError;
import org.encog.persist.Persistor;
import org.encog.persist.persistors.generic.Object2XML;
import org.encog.util.csv.CSVFormat;
import org.encog.util.csv.NumberList;
import org.encog.util.obj.ReflectionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Encog persistor used to persist the RadialBasisFunctionLayer class.
 * 
 * @author jheaton
 */
public class RadialBasisFunctionLayerPersistor implements Persistor {

	/**
	 * XML tag for the radial functions collection.
	 */
	public static final String PROPERTY_RBF = "rbf";
	
	/**
	 * The centers.
	 */
	public static final String PROPERTY_CENTERS = "centers";

	
	/**
	 * The peak.
	 */
	public static final String PROPERTY_PEAK = "peak";

	
	/**
	 * The width.
	 */
	public static final String PROPERTY_WIDTH = "width";

	
	/**
	 * The logging object.
	 */
	@SuppressWarnings("unused")
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * Load a RBF layer.
	 * 
	 * @param in
	 *            The XML to read from.
	 * @return The object that was loaded.
	 */
	public EncogPersistedObject load(final ReadXML in) {
		int neuronCount = 0;
		int x = 0;
		int y = 0;
		int dimensions = 1;
		RadialBasisFunction[] rbfs = new RadialBasisFunction[0];

		final String end = in.getTag().getName();

		while (in.readToTag()) {
			if (in.is(BasicLayerPersistor.PROPERTY_NEURONS, true)) {
				neuronCount = in.readIntToTag();
			} else if (in.is(BasicLayerPersistor.PROPERTY_X, true)) {
				x = in.readIntToTag();
			} else if (in.is(BasicLayerPersistor.PROPERTY_Y, true)) {
				y = in.readIntToTag();
			} else if (in.is(RadialBasisFunctionLayerPersistor.PROPERTY_RBF,
					true)) {
				rbfs = loadAllRBF(in);
			} else if (in.is(end, false)) {
				break;
			}
		}

		RadialBasisFunctionLayer layer = new RadialBasisFunctionLayer(neuronCount);
		layer.setRadialBasisFunction(rbfs);
		layer.setX(x);
		layer.setY(y);

		return layer;
	}

	private RadialBasisFunction[] loadAllRBF(final ReadXML in) {

		List<RadialBasisFunction> rbfs = new ArrayList<RadialBasisFunction>();
		
		while (in.readToTag()) {
			if (in.is(PROPERTY_RBF, false)) {
				break;
			} else {
				String name = in.getTag().getName();
				RadialBasisFunction rbf = loadRBF(name,in);
				rbfs.add(rbf);
			}  
		}
		
		RadialBasisFunction[] result = new RadialBasisFunction[rbfs.size()];
		
		for(int i=0;i<rbfs.size();i++)
			result[i] = rbfs.get(i);
		
		return result;
	}
	
	private RadialBasisFunction loadRBF(final String name,final ReadXML in) {
		
		Class<?> clazz = ReflectionUtil.resolveEncogClass(name);
		
		if( clazz==null ) {
			throw new NeuralNetworkError("Unknown activation function type: " + name);
		}
		
		RadialBasisFunction result;
		try {
			result = (RadialBasisFunction) clazz
					.newInstance();
		} catch (InstantiationException e) {
			throw new PersistError(e);
		} catch (IllegalAccessException e) {
			throw new PersistError(e);
		}
		
		while (in.readToTag()) {
			if (in.is(name, false)) {
				break;
			} else if (in.is(PROPERTY_CENTERS, true)) {
				String str = in.readTextToTag();
				double[] centers = NumberList.fromList(CSVFormat.EG_FORMAT, str);
				result.setCenters(centers);
			} else if (in.is(PROPERTY_PEAK, true)) {
				String str = in.readTextToTag();
				double d = Double.parseDouble(str);
				result.setPeak(d);
			} else if (in.is(PROPERTY_WIDTH,
					true)) {
				String str = in.readTextToTag();
				double d = Double.parseDouble(str);
				result.setWidth(d);
			} 
		}
		
		return result;
	}

	/**
	 * Save a RBF layer.
	 * 
	 * @param obj
	 *            The object to save.
	 * @param out
	 *            XML stream to write to.
	 */
	public void save(final EncogPersistedObject obj, final WriteXML out) {
		PersistorUtil.beginEncogObject(
				EncogPersistedCollection.TYPE_RADIAL_BASIS_LAYER, out, obj,
				false);
		final RadialBasisFunctionLayer layer = (RadialBasisFunctionLayer) obj;

		out.addProperty(BasicLayerPersistor.PROPERTY_NEURONS, layer
				.getNeuronCount());
		out.addProperty(BasicLayerPersistor.PROPERTY_X, layer.getX());
		out.addProperty(BasicLayerPersistor.PROPERTY_Y, layer.getY());

		saveRBF(out, layer);

		out.endTag();
	}

	private void saveRBF(WriteXML out, RadialBasisFunctionLayer layer) {
		
		out.beginTag(RadialBasisFunctionLayerPersistor.PROPERTY_RBF);
		for (RadialBasisFunction rbf : layer.getRadialBasisFunction()) {
			out.beginTag(rbf.getClass().getSimpleName());
			out.addProperty(PROPERTY_CENTERS, rbf.getCenters(), rbf.getCenters().length);
			out.addProperty(PROPERTY_PEAK, rbf.getPeak());
			out.addProperty(PROPERTY_WIDTH, rbf.getWidth());
			out.endTag();
		}
		out.endTag();
	}

}
