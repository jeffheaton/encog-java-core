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
package org.encog.neural.networks.training;

import org.encog.ml.MLRegression;

/**
 * Used by simulated annealing and genetic algorithms to calculate the score
 * for a neural network.  This allows networks to be ranked.  We may be seeking
 * a high or a low score, depending on the value the shouldMinimize
 * method returns.
 */
public interface CalculateScore {
	
	/**
	 * Calculate this network's score.
	 * @param network The network.
	 * @return The score.
	 */
	double calculateScore(MLRegression network);
	
	/**
	 * @return True if the goal is to minimize the score.
	 */
	boolean shouldMinimize();
}
