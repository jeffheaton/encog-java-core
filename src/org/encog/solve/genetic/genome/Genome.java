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

package org.encog.solve.genetic.genome;

import java.util.List;

/**
 * A genome is the basic blueprint for creating an organism in Encog. A genome
 * is made up of one or more chromosomes, which are in turn made up of genes.
 * 
 */
public interface Genome extends Comparable<Genome> {

	/**
	 * @return The number of genes in this genome.
	 */
	int calculateGeneCount();

	/**
	 * Use the genes to update the organism.
	 */
	void decode();

	/**
	 * Use the organism to update the genes.
	 */
	void encode();

	/**
	 * Get the adjusted score, this considers old-age penalties and youth
	 * bonuses. If there are no such bonuses or penalties, this is the same as
	 * the score.
	 * @return The adjusted score.
	 */
	double getAdjustedScore();

	/**
	 * @return The amount of offspring this genome will have.
	 */
	double getAmountToSpawn();

	/**
	 * @return The chromosomes that make up this genome.
	 */
	List<Chromosome> getChromosomes();

	/**
	 * @return The genome ID.
	 */
	long getGenomeID();

	/**
	 * @return The organism produced by this genome.
	 */
	Object getOrganism();

	/**
	 * @return The score for this genome.
	 */
	double getScore();	

	/**
	 * Mate with another genome and produce two children.
	 * @param father The father genome.
	 * @param child1 The first child.
	 * @param child2 The second child.
	 */
	void mate(Genome father, Genome child1, Genome child2);

	/**
	 * Set the adjusted score.
	 * @param adjustedScore The adjusted score.
	 */
	public void setAdjustedScore(double adjustedScore);

	/**
	 * Set the amount to spawn.
	 * @param amountToSpawn The amount to spawn.
	 */
	public void setAmountToSpawn(double amountToSpawn);

	/**
	 * Set the genome ID.
	 * @param genomeID The genome id.
	 */
	void setGenomeID(long genomeID);

	/** 
	 * Set the score.
	 * @param score The new score.
	 */
	void setScore(double score);
}
