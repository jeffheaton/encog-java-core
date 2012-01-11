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
package org.encog.neural.networks.training.cross;

import org.encog.ml.MLMethod;
import org.encog.ml.TrainingImplementationType;
import org.encog.ml.data.folded.FoldedDataSet;
import org.encog.ml.train.BasicTraining;

/**
 * Base class for cross training trainers. Must use a folded dataset.  
 */
public abstract class CrossTraining extends BasicTraining {

	/**
	 * The network to train.
	 */
	private final MLMethod network;
	
	/**
	 * The folded dataset.
	 */
	private final FoldedDataSet folded;	

	/**
	 * Construct a cross trainer.
	 * @param network The network.
	 * @param training The training data.
	 */
	public CrossTraining(final MLMethod network,
			final FoldedDataSet training) {
		super(TrainingImplementationType.Iterative);
		this.network = network;
		setTraining(training);
		this.folded = training;
	}

	/**
	 * @return The folded training data.
	 */
	public FoldedDataSet getFolded() {
		return this.folded;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public MLMethod getMethod() {
		return this.network;
	}

}
