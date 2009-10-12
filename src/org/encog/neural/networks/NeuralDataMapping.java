/*
 * Encog Artificial Intelligence Framework v2.x
 * Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 * 
 * Copyright 2008-2009, Heaton Research Inc., and individual contributors.
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
package org.encog.neural.networks;

import org.encog.neural.data.NeuralData;

/**
 * Used to map one neural data object to another. Useful for a BAM network.
 * 
 */
public class NeuralDataMapping {

	/**
	 * Copy from one object to the other.
	 * 
	 * @param source
	 *            The source object.
	 * @param target
	 *            The target object.
	 */
	public static void copy(final NeuralDataMapping source,
			final NeuralDataMapping target) {
		for (int i = 0; i < source.getFrom().size(); i++) {
			target.getFrom().setData(i, source.getFrom().getData(i));
		}

		for (int i = 0; i < source.getTo().size(); i++) {
			target.getTo().setData(i, source.getTo().getData(i));
		}
	}

	/**
	 * The source data.
	 */
	private NeuralData from;

	/**
	 * The target data.
	 */
	private NeuralData to;

	/**
	 * Construct the neural data mapping class, with null values.
	 */
	public NeuralDataMapping() {
		this.from = null;
		this.to = null;
	}

	/**
	 * Construct the neural data mapping class with the specified values.
	 * 
	 * @param from
	 *            The source data.
	 * @param to
	 *            The target data.
	 */
	public NeuralDataMapping(final NeuralData from, final NeuralData to) {
		this.from = from;
		this.to = to;
	}

	/**
	 * @return The "from" data.
	 */
	public NeuralData getFrom() {
		return this.from;
	}

	/**
	 * @return The "to" data.
	 */
	public NeuralData getTo() {
		return this.to;
	}

	/**
	 * Set the from data.
	 * 
	 * @param from
	 *            The from data.
	 */
	public void setFrom(final NeuralData from) {
		this.from = from;
	}

	/**
	 * Set the target data.
	 * 
	 * @param to
	 *            The target data.
	 */
	public void setTo(final NeuralData to) {
		this.to = to;
	}
}
