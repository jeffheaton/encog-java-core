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
package org.encog.engine.network.flat;

import org.encog.engine.EngineMachineLearning;
import org.encog.engine.validate.BasicMachineLearningValidate;

/**
 * Validate the network to be sure it can run on OpenCL.
 * 
 */
public class ValidateForOpenCL extends BasicMachineLearningValidate {

	/**
	 * Determine if the network is valid for OpenCL.
	 * 
	 * @param network
	 *            The network to check.
	 * @return The string indicating the error that prevents OpenCL from using
	 *         the network, or null if the network is fine for OpenCL.
	 */
	@Override
	public String isValid(final EngineMachineLearning network) {

		if (!(network instanceof FlatNetwork)) {
			return "Only flat networks are valid to be used for OpenCL";
		}

		final FlatNetwork flat = (FlatNetwork) network;

		for (final int activation : flat.getActivationType()) {
			if ((activation != ActivationFunctions.ACTIVATION_LINEAR)
					&& (activation != ActivationFunctions.ACTIVATION_SIGMOID)
					&& (activation != ActivationFunctions.ACTIVATION_TANH)) {
				return "Can't use OpenCL if activation function is not linear, sigmoid or tanh.";
			}
		}

		boolean hasContext = false;
		for (int i = 0; i < flat.getLayerCounts().length; i++) {
			if (flat.getContextTargetOffset()[i] != 0) {
				hasContext = true;
			}

			if (flat.getContextTargetSize()[i] != 0) {
				hasContext = true;
			}
		}

		if (hasContext) {
			return "Can't use OpenCL if context neurons are present.";
		}

		return null;
	}

}
