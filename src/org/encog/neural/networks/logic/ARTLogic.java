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

package org.encog.neural.networks.logic;

import org.encog.neural.networks.BasicNetwork;

/**
 * Base class for all forms of the adaptive resonance theory (ART) classes.
 * 
 * @author jheaton
 * 
 */
public abstract class ARTLogic implements NeuralLogic {

	/**
	 * Neural network property, the A1 parameter.
	 */
	public static final String PROPERTY_A1 = "A1";

	/**
	 * Neural network property, the B1 parameter.
	 */
	public static final String PROPERTY_B1 = "B1";

	/**
	 * Neural network property, the C1 parameter.
	 */
	public static final String PROPERTY_C1 = "C1";

	/**
	 * Neural network property, the D1 parameter.
	 */
	public static final String PROPERTY_D1 = "D1";

	/**
	 * Neural network property, the L parameter.
	 */
	public static final String PROPERTY_L = "L";

	/**
	 * Neural network property, the vigilance parameter.
	 */
	public static final String PROPERTY_VIGILANCE = "VIGILANCE";

	/**
	 * The network.
	 */
	private BasicNetwork network;

	/**
	 * @return The network in use.
	 */
	public BasicNetwork getNetwork() {
		return this.network;
	}

	/**
	 * Setup the network logic, read parameters from the network.
	 * 
	 * @param network
	 *            The network that this logic class belongs to.
	 */
	public void init(final BasicNetwork network) {
		this.network = network;
	}

}
