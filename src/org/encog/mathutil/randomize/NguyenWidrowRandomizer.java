/*
 * Encog(tm) Core v2.6 - Java Version
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
package org.encog.mathutil.randomize;

import org.encog.EncogError;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.Layer;

/**
 * Implementation of <i>Nguyen-Widrow</i> weight initialization. This is the
 * default weight initialization used by Encog, as it generally provides the
 * most trainable neural network.
 * 
 * 
 * @author St?phan Corriveau
 * 
 */
public class NguyenWidrowRandomizer extends RangeRandomizer implements
		Randomizer {

	/**
	 * Construct a Nguyen-Widrow randomizer.
	 * 
	 * @param min
	 *            The min of the range.
	 * @param max
	 *            The max of the range.
	 */
	public NguyenWidrowRandomizer(final double min, final double max) {
		super(min, max);
	}


}
