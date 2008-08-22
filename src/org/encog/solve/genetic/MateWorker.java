/*
  * Encog Neural Network and Bot Library for Java v1.x
  * http://www.heatonresearch.com/encog/
  * http://code.google.com/p/encog-java/
  * 
  * Copyright 2008, Heaton Research Inc., and individual contributors.
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

/**
 * MateWorker: This class is used in conjunction with a thread pool.
 * This allows the genetic algorithm to offload all of those calculations
 * to a thread pool.  
 */
public class MateWorker<CHROMOSME_TYPE extends Chromosome<?, ?>> implements
		Callable<Integer> {
	private final CHROMOSME_TYPE mother;
	private final CHROMOSME_TYPE father;
	private final CHROMOSME_TYPE child1;
	private final CHROMOSME_TYPE child2;

	public MateWorker(final CHROMOSME_TYPE mother, final CHROMOSME_TYPE father,
			final CHROMOSME_TYPE child1, final CHROMOSME_TYPE child2) {
		this.mother = mother;
		this.father = father;
		this.child1 = child1;
		this.child2 = child2;
	}

	@SuppressWarnings("unchecked")
	public Integer call() throws Exception {
		this.mother.mate(this.father, 
				this.child1, 
				this.child2);
		return null;
	}

}
