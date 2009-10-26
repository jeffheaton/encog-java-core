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

package org.encog.neural.networks.training.propagation;

import org.encog.neural.networks.NeuralOutputHolder;

/**
 * Defines the specifics to one of the propagation methods. The individual ways
 * that each of the propagation methods uses to modify the weight and] threshold
 * matrix are defined here.
 * 
 * @author jheaton
 * 
 */
public interface PropagationMethod {

	/**
	 * Calculate the error between these two levels.
	 * @param output The output to the "to level".
	 * @param fromLevel The from level.
	 * @param toLevel The target level.
	 */
	void calculateError(final NeuralOutputHolder output,
			final PropagationLevel fromLevel, 
			final PropagationLevel toLevel);

	/**
	 * Init with the specified propagation object.
	 * @param propagationUtil The propagation object that this method will
	 * be used with.
	 */
	void init(PropagationUtil propagationUtil);

	/**
	 * Apply the accumulated deltas and learn.
	 */
	void learn();

}
