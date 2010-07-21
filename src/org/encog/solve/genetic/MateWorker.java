/*
 * Encog(tm) Core v2.5 
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 * 
 * Copyright 2008-2010 by Heaton Research Inc.
 * 
 * Released under the LGPL.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 * 
 * Encog and Heaton Research are Trademarks of Heaton Research, Inc.
 * For information on Heaton Research trademarks, visit:
 * 
 * http://www.heatonresearch.com/copyright.html
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
