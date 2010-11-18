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
package org.encog.solve.genetic;

import org.encog.engine.concurrency.EngineTask;
import org.encog.solve.genetic.genome.Genome;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	 * The logging object.
	 */
	@SuppressWarnings("unused")
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * The mother.
	 */
	private final Genome mother;

	/**
	 * 
	 * @param mother
	 *            The mother.
	 * @param father
	 *            The father.
	 * @param child1
	 *            The first child.
	 * @param child2
	 *            The second child.
	 */
	public MateWorker(final Genome mother, final Genome father,
			final Genome child1, final Genome child2) {
		this.mother = mother;
		this.father = father;
		this.child1 = child1;
		this.child2 = child2;
	}

	/**
	 * Mate the two chromosomes.
	 */
	public void run() {
		mother.mate(father, child1, child2);
	}

}
