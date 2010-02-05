/*
 * Encog(tm) Artificial Intelligence Framework v2.3
 * Java Version
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

package org.encog.util.randomize;

import org.encog.matrix.Matrix;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.Layer;
import org.encog.neural.networks.synapse.Synapse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides basic functionality that most randomizers will need.
 * 
 * @author jheaton
 * 
 */
public abstract class BasicRandomizer implements Randomizer {

	/**
	 * The logging object.
	 */
	@SuppressWarnings("unused")
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * Randomize the synapses and thresholds in the basic network based on an
	 * array, modify the array. Previous values may be used, or they may be
	 * discarded, depending on the randomizer.
	 * 
	 * @param network
	 *            A network to randomize.
	 */
	public void randomize(final BasicNetwork network) {

		// randomize the weight matrix
		for (final Synapse synapse : network.getStructure().getSynapses()) {
			if (synapse.getMatrix() != null) {
				randomize(synapse.getMatrix());
			}
		}

		// randomize the thresholds
		for (final Layer layer : network.getStructure().getLayers()) {
			if (layer.hasThreshold()) {
				randomize(layer.getThreshold());
			}
		}
	}

	/**
	 * Randomize the array based on an array, modify the array. Previous values
	 * may be used, or they may be discarded, depending on the randomizer.
	 * 
	 * @param d
	 *            An array to randomize.
	 */
	public void randomize(final double[] d) {
		for (int i = 0; i < d.length; i++) {
			d[i] = randomize(d[i]);
		}

	}

	/**
	 * Randomize the array based on an array, modify the array. Previous values
	 * may be used, or they may be discarded, depending on the randomizer.
	 * 
	 * @param d
	 *            An array to randomize.
	 */
	public void randomize(final Double[] d) {
		for (int i = 0; i < d.length; i++) {
			d[i] = randomize(d[i]);
		}
	}

	/**
	 * Randomize the 2d array based on an array, modify the array. Previous
	 * values may be used, or they may be discarded, depending on the
	 * randomizer.
	 * 
	 * @param d
	 *            An array to randomize.
	 */
	public void randomize(final double[][] d) {
		for (int r = 0; r < d.length; r++) {
			for (int c = 0; c < d[0].length; c++) {
				d[r][c] = randomize(d[r][c]);
			}
		}

	}

	/**
	 * Randomize the 2d array based on an array, modify the array. Previous
	 * values may be used, or they may be discarded, depending on the
	 * randomizer.
	 * 
	 * @param d
	 *            An array to randomize.
	 */
	public void randomize(final Double[][] d) {
		for (int r = 0; r < d.length; r++) {
			for (int c = 0; c < d[0].length; c++) {
				d[r][c] = randomize(d[r][c]);
			}
		}

	}

	/**
	 * Randomize the matrix based on an array, modify the array. Previous values
	 * may be used, or they may be discarded, depending on the randomizer.
	 * 
	 * @param m
	 *            A matrix to randomize.
	 */
	public void randomize(final Matrix m) {
		final double[][] d = m.getData();
		for (int r = 0; r < m.getRows(); r++) {
			for (int c = 0; c < m.getCols(); c++) {
				d[r][c] = randomize(d[r][c]);
			}
		}
	}

}
