package org.encog.solve.genetic.population;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.encog.neural.networks.training.genetic.NeuralGenome;
import org.encog.solve.genetic.Genome;

public class BasicPopulation implements Population {
	/**
	 * How many genomes should be created.
	 */
	private int populationSize;
	
	/**
	 * The population.
	 */
	private final List<Genome> genomes = new ArrayList<Genome>();
	
	public BasicPopulation(int populationSize) {
		this.populationSize = populationSize;
	}

	/**
	 * Get the population size.
	 * 
	 * @return The population size.
	 */
	public int getPopulationSize() {
		return this.populationSize;
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

	/**
	 * Sort the population.
	 */
	public void sort() {
		Collections.sort(this.genomes);
	}
	
	public List<Genome> getGenomes() {
		return genomes;
	}

	@Override
	public Genome getBest() {
		if( this.genomes.size()==0)
			return null;
		else 
			return this.genomes.get(0);
	}

	@Override
	public void add(Genome genome) {
		this.genomes.add(genome);
		
	}
}
