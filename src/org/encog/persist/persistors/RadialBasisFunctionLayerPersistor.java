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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.encog.neural.networks.layers.RadialBasisFunctionLayer;
import org.encog.parse.tags.read.ReadXML;
import org.encog.parse.tags.write.WriteXML;
import org.encog.persist.EncogPersistedCollection;
import org.encog.persist.EncogPersistedObject;
import org.encog.persist.Persistor;
import org.encog.util.math.rbf.GaussianFunction;
import org.encog.util.math.rbf.RadialBasisFunction;
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
	public static final String PROPERTY_RADIAL_FUNCTIONS = "radialFunctions";

	/**
	 * XML tag for the radial functions collection.
	 */
	public static final String PROPERTY_RADIAL_FUNCTION = "RadialFunction";

	/**
	 * The center of the RBF. XML property.
	 */
	public static final String PROPERTY_CENTER = "center";

	/**
	 * The peak of the RBF. XML property.
	 */
	public static final String PROPERTY_PEAK = "peak";

	/**
	 * The width of the RBF. XML property.
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

		final List<RadialBasisFunction> radialFunctions = new ArrayList<RadialBasisFunction>();
		int neuronCount = 0;
		int x = 0;
		int y = 0;

		final String end = in.getTag().getName();

		while (in.readToTag()) {
			if (in.is(BasicLayerPersistor.PROPERTY_NEURONS, true)) {
				neuronCount = in.readIntToTag();
			} else if (in.is(BasicLayerPersistor.PROPERTY_X, true)) {
				x = in.readIntToTag();
			} else if (in.is(BasicLayerPersistor.PROPERTY_Y, true)) {
				y = in.readIntToTag();
			} else if (in
					.is(
							RadialBasisFunctionLayerPersistor.PROPERTY_RADIAL_FUNCTIONS,
							true)) {
				loadRadialFunctions(in, radialFunctions);
			}
			if (in.is(end, false)) {
				break;
			}
		}

		if (neuronCount > 0) {
			RadialBasisFunctionLayer layer;

			layer = new RadialBasisFunctionLayer(neuronCount);

			layer.setX(x);
			layer.setY(y);

			int i = 0;
			for (final RadialBasisFunction rbf : radialFunctions) {
				layer.getRadialBasisFunction()[i++] = rbf;
			}

			return layer;
		}
		return null;
	}

	/**
	 * Load a RBF function.
	 * 
	 * @param in
	 *            The XML reader.
	 * @return the RBF loaded.
	 */
	private RadialBasisFunction loadRadialFunction(final ReadXML in) {
		final Map<String, String> properties = in.readPropertyBlock();
		final double center = Double.parseDouble(properties
				.get(RadialBasisFunctionLayerPersistor.PROPERTY_CENTER));
		final double width = Double.parseDouble(properties
				.get(RadialBasisFunctionLayerPersistor.PROPERTY_WIDTH));
		final double peak = Double.parseDouble(properties
				.get(RadialBasisFunctionLayerPersistor.PROPERTY_PEAK));
		return new GaussianFunction(center, peak, width);
	}

	/**
	 * Load a list of radial functions.
	 * 
	 * @param in
	 *            THe XML reader.
	 * @param radialFunctions
	 *            The radial functions.
	 */
	private void loadRadialFunctions(final ReadXML in,
			final List<RadialBasisFunction> radialFunctions) {

		final String end = in.getTag().getName();

		while (in.readToTag()) {
			if (in.is(
					RadialBasisFunctionLayerPersistor.PROPERTY_RADIAL_FUNCTION,
					true)) {
				final RadialBasisFunction rbf = loadRadialFunction(in);
				radialFunctions.add(rbf);
			}
			if (in.is(end, false)) {
				break;
			}
		}

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

		out
				.beginTag(RadialBasisFunctionLayerPersistor.PROPERTY_RADIAL_FUNCTIONS);
		for (int i = 0; i < layer.getNeuronCount(); i++) {
			final RadialBasisFunction rbf = layer.getRadialBasisFunction()[i];
			out
					.beginTag(RadialBasisFunctionLayerPersistor.PROPERTY_RADIAL_FUNCTION);
			out.addProperty(RadialBasisFunctionLayerPersistor.PROPERTY_CENTER,
					rbf.getCenter());
			out.addProperty(RadialBasisFunctionLayerPersistor.PROPERTY_PEAK,
					rbf.getPeak());
			out.addProperty(RadialBasisFunctionLayerPersistor.PROPERTY_WIDTH,
					rbf.getWidth());
			out.endTag();
		}
		out.endTag();

		out.endTag();
	}

}
