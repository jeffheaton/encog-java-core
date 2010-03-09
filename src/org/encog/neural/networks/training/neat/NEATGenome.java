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

import org.encog.mathutil.randomize.RangeRandomizer;
import org.encog.neural.NeuralNetworkError;
import org.encog.neural.activation.ActivationLinear;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.synapse.neat.NEATLink;
import org.encog.neural.networks.synapse.neat.NEATNeuron;
import org.encog.neural.networks.synapse.neat.NEATNeuronType;
import org.encog.neural.networks.synapse.neat.NEATSynapse;
import org.encog.solve.genetic.genes.Gene;
import org.encog.solve.genetic.genome.BasicGenome;
import org.encog.solve.genetic.genome.Chromosome;

/**
 * Implements a NEAT genome.  This is a "blueprint" for creating a neural network.
 * 
 * NeuroEvolution of Augmenting Topologies (NEAT) is a genetic algorithm for the
 * generation of evolving artificial neural networks. It was developed by Ken
 * Stanley while at The University of Texas at Austin.
 * 
 * http://www.cs.ucf.edu/~kstanley/
 * 
 */
public class NEATGenome extends BasicGenome implements Cloneable {

	public static final double TWEAK_DISJOINT = 1;
	public static final double TWEAK_EXCESS = 1;
	public static final double TWEAK_MATCHED = 0.4;

	private final int inputCount;
	private final Chromosome linksChromosome;
	private int networkDepth;
	private final Chromosome neuronsChromosome;
	private final int outputCount;
	private long speciesID;
	private final NEATTraining training;

	public NEATGenome(final NEATGenome other) {
		super(other.training);

		neuronsChromosome = new Chromosome();
		linksChromosome = new Chromosome();

		setGenomeID(other.getGenomeID());
		networkDepth = other.networkDepth;
		setScore(other.getScore());
		setAdjustedScore(other.getAdjustedScore());
		setAmountToSpawn(other.getAmountToSpawn());
		inputCount = other.inputCount;
		outputCount = other.outputCount;
		speciesID = other.speciesID;
		training = other.training;

		// copy neurons
		for (final Gene gene : other.getNeurons().getGenes()) {
			final NEATNeuronGene oldGene = (NEATNeuronGene) gene;
			final NEATNeuronGene newGene = new NEATNeuronGene(oldGene
					.getNeuronType(), oldGene.getId(), oldGene.getSplitX(),
					oldGene.getSplitY(), oldGene.isRecurrent(), oldGene
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

	public NEATGenome(final NEATTraining training, final long genomeID,
			final Chromosome neurons, final Chromosome links,
			final int inputCount, final int outputCount) {
		super(training);
		setGenomeID(genomeID);
		linksChromosome = links;
		neuronsChromosome = neurons;
		setAmountToSpawn(0);
		setAdjustedScore(0);
		this.inputCount = inputCount;
		this.outputCount = outputCount;
		this.training = training;
	}

	public NEATGenome(final NEATTraining training, final long id,
			final int inputCount, final int outputCount) {
		super(training);
		setGenomeID(id);
		setAdjustedScore(0);
		this.inputCount = inputCount;
		this.outputCount = outputCount;
		setAmountToSpawn(0);
		speciesID = 0;
		this.training = training;

		final double inputRowSlice = 0.8 / (inputCount);
		neuronsChromosome = new Chromosome();
		linksChromosome = new Chromosome();

		for (int i = 0; i < inputCount; i++) {
			neuronsChromosome.add(new NEATNeuronGene(NEATNeuronType.Input, i,
					0, 0.1 + i * inputRowSlice));
		}

		neuronsChromosome.add(new NEATNeuronGene(NEATNeuronType.Bias,
				inputCount, 0, 0.9));

		final double outputRowSlice = 1 / (double) (outputCount + 1);

		for (int i = 0; i < outputCount; i++) {
			neuronsChromosome.add(new NEATNeuronGene(NEATNeuronType.Output, i
					+ inputCount + 1, 1, (i + 1) * outputRowSlice));
		}

		for (int i = 0; i < inputCount + 1; i++) {
			for (int j = 0; j < outputCount; j++) {
				linksChromosome.add(new NEATLinkGene(
						((NEATNeuronGene) neuronsChromosome.get(i)).getId(),
						((NEATNeuronGene) getNeurons().get(inputCount + j + 1))
								.getId(), true, inputCount + outputCount + 1
								+ getNumGenes(), RangeRandomizer.randomize(-1,
								1), false));
			}
		}

	}

	void addLink(final double mutationRate, final double chanceOfLooped,
			int numTrysToFindLoop, int numTrysToAddLink) {

		// should we even add the link
		if (Math.random() > mutationRate) {
			return;
		}

		// the link will be between these two neurons
		int neuron1ID = -1;
		int neuron2ID = -1;

		boolean recurrent = false;

		// a self-connected loop?
		if (Math.random() < chanceOfLooped) {

			// try to find(randomly) a neuron to add a self-connected link to
			while ((numTrysToFindLoop--) > 0) {
				final NEATNeuronGene neuronGene = chooseRandomNeuron(false);

				// no self-links on input or bias neurons
				if (!neuronGene.isRecurrent()
						&& (neuronGene.getNeuronType() != NEATNeuronType.Bias)
						&& (neuronGene.getNeuronType() != NEATNeuronType.Input)) {
					neuron1ID = neuron2ID = neuronGene.getId();

					neuronGene.setRecurrent(true);
					recurrent = true;

					numTrysToFindLoop = 0;
				}
			}
		} else {
			// try to add a regular link
			while ((numTrysToAddLink--) > 0) {
				final NEATNeuronGene neuron1 = chooseRandomNeuron(true);
				final NEATNeuronGene neuron2 = chooseRandomNeuron(false);

				if (!isDuplicateLink(neuron1ID, neuron2ID)
						&& (neuron1.getId() != neuron2.getId())
						&& (neuron2.getNeuronType() != NEATNeuronType.Bias)) {

					neuron1ID = -1;
					neuron2ID = -1;
					break;
				}
			}
		}

		// did we fail to find a link
		if ((neuron1ID < 0) || (neuron2ID < 0)) {
			return;
		}

		// check to see if this innovation has already been tried
		final NEATInnovation innovation = training.getInnovations()
				.checkInnovation(neuron1ID, neuron1ID,
						NEATInnovationType.NewLink);

		// see if this is a recurrent(backwards) link
		final NEATNeuronGene neuronGene = (NEATNeuronGene) neuronsChromosome
				.get(getElementPos(neuron1ID));
		if (neuronGene.getSplitY() > neuronGene.getSplitY()) {
			recurrent = true;
		}

		// is this a new innovation?
		if (innovation == null) {
			// new innovation
			training.getInnovations().createNewInnovation(neuron1ID, neuron2ID,
					NEATInnovationType.NewLink);

			final int id2 = training.getInnovations().assignInnovationNumber();

			final NEATLinkGene linkGene = new NEATLinkGene(neuron1ID,
					neuron2ID, true, id2, RangeRandomizer.randomize(-1, 1),
					recurrent);
			linksChromosome.add(linkGene);
		} else {
			// existing innovation
			final NEATLinkGene linkGene = new NEATLinkGene(neuron1ID,
					neuron2ID, true, innovation.getInnovationID(),
					RangeRandomizer.randomize(-1, 1), recurrent);
			linksChromosome.add(linkGene);
		}
	}

	void addNeuron(final double mutationRate, int numTrysToFindOldLink) {

		// should we add a neuron?
		if (Math.random() > mutationRate) {
			return;
		}

		// the link to split
		NEATLinkGene splitLink = null;

		final int sizeThreshold = inputCount + outputCount + 10;

		// if there are not at least
		int upperLimit;
		if (linksChromosome.size() < sizeThreshold) {
			upperLimit = getNumGenes() - 1 - (int) Math.sqrt(getNumGenes());
		} else {
			upperLimit = getNumGenes() - 1;
		}

		while ((numTrysToFindOldLink--) > 0) {
			// choose a link, use the square root to prefer the older links
			final int i = RangeRandomizer.randomInt(0, upperLimit);
			final NEATLinkGene link = (NEATLinkGene) linksChromosome.get(i);

			// get the from neuron
			final int fromNeuron = link.getFromNeuronID();

			if ((link.isEnabled())
					&& (!link.isRecurrent())
					&& (((NEATNeuronGene) getNeurons().get(
							getElementPos(fromNeuron))).getNeuronType() != NEATNeuronType.Bias)) {
				splitLink = link;
				break;
			}
		}

		if (splitLink == null) {
			return;
		}

		splitLink.setEnabled(false);

		final double originalWeight = splitLink.getWeight();

		final int from = splitLink.getFromNeuronID();
		final int to = splitLink.getToNeuronID();

		final NEATNeuronGene fromGene = (NEATNeuronGene) getNeurons().get(
				getElementPos(from));
		final NEATNeuronGene toGene = (NEATNeuronGene) getNeurons().get(
				getElementPos(to));

		final double newDepth = (fromGene.getSplitY() + toGene.getSplitY()) / 2;
		final double newWidth = (fromGene.getSplitX() + toGene.getSplitX()) / 2;

		// has this innovation already been tried?
		NEATInnovation innovation = training.getInnovations().checkInnovation(
				from, to, NEATInnovationType.NewNeuron);

		// prevent chaining
		if (innovation != null) {
			final int neuronID = innovation.getNeuronID();

			if (alreadyHaveThisNeuronID(neuronID)) {
				innovation = null;
			}
		}

		if (innovation == null) {
			// this innovation has not been tried, create it
			final int newNeuronID = training.getInnovations()
					.createNewInnovation(from, to,
							NEATInnovationType.NewNeuron,
							NEATNeuronType.Hidden, newWidth, newDepth);

			neuronsChromosome.add(new NEATNeuronGene(NEATNeuronType.Hidden,
					newNeuronID, newDepth, newWidth));

			// add the first link
			final int link1ID = training.getInnovations()
					.assignInnovationNumber();

			training.getInnovations().createNewInnovation(from, newNeuronID,
					NEATInnovationType.NewLink);

			final NEATLinkGene link1 = new NEATLinkGene(from, newNeuronID,
					true, link1ID, 1.0, false);

			linksChromosome.add(link1);

			// add the second link
			final int link2ID = training.getInnovations()
					.assignInnovationNumber();

			training.getInnovations().createNewInnovation(newNeuronID, to,
					NEATInnovationType.NewLink);

			final NEATLinkGene link2 = new NEATLinkGene(newNeuronID, to, true,
					link2ID, originalWeight, false);

			linksChromosome.add(link2);
		}

		else {
			// existing innovation
			final int newNeuronID = innovation.getNeuronID();

			final NEATInnovation innovationLink1 = training.getInnovations()
					.checkInnovation(from, newNeuronID,
							NEATInnovationType.NewLink);
			final NEATInnovation innovationLink2 = training.getInnovations()
					.checkInnovation(newNeuronID, to,
							NEATInnovationType.NewLink);

			if ((innovationLink1 == null) || (innovationLink2 == null)) {
				throw new NeuralNetworkError("NEAT Error");
			}

			final NEATLinkGene link1 = new NEATLinkGene(from, newNeuronID,
					true, innovationLink1.getInnovationID(), 1.0, false);
			final NEATLinkGene link2 = new NEATLinkGene(newNeuronID, to, true,
					innovationLink2.getInnovationID(), originalWeight, false);

			linksChromosome.add(link1);
			linksChromosome.add(link2);

			final NEATNeuronGene newNeuron = new NEATNeuronGene(
					NEATNeuronType.Hidden, newNeuronID, newDepth, newWidth);

			neuronsChromosome.add(newNeuron);
		}

		return;
	}

	public boolean alreadyHaveThisNeuronID(final int id) {
		for (final Gene gene : neuronsChromosome.getGenes()) {

			final NEATNeuronGene neuronGene = (NEATNeuronGene) gene;

			if (neuronGene.getId() == id) {
				return true;
			}
		}

		return false;
	}

	private NEATNeuronGene chooseRandomNeuron(final boolean includeInput) {
		int start;

		if (includeInput) {
			start = 0;
		} else {
			start = inputCount + 1;
		}

		final int neuronPos = RangeRandomizer.randomInt(start, getNeurons()
				.size() - 1);
		final NEATNeuronGene neuronGene = (NEATNeuronGene) neuronsChromosome
				.get(neuronPos);
		return neuronGene;

	}

	public void decode() {
		final List<NEATNeuron> neurons = new ArrayList<NEATNeuron>();

		for (final Gene gene : getNeurons().getGenes()) {
			final NEATNeuronGene neuronGene = (NEATNeuronGene) gene;
			final NEATNeuron neuron = new NEATNeuron(
					neuronGene.getNeuronType(), neuronGene.getId(), neuronGene
							.getSplitY(), neuronGene.getSplitX(), neuronGene
							.getActivationResponse());

			neurons.add(neuron);
		}

		// now to create the links.
		for (final Gene gene : getLinks().getGenes()) {
			final NEATLinkGene linkGene = (NEATLinkGene) gene;
			if (linkGene.isEnabled()) {
				int element = getElementPos(linkGene.getFromNeuronID());
				final NEATNeuron fromNeuron = neurons.get(element);

				element = getElementPos(linkGene.getToNeuronID());
				final NEATNeuron toNeuron = neurons.get(element);

				final NEATLink link = new NEATLink(linkGene.getWeight(),
						fromNeuron, toNeuron, linkGene.isRecurrent());

				fromNeuron.getOutputboundLinks().add(link);
				toNeuron.getInboundLinks().add(link);

			}
		}

		final BasicLayer inputLayer = new BasicLayer(new ActivationLinear(),
				false, inputCount);
		final BasicLayer outputLayer = new BasicLayer(training
				.getOutputActivationFunction(), false, outputCount);
		final NEATSynapse synapse = new NEATSynapse(inputLayer, outputLayer,
				neurons, training.getNeatActivationFunction(), networkDepth);
		synapse.setSnapshot(this.training.isSnapshot());
		inputLayer.addSynapse(synapse);
		final BasicNetwork network = new BasicNetwork();
		network.tagLayer(BasicNetwork.TAG_INPUT, inputLayer);
		network.tagLayer(BasicNetwork.TAG_OUTPUT, outputLayer);
		network.getStructure().finalizeStructure();
		setOrganism(network);

	}

	public void encode() {
		// TODO Auto-generated method stub

	}

	double getCompatibilityScore(final NEATGenome genome) {
		double numDisjoint = 0;
		double numExcess = 0;
		double numMatched = 0;
		double weightDifference = 0;

		int g1 = 0;
		int g2 = 0;

		while ((g1 < linksChromosome.size() - 1)
				|| (g2 < linksChromosome.size() - 1)) {

			if (g1 == linksChromosome.size() - 1) {
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
			final long id1 = ((NEATLinkGene) linksChromosome.get(g1))
					.getInnovationId();
			final long id2 = ((NEATLinkGene) genome.getLinks().get(g2))
					.getInnovationId();

			// innovation numbers are identical so increase the matched score
			if (id1 == id2) {
				g1++;
				g2++;
				numMatched++;

				// get the weight difference between these two genes
				weightDifference += Math.abs(((NEATLinkGene) linksChromosome
						.get(g1)).getWeight()
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

	private int getElementPos(final int neuronID) {

		for (int i = 0; i < getNeurons().size(); i++) {
			final NEATNeuronGene neuronGene = (NEATNeuronGene) neuronsChromosome
					.getGene(i);
			if (neuronGene.getId() == neuronID) {
				return i;
			}
		}

		return -1;
	}

	public int getInputCount() {
		return inputCount;
	}

	public Chromosome getLinks() {
		return linksChromosome;
	}

	public int getNetworkDepth() {
		return networkDepth;
	}

	public Chromosome getNeurons() {
		return neuronsChromosome;
	}

	public int getNumGenes() {
		return linksChromosome.size();
	}

	public int getOutputCount() {
		return outputCount;
	}

	public long getSpeciesID() {
		return speciesID;
	}

	public double getSplitY(final int nd) {
		return ((NEATNeuronGene) neuronsChromosome.get(nd)).getSplitY();
	}

	public boolean isDuplicateLink(final int fromNeuronID, final int toNeuronID) {
		for (final Gene gene : getLinks().getGenes()) {
			final NEATLinkGene linkGene = (NEATLinkGene) gene;
			if ((linkGene.getFromNeuronID() == fromNeuronID)
					&& (linkGene.getToNeuronID() == toNeuronID)) {
				return true;
			}
		}

		return false;
	}

	void mutateActivationResponse(final double mutateRate,
			final double maxPertubation) {
		for (final Gene gene : neuronsChromosome.getGenes()) {
			if (Math.random() < mutateRate) {
				final NEATNeuronGene neuronGene = (NEATNeuronGene) gene;
				neuronGene.setActivationResponse(neuronGene
						.getActivationResponse()
						+ RangeRandomizer.randomize(-1, 1) * maxPertubation);
			}
		}
	}

	void mutateWeights(final double mutateRate, final double probNewMutate,
			final double maxPertubation) {
		for (final Gene gene : linksChromosome.getGenes()) {
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

	void randomize() {
		for (final Gene link : getLinks().getGenes()) {
			((NEATLinkGene) link).setWeight(RangeRandomizer.randomize(-1, 1));
		}
	}

	/**
	 * @param networkDepth
	 *            the networkDepth to set
	 */
	public void setNetworkDepth(final int networkDepth) {
		this.networkDepth = networkDepth;
	}

	public void setSpeciesID(final long species) {
		speciesID = species;
	}

	public void sortGenes() {
		Collections.sort(linksChromosome.getGenes());
	}

	@Override
	public String toString() {
		final StringBuilder result = new StringBuilder();
		result.append("[NEATGenome:");
		result.append(getGenomeID());
		result.append(",fitness=");
		result.append(getScore());
		result.append(")");
		return result.toString();
	}

}
