/*
 * Encog(tm) Core v3.3 - Java Version
 * http://www.heatonresearch.com/encog/
 * https://github.com/encog/encog-java-core
 
 * Copyright 2008-2014 Heaton Research, Inc.
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
package org.encog.ml.fitness;

import java.io.Serializable;

import org.encog.ml.CalculateScore;

/**
 * A fitness objective.
 */
public class FitnessObjective implements Serializable {
	/**
	 * The serial id.
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * The weight.
	 */
	private final double weight;
	
	/**
	 * The score function.
	 */
	private final CalculateScore score;
	
	/**
	 * Construct the fitness objective.
	 * @param weight The weight.
	 * @param score The score.
	 */
	public FitnessObjective(double weight, CalculateScore score) {
		super();
		this.weight = weight;
		this.score = score;
	}
	/**
	 * @return the weight
	 */
	public double getWeight() {
		return weight;
	}
	/**
	 * @return the score
	 */
	public CalculateScore getScore() {
		return score;
	}
}
