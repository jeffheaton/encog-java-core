/*
 * Encog(tm) Core v3.2 - Java Version
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
package org.encog.ml.genetic.population;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.encog.ml.genetic.GeneticAlgorithm;
import org.encog.ml.genetic.genome.Genome;

/**
 * Defines the basic functionality for a population of genomes.
 */
public class BasicPopulation implements Population {
	
	/**
	 * The serial id.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The population.
	 */
	private final List<Genome> genomes = new ArrayList<Genome>();

	/**
	 * The object name.
	 */
	private String name;

	/**
	 * How many genomes should be created.
	 */
	private int populationSize;

	/**
	 * Construct an empty population.
	 */
	public BasicPopulation() {
		this.populationSize = 0;
	}

	/**
	 * Construct a population.
	 * 
	 * @param thePopulationSize
	 *            The population size.
	 */
	public BasicPopulation(final int thePopulationSize) {
		this.populationSize = thePopulationSize;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void add(final Genome genome) {
		this.genomes.add(genome);
		genome.setPopulation(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addAll(final List<? extends Genome> newPop) {
		this.genomes.addAll(newPop);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void claim(final GeneticAlgorithm ga) {
		for (final Genome genome : this.genomes) {
			genome.setGeneticAlgorithm(ga);
		}

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void clear() {
		this.genomes.clear();

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Genome get(final int i) {
		return this.genomes.get(i);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Genome getBest() {
		if (this.genomes.size() == 0) {
			return null;
		} else {
			return this.genomes.get(0);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Genome> getGenomes() {
		return this.genomes;
	}

	/**
	 * @return The name.
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getPopulationSize() {
		return this.populationSize;
	}

	/**
	 * Set the name.
	 * 
	 * @param theName
	 *            The new name.
	 */
	public void setName(final String theName) {
		this.name = theName;

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setPopulationSize(final int thePopulationSize) {
		this.populationSize = thePopulationSize;
	}

	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int size() {
		return this.genomes.size();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void sort() {
		Collections.sort(this.genomes);
	}
}
