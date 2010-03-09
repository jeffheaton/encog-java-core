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
package org.encog.solve.genetic.population;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.encog.solve.genetic.genome.Genome;
import org.encog.solve.genetic.innovation.InnovationList;
import org.encog.solve.genetic.species.Species;
import org.encog.util.identity.BasicGenerateID;
import org.encog.util.identity.GenerateID;

/**
 * Defines the basic functionality for a population of genomes.
 */
public class BasicPopulation implements Population {
	private final GenerateID geneIDGenerate = new BasicGenerateID();

	private final GenerateID genomeIDGenerate = new BasicGenerateID();
	/**
	 * The population.
	 */
	private final List<Genome> genomes = new ArrayList<Genome>();
	private final GenerateID innovationIDGenerate = new BasicGenerateID();
	private InnovationList innovations;

	private double oldAgePenalty = 0.3;
	private int oldAgeThreshold = 50;
	/**
	 * How many genomes should be created.
	 */
	private int populationSize;
	private final List<Species> species = new ArrayList<Species>();
	private final GenerateID speciesIDGenerate = new BasicGenerateID();
	private double survivalRate = 0.2;
	private int youngBonusAgeThreshhold = 10;

	private double youngFitnessBonus = 0.3;

	public BasicPopulation(final int populationSize) {
		this.populationSize = populationSize;
	}

	public void add(final Genome genome) {
		genomes.add(genome);

	}

	public void addAll(final List<? extends Genome> newPop) {
		genomes.addAll(newPop);
	}

	public long assignGeneID() {
		return geneIDGenerate.generate();
	}

	public long assignGenomeID() {
		return genomeIDGenerate.generate();
	}

	public long assignInnovationID() {
		return innovationIDGenerate.generate();
	}

	public long assignSpeciesID() {
		return speciesIDGenerate.generate();
	}

	public void clear() {
		genomes.clear();

	}

	public Genome get(final int i) {
		return genomes.get(i);
	}

	public Genome getBest() {
		if (genomes.size() == 0) {
			return null;
		} else {
			return genomes.get(0);
		}
	}

	public List<Genome> getGenomes() {
		return genomes;
	}

	public InnovationList getInnovations() {
		return innovations;
	}

	public double getOldAgePenalty() {
		return oldAgePenalty;
	}

	public int getOldAgeThreshold() {
		return oldAgeThreshold;
	}

	/**
	 * Get the population size.
	 * 
	 * @return The population size.
	 */
	public int getPopulationSize() {
		return populationSize;
	}

	public List<Species> getSpecies() {
		return species;
	}

	public double getSurvivalRate() {
		return survivalRate;
	}

	public int getYoungBonusAgeThreshhold() {
		return youngBonusAgeThreshhold;
	}

	public double getYoungFitnessBonus() {
		return youngFitnessBonus;
	}

	public void setInnovations(final InnovationList innovations) {
		this.innovations = innovations;
	}

	public void setOldAgePenalty(final double oldAgePenalty) {
		this.oldAgePenalty = oldAgePenalty;
	}

	public void setOldAgeThreshold(final int oldAgeThreshold) {
		this.oldAgeThreshold = oldAgeThreshold;
	}

	/**
	 * Set the population size.
	 * 
	 * @param populationSize
	 *            The population size.
	 */
	public void setPopulationSize(final int populationSize) {
		this.populationSize = populationSize;
	}

	public void setSurvivalRate(final double survivalRate) {
		this.survivalRate = survivalRate;
	}

	public void setYoungBonusAgeThreshhold(final int youngBonusAgeThreshhold) {
		this.youngBonusAgeThreshhold = youngBonusAgeThreshhold;
	}

	public void setYoungFitnessBonus(final double youngFitnessBonus) {
		this.youngFitnessBonus = youngFitnessBonus;
	}

	public int size() {
		return genomes.size();
	}

	/**
	 * Sort the population.
	 */
	public void sort() {
		Collections.sort(genomes);
	}

}
