package org.encog.solve.genetic.population;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.encog.neural.networks.training.genetic.NeuralGenome;
import org.encog.solve.genetic.genome.Genome;
import org.encog.solve.genetic.innovation.InnovationList;
import org.encog.solve.genetic.species.Species;
import org.encog.util.identity.BasicGenerateID;
import org.encog.util.identity.GenerateID;

public class BasicPopulation implements Population {
	/**
	 * How many genomes should be created.
	 */
	private int populationSize;
	
	private final GenerateID genomeIDGenerate = new BasicGenerateID();
	private final GenerateID speciesIDGenerate = new BasicGenerateID();
	private final GenerateID geneIDGenerate = new BasicGenerateID();
	private final GenerateID innovationIDGenerate = new BasicGenerateID();
	
	private int youngBonusAgeThreshhold = 10;
	private double youngFitnessBonus = 0.3;
	private int oldAgeThreshold = 50;
	private double oldAgePenalty = 0.3;
	private double survivalRate = 0.2;
	private final List<Species> species = new ArrayList<Species>();
	private InnovationList innovations;
	
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

	public Genome getBest() {
		if( this.genomes.size()==0)
			return null;
		else 
			return this.genomes.get(0);
	}

	public void add(Genome genome) {
		this.genomes.add(genome);
		
	}

	public long assignGeneID() {
		return this.geneIDGenerate.generate();
	}

	public long assignGenomeID() {
		return this.genomeIDGenerate.generate();
	}

	public long assignInnovationID() {
		return this.innovationIDGenerate.generate();
	}

	public long assignSpeciesID() {
		return this.speciesIDGenerate.generate();
	}

	public int size() {
		return this.genomes.size();
	}

	public void addAll(List<? extends Genome> newPop) {
		this.genomes.addAll(newPop);
	}

	public void clear() {
		this.genomes.clear();
		
	}

	public Genome get(int i) {
		return this.genomes.get(i);
	}

	public int getYoungBonusAgeThreshhold() {
		return youngBonusAgeThreshhold;
	}

	public void setYoungBonusAgeThreshhold(int youngBonusAgeThreshhold) {
		this.youngBonusAgeThreshhold = youngBonusAgeThreshhold;
	}

	public double getYoungFitnessBonus() {
		return youngFitnessBonus;
	}

	public void setYoungFitnessBonus(double youngFitnessBonus) {
		this.youngFitnessBonus = youngFitnessBonus;
	}

	public int getOldAgeThreshold() {
		return oldAgeThreshold;
	}

	public void setOldAgeThreshold(int oldAgeThreshold) {
		this.oldAgeThreshold = oldAgeThreshold;
	}

	public double getOldAgePenalty() {
		return oldAgePenalty;
	}

	public void setOldAgePenalty(double oldAgePenalty) {
		this.oldAgePenalty = oldAgePenalty;
	}

	public double getSurvivalRate() {
		return survivalRate;
	}

	public void setSurvivalRate(double survivalRate) {
		this.survivalRate = survivalRate;
	}

	public List<Species> getSpecies() {
		return species;
	}

	public InnovationList getInnovations() {
		return innovations;
	}

	public void setInnovations(InnovationList innovations) {
		this.innovations = innovations;
	}
	
	
}
