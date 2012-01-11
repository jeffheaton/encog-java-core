/*
 * Encog(tm) Core v3.1 - Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 
 * Copyright 2008-2012 Heaton Research, Inc.
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
package org.encog.neural.art;

import org.encog.ml.BasicML;

/**
 * Adaptive Resonance Theory (ART) is a form of neural network developed 
 * by Stephen Grossberg and Gail Carpenter. There are several versions 
 * of the ART neural network, which are numbered ART-1, ART-2 and ART-3. 
 * The ART neural network is trained using either a supervised or 
 * unsupervised learning algorithm, depending on the version of ART being 
 * used. ART neural networks are used for pattern recognition and prediction.
 *
 * Plasticity is an important part for all Adaptive Resonance Theory (ART) 
 * neural networks. Unlike most neural networks, ART networks do not have 
 * a distinct training and usage stage. The ART network will learn as it is 
 * used. 
 */
public class ART extends BasicML {
	/**
	 * Serial id.
	 */
	private static final long serialVersionUID = 1L;
	
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
	 * Neural network property for no winner.
	 */
	public static final String PROPERTY_NO_WINNER = "noWinner";

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void updateProperties() {
		// unneeded
	}
	
}
