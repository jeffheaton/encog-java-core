/*
 * Encog Artificial Intelligence Framework v1.x
 * Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 * 
 * Copyright 2008-2009, Heaton Research Inc., and individual contributors.
 * See the copyright.txt in the distribution for a full listing of 
 * individual contributors.
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
 */
package org.encog.solve.genetic;

import java.util.concurrent.Callable;

import org.encog.util.concurrency.EncogTask;

/**
 * MateWorker: This class is used in conjunction with a thread pool. This allows
 * the genetic algorithm to offload all of those calculations to a thread pool.
 */
public class MateWorker<GENE_TYPE> implements
		EncogTask {
	
	/**
	 * The mother.
	 */
	private final Chromosome<GENE_TYPE> mother;
	
	/**
	 * The father.
	 */
	private final Chromosome<GENE_TYPE> father;
	
	/**
	 * The first child.
	 */
	private final Chromosome<GENE_TYPE> child1;
	
	/**
	 * The second child.
	 */
	private final Chromosome<GENE_TYPE> child2;

	/**
	 * 
	 * @param mother The mother.
	 * @param father The father.
	 * @param child1 The first child.
	 * @param child2 The second child.
	 */
	public MateWorker(final Chromosome<GENE_TYPE> mother, 
			final Chromosome<GENE_TYPE> father,
			final Chromosome<GENE_TYPE> child1, 
			final Chromosome<GENE_TYPE> child2) {
		this.mother = mother;
		this.father = father;
		this.child1 = child1;
		this.child2 = child2;
	}


	@Override
	public void run() {
		this.mother.mate(this.father, this.child1, this.child2);
	}

}
