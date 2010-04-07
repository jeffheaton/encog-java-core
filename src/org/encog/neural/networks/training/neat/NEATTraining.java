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

package org.encog.neural.networks.training.neat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.encog.EncogError;
import org.encog.cloud.EncogCloud;
import org.encog.mathutil.randomize.RangeRandomizer;
import org.encog.neural.activation.ActivationFunction;
import org.encog.neural.activation.ActivationLinear;
import org.encog.neural.activation.ActivationSigmoid;
import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.Layer;
import org.encog.neural.networks.training.CalculateScore;
import org.encog.neural.networks.training.Strategy;
import org.encog.neural.networks.training.Train;
import org.encog.neural.networks.training.TrainingError;
import org.encog.neural.networks.training.genetic.GeneticScoreAdapter;
import org.encog.solve.genetic.GeneticAlgorithm;
import org.encog.solve.genetic.genome.Chromosome;
import org.encog.solve.genetic.genome.Genome;
import org.encog.solve.genetic.genome.GenomeComparator;
import org.encog.solve.genetic.population.BasicPopulation;
import org.encog.solve.genetic.population.Population;
import org.encog.solve.genetic.species.BasicSpecies;
import org.encog.solve.genetic.species.Species;

/**
 * The two parents.
 */
enum NEATParent {
	/**
	 * The father.
	 */
	Dad,

	/**
	 * The mother.
	 */
	Mom
};

/**
 * Implements NEAT genetic training.
 * 
 * NeuroEvolution of Augmenting Topologies (NEAT) is a genetic algorithm for the
 * generation of evolving artificial neural networks. It was developed by Ken
 * Stanley while at The University of Texas at Austin.
 * 
 * http://www.cs.ucf.edu/~kstanley/
 * 
 */
public class NEATTraining extends GeneticAlgorithm implements Train {

	/**
	 * The average fit adjustment.
	 */
	private double averageFitAdjustment;

	/**
	 * THe best ever score.
	 */
	private double bestEverScore;

	/**
	 * The number of inputs.
	 */
	private final int inputCount;

	/**
	 * The activation function for neat to use.
	 */
	private ActivationFunction neatActivationFunction = new ActivationSigmoid();

	/**
	 * The activation function to use on the output layer of Encog.
	 */
	private ActivationFunction outputActivationFunction = new ActivationLinear();

	/**
	 * The number of output neurons.
	 */
	private final int outputCount;

	/**
	 * The activation mutation rate.
	 */
	private double paramActivationMutationRate = 0.1;

	/**
	 * The likelyhood of adding a link.
	 */
	private double paramChanceAddLink = 0.07;

	/**
	 * The likelyhood of adding a node.
	 */
	private double paramChanceAddNode = 0.04;

	/**
	 * THe likelyhood of adding a recurrent link.
	 */
	private double paramChanceAddRecurrentLink = 0.05;

	/**
	 * The compatibility threshold for a species.
	 */
	private double paramCompatibilityThreshold = 0.26;

	/**
	 * The crossover rate.
	 */
	private double paramCrossoverRate = 0.7;

	/**
	 * The max activation perturbation.
	 */
	private double paramMaxActivationPerturbation = 0.1;

	/**
	 * The maximum number of species.
	 */
	private int paramMaxNumberOfSpecies = 0;

	/**
	 * The maximum number of neurons.
	 */
	private double paramMaxPermittedNeurons = 100;

	/**
	 * The maximum weight perturbation.
	 */
	private double paramMaxWeightPerturbation = 0.5;

	/**
	 * The mutation rate.
	 */
	private double paramMutationRate = 0.2;

	/**
	 * The number of link add attempts.
	 */
	private int paramNumAddLinkAttempts = 5;

	/**
	 * The number of generations allowed with no improvement.
	 */
	private int paramNumGensAllowedNoImprovement = 15;

	/**
	 * The number of tries to find a looped link.
	 */
	private int paramNumTrysToFindLoopedLink = 5;

	/**
	 * The number of tries to find an old link.
	 */
	private int paramNumTrysToFindOldLink = 5;

	/**
	 * The probability that the weight will be totally replaced.
	 */
	private double paramProbabilityWeightReplaced = 0.1;

	/**
	 * The splits.
	 */
	private List<SplitDepth> splits;

	/**
	 * The total fit adjustment.
	 */
	private double totalFitAdjustment;

	/**
	 * Determines if we are using snapshot mode.
	 */
	private boolean snapshot;
	
	private EncogCloud cloud;

	/**
	 * Construct neat training with a predefined population.
	 * 
	 * @param calculateScore
	 *            The score object to use.
	 * @param population
	 *            The population to use.
	 */
	public NEATTraining(final CalculateScore calculateScore,
			final Population population) {
		if (population.size() < 1) {
			throw new TrainingError("Population can not be empty.");
		}

		NEATGenome genome = (NEATGenome) population.getGenomes().get(0);
		this.setCalculateScore(new GeneticScoreAdapter(calculateScore));
		this.setPopulation(population);
		this.inputCount = genome.getInputCount();
		this.outputCount = genome.getOutputCount();
		
		for(Genome obj: population.getGenomes() )
		{
			NEATGenome neat = (NEATGenome)obj;
			neat.setGeneticAlgorithm(this);
		}

		init();
	}

	/**
	 * Construct a neat trainer with a new population.
	 * 
	 * @param calculateScore
	 *            The score calculation object.
	 * @param inputCount
	 *            The input neuron count.
	 * @param outputCount
	 *            The output neuron count.
	 * @param populationSize
	 *            The population size.
	 */
	public NEATTraining(final CalculateScore calculateScore,
			final int inputCount, final int outputCount,
			final int populationSize) {

		this.inputCount = inputCount;
		this.outputCount = outputCount;

		setCalculateScore(new GeneticScoreAdapter(calculateScore));
		setComparator(new GenomeComparator(getCalculateScore()));
		setPopulation(new BasicPopulation(populationSize));

		// create the initial population
		for (int i = 0; i < populationSize; i++) {
			getPopulation().add(
					new NEATGenome(this, getPopulation().assignGenomeID(),
							inputCount, outputCount));
		}

		init();
	}

	public NEATTraining(CalculateScore score, BasicNetwork network,
			Population population) {
		Layer inputLayer = network.getLayer(BasicNetwork.TAG_INPUT);
		Layer outputLayer = network.getLayer(BasicNetwork.TAG_OUTPUT);
		setCalculateScore(new GeneticScoreAdapter(score));
		setComparator(new GenomeComparator(getCalculateScore()));
		this.inputCount = inputLayer.getNeuronCount();
		this.outputCount = outputLayer.getNeuronCount();
		this.setPopulation(population);
		
		for(Genome obj: population.getGenomes() )
		{
			if( !(obj instanceof NEATGenome) ) {
				throw new TrainingError("Population can only contain objects of NEATGenome.");
			}
			
			NEATGenome neat = (NEATGenome)obj;
			
			if( neat.getInputCount()!=inputCount ||
				neat.getOutputCount()!=outputCount )
			{
				throw new TrainingError("All NEATGenome's must have the same input and output sizes as the base network.");
			}
			neat.setGeneticAlgorithm(this);
		}
		
		init();
	}

	/**
	 * setup for training.
	 */
	private void init() {
		NEATGenome genome = (NEATGenome) getPopulation().getGenomes().get(0);

		getPopulation().setInnovations(
				new NEATInnovationList(getPopulation(), genome.getLinks(),
						genome.getNeurons()));

		splits = split(null, 0, 1, 0);

		if (getCalculateScore().shouldMinimize()) {
			bestEverScore = Double.MAX_VALUE;
		} else {
			bestEverScore = Double.MIN_VALUE;
		}

		resetAndKill();
		sortAndRecord();
		speciateAndCalculateSpawnLevels();
	}

	/**
	 * Add a neuron.
	 * 
	 * @param nodeID
	 *            The neuron id.
	 * @param vec
	 *            THe list of id's used.
	 */
	public void addNeuronID(final long nodeID, final List<Long> vec) {
		for (int i = 0; i < vec.size(); i++) {
			if (vec.get(i) == nodeID) {
				return;
			}
		}

		vec.add(nodeID);

		return;
	}

	/**
	 * Not supported, will throw an error.
	 * 
	 * @param strategy
	 *            Not used.
	 */
	public void addStrategy(final Strategy strategy) {
		throw new TrainingError(
				"Strategies are not supported by this training method.");
	}

	/**
	 * Adjust the species compatibility threshold. This prevents us from having
	 * too many species.
	 */
	public void adjustCompatibilityThreshold() {

		// has this been disabled (unlimited species)
		if (paramMaxNumberOfSpecies < 1) {
			return;
		}

		final double thresholdIncrement = 0.01;

		if (getPopulation().getSpecies().size() > paramMaxNumberOfSpecies) {
			paramCompatibilityThreshold += thresholdIncrement;
		}

		else if (getPopulation().getSpecies().size() < 2) {
			paramCompatibilityThreshold -= thresholdIncrement;
		}

	}

	/**
	 * Adjust each species score.
	 */
	public void adjustSpeciesScore() {
		for (final Species s : getPopulation().getSpecies()) {
			// loop over all genomes and adjust scores as needed
			for (final Genome member : s.getMembers()) {
				double score = member.getScore();

				// apply a youth bonus
				if (s.getAge() < this.getPopulation().getYoungBonusAgeThreshold()) {
					score = this.getComparator().applyBonus(score,
							this.getPopulation().getYoungScoreBonus());
				}

				// apply an old age penalty
				if (s.getAge() > this.getPopulation().getOldAgeThreshold()) {
					score = this.getComparator().applyPenalty(score,
							this.getPopulation().getOldAgePenalty());
				}

				final double adjustedScore = score / s.getMembers().size();

				member.setAdjustedScore(adjustedScore);

			}
		}
	}

	/**
	 * Calculate the network depth for the specified genome.
	 * 
	 * @param genome
	 *            The genome to calculate.
	 */
	private void calculateNetDepth(final NEATGenome genome) {
		int maxSoFar = 0;

		for (int nd = 0; nd < genome.getNeurons().size(); ++nd) {
			for (final SplitDepth split : splits) {

				if ((genome.getSplitY(nd) == split.getValue())
						&& (split.getDepth() > maxSoFar)) {
					maxSoFar = split.getDepth();
				}
			}
		}

		genome.setNetworkDepth(maxSoFar + 2);
	}

	/**
	 * Perform the crossover.
	 * 
	 * @param mom
	 *            The mother.
	 * @param dad
	 *            The father.
	 * @return The child.
	 */
	public NEATGenome crossover(final NEATGenome mom, final NEATGenome dad) {
		NEATParent best;

		// first determine who is more fit, the mother or the father?
		if (mom.getScore() == dad.getScore()) {
			if (mom.getNumGenes() == dad.getNumGenes()) {
				if (Math.random() > 0) {
					best = NEATParent.Mom;
				} else {
					best = NEATParent.Dad;
				}
			}

			else {
				if (mom.getNumGenes() < dad.getNumGenes()) {
					best = NEATParent.Mom;
				}

				else {
					best = NEATParent.Dad;
				}
			}
		}

		else {
			if (getComparator().isBetterThan(mom.getScore(), dad.getScore())) {
				best = NEATParent.Mom;
			}

			else {
				best = NEATParent.Dad;
			}
		}

		final Chromosome babyNeurons = new Chromosome();
		final Chromosome babyGenes = new Chromosome();

		final List<Long> vecNeurons = new ArrayList<Long>();

		int curMom = 0;
		int curDad = 0;

		NEATLinkGene momGene;
		NEATLinkGene dadGene;

		NEATLinkGene selectedGene = null;

		while ((curMom < mom.getNumGenes()) || (curDad < dad.getNumGenes())) {

			if (curMom < mom.getNumGenes()) {
				momGene = (NEATLinkGene) mom.getLinks().get(curMom);
			} else {
				momGene = null;
			}

			if (curDad < dad.getNumGenes()) {
				dadGene = (NEATLinkGene) dad.getLinks().get(curDad);
			} else {
				dadGene = null;
			}

			if ((momGene == null) && (dadGene != null)) {
				if (best == NEATParent.Dad) {
					selectedGene = dadGene;
				}
				curDad++;
			} else if ((dadGene == null) && (momGene != null)) {
				if (best == NEATParent.Mom) {
					selectedGene = momGene;
				}
				curMom++;
			} else if (momGene.getInnovationId() < dadGene.getInnovationId()) {
				if (best == NEATParent.Mom) {
					selectedGene = momGene;
				}
				curMom++;
			} else if (dadGene.getInnovationId() < momGene.getInnovationId()) {
				if (best == NEATParent.Dad) {
					selectedGene = dadGene;
				}
				curDad++;
			} else if (dadGene.getInnovationId() == momGene.getInnovationId()) {
				if (Math.random() < 0.5f) {
					selectedGene = momGene;
				}

				else {
					selectedGene = dadGene;
				}
				curMom++;
				curDad++;

			}

			if (babyGenes.size() == 0) {
				babyGenes.add(selectedGene);
			}

			else {
				if (((NEATLinkGene) babyGenes.get(babyGenes.size() - 1))
						.getInnovationId() != selectedGene.getInnovationId()) {
					babyGenes.add(selectedGene);
				}
			}

			// Check if we already have the nodes referred to in SelectedGene.
			// If not, they need to be added.
			addNeuronID(selectedGene.getFromNeuronID(), vecNeurons);
			addNeuronID(selectedGene.getToNeuronID(), vecNeurons);

		}// end while

		// now create the required nodes. First sort them into order
		Collections.sort(vecNeurons);

		for (int i = 0; i < vecNeurons.size(); i++) {
			babyNeurons.add(getInnovations().createNeuronFromID(
					vecNeurons.get(i)));
		}

		// finally, create the genome
		final NEATGenome babyGenome = new NEATGenome(this, getPopulation()
				.assignGenomeID(), babyNeurons, babyGenes, mom.getInputCount(),
				mom.getOutputCount());

		return babyGenome;
	}

	/**
	 * Called when training is done.
	 */
	public void finishTraining() {

	}

	/**
	 * return The error for the best genome.
	 */
	public double getError() {
		return getPopulation().getBest().getScore();
	}

	/**
	 * @return The innovations.
	 */
	public NEATInnovationList getInnovations() {
		return (NEATInnovationList) getPopulation().getInnovations();
	}

	/**
	 * @return The input count.
	 */
	public int getInputCount() {
		return inputCount;
	}

	/**
	 * @return The activation function to use with NEAT.
	 */
	public ActivationFunction getNeatActivationFunction() {
		return neatActivationFunction;
	}

	/**
	 * @return A network created for the best genome.
	 */
	public BasicNetwork getNetwork() {
		return (BasicNetwork) getPopulation().getBest().getOrganism();
	}

	/**
	 * @return The activation function to use with the Encog output layer.
	 */
	public ActivationFunction getOutputActivationFunction() {
		return outputActivationFunction;
	}

	/**
	 * @return The number of output neurons.
	 */
	public int getOutputCount() {
		return outputCount;
	}

	/**
	 * @return The mutation rate.
	 */
	public double getParamActivationMutationRate() {
		return paramActivationMutationRate;
	}

	/**
	 * @return The chance that we will add a link.
	 */
	public double getParamChanceAddLink() {
		return paramChanceAddLink;
	}

	/**
	 * @return The chance that we will add a node.
	 */
	public double getParamChanceAddNode() {
		return paramChanceAddNode;
	}

	/**
	 * @return The chance we will add a recurrent link.
	 */
	public double getParamChanceAddRecurrentLink() {
		return paramChanceAddRecurrentLink;
	}

	/**
	 * @return The compatibility threshold for a species.
	 */
	public double getParamCompatibilityThreshold() {
		return paramCompatibilityThreshold;
	}

	/**
	 * @return The crossover rate.
	 */
	public double getParamCrossoverRate() {
		return paramCrossoverRate;
	}

	/**
	 * @return THe maximum activation perturbation.
	 */
	public double getParamMaxActivationPerturbation() {
		return paramMaxActivationPerturbation;
	}

	/**
	 * @return The maximum number of species.
	 */
	public int getParamMaxNumberOfSpecies() {
		return paramMaxNumberOfSpecies;
	}

	/**
	 * @return THe maximum neurons.
	 */
	public double getParamMaxPermittedNeurons() {
		return paramMaxPermittedNeurons;
	}

	/**
	 * @return THe max weight perturbation.
	 */
	public double getParamMaxWeightPerturbation() {
		return paramMaxWeightPerturbation;
	}

	/**
	 * @return The mutation rate.
	 */
	public double getParamMutationRate() {
		return paramMutationRate;
	}

	/**
	 * @return The number of attempts to add a link.
	 */
	public int getParamNumAddLinkAttempts() {
		return paramNumAddLinkAttempts;
	}

	/**
	 * @return The number of generations allowed with no improvement.
	 */
	public int getParamNumGensAllowedNoImprovement() {
		return paramNumGensAllowedNoImprovement;
	}

	/**
	 * @return The number of tries to find a looped link.
	 */
	public int getParamNumTrysToFindLoopedLink() {
		return paramNumTrysToFindLoopedLink;
	}

	/**
	 * @return The number of tries to find an old link.
	 */
	public int getParamNumTrysToFindOldLink() {
		return paramNumTrysToFindOldLink;
	}

	/**
	 * @return THe propbability that a weight will be replaced.
	 */
	public double getParamProbabilityWeightReplaced() {
		return paramProbabilityWeightReplaced;
	}

	/**
	 * Returns an empty list, strategies are not supported.
	 * @return The strategies in use(none).
	 */
	public List<Strategy> getStrategies() {
		return new ArrayList<Strategy>();
	}

	/**
	 * Returns null, does not use a training set, rather uses a score function.
	 * @return null, not used.
	 */
	public NeuralDataSet getTraining() {
		return null;
	}

	/**
	 * Perform one training iteration.
	 */
	@Override
	public void iteration() {

		final List<NEATGenome> newPop = new ArrayList<NEATGenome>();

		int numSpawnedSoFar = 0;

		for (final Species s : getPopulation().getSpecies()) {
			if (numSpawnedSoFar < getPopulation().size()) {
				int numToSpawn = (int) Math.round(s.getNumToSpawn());

				boolean bChosenBestYet = false;

				while ((numToSpawn--) > 0) {
					NEATGenome baby = null;

					if (!bChosenBestYet) {
						baby = (NEATGenome)s.getLeader();

						bChosenBestYet = true;
					}

					else {
						// if the number of individuals in this species is only
						// one
						// then we can only perform mutation
						if (s.getMembers().size() == 1) {
							// spawn a child
							baby = new NEATGenome((NEATGenome) s.chooseParent());
						} else {
							final NEATGenome g1 = (NEATGenome) s.chooseParent();

							if (Math.random() < paramCrossoverRate) {
								NEATGenome g2 = (NEATGenome) s.chooseParent();

								int NumAttempts = 5;

								while ((g1.getGenomeID() == g2.getGenomeID())
										&& ((NumAttempts--) > 0)) {
									g2 = (NEATGenome) s.chooseParent();
								}

								if (g1.getGenomeID() != g2.getGenomeID()) {
									baby = crossover(g1, g2);
								}
							}

							else {
								baby = new NEATGenome(g1);
							}
						}

						if (baby != null) {
							baby.setGenomeID(getPopulation().assignGenomeID());

							if (baby.getNeurons().size() < paramMaxPermittedNeurons) {
								baby.addNeuron(paramChanceAddNode,
										paramNumTrysToFindOldLink);
							}

							// now there's the chance a link may be added
							baby.addLink(paramChanceAddLink,
									paramChanceAddRecurrentLink,
									paramNumTrysToFindLoopedLink,
									paramNumAddLinkAttempts);

							// mutate the weights
							baby.mutateWeights(paramMutationRate,
									paramProbabilityWeightReplaced,
									paramMaxWeightPerturbation);

							baby.mutateActivationResponse(
									paramActivationMutationRate,
									paramMaxActivationPerturbation);

						}
					}

					if (baby != null) {
						// sort the baby's genes by their innovation numbers
						baby.sortGenes();

						// add to new pop
						if (newPop.contains(baby)) {
							throw new EncogError("readd");
						}
						newPop.add(baby);

						++numSpawnedSoFar;

						if (numSpawnedSoFar == getPopulation().size()) {
							numToSpawn = 0;
						}
					}
				}
			}
		}

		while (newPop.size() < getPopulation().size()) {
			newPop.add(tournamentSelection(getPopulation().size() / 5));
		}

		getPopulation().clear();
		getPopulation().addAll(newPop);
		
		resetAndKill();		
		sortAndRecord();
		speciateAndCalculateSpawnLevels();
	}

	/**
	 * Reset for an iteration.
	 */
	public void resetAndKill() {
		totalFitAdjustment = 0;
		averageFitAdjustment = 0;

		final Object[] speciesArray = getPopulation().getSpecies().toArray();

		for (int i = 0; i < speciesArray.length; i++) {
			final Species s = (Species) speciesArray[i];
			s.purge();

			if ((s.getGensNoImprovement() > paramNumGensAllowedNoImprovement)
					&& getComparator().isBetterThan(bestEverScore,
							s.getBestScore())) {
				getPopulation().getSpecies().remove(s);
			}
		}
	}

	/**
	 * Not used.
	 * @param error Not used.
	 */
	public void setError(final double error) {
	}

	/**
	 * Set the NEAT activation, used by the NEAT neurons.
	 * @param neatActivationFunction The activation function.
	 */
	public void setNeatActivationFunction(
			final ActivationFunction neatActivationFunction) {
		this.neatActivationFunction = neatActivationFunction;
	}

	/**
	 * Set the activatoin function for the Encog output layer.
	 * @param outputActivationFunction The activation function.
	 */
	public void setOutputActivationFunction(
			final ActivationFunction outputActivationFunction) {
		this.outputActivationFunction = outputActivationFunction;
	}

	/**
	 * Set the activation mutation rate.
	 * @param paramActivationMutationRate The mutation rate.
	 */
	public void setParamActivationMutationRate(
			final double paramActivationMutationRate) {
		this.paramActivationMutationRate = paramActivationMutationRate;
	}

	/**
	 * Set the chance to add a link.
	 * @param paramChanceAddLink The chance to add a link.
	 */
	public void setParamChanceAddLink(final double paramChanceAddLink) {
		this.paramChanceAddLink = paramChanceAddLink;
	}

	/**
	 * Set the chance to add a node.
	 * @param paramChanceAddNode The chance to add a node.
	 */
	public void setParamChanceAddNode(final double paramChanceAddNode) {
		this.paramChanceAddNode = paramChanceAddNode;
	}

	/**
	 * Set the chance to add a recurrent link.
	 * @param paramChanceAddRecurrentLink The chance to add a recurrent link.
	 */
	public void setParamChanceAddRecurrentLink(
			final double paramChanceAddRecurrentLink) {
		this.paramChanceAddRecurrentLink = paramChanceAddRecurrentLink;
	}

	/**
	 * Set the compatibility threshold for species.
	 * @param paramCompatibilityThreshold The threshold.
	 */
	public void setParamCompatibilityThreshold(
			final double paramCompatibilityThreshold) {
		this.paramCompatibilityThreshold = paramCompatibilityThreshold;
	}

	/**
	 * Set the cross over rate.
	 * @param paramCrossoverRate The crossover rate.
	 */
	public void setParamCrossoverRate(final double paramCrossoverRate) {
		this.paramCrossoverRate = paramCrossoverRate;
	}

	/**
	 * Set the max activation perturbation.
	 * @param paramMaxActivationPerturbation The max perturbation.
	 */
	public void setParamMaxActivationPerturbation(
			final double paramMaxActivationPerturbation) {
		this.paramMaxActivationPerturbation = paramMaxActivationPerturbation;
	}

	/**
	 * Set the maximum number of species.
	 * @param paramMaxNumberOfSpecies The max number of species.
	 */
	public void setParamMaxNumberOfSpecies(final int paramMaxNumberOfSpecies) {
		this.paramMaxNumberOfSpecies = paramMaxNumberOfSpecies;
	}

	/**
	 * Set the max permitted neurons.
	 * @param paramMaxPermittedNeurons The max permitted neurons.
	 */
	public void setParamMaxPermittedNeurons(
			final double paramMaxPermittedNeurons) {
		this.paramMaxPermittedNeurons = paramMaxPermittedNeurons;
	}

	/**
	 * Set the max weight perturbation.
	 * @param paramMaxWeightPerturbation The max weight perturbation.
	 */
	public void setParamMaxWeightPerturbation(
			final double paramMaxWeightPerturbation) {
		this.paramMaxWeightPerturbation = paramMaxWeightPerturbation;
	}

	/**
	 * Set the mutation rate.
	 * @param paramMutationRate The mutation rate.
	 */
	public void setParamMutationRate(final double paramMutationRate) {
		this.paramMutationRate = paramMutationRate;
	}

	/**
	 * Set the number of attempts to add a link.
	 * @param paramNumAddLinkAttempts The number of attempts to add a link.
	 */
	public void setParamNumAddLinkAttempts(final int paramNumAddLinkAttempts) {
		this.paramNumAddLinkAttempts = paramNumAddLinkAttempts;
	}

	/**
	 * Set the number of no-improvement generations allowed.
	 * @param paramNumGensAllowedNoImprovement The number of generations.
	 */
	public void setParamNumGensAllowedNoImprovement(
			final int paramNumGensAllowedNoImprovement) {
		this.paramNumGensAllowedNoImprovement = paramNumGensAllowedNoImprovement;
	}

	/**
	 * Set the number of tries to create a looped link.
	 * @param paramNumTrysToFindLoopedLink Number of tries.
	 */
	public void setParamNumTrysToFindLoopedLink(
			final int paramNumTrysToFindLoopedLink) {
		this.paramNumTrysToFindLoopedLink = paramNumTrysToFindLoopedLink;
	}

	/**
	 * Set the number of tries to try an old link.
	 * @param paramNumTrysToFindOldLink Number of tries.
	 */
	public void setParamNumTrysToFindOldLink(final int paramNumTrysToFindOldLink) {
		this.paramNumTrysToFindOldLink = paramNumTrysToFindOldLink;
	}

	/**
	 * Set the probability to replace a weight.
	 * @param paramProbabilityWeightReplaced The probability.
	 */
	public void setParamProbabilityWeightReplaced(
			final double paramProbabilityWeightReplaced) {
		this.paramProbabilityWeightReplaced = paramProbabilityWeightReplaced;
	}

	/**
	 * Sort the genomes.
	 */
	public void sortAndRecord() {
		
		for( Genome genome: this.getPopulation().getGenomes() )
		{
			genome.decode();
			calculateScore(genome);
		}
		
		getPopulation().sort();

		bestEverScore = getComparator().bestScore(getError(), bestEverScore);
	}

	/**
	 * Determine the species.
	 */
	public void speciateAndCalculateSpawnLevels() {

		// calculate compatibility between genomes and species
		adjustCompatibilityThreshold();

		// assign genomes to species (if any exist)
		for (final Genome g : getPopulation().getGenomes()) {
			final NEATGenome genome = (NEATGenome) g;
			boolean added = false;

			for (final Species s : getPopulation().getSpecies()) {
				final double compatibility = genome
						.getCompatibilityScore((NEATGenome) s.getLeader());

				if (compatibility <= paramCompatibilityThreshold) {
					addSpeciesMember(s,genome);
					genome.setSpeciesID(s.getSpeciesID());
					added = true;
					break;
				}
			}

			// if this genome did not fall into any existing species, create a
			// new species
			if (!added) {
				getPopulation().getSpecies().add(
						new BasicSpecies(this.getPopulation(), genome, getPopulation()
								.assignSpeciesID()));
			}
		}

		adjustSpeciesScore();

		for (final Genome g : getPopulation().getGenomes()) {
			final NEATGenome genome = (NEATGenome) g;
			totalFitAdjustment += genome.getAdjustedScore();
		}

		averageFitAdjustment = totalFitAdjustment / getPopulation().size();

		for (final Genome g : getPopulation().getGenomes()) {
			final NEATGenome genome = (NEATGenome) g;
			final double toSpawn = genome.getAdjustedScore()
					/ averageFitAdjustment;
			genome.setAmountToSpawn(toSpawn);
		}

		for (final Species species : getPopulation().getSpecies()) {
			species.calculateSpawnAmount();
		}
	}

	/**
	 * Calculate splits.
	 * @param result The resulting list, used for recursive calls.
	 * @param low The high to check.
	 * @param high The low to check.
	 * @param depth The depth.
	 * @return A list of split depths.
	 */
	private List<SplitDepth> split(List<SplitDepth> result, final double low,
			final double high, final int depth) {
		if (result == null) {
			result = new ArrayList<SplitDepth>();
		}

		final double span = high - low;

		result.add(new SplitDepth(low + span / 2, depth + 1));

		if (depth > 6) {
			return result;
		}

		else {
			split(result, low, low + span / 2, depth + 1);
			split(result, low + span / 2, high, depth + 1);
			return result;
		}
	}

	/**
	 * Select a gene using a tournament.
	 * @param numComparisons The number of compares to do.
	 * @return The chosen genome.
	 */
	public NEATGenome tournamentSelection(final int numComparisons) {
		double bestScoreSoFar = 0;

		int ChosenOne = 0;

		for (int i = 0; i < numComparisons; ++i) {
			final int ThisTry = (int) RangeRandomizer.randomize(0,
					getPopulation().size() - 1);

			if (getPopulation().get(ThisTry).getScore() > bestScoreSoFar) {
				ChosenOne = ThisTry;

				bestScoreSoFar = getPopulation().get(ThisTry).getScore();
			}
		}

		return (NEATGenome) getPopulation().get(ChosenOne);
	}

	/**
	 * @return Are we using snapshot mode.
	 */
	public boolean isSnapshot() {
		return snapshot;
	}

	/**
	 * Set if we are using snapshot mode.
	 * 
	 * @param snapshot
	 *            True if we are using snapshot mode.
	 */
	public void setSnapshot(boolean snapshot) {
		this.snapshot = snapshot;
	}

	public EncogCloud getCloud() {
		return this.cloud;
	}

	public void setCloud(EncogCloud cloud) {
		this.cloud = cloud;		
	}

}
