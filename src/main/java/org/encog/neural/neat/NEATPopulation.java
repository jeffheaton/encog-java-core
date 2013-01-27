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
import org.encog.ml.MLError;
import org.encog.ml.MLRegression;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.ea.codec.GeneticCODEC;
import org.encog.ml.ea.population.BasicPopulation;
import org.encog.neural.NeuralNetworkError;
import org.encog.neural.hyperneat.FactorHyperNEATGenome;
import org.encog.neural.hyperneat.HyperNEATCODEC;
import org.encog.neural.hyperneat.HyperNEATGenome;
import org.encog.neural.hyperneat.substrate.Substrate;
import org.encog.neural.neat.training.NEATGenome;
import org.encog.neural.neat.training.NEATInnovationList;
import org.encog.util.identity.BasicGenerateID;
import org.encog.util.identity.GenerateID;
import org.encog.util.obj.ChooseObject;

public class NEATPopulation extends BasicPopulation implements Serializable, MLError, MLRegression {
	
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

	public static final int DEFAULT_CYCLES = 4;

	public static final String PROPERTY_CYCLES = "cycles";
	
	private int activationCycles = NEATPopulation.DEFAULT_CYCLES;
	
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
	private NEATInnovationList innovations;
	
	private final double weightRange = 5;
	
	private NEATGenome bestGenome;
	private NEATNetwork bestNetwork;

	/**
	 * The number of input units. All members of the population must agree with
	 * this number.
	 */
	int inputCount;
	
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
	
	private Substrate substrate;

	private ChooseObject<ActivationFunction> activationFunctions = new ChooseObject<ActivationFunction>();

	private GeneticCODEC codec;
	
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
		
		this.setNEATActivationFunction(new ActivationSteepenedSigmoid());

		if (populationSize == 0) {
			throw new NeuralNetworkError(
					"Population must have more than zero genomes.");
		}

		reset(populationSize);

	}
	
	public NEATPopulation(Substrate theSubstrate, int populationSize) {
		super(populationSize,new FactorHyperNEATGenome());
		this.substrate = theSubstrate;
		this.inputCount = 6;
		this.outputCount = 2;
		HyperNEATGenome.buildCPPNActivationFunctions(this.activationFunctions);
		
		reset(populationSize);
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
	
	public NEATInnovationList getInnovations() {
		return this.innovations;
	}

	/**
	 * @return the inputCount
	 */
	public int getInputCount() {
		return inputCount;
	}

	public double getOldAgePenalty() {
		return this.oldAgePenalty;
	}

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

	public double getSurvivalRate() {
		return this.survivalRate;
	}
	
	public int getYoungBonusAgeThreshold() {
		return this.youngBonusAgeThreshold;
	}
	
	public double getYoungScoreBonus() {
		return this.youngScoreBonus;
	}


	public void reset(int populationSize) {
		// create the genome factory
		if( isHyperNEAT() ) {
			this.codec = new HyperNEATCODEC();
			setGenomeFactory( new FactorHyperNEATGenome() );
		} else {
			this.codec = new NEATCODEC();
			setGenomeFactory( new FactorNEATGenome() );
		}
		
		// create the new genomes
		this.getGenomes().clear();
		this.setPopulationSize(populationSize);		
		
		// reset counters
		this.getGeneIDGenerate().setCurrentID(1);
		this.getGenomeIDGenerate().setCurrentID(1);
		this.getInnovationIDGenerate().setCurrentID(1);
		this.getSpeciesIDGenerate().setCurrentID(1);
		
		// create the initial population
		for (int i = 0; i < populationSize; i++) {
			NEATGenome genome = getGenomeFactory().factor(this, assignGenomeID(), inputCount,
					outputCount);
			add(genome);
		}

		// create initial innovations
		this.setInnovations(new NEATInnovationList(this));
	}

	public void setActivationCycles(int activationCycles) {
		this.activationCycles = activationCycles;
	}

	public void setInnovations(final NEATInnovationList theInnovations) {
		this.innovations = theInnovations;
	}

	/**
	 * @param inputCount the inputCount to set
	 */
	public void setInputCount(int inputCount) {
		this.inputCount = inputCount;
	}



	public void setOldAgePenalty(final double theOldAgePenalty) {
		this.oldAgePenalty = theOldAgePenalty;
	}

	public void setOldAgeThreshold(final int theOldAgeThreshold) {
		this.oldAgeThreshold = theOldAgeThreshold;
	}

	/**
	 * @param outputCount the outputCount to set
	 */
	public void setOutputCount(int outputCount) {
		this.outputCount = outputCount;
	}

	public void setSurvivalRate(final double theSurvivalRate) {
		this.survivalRate = theSurvivalRate;
	}

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
	
	private void updateBestNetwork() {
		if( this.bestGenome!=this.getGenomes().get(0)) {
			this.bestGenome = (NEATGenome) this.getGenomes().get(0);
			this.bestNetwork = (NEATNetwork)this.codec.decode(this.bestGenome);
		}
	}

	@Override
	public double calculateError(MLDataSet data) {
		updateBestNetwork();
		return this.bestNetwork.calculateError(data);
	}

	@Override
	public MLData compute(MLData input) {
		updateBestNetwork();
		return this.bestNetwork.compute(input);
	}

	public boolean isHyperNEAT() {
		return this.substrate!=null;
	}
	
	public Substrate getSubstrate() {
		return this.substrate;
	}

	/**
	 * @return the activationFunctions
	 */
	public ChooseObject<ActivationFunction> getActivationFunctions() {
		return activationFunctions;
	}

	public void setNEATActivationFunction(ActivationFunction af) {
		this.activationFunctions.clear();
		this.activationFunctions.add(1.0, af);
		this.activationFunctions.finalizeStructure();
	}

	/**
	 * @return the genomeFactory
	 */
	public NEATGenomeFactory getGenomeFactory() {
		return (NEATGenomeFactory)super.getGenomeFactory();
	}

	/**
	 * @param substrate the substrate to set
	 */
	public void setSubstrate(Substrate substrate) {
		this.substrate = substrate;
	}

	/**
	 * @return the codec
	 */
	public GeneticCODEC getCodec() {
		return codec;
	}

	/**
	 * @param codec the codec to set
	 */
	public void setCodec(GeneticCODEC codec) {
		this.codec = codec;
	}

	/**
	 * @return the weightRange
	 */
	public double getWeightRange() {
		return weightRange;
	}

	public static double clampWeight(double w, double weightRange) {
		if( w<-weightRange ) {
			return -weightRange;
		} else if( w>weightRange ) {
			return weightRange;
		} else {
			return w;
		}
	}
	
	

}
