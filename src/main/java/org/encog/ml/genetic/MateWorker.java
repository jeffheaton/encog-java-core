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
package org.encog.ml.genetic;

import org.encog.ml.genetic.genome.Genome;
import org.encog.util.concurrency.EngineTask;

/**
 * This class is used in conjunction with a thread pool. This allows the genetic
 * algorithm to offload all of those calculations to a thread pool.
 */
public class MateWorker implements EngineTask {

	/**
	 * The first child.
	 */
	private final Genome child1;

	/**
	 * The second child.
	 */
	private final Genome child2;

	/**
	 * The father.
	 */
	private final Genome father;

	/**
	 * The mother.
	 */
	private final Genome mother;

	/**
	 * 
	 * @param theMother
	 *            The mother.
	 * @param theFather
	 *            The father.
	 * @param theChild1
	 *            The first child.
	 * @param theChild2
	 *            The second child.
	 */
	public MateWorker(final Genome theMother, final Genome theFather,
			final Genome theChild1, final Genome theChild2) {
		this.mother = theMother;
		this.father = theFather;
		this.child1 = theChild1;
		this.child2 = theChild2;
	}

	/**
	 * Mate the two chromosomes.
	 */
	public final void run() {
		mother.mate(father, child1, child2);
	}

}
