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
package org.encog.neural.networks.training.propagation.resilient;

/**
 * Constants used for Resilient Propagation (RPROP) training.
 */
public final class RPROPConst {
	
	/**
	 * Private constructor.
	 */
	private RPROPConst() {
		
	}
	
	/**
	 * The default zero tolerance.
	 */
	public static final double DEFAULT_ZERO_TOLERANCE = 0.00000000000000001;

	/**
	 * The POSITIVE ETA value. This is specified by the resilient propagation
	 * algorithm. This is the percentage by which the deltas are increased by if
	 * the partial derivative is greater than zero.
	 */
	public static final double POSITIVE_ETA = 1.2;

	/**
	 * The NEGATIVE ETA value. This is specified by the resilient propagation
	 * algorithm. This is the percentage by which the deltas are increased by if
	 * the partial derivative is less than zero.
	 */
	public static final double NEGATIVE_ETA = 0.5;

	/**
	 * The minimum delta value for a weight matrix value.
	 */
	public static final double DELTA_MIN = 1e-6;

	/**
	 * The starting update for a delta.
	 */
	public static final double DEFAULT_INITIAL_UPDATE = 0.1;

	/**
	 * The maximum amount a delta can reach.
	 */
	public static final double DEFAULT_MAX_STEP = 50;

}
