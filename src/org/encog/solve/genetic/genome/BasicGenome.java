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

import java.util.ArrayList;
import java.util.List;

import org.encog.solve.genetic.GeneticAlgorithm;
import org.encog.solve.genetic.GeneticError;

/**
 * A basic abstract genome.  Provides base functionality.
 */
public abstract class BasicGenome implements Genome {

	private double adjustedScore;
	private double amountToSpawn;
	private final List<Chromosome> chromosomes = new ArrayList<Chromosome>();
	private final GeneticAlgorithm geneticAlgorithm;
	private long genomeID;
	private Object organism;
	private double score;

	public BasicGenome(final GeneticAlgorithm geneticAlgorithm) {
		this.geneticAlgorithm = geneticAlgorithm;
	}

	public int calculateGeneCount() {
		double result = 0;

		for (final Chromosome chromosome : chromosomes) {
			result += chromosome.getGenes().size();
		}
		return (int) (result / getChromosomes().size());
	}

	/**
	 * Used to compare two chromosomes. Used to sort by score.
	 * 
	 * @param other
	 *            The other chromosome to compare.
	 * @return The value 0 if the argument is a chromosome that has an equal
	 *         score to this chromosome; a value less than 0 if the argument is
	 *         a chromosome with a score greater than this chromosome; and a
	 *         value greater than 0 if the argument is a chromosome what a score
	 *         less than this chromosome.
	 */
	public int compareTo(final Genome other) {

		if (geneticAlgorithm.getCalculateScore().shouldMinimize()) {
			if (getScore() > other.getScore()) {
				return 1;
			}
			return -1;
		} else {
			if (getScore() > other.getScore()) {
				return -1;
			}
			return 1;

		}
	}

	public double getAdjustedScore() {
		return adjustedScore;
	}

	public double getAmountToSpawn() {
		return amountToSpawn;
	}

	public List<Chromosome> getChromosomes() {
		return chromosomes;
	}

	public GeneticAlgorithm getGeneticAlgorithm() {
		return geneticAlgorithm;
	}

	public long getGenomeID() {
		return genomeID;
	}

	public Object getOrganism() {
		return organism;
	}

	public double getScore() {
		return score;
	}

	public void mate(final Genome father, final Genome child1,
			final Genome child2) {
		final int motherChromosomes = getChromosomes().size();
		final int fatherChromosomes = father.getChromosomes().size();

		if (motherChromosomes != fatherChromosomes) {
			throw new GeneticError(
					"Mother and father must have same chromosome count, Mother:"
							+ motherChromosomes + ",Father:"
							+ fatherChromosomes);
		}

		for (int i = 0; i < fatherChromosomes; i++) {
			final Chromosome motherChromosome = chromosomes.get(i);
			final Chromosome fatherChromosome = father.getChromosomes().get(i);
			final Chromosome offspring1Chromosome = child1.getChromosomes()
					.get(i);
			final Chromosome offspring2Chromosome = child2.getChromosomes()
					.get(i);

			geneticAlgorithm.getCrossover().mate(motherChromosome,
					fatherChromosome, offspring1Chromosome,
					offspring2Chromosome);

			if (Math.random() < geneticAlgorithm.getMutationPercent()) {
				geneticAlgorithm.getMutate().performMutation(
						offspring1Chromosome);
			}

			if (Math.random() < geneticAlgorithm.getMutationPercent()) {
				geneticAlgorithm.getMutate().performMutation(
						offspring2Chromosome);
			}
		}

		child1.decode();
		child2.decode();
		geneticAlgorithm.calculateScore(child1);
		geneticAlgorithm.calculateScore(child2);
	}

	public void setAdjustedScore(final double adjustedScore) {
		this.adjustedScore = adjustedScore;
	}

	public void setAmountToSpawn(final double amountToSpawn) {
		this.amountToSpawn = amountToSpawn;
	}

	public void setGenomeID(final long genomeID) {
		this.genomeID = genomeID;
	}

	public void setOrganism(final Object organism) {
		this.organism = organism;
	}

	public void setScore(final double score) {
		this.score = score;
	}

	/**
	 * Convert the chromosome to a string.
	 * 
	 * @return The chromosome as a string.
	 */
	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("[BasicGenome: score=");
		builder.append(getScore());
		return builder.toString();
	}

}
