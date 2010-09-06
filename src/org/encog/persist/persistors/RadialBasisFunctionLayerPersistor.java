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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.encog.mathutil.rbf.GaussianFunction;
import org.encog.mathutil.rbf.RadialBasisFunction;
import org.encog.neural.networks.layers.RadialBasisFunctionLayer;
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
 * The Encog persistor used to persist the RadialBasisFunctionLayer class.
 * 
 * @author jheaton
 */
public class RadialBasisFunctionLayerPersistor implements Persistor {

	/**
	 * XML tag for the number of dimensions.
	 */
	public static final String PROPERTY_RADIAL_DIMENSIONS = "dimensions";

	/**
	 * XML tag for the radial functions collection.
	 */
	public static final String PROPERTY_RADIAL_RADIUS = "radius";

	/**
	 * The center of the RBF. XML property.
	 */
	public static final String PROPERTY_CENTER = "center";

	/**
	 * A set of centers
	 */
	public static final String PROPERTY_SET = "set";

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
		double[] radius = null;
		List<Object> centers = null;

		final String end = in.getTag().getName();

		while (in.readToTag()) {
			if (in.is(BasicLayerPersistor.PROPERTY_NEURONS, true)) {
				neuronCount = in.readIntToTag();
			} else if (in.is(BasicLayerPersistor.PROPERTY_X, true)) {
				x = in.readIntToTag();
			} else if (in.is(BasicLayerPersistor.PROPERTY_Y, true)) {
				y = in.readIntToTag();
			} else if (in.is(RadialBasisFunctionLayerPersistor.PROPERTY_RADIAL_DIMENSIONS,true)) {
				dimensions = in.readIntToTag();
			} else if (in.is(RadialBasisFunctionLayerPersistor.PROPERTY_RADIAL_RADIUS,true)) {
				String str = in.readTextToTag();
				radius = NumberList.fromList(CSVFormat.EG_FORMAT, str);
			} else if (in.is(RadialBasisFunctionLayerPersistor.PROPERTY_CENTER,true)) {
				centers = loadCenters(in);
			} else
			if (in.is(end, false)) {
				break;
			}
		}
		
		RadialBasisFunctionLayer layer = new RadialBasisFunctionLayer(neuronCount,dimensions);
		layer.setX(x);
		layer.setY(y);
		
		for(int i=0;i<radius.length;i++) {
			layer.getRadius()[i] = radius[i];
		}
		
		int xx = 0;
		for(Object obj: centers)
		{
			double[] d = (double[])obj;
			for(int yy = 0 ; yy<d.length; yy++)
			{
				layer.getCenter()[xx][yy] = d[yy];
			}
			xx++;
		}
	
		return layer;
	}

	/**
	 * Load a RBF function.
	 * 
	 * @param in
	 *            The XML reader.
	 * @return the RBF loaded.
	 */
	private List<Object> loadCenters(final ReadXML in) {
		List<Object> result = new ArrayList<Object>();
		final String end = in.getTag().getName();
		
		while (in.readToTag()) {
			if (in.is(RadialBasisFunctionLayerPersistor.PROPERTY_SET, true)) {
				String str = in.readTextToTag();
				double[] d = NumberList.fromList(CSVFormat.EG_FORMAT, str);
				result.add(d);
			} else if (in.is(end, false)) {
				break;
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
		out.addProperty(RadialBasisFunctionLayerPersistor.PROPERTY_RADIAL_DIMENSIONS, layer
				.getDimensions());
		out.addProperty(BasicLayerPersistor.PROPERTY_X, layer.getX());
		out.addProperty(BasicLayerPersistor.PROPERTY_Y, layer.getY());
		
		StringBuilder result = new StringBuilder();
		
		NumberList.toList(CSVFormat.EG_FORMAT, result, layer.getRadius());
		
		out.addProperty(RadialBasisFunctionLayerPersistor.PROPERTY_RADIAL_RADIUS, result.toString());
		
		out.beginTag(RadialBasisFunctionLayerPersistor.PROPERTY_CENTER);
		
		for(int x = 0; x<layer.getCenter().length; x++ )
		{
			result.setLength(0);
			NumberList.toList(CSVFormat.EG_FORMAT, result, layer.getCenter()[x]);
			out.addProperty(RadialBasisFunctionLayerPersistor.PROPERTY_SET, result.toString());
		}
		
		out.endTag();

		out.endTag();
	}

}
