/*
 * Encog(tm) Core v3.2 - Java Version
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
package org.encog.ml.ea.genome;

import java.io.Serializable;

import org.encog.ml.ea.population.Population;
import org.encog.ml.ea.train.EvolutionaryAlgorithm;

/**
 * A basic abstract genome. Provides base functionality.
 */
public abstract class BasicGenome implements Genome, Serializable {

	/**
	 * Serial id.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The adjusted score.
	 */
	private double adjustedScore = Double.NaN;

	/**
	 * The score of this genome.
	 */
	private double score = Double.NaN;

	/**
	 * The population this genome belongs to.
	 */
	private Population population;

	/**
	 * @return The adjusted score, which considers bonuses.
	 */
	@Override
	public double getAdjustedScore() {
		return this.adjustedScore;
	}

	/**
	 * @return the population
	 */
	@Override
	public Population getPopulation() {
		return this.population;
	}

	/**
	 * @return The score.
	 */
	@Override
	public double getScore() {
		return this.score;
	}

	/**
	 * Set the adjusted score.
	 * 
	 * @param theAdjustedScore
	 *            The score.
	 */
	@Override
	public void setAdjustedScore(final double theAdjustedScore) {
		this.adjustedScore = theAdjustedScore;
	}

	/**
	 * @param thePopulation
	 *            the population to set
	 */
	@Override
	public void setPopulation(final Population thePopulation) {
		this.population = thePopulation;
	}

	/**
	 * Set the score.
	 * 
	 * @param theScore
	 *            Set the score.
	 */
	@Override
	public void setScore(final double theScore) {
		this.score = theScore;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("[");
		builder.append(this.getClass().getSimpleName());
		builder.append(": score=");
		builder.append(getScore());
		return builder.toString();
	}

}
