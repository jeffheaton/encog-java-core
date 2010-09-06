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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.encog.mathutil.rbf.GaussianFunction;
import org.encog.mathutil.rbf.RadialBasisFunction;
import org.encog.neural.networks.layers.RadialBasisFunctionLayer;
import org.encog.parse.tags.read.ReadXML;
import org.encog.parse.tags.write.WriteXML;
import org.junit.Assert;
import org.junit.Test;

/**
 * Simple unit test for the radial basis function layer.
 * @author jheaton
 *
 */
public class TestRadialBasisFunctionLayerPersistor {

	/**
	 * Create a test RBF layer to save and load.
	 * @return The created test layer.
	 */
	private RadialBasisFunctionLayer createTestLayer() {
		final RadialBasisFunctionLayer layer = new RadialBasisFunctionLayer(1,2);
		layer.getRadius()[0] = 1;
		layer.getCenter()[0][0] = 2;
		layer.getCenter()[0][1] = 3;
		return layer;
	}

	/**
	 * 
	 * @param store A byte array that holds the stored object.
	 * @return The loaded object.
	 * @throws Exception If an exception happens, this is a unit test, so let
	 * the unit test runner report it.
	 */
	private RadialBasisFunctionLayer loadRadialFunctionLayer(final byte[] store)
			throws Exception {
		final ByteArrayInputStream input = new ByteArrayInputStream(store);
		final RadialBasisFunctionLayerPersistor p = 
			new RadialBasisFunctionLayerPersistor();
		final ReadXML readXML = new ReadXML(input);
		final RadialBasisFunctionLayer result = (RadialBasisFunctionLayer) p
				.load(readXML);
		input.close();
		return result;
	}

	/**
	 * Save a radial basis function layer to a byte array.
	 * @param layer The layer to be saved.
	 * @return The layer saved as a byte array.
	 * @throws Exception If an exception happens, this is a unit test, so let
	 * the unit test runner report it.
	 */
	private byte[] saveRadialFunctionLayer(final RadialBasisFunctionLayer layer)
			throws Exception {
		final ByteArrayOutputStream output = new ByteArrayOutputStream();
		final RadialBasisFunctionLayerPersistor p = 
			new RadialBasisFunctionLayerPersistor();
		final WriteXML xmlOutput = new WriteXML(output);
		xmlOutput.beginDocument();
		p.save(layer, xmlOutput);
		xmlOutput.endDocument();
		output.close();
		final byte[] array = output.toByteArray();

		return array;
	}

	/**
	 * Test saving and then loading a RBF layer.
	 * @throws Exception If an exception happens, this is a unit test, so let
	 * the unit test runner report it.
	 */
	@Test
	public void testPersistRBF() throws Exception {
		final RadialBasisFunctionLayer layer = createTestLayer();
		final byte[] store = saveRadialFunctionLayer(layer);
		final RadialBasisFunctionLayer layer2 = loadRadialFunctionLayer(store);
		Assert.assertEquals(layer.getNeuronCount(), layer2.getNeuronCount());

		Assert.assertEquals( 1.0, layer.getRadius()[0], 0.1);
		Assert.assertEquals( 2.0, layer.getCenter()[0][0], 0.1 );
		Assert.assertEquals( 3.0, layer.getCenter()[0][1], 0.1 );

		
	}

}
