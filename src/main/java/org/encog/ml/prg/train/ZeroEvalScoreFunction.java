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
package org.encog.ml.prg.train;

import java.io.Serializable;

import org.encog.ml.CalculateScore;
import org.encog.ml.MLMethod;
import org.encog.ml.data.MLData;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.ml.prg.EncogProgram;

/**
 * This is a very simple evaluation function that simply passes in all zeros for
 * the input arguments. Make sure that the genome you are testing supports
 * floating point numbers for inputs.
 * 
 * This evaluation function is useful to test to very quickly verify that a
 * genome is valid and does not generate any obvious division by zero issues.
 * This allows a population generator to quickly eliminate some invalid genomes.
 */
public class ZeroEvalScoreFunction implements CalculateScore, Serializable {

	/**
	 * The serial id.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public double calculateScore(final MLMethod genome) {
		final EncogProgram prg = (EncogProgram) genome;
		final PrgPopulation pop = (PrgPopulation) prg.getPopulation();
		final MLData inputData = new BasicMLData(pop.getContext()
				.getDefinedVariables().size());
		prg.compute(inputData);
		return 0;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean requireSingleThreaded() {
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean shouldMinimize() {
		return true;
	}

}
