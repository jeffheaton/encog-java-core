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
package org.encog.ml.ea.score;

import java.io.Serializable;

import org.encog.ml.CalculateScore;
import org.encog.ml.MLMethod;

/**
 * An empty score function.  Simply returns zero as the score, always.
 */
public class EmptyScoreFunction implements CalculateScore, Serializable {

	/**
	 * Serial ID.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public double calculateScore(final MLMethod phenotype) {
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
