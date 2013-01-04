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
package org.encog.neural.neat;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.encog.engine.network.activation.ActivationFunction;
import org.encog.engine.network.activation.ActivationSteepenedSigmoid;
import org.encog.ml.ea.population.BasicPopulation;
import org.encog.neural.NeuralNetworkError;
import org.encog.neural.neat.training.NEATGenome;
import org.encog.neural.neat.training.NEATInnovationList;
import org.encog.neural.neat.training.innovation.InnovationList;
import org.encog.util.identity.BasicGenerateID;
import org.encog.util.identity.GenerateID;

public class NEATPopulation extends BasicPopulation implements Serializable {
	
	/**
	 * Thed default old age penalty.
	 */
	public static final double DEFAULT_OLD_AGE_PENALTY = 0.3;

	/**
	 * The default old age threshold.
	 */
	public static final int DEFAULT_OLD_AGE_THRESHOLD = 50;
	
	/**
	 * The default survival rate.
	 */
	public static final double DEFAULT_SURVIVAL_RATE = 0.2;
	
	/**
	 * The default youth penalty.
	 */
	public static final double DEFAULT_YOUTH_BONUS = 0.3;
	
	/**
	 * The default youth threshold.
	 */
	public static final int DEFAULT_YOUTH_THRESHOLD = 10;
	
	/**
	 * Property tag for the genomes collection.
	 */
	public static final String PROPERTY_GENOMES = "genomes";
	
	/**
	 * Property tag for the innovations collection.
	 */
	public static final String PROPERTY_INNOVATIONS = "innovations";
	
	public static final String PROPERTY_NEAT_ACTIVATION = "neatAct";
	
	/**
	 * Property tag for the next gene id.
	 */
	public static final String PROPERTY_NEXT_GENE_ID = "nextGeneID";
	
	/**
	 * Property tag for the next genome id.
	 */
	public static final String PROPERTY_NEXT_GENOME_ID = "nextGenomeID";
	
	/**
	 * Property tag for the next innovation id.
	 */
	public static final String PROPERTY_NEXT_INNOVATION_ID = "nextInnovationID";
	
	/**
	 * Property tag for the next species id.
	 */
	public static final String PROPERTY_NEXT_SPECIES_ID = "nextSpeciesID";

	/**
	 * Property tag for the old age penalty.
	 */
	public static final String PROPERTY_OLD_AGE_PENALTY = "oldAgePenalty";
	
	/**
	 * Property tag for the old age threshold.
	 */
	public static final String PROPERTY_OLD_AGE_THRESHOLD = "oldAgeThreshold";
	
	/**
	 * Property tag for the population size.
	 */
	public static final String PROPERTY_POPULATION_SIZE = "populationSize";

	/**
	 * Property tag for the species collection.
	 */
	public static final String PROPERTY_SPECIES = "species";
	
	/**
	 * Property tag for the survival rate.
	 */
	public static final String PROPERTY_SURVIVAL_RATE = "survivalRate";
	
	/**
	 * Property tag for the young age bonus.
	 */
	public static final String PROPERTY_YOUNG_AGE_BONUS = "youngAgeBonus";
	
	/**
	 * Property tag for the young age threshold.
	 */
	public static final String PROPERTY_YOUNG_AGE_THRESHOLD = "youngAgeThreshold";
	
	/**
	 * Serial id.
	 */
	private static final long serialVersionUID = 1L;
	
	private int activationCycles = 4;
	
	/**
	 * Generate gene id's.
	 */
	private final GenerateID geneIDGenerate = new BasicGenerateID();
	
	/**
	 * Generate genome id's.
	 */
	private final GenerateID genomeIDGenerate = new BasicGenerateID();

	/**
	 * Generate innovation id's.
	 */
	private final GenerateID innovationIDGenerate = new BasicGenerateID();
	
	/**
	 * A list of innovations, or null if this feature is not being used.
	 */
	private InnovationList innovations;

	/**
	 * The number of input units. All members of the population must agree with
	 * this number.
	 */
	int inputCount;
	
	/**
	 * The activation function for neat to use.
	 */
	private ActivationFunction neatActivationFunction = new ActivationSteepenedSigmoid();

	/**
	 * The old age penalty.
	 */
	private double oldAgePenalty = DEFAULT_OLD_AGE_PENALTY;

	/**
	 * The old age threshold.
	 */
	private int oldAgeThreshold = DEFAULT_OLD_AGE_THRESHOLD;

	/**
	 * The number of output units. All members of the population must agree with
	 * this number.
	 */
	int outputCount;
	
	private final List<NEATSpecies> species = new ArrayList<NEATSpecies>();

	/**
	 * Generate species id's.
	 */
	private final GenerateID speciesIDGenerate = new BasicGenerateID();

	/**
	 * The survival rate.
	 */
	private double survivalRate = DEFAULT_SURVIVAL_RATE;

	/**
	 * The young threshold.
	 */
	private int youngBonusAgeThreshold = DEFAULT_YOUTH_THRESHOLD;
	
	/**
	 * The young score bonus.
	 */
	private double youngScoreBonus = DEFAULT_YOUTH_BONUS;
	
	private int maxIndividualSize = 100;

	public NEATPopulation() {

	}

	/**
	 * Construct a starting NEAT population.
	 * 
	 * @param inputCount
	 *            The input neuron count.
	 * @param outputCount
	 *            The output neuron count.
	 * @param populationSize
	 *            The population size.
	 */
	public NEATPopulation(final int inputCount, final int outputCount,
			final int populationSize) {
		super(populationSize, null);
		this.inputCount = inputCount;
		this.outputCount = outputCount;

		if (populationSize == 0) {
			throw new NeuralNetworkError(
					"Population must have more than zero genomes.");
		}

		reset(populationSize);

	}

	/**
	 * {@inheritDoc}
	 */
	public long assignGeneID() {
		return this.geneIDGenerate.generate();
	}
	
	/**
	 * {@inheritDoc}
	 */
	public long assignGenomeID() {
		return this.genomeIDGenerate.generate();
	}

	/**
	 * {@inheritDoc}
	 */
	public long assignInnovationID() {
		return this.innovationIDGenerate.generate();
	}
	
	/**
	 * {@inheritDoc}
	 */
	public long assignSpeciesID() {
		return this.speciesIDGenerate.generate();
	}

	public int getActivationCycles() {
		return activationCycles;
	}

	/**
	 * @return the geneIDGenerate
	 */
	public GenerateID getGeneIDGenerate() {
		return this.geneIDGenerate;
	}

	/**
	 * @return the genomeIDGenerate
	 */
	public GenerateID getGenomeIDGenerate() {
		return this.genomeIDGenerate;
	}

	/**
	 * @return the innovationIDGenerate
	 */
	public GenerateID getInnovationIDGenerate() {
		return this.innovationIDGenerate;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public InnovationList getInnovations() {
		return this.innovations;
	}

	/**
	 * @return the inputCount
	 */
	public int getInputCount() {
		return inputCount;
	}
	
	/**
	 * @return the neatActivationFunction
	 */
	public ActivationFunction getNeatActivationFunction() {
		return neatActivationFunction;
	}

	/**
	 * {@inheritDoc}
	 */
	public double getOldAgePenalty() {
		return this.oldAgePenalty;
	}

	/**
	 * {@inheritDoc}
	 */
	public int getOldAgeThreshold() {
		return this.oldAgeThreshold;
	}

	/**
	 * @return the outputCount
	 */
	public int getOutputCount() {
		return outputCount;
	}

	/**
	 * @return the species
	 */
	public List<NEATSpecies> getSpecies() {
		return species;
	}

	/**
	 * @return the speciesIDGenerate
	 */
	public GenerateID getSpeciesIDGenerate() {
		return this.speciesIDGenerate;
	}

	/**
	 * {@inheritDoc}
	 */
	public double getSurvivalRate() {
		return this.survivalRate;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public int getYoungBonusAgeThreshold() {
		return this.youngBonusAgeThreshold;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public double getYoungScoreBonus() {
		return this.youngScoreBonus;
	}


	public void reset(int populationSize) {
		this.getGenomes().clear();
		this.setPopulationSize(populationSize);		
		
		// reset counters
		this.getGeneIDGenerate().setCurrentID(1);
		this.getGenomeIDGenerate().setCurrentID(1);
		this.getInnovationIDGenerate().setCurrentID(1);
		this.getSpeciesIDGenerate().setCurrentID(1);
		
		// create the initial population
		for (int i = 0; i < populationSize; i++) {
			NEATGenome genome = new NEATGenome(assignGenomeID(), inputCount,
					outputCount);
			add(genome);
		}

		// create initial innovations
		NEATGenome genome = (NEATGenome) this.getGenomes().get(0);
		this.setInnovations(new NEATInnovationList(this, genome.getLinksChromosome(),
				genome.getNeuronsChromosome()));
	}

	public void setActivationCycles(int activationCycles) {
		this.activationCycles = activationCycles;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setInnovations(final InnovationList theInnovations) {
		this.innovations = theInnovations;
	}

	/**
	 * @param inputCount the inputCount to set
	 */
	public void setInputCount(int inputCount) {
		this.inputCount = inputCount;
	}

	/**
	 * @param neatActivationFunction the neatActivationFunction to set
	 */
	public void setNeatActivationFunction(
			ActivationFunction neatActivationFunction) {
		this.neatActivationFunction = neatActivationFunction;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setOldAgePenalty(final double theOldAgePenalty) {
		this.oldAgePenalty = theOldAgePenalty;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setOldAgeThreshold(final int theOldAgeThreshold) {
		this.oldAgeThreshold = theOldAgeThreshold;
	}

	/**
	 * @param outputCount the outputCount to set
	 */
	public void setOutputCount(int outputCount) {
		this.outputCount = outputCount;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setSurvivalRate(final double theSurvivalRate) {
		this.survivalRate = theSurvivalRate;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setYoungBonusAgeThreshhold(
			final int theYoungBonusAgeThreshold) {
		this.youngBonusAgeThreshold = theYoungBonusAgeThreshold;
	}

	/**
	 * @param theYoungBonusAgeThreshold
	 *            the youngBonusAgeThreshold to set
	 */
	public void setYoungBonusAgeThreshold(
			final int theYoungBonusAgeThreshold) {
		this.youngBonusAgeThreshold = theYoungBonusAgeThreshold;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setYoungScoreBonus(final double theYoungScoreBonus) {
		this.youngScoreBonus = theYoungScoreBonus;
	}

	@Override
	public int getMaxIndividualSize() {
		return maxIndividualSize;
	}

	public void setMaxIndividualSize(int maxIndividualSize) {
		this.maxIndividualSize = maxIndividualSize;
	}

	

}
