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
package org.encog.neural.networks.training.cross;

import org.encog.neural.data.folded.FoldedDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.training.BasicTraining;

/**
 * Base class for cross training trainers. Must use a folded dataset.  
 */
public abstract class CrossTraining extends BasicTraining {

	/**
	 * The network to train.
	 */
	private final BasicNetwork network;
	
	/**
	 * The folded dataset.
	 */
	private final FoldedDataSet folded;	

	/**
	 * Construct a cross trainer.
	 * @param network The network.
	 * @param training The training data.
	 */
	public CrossTraining(final BasicNetwork network,
			final FoldedDataSet training) {
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
	 * @return The network.
	 */
	@Override
	public BasicNetwork getNetwork() {
		return this.network;
	}

}
