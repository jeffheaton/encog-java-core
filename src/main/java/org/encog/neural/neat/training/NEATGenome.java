/*
 * Encog(tm) Core v3.1 - Java Version
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
package org.encog.neural.neat.training;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.encog.EncogError;
import org.encog.mathutil.randomize.RangeRandomizer;
import org.encog.ml.genetic.genes.Gene;
import org.encog.ml.genetic.genome.BasicGenome;
import org.encog.ml.genetic.genome.Chromosome;
import org.encog.neural.NeuralNetworkError;
import org.encog.neural.neat.NEATLink;
import org.encog.neural.neat.NEATNetwork;
import org.encog.neural.neat.NEATNeuron;
import org.encog.neural.neat.NEATNeuronType;
import org.encog.neural.neat.NEATPopulation;

/**
 * Implements a NEAT genome. This is a "blueprint" for creating a neural
 * network.
 *
 * NeuroEvolution of Augmenting Topologies (NEAT) is a genetic algorithm for the
 * generation of evolving artificial neural networks. It was developed by Ken
 * Stanley while at The University of Texas at Austin.
 *
 * http://www.cs.ucf.edu/~kstanley/
 *
 */
public class NEATGenome extends BasicGenome implements Cloneable, Serializable {

	/**
	 * Serial id.
	 */
	private static final long serialVersionUID = 1L;
	
	public static final String PROPERTY_NEURONS = "neurons";
	public static final String PROPERTY_LINKS = "links";

	
	/**
	 * The adjustment factor for disjoint genes.
	 */
	public static final double TWEAK_DISJOINT = 1;

	/**
	 * The adjustment factor for excess genes.
	 */
	public static final double TWEAK_EXCESS = 1;

	/**
	 * The adjustment factor for matched genes.
	 */
	public static final double TWEAK_MATCHED = 0.4;

	/**
	 * The number of inputs.
	 */
	private int inputCount;

	/**
	 * The chromsome that holds the links.
	 */
	private Chromosome linksChromosome;

	/**
	 * THe network depth.
	 */
	private int networkDepth;

	/**
	 * The chromosome that holds the neurons.
	 */
	private Chromosome neuronsChromosome;

	/**
	 * The number of outputs.
	 */
	private int outputCount;

	/**
	 * The species id.
	 */
	private long speciesID;

	/**
	 * Construct a genome by copying another.
	 *
	 * @param other
	 *            The other genome.
	 */
	public NEATGenome(final NEATGenome other) {
		this.neuronsChromosome = new Chromosome();
		this.linksChromosome = new Chromosome();
		this.setGeneticAlgorithm(other.getGeneticAlgorithm());

		getChromosomes().add(this.neuronsChromosome);
		getChromosomes().add(this.linksChromosome);

		setGenomeID(other.getGenomeID());
		this.networkDepth = other.networkDepth;
		this.setPopulation(other.getPopulation());
		setScore(other.getScore());
		setAdjustedScore(other.getAdjustedScore());
		setAmountToSpawn(other.getAmountToSpawn());
		this.inputCount = other.inputCount;
		this.outputCount = other.outputCount;
		this.speciesID = other.speciesID;

		// copy neurons
		for (final Gene gene : other.getNeurons().getGenes()) {
			final NEATNeuronGene oldGene = (NEATNeuronGene) gene;
			final NEATNeuronGene newGene = new NEATNeuronGene(oldGene
					.getNeuronType(), oldGene.getId(), oldGene.getSplitY(),
					oldGene.getSplitX(), oldGene
							.getActivationResponse());
			getNeurons().add(newGene);
		}

		// copy links
		for (final Gene gene : other.getLinks().getGenes()) {
			final NEATLinkGene oldGene = (NEATLinkGene) gene;
			final NEATLinkGene newGene = new NEATLinkGene(oldGene
					.getFromNeuronID(), oldGene.getToNeuronID(), oldGene
					.isEnabled(), oldGene.getInnovationId(), oldGene
					.getWeight(), oldGene.isRecurrent());
			getLinks().add(newGene);
		}

	}

	/**
	 * Create a NEAT gnome.
	 * @param genomeID
	 *            The genome id.
	 * @param neurons
	 *            The neurons.
	 * @param links
	 *            The links.
	 * @param inputCount
	 *            The input count.
	 * @param outputCount
	 *            The output count.
	 */
	public NEATGenome(final long genomeID,
			final Chromosome neurons, final Chromosome links,
			final int inputCount, final int outputCount) {
		setGenomeID(genomeID);
		this.linksChromosome = links;
		this.neuronsChromosome = neurons;
		setAmountToSpawn(0);
		setAdjustedScore(0);
		this.inputCount = inputCount;
		this.outputCount = outputCount;

		getChromosomes().add(this.neuronsChromosome);
		getChromosomes().add(this.linksChromosome);
	}

	/**
	 * Construct a genome, do not provide links and neurons.
	 * @param id
	 *            The genome id.
	 * @param inputCount
	 *            The input count.
	 * @param outputCount
	 *            The output count.
	 */
	public NEATGenome(final long id,
			final int inputCount, final int outputCount) {
		setGenomeID(id);
		setAdjustedScore(0);
		this.inputCount = inputCount;
		this.outputCount = outputCount;
		setAmountToSpawn(0);
		this.speciesID = 0;

		final double inputRowSlice = 0.8 / (inputCount);
		this.neuronsChromosome = new Chromosome();
		this.linksChromosome = new Chromosome();

		getChromosomes().add(this.neuronsChromosome);
		getChromosomes().add(this.linksChromosome);

		for (int i = 0; i < inputCount; i++) {
			this.neuronsChromosome.add(new NEATNeuronGene(NEATNeuronType.Input,
					i, 0, 0.1 + i * inputRowSlice));
		}

		this.neuronsChromosome.add(new NEATNeuronGene(NEATNeuronType.Bias,
				inputCount, 0, 0.9));

		final double outputRowSlice = 1 / (double) (outputCount + 1);

		for (int i = 0; i < outputCount; i++) {
			this.neuronsChromosome.add(new NEATNeuronGene(
					NEATNeuronType.Output, i + inputCount + 1, 1, (i + 1)
							* outputRowSlice));
		}

		for (int i = 0; i < inputCount + 1; i++) {
			for (int j = 0; j < outputCount; j++) {
				this.linksChromosome.add(new NEATLinkGene(
						((NEATNeuronGene) this.neuronsChromosome.get(i))
								.getId(), ((NEATNeuronGene) getNeurons().get(
								inputCount + j + 1)).getId(), true, inputCount
								+ outputCount + 1 + getNumGenes(),
						RangeRandomizer.randomize(-1, 1), false));
			}
		}

	}
	
	public NEATGenome() {
		
	}

	/**
	 * Mutate the genome by adding a link to this genome.
	 *
	 * @param mutationRate
	 *            The mutation rate.
	 * @param chanceOfLooped
	 *            The chance of a self-connected neuron.
	 * @param numTrysToFindLoop
	 *            The number of tries to find a loop.
	 * @param numTrysToAddLink
	 *            The number of tries to add a link.
	 */
	void addLink(final double mutationRate, final double chanceOfLooped,
			final int numTrysToFindLoop, final int numTrysToAddLink) {

		// should we even add the link
		if (Math.random() > mutationRate) {
			return;
		}

		int countTrysToFindLoop = numTrysToFindLoop;
		int countTrysToAddLink = numTrysToFindLoop;

		// the link will be between these two neurons
		long neuron1ID = -1;
		long neuron2ID = -1;

		boolean recurrent = false;

		// try to add a link
		while ((countTrysToAddLink--) > 0) {
			final NEATNeuronGene neuron1 = chooseRandomNeuron(true);
			final NEATNeuronGene neuron2 = chooseRandomNeuron(false);

			if (!isDuplicateLink(neuron1.getId(), neuron2.getId())
					&& (neuron2.getNeuronType() != NEATNeuronType.Bias)) {

				neuron1ID = neuron1.getId();
				neuron2ID = neuron2.getId();
				break;
			}
		}

		// did we fail to find a link
		if ((neuron1ID < 0) || (neuron2ID < 0)) {
			return;
		}
		
		// check to see if this innovation has already been tried
		final NEATInnovation innovation = ((NEATTraining) getGeneticAlgorithm())
				.getInnovations().checkInnovation(neuron1ID, neuron1ID,
						NEATInnovationType.NewLink);

		// see if this is a recurrent(backwards) link
		final NEATNeuronGene neuronGene
		  = (NEATNeuronGene) this.neuronsChromosome
				.get(getElementPos(neuron1ID));
		if (neuronGene.getSplitY() > neuronGene.getSplitY()) {
			recurrent = true;
		}		

		validate();
		// is this a new innovation?
		if (innovation == null) {
			// new innovation
			((NEATTraining) getGeneticAlgorithm()).getInnovations()
					.createNewInnovation(neuron1ID, neuron2ID,
							NEATInnovationType.NewLink);

			final long id2 = ((NEATTraining) getGeneticAlgorithm())
					.getPopulation().assignInnovationID();

			final NEATLinkGene linkGene = new NEATLinkGene(neuron1ID,
					neuron2ID, true, id2, RangeRandomizer.randomize(-1, 1),
					recurrent);
			this.linksChromosome.add(linkGene);
		} else {
			// existing innovation
			final NEATLinkGene linkGene = new NEATLinkGene(neuron1ID,
					neuron2ID, true, innovation.getInnovationID(),
					RangeRandomizer.randomize(-1, 1), recurrent);
			this.linksChromosome.add(linkGene);
		}
		
		validate();
	}

	/**
	 * Mutate the genome by adding a neuron.
	 *
	 * @param mutationRate
	 *            The mutation rate.
	 * @param numTrysToFindOldLink
	 *            The number of tries to find a link to split.
	 */
	void addNeuron(final double mutationRate, final int numTrysToFindOldLink) {

		// should we add a neuron?
		if (Math.random() > mutationRate) {
			return;
		}

		int countTrysToFindOldLink = numTrysToFindOldLink;

		// the link to split
		NEATLinkGene splitLink = null;

		final int sizeBias = this.inputCount + this.outputCount + 10;

		// if there are not at least
		int upperLimit;
		if (this.linksChromosome.size() < sizeBias) {
			upperLimit = getNumGenes() - 1 - (int) Math.sqrt(getNumGenes());
		} else {
			upperLimit = getNumGenes() - 1;
		}

		while ((countTrysToFindOldLink--) > 0) {
			// choose a link, use the square root to prefer the older links
			final int i = RangeRandomizer.randomInt(0, upperLimit);
			final NEATLinkGene link = (NEATLinkGene) this.linksChromosome
					.get(i);

			// get the from neuron
			final long fromNeuron = link.getFromNeuronID();

			if ((link.isEnabled())
					&& (!link.isRecurrent())
					&& (((NEATNeuronGene) getNeurons().get(
							getElementPos(fromNeuron))).getNeuronType()
							!= NEATNeuronType.Bias)) {
				splitLink = link;
				break;
			}
		}

		if (splitLink == null) {
			return;
		}

		splitLink.setEnabled(false);

		final double originalWeight = splitLink.getWeight();

		final long from = splitLink.getFromNeuronID();
		final long to = splitLink.getToNeuronID();

		final NEATNeuronGene fromGene = (NEATNeuronGene) getNeurons().get(
				getElementPos(from));
		final NEATNeuronGene toGene = (NEATNeuronGene) getNeurons().get(
				getElementPos(to));

		final double newDepth = (fromGene.getSplitY() + toGene.getSplitY()) / 2;
		final double newWidth = (fromGene.getSplitX() + toGene.getSplitX()) / 2;

		// has this innovation already been tried?
		NEATInnovation innovation = ((NEATTraining) getGeneticAlgorithm())
				.getInnovations().checkInnovation(from, to,
						NEATInnovationType.NewNeuron);

		validate();
		// prevent chaining
		if (innovation != null) {
			final long neuronID = innovation.getNeuronID();

			if (alreadyHaveThisNeuronID(neuronID)) {
				innovation = null;
			}
		}

		if (innovation == null) {
			
			// this innovation has not been tried, create it
			final long newNeuronID = ((NEATTraining) getGeneticAlgorithm())
					.getInnovations().createNewInnovation(from, to,
							NEATInnovationType.NewNeuron,
							NEATNeuronType.Hidden, newWidth, newDepth);

			this.neuronsChromosome.add(new NEATNeuronGene(
					NEATNeuronType.Hidden, newNeuronID, newDepth, newWidth));

			// add the first link
			final long link1ID = ((NEATTraining) getGeneticAlgorithm())
					.getPopulation().assignInnovationID();

			((NEATTraining) getGeneticAlgorithm()).getInnovations()
					.createNewInnovation(from, newNeuronID,
							NEATInnovationType.NewLink);

			final NEATLinkGene link1 = new NEATLinkGene(from, newNeuronID,
					true, link1ID, 1.0, false);

			validate();
			this.linksChromosome.add(link1);
			validate();

			// add the second link
			final long link2ID = ((NEATTraining) getGeneticAlgorithm())
					.getPopulation().assignInnovationID();

			((NEATTraining) getGeneticAlgorithm()).getInnovations()
					.createNewInnovation(newNeuronID, to,
							NEATInnovationType.NewLink);

			final NEATLinkGene link2 = new NEATLinkGene(newNeuronID, to, true,
					link2ID, originalWeight, false);

			validate();
			this.linksChromosome.add(link2);
			validate();
		}

		else {
			// existing innovation
			final long newNeuronID = innovation.getNeuronID();

			final NEATInnovation innovationLink1
			     = ((NEATTraining) getGeneticAlgorithm())
					.getInnovations().checkInnovation(from, newNeuronID,
							NEATInnovationType.NewLink);
			final NEATInnovation innovationLink2
			     = ((NEATTraining) getGeneticAlgorithm())
					.getInnovations().checkInnovation(newNeuronID, to,
							NEATInnovationType.NewLink);

			if ((innovationLink1 == null) || (innovationLink2 == null)) {
				throw new NeuralNetworkError("NEAT Error");
			}

			final NEATLinkGene link1 = new NEATLinkGene(from, newNeuronID,
					true, innovationLink1.getInnovationID(), 1.0, false);
			final NEATLinkGene link2 = new NEATLinkGene(newNeuronID, to, true,
					innovationLink2.getInnovationID(), originalWeight, false);

			this.linksChromosome.add(link1);
			this.linksChromosome.add(link2);

			final NEATNeuronGene newNeuron = new NEATNeuronGene(
					NEATNeuronType.Hidden, newNeuronID, newDepth, newWidth);

			validate();
			this.neuronsChromosome.add(newNeuron);
			validate();
		}
		
		return;
	}

	/**
	 * Do we already have this neuron id?
	 *
	 * @param id
	 *            The id to check for.
	 * @return True if we already have this neuron id.
	 */
	public boolean alreadyHaveThisNeuronID(final long id) {
		for (final Gene gene : this.neuronsChromosome.getGenes()) {

			final NEATNeuronGene neuronGene = (NEATNeuronGene) gene;

			if (neuronGene.getId() == id) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Choose a random neuron.
	 *
	 * @param includeInput
	 *            Should the input and bias neurons be included.
	 * @return The random neuron.
	 */
	private NEATNeuronGene chooseRandomNeuron(final boolean includeInput) {
		int start;

		if (includeInput) {
			start = 0;
		} else {
			start = this.inputCount + 1;
		}

		final int neuronPos = RangeRandomizer.randomInt(start, getNeurons()
				.size() - 1);
		final NEATNeuronGene neuronGene
			= (NEATNeuronGene) this.neuronsChromosome
				.get(neuronPos);
		return neuronGene;

	}

	/**
	 * Convert the genes to an actual network.
	 */
	public void decode() {
		validate();
		NEATPopulation pop = (NEATPopulation)this.getPopulation();
		
		final List<NEATNeuron> neurons = new ArrayList<NEATNeuron>();

		for (final Gene gene : getNeurons().getGenes()) {
			final NEATNeuronGene neuronGene = (NEATNeuronGene) gene;
			final NEATNeuron neuron = new NEATNeuron(
					neuronGene.getNeuronType(), neuronGene.getId(), neuronGene
							.getSplitY(), neuronGene.getSplitX());

			neurons.add(neuron);
		}

		// now to create the links.
		for (final Gene gene : getLinks().getGenes()) {
			final NEATLinkGene linkGene = (NEATLinkGene) gene;
			if (linkGene.isEnabled()) {
				int element = getElementPos(linkGene.getFromNeuronID());
				final NEATNeuron fromNeuron = neurons.get(element);

				element = getElementPos(linkGene.getToNeuronID());
				if( element==-1 ) {
					System.out.println("test");
				}
				final NEATNeuron toNeuron = neurons.get(element);

				final NEATLink link = new NEATLink(linkGene.getWeight(),
						fromNeuron, toNeuron, linkGene.isRecurrent());

				fromNeuron.getOutputboundLinks().add(link);
				toNeuron.getInboundLinks().add(link);

			}
		}

		NEATNetwork network = new NEATNetwork(inputCount, 
				outputCount,
				neurons,
				pop.getNeatActivationFunction(), 
				0);
		
		
		network.setActivationCycles(pop.getActivationCycles());		
		setOrganism(network);		
	}

	/**
	 * Convert the network to genes. Not currently supported.
	 */
	public void encode() {

	}

	/**
	 * Get the compatibility score with another genome. Used to determine
	 * species.
	 *
	 * @param genome
	 *            The other genome.
	 * @return The score.
	 */
	public double getCompatibilityScore(final NEATGenome genome) {
		double numDisjoint = 0;
		double numExcess = 0;
		double numMatched = 0;
		double weightDifference = 0;

		int g1 = 0;
		int g2 = 0;

		while ((g1 < this.linksChromosome.size() - 1)
				|| (g2 < this.linksChromosome.size() - 1)) {

			if (g1 == this.linksChromosome.size() - 1) {
				g2++;
				numExcess++;

				continue;
			}

			if (g2 == genome.getLinks().size() - 1) {
				g1++;
				numExcess++;

				continue;
			}

			// get innovation numbers for each gene at this point
			final long id1 = ((NEATLinkGene) this.linksChromosome.get(g1))
					.getInnovationId();
			final long id2 = ((NEATLinkGene) genome.getLinks().get(g2))
					.getInnovationId();

			// innovation numbers are identical so increase the matched score
			if (id1 == id2) {
				g1++;
				g2++;
				numMatched++;

				// get the weight difference between these two genes
				weightDifference += Math
						.abs(((NEATLinkGene) this.linksChromosome.get(g1))
								.getWeight()
								- ((NEATLinkGene) genome.getLinks().get(g2))
										.getWeight());
			}

			// innovation numbers are different so increment the disjoint score
			if (id1 < id2) {
				numDisjoint++;
				g1++;
			}

			if (id1 > id2) {
				++numDisjoint;
				++g2;
			}

		}

		int longest = genome.getNumGenes();

		if (getNumGenes() > longest) {
			longest = getNumGenes();
		}

		final double score = (NEATGenome.TWEAK_EXCESS * numExcess / longest)
				+ (NEATGenome.TWEAK_DISJOINT * numDisjoint / longest)
				+ (NEATGenome.TWEAK_MATCHED * weightDifference / numMatched);

		return score;
	}

	/**
	 * Get the specified neuron's index.
	 *
	 * @param neuronID
	 *            The neuron id to check for.
	 * @return The index.
	 */
	private int getElementPos(final long neuronID) {

		for (int i = 0; i < getNeurons().size(); i++) {
			final NEATNeuronGene neuronGene = (NEATNeuronGene) this.neuronsChromosome
					.getGene(i);
			if (neuronGene.getId() == neuronID) {
				return i;
			}
		}

		return -1;
	}

	/**
	 * @return The number of input neurons.
	 */
	public int getInputCount() {
		return this.inputCount;
	}

	/**
	 * @return THe links chromosome.
	 */
	public Chromosome getLinks() {
		return this.linksChromosome;
	}

	/**
	 * @return The network depth.
	 */
	public int getNetworkDepth() {
		return this.networkDepth;
	}

	/**
	 * @return The neurons chromosome.
	 */
	public Chromosome getNeurons() {
		return this.neuronsChromosome;
	}

	/**
	 * @return The number of genes in the links chromosome.
	 */
	public int getNumGenes() {
		return this.linksChromosome.size();
	}

	/**
	 * @return The output count.
	 */
	public int getOutputCount() {
		return this.outputCount;
	}

	/**
	 * @return The species ID.
	 */
	public long getSpeciesID() {
		return this.speciesID;
	}

	/**
	 * Get the specified split y.
	 *
	 * @param nd
	 *            The neuron.
	 * @return The split y.
	 */
	public double getSplitY(final int nd) {
		return ((NEATNeuronGene) this.neuronsChromosome.get(nd)).getSplitY();
	}

	/**
	 * Determine if this is a duplicate link.
	 *
	 * @param fromNeuronID
	 *            The from neuron id.
	 * @param toNeuronID
	 *            The to neuron id.
	 * @return True if this is a duplicate link.
	 */
	public boolean isDuplicateLink(final long fromNeuronID,
			final long toNeuronID) {
		for (final Gene gene : getLinks().getGenes()) {
			final NEATLinkGene linkGene = (NEATLinkGene) gene;
			if ((linkGene.getFromNeuronID() == fromNeuronID)
					&& (linkGene.getToNeuronID() == toNeuronID)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Mutate the activation response.
	 *
	 * @param mutateRate
	 *            The mutation rate.
	 * @param maxPertubation
	 *            The maximum to perturb it by.
	 */
	public void mutateActivationResponse(final double mutateRate,
			final double maxPertubation) {
		for (final Gene gene : this.neuronsChromosome.getGenes()) {
			if (Math.random() < mutateRate) {
				final NEATNeuronGene neuronGene = (NEATNeuronGene) gene;
				neuronGene.setActivationResponse(neuronGene
						.getActivationResponse()
						+ RangeRandomizer.randomize(-1, 1) * maxPertubation);
			}
		}
	}

	/**
	 * Mutate the weights.
	 *
	 * @param mutateRate
	 *            The mutation rate.
	 * @param probNewMutate
	 *            The probability of a whole new weight.
	 * @param maxPertubation
	 *            The max perturbation.
	 */
	public void mutateWeights(final double mutateRate,
			final double probNewMutate, final double maxPertubation) {
		for (final Gene gene : this.linksChromosome.getGenes()) {
			final NEATLinkGene linkGene = (NEATLinkGene) gene;
			if (Math.random() < mutateRate) {
				if (Math.random() < probNewMutate) {
					linkGene.setWeight(RangeRandomizer.randomize(-1, 1));
				} else {
					linkGene
							.setWeight(linkGene.getWeight()
									+ RangeRandomizer.randomize(-1, 1)
									* maxPertubation);
				}
			}
		}
	}

	/**
	 * @param networkDepth
	 *            the networkDepth to set
	 */
	public void setNetworkDepth(final int networkDepth) {
		this.networkDepth = networkDepth;
	}

	/**
	 * Set the species id.
	 *
	 * @param species
	 *            The species id.
	 */
	public void setSpeciesID(final long species) {
		this.speciesID = species;
	}

	/**
	 * Sort the genes.
	 */
	public void sortGenes() {
		Collections.sort(this.linksChromosome.getGenes());
	}

	/**
	 * @return the linksChromosome
	 */
	public Chromosome getLinksChromosome() {
		return linksChromosome;
	}

	/**
	 * @param linksChromosome the linksChromosome to set
	 */
	public void setLinksChromosome(Chromosome linksChromosome) {
		this.linksChromosome = linksChromosome;
	}

	/**
	 * @return the neuronsChromosome
	 */
	public Chromosome getNeuronsChromosome() {
		return neuronsChromosome;
	}

	/**
	 * @param neuronsChromosome the neuronsChromosome to set
	 */
	public void setNeuronsChromosome(Chromosome neuronsChromosome) {
		this.neuronsChromosome = neuronsChromosome;
	}

	/**
	 * @param inputCount the inputCount to set
	 */
	public void setInputCount(int inputCount) {
		this.inputCount = inputCount;
	}

	/**
	 * @param outputCount the outputCount to set
	 */
	public void setOutputCount(int outputCount) {
		this.outputCount = outputCount;
	}	
	
	public void validate() {
		
		// make sure all input neurons are at the beginning
		for(int i=0;i<this.inputCount;i++) {
			NEATNeuronGene gene = (NEATNeuronGene)this.neuronsChromosome.getGene(i);
			if( gene.getNeuronType()!=NEATNeuronType.Input ) {
				throw new EncogError("NEAT Neuron Gene " + i + " should be an input gene.");
			}
		}
		
		// make sure that the bias neuron is where it should be
		NEATNeuronGene g = (NEATNeuronGene)this.neuronsChromosome.getGene(this.inputCount);
		if( g.getNeuronType()!=NEATNeuronType.Bias ) {
			throw new EncogError("NEAT Neuron Gene " + this.inputCount + " should be a bias gene.");
		}
		
		// make sure that all input neurons are connected
		for(int i=0;i<this.inputCount;i++) {
			NEATNeuronGene gene = (NEATNeuronGene)this.neuronsChromosome.getGene(i);
			
			boolean found = false;
			for(Gene lg: this.linksChromosome.getGenes()) {
				NEATLinkGene nlg = (NEATLinkGene)lg;
				if( nlg.getFromNeuronID()==gene.getId()) {
					found = true;
					break;
				}
			}
			
			if( !found ) {
				throw new EncogError("Input neuron " + i + " is unconnected.");
			}			
		}
		
		// make sure that there are no double links
		Map<String,NEATLinkGene> map = new HashMap<String,NEATLinkGene>();
		for(Gene lg: this.linksChromosome.getGenes()) {
			NEATLinkGene nlg = (NEATLinkGene)lg;
			String key = nlg.getFromNeuronID() + "->" + nlg.getToNeuronID();
			if(map.containsKey(key)) {
				throw new EncogError("Double link found: " + key);
			} 
			map.put(key, nlg);
		}
	}
}
