/*
 * Encog(tm) Core v2.4
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
package org.encog.solve.genetic.genome;

import java.util.List;

/**
 * A genome is the basic blueprint for creating an organism in Encog. A genome
 * is made up of one or more chromosomes, which are in turn made up of genes.
 * 
 */
public interface Genome extends Comparable<Genome> {

	int calculateGeneCount();

	void decode();

	void encode();

	public double getAdjustedScore();

	public double getAmountToSpawn();

	List<Chromosome> getChromosomes();

	long getGenomeID();

	Object getOrganism();

	double getScore();

	void mate(Genome father, Genome child1, Genome child2);

	public void setAdjustedScore(double adjustedScore);

	public void setAmountToSpawn(double amountToSpawn);

	void setGenomeID(long genomeID);

	void setScore(double score);
}
