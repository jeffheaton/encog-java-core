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
import org.encog.neural.activation.ActivationStep;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.synapse.neat.NEATLink;
import org.encog.neural.networks.synapse.neat.NEATNeuron;
import org.encog.neural.networks.synapse.neat.NEATNeuronType;
import org.encog.neural.networks.synapse.neat.NEATSynapse;
import org.encog.solve.genetic.BasicGenome;
import org.encog.solve.genetic.Chromosome;
import org.encog.solve.genetic.genes.Gene;

public class NEATGenome extends BasicGenome implements Cloneable {

	public static final double TWEAK_DISJOINT = 1;
	public static final double TWEAK_EXCESS = 1;
	public static final double TWEAK_MATCHED = 0.4;

	private int genomeID;
	private Chromosome neuronsChromosome;
	private Chromosome linksChromosome;
	private BasicNetwork network;
	private int networkDepth;
	private double adjustedScore;
	private double amountToSpawn;
	private final int inputCount;
	private final int outputCount;
	private int speciesID;
	private final NEATTraining training;

	public NEATGenome(NEATTraining training, int id, int inputCount, int outputCount) {
		super(training);
		this.network = null;
		this.genomeID = id;
		this.adjustedScore = 0;
		this.inputCount = inputCount;
		this.outputCount = outputCount;
		this.amountToSpawn = 0;
		this.speciesID = 0;
		this.training = training;
		
		double inputRowSlice = 0.8 / (double) (inputCount);
		this.neuronsChromosome = new Chromosome();
		this.linksChromosome = new Chromosome();
		
		for (int i = 0; i < inputCount; i++) {
			neuronsChromosome.add(new NEATNeuronGene(NEATNeuronType.Input, i, 0, 0.1 + i
					* inputRowSlice));
		}

		neuronsChromosome.add(new NEATNeuronGene(NEATNeuronType.Bias, inputCount, 0, 0.9));

		double outputRowSlice = 1 / (double) (outputCount + 1);

		for (int i = 0; i < outputCount; i++) {
			neuronsChromosome.add(new NEATNeuronGene(NEATNeuronType.Output, i
					+ inputCount + 1, 1, (i + 1) * outputRowSlice));
		}

		for (int i = 0; i < inputCount + 1; i++) {
			for (int j = 0; j < outputCount; j++) {
				this.linksChromosome.add(new NEATLinkGene(((NEATNeuronGene)this.neuronsChromosome.get(i)).getId(),
						((NEATNeuronGene)this.getNeurons().get(inputCount + j + 1)).getId(),
						true, inputCount + outputCount + 1 + getNumGenes(),
						RangeRandomizer.randomize(-1, 1), false));
			}
		}

	}

	public NEATGenome(NEATTraining training,int genomeID, Chromosome neurons,
			Chromosome links, int inputCount, int outputCount) {
		super(training);
		this.genomeID = genomeID;
		this.network = null;
		this.linksChromosome = links;
		this.neuronsChromosome = neurons;
		this.amountToSpawn = 0;
		this.adjustedScore = 0;
		this.inputCount = inputCount;
		this.outputCount = outputCount;
		this.training = training;
	}
	
	public NEATGenome(NEATGenome other) {
		super(other.training);
		
		this.neuronsChromosome = new Chromosome();
		this.linksChromosome = new Chromosome();
		
		this.genomeID = other.genomeID;
		this.network = other.network;
		this.networkDepth = other.networkDepth;
		this.setScore(other.getScore());
		this.adjustedScore=other.adjustedScore;
		this.amountToSpawn=other.amountToSpawn;
		this.inputCount=other.inputCount;
		this.outputCount=other.outputCount;
		this.speciesID=other.speciesID;
		this.training=other.training;
		
		// copy neurons
		for(Gene gene: other.getNeurons().getGenes())
		{
			NEATNeuronGene oldGene = (NEATNeuronGene)gene;
			NEATNeuronGene newGene = new NEATNeuronGene(
				oldGene.getNeuronType(),
				oldGene.getId(),
				oldGene.getSplitX(),
				oldGene.getSplitY(),
				oldGene.isRecurrent(),
		        oldGene.getActivationResponse());
			this.getNeurons().add(newGene);
		}
		
		// copy links
		for(Gene gene: other.getLinks().getGenes() )
		{
			NEATLinkGene oldGene = (NEATLinkGene)gene;
			NEATLinkGene newGene = new NEATLinkGene( 
				oldGene.getFromNeuronID(), 
				oldGene.getToNeuronID(), 
				oldGene.isEnabled(), 
				oldGene.getInnovationID(), 
				oldGene.getWeight(),
				oldGene.isRecurrent());
			this.getLinks().add(newGene);
		}
		
	}

	void randomize() {
		for (Gene link : this.getLinks().getGenes()) {
			((NEATLinkGene)link).setWeight(RangeRandomizer.randomize(-1, 1));
		}
	}

	public BasicNetwork createNetwork() {

		List<NEATNeuron> neurons = new ArrayList<NEATNeuron>();

		for (Gene gene : this.getNeurons().getGenes()) {
			NEATNeuronGene neuronGene = (NEATNeuronGene)gene;
			NEATNeuron neuron = new NEATNeuron(
					neuronGene.getNeuronType(),
					neuronGene.getId(), 
					neuronGene.getSplitY(), 
					neuronGene.getSplitX(), 
					neuronGene.getActivationResponse());

			neurons.add(neuron);
		}

		// now to create the links.
		for (Gene gene : this.getLinks().getGenes()) {
			NEATLinkGene linkGene = (NEATLinkGene)gene;
			if (linkGene.isEnabled()) {
				int element = getElementPos(linkGene.getFromNeuronID());
				NEATNeuron fromNeuron = neurons.get(element);

				element = getElementPos(linkGene.getToNeuronID());
				NEATNeuron toNeuron = neurons.get(element);

				NEATLink link = new NEATLink(linkGene.getWeight(), fromNeuron,
						toNeuron, linkGene.isRecurrent());

				fromNeuron.getOutputboundLinks().add(link);
				toNeuron.getInboundLinks().add(link);

			}
		}

		BasicLayer inputLayer = new BasicLayer(new ActivationLinear(),false,this.inputCount);
		BasicLayer outputLayer = new BasicLayer(this.training.getOutputActivationFunction(),false,this.outputCount);
		NEATSynapse synapse = new NEATSynapse(inputLayer, outputLayer, neurons,this.training.getNeatActivationFunction(),
				this.networkDepth);
		inputLayer.addSynapse(synapse);
		this.network = new BasicNetwork();
		this.network.tagLayer(BasicNetwork.TAG_INPUT, inputLayer);
		this.network.tagLayer(BasicNetwork.TAG_OUTPUT, outputLayer);
		this.network.getStructure().finalizeStructure();

		return this.network;
	}

	private int getElementPos(int neuronID) {

		for (int i = 0; i < this.getNeurons().size(); i++) {
			NEATNeuronGene neuronGene = (NEATNeuronGene)this.neuronsChromosome.getGene(i);
			if (neuronGene.getId() == neuronID)
				return i;
		}

		return -1;
	}

	public boolean isDuplicateLink(int fromNeuronID, int toNeuronID) {
		for (Gene gene : this.getLinks().getGenes()) {
			NEATLinkGene linkGene = (NEATLinkGene)gene;
			if ((linkGene.getFromNeuronID() == fromNeuronID)
					&& (linkGene.getToNeuronID() == toNeuronID))
				return true;
		}

		return false;
	}

	private NEATNeuronGene chooseRandomNeuron(boolean includeInput) {
		int start;

		if (includeInput)
			start = 0;
		else
			start = this.inputCount + 1;

		int neuronPos = (int) RangeRandomizer.randomInt(start, this.getNeurons()
				.size()-1);
		NEATNeuronGene neuronGene = (NEATNeuronGene)this.neuronsChromosome.get(neuronPos);
		return neuronGene;

	}

	void addLink(double mutationRate, double chanceOfLooped,
			int numTrysToFindLoop,
			int numTrysToAddLink) {

		// should we even add the link
		if (Math.random() > mutationRate)
			return;

		// the link will be between these two neurons
		int neuron1ID = -1;
		int neuron2ID = -1;

		boolean recurrent = false;

		// a self-connected loop?
		if (Math.random() < chanceOfLooped) {
			
			// try to find(randomly) a neuron to add a self-connected link to
			while ((numTrysToFindLoop--) > 0) {
				NEATNeuronGene neuronGene = chooseRandomNeuron(false);

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
				NEATNeuronGene neuron1 = chooseRandomNeuron(true); 
				NEATNeuronGene neuron2 = chooseRandomNeuron(false);

				if ( !this.isDuplicateLink(neuron1ID, neuron2ID)
					&& (neuron1.getId() != neuron2.getId())
					&& neuron2.getNeuronType()!=NEATNeuronType.Bias) {

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
		NEATInnovation innovation = this.training.getInnovations().checkInnovation(neuron1ID,
				neuron1ID, NEATInnovationType.NewLink);

		// see if this is a recurrent(backwards) link
		NEATNeuronGene neuronGene = (NEATNeuronGene)neuronsChromosome.get(getElementPos(neuron1ID));
		if (neuronGene.getSplitY() > neuronGene.getSplitY()) {
			recurrent = true;
		}

		// is this a new innovation?
		if (innovation==null) {
			// new innovation
			this.training.getInnovations().createNewInnovation(neuron1ID,
					neuron2ID, NEATInnovationType.NewLink);

			int id2 = this.training.getInnovations().assignInnovationNumber();

			NEATLinkGene linkGene = new NEATLinkGene(neuron1ID, neuron2ID,
					true, id2, RangeRandomizer.randomize(-1, 1), recurrent);
			this.linksChromosome.add(linkGene);
		} else {
			// existing innovation
			NEATLinkGene linkGene = new NEATLinkGene(neuron1ID, neuron2ID,
					true, innovation.getInnovationID(), RangeRandomizer.randomize(-1, 1), recurrent);
			this.linksChromosome.add(linkGene);
		}
	}

	void addNeuron(double mutationRate,
			int numTrysToFindOldLink) {
		
		// should we add a neuron?
		if (Math.random() > mutationRate)
			return;

		// the link to split
		NEATLinkGene splitLink = null;

		int sizeThreshold = this.inputCount + this.outputCount + 10;
		
		// if there are not at least 
		int upperLimit;
		if (this.linksChromosome.size()<sizeThreshold) {
			upperLimit = getNumGenes() - 1 - (int) Math.sqrt(getNumGenes());
		}
		else {
			upperLimit = getNumGenes()-1;
		}
					
		while ((numTrysToFindOldLink--) > 0) {
			// choose a link, use the square root to prefer the older links
			int i = (int) RangeRandomizer.randomInt(0, upperLimit);
			NEATLinkGene link = (NEATLinkGene)this.linksChromosome.get(i);

			// get the from neuron
			int fromNeuron = link.getFromNeuronID();

			if ((link.isEnabled())
					&& (!link.isRecurrent())
					&& (((NEATNeuronGene)this.getNeurons().get(getElementPos(fromNeuron)))
							.getNeuronType() != NEATNeuronType.Bias)) {
				splitLink = link;
				break;
			}
		}

		if( splitLink==null)
			return;
		
		splitLink.setEnabled(false);

		double originalWeight = splitLink.getWeight();

		int from = splitLink.getFromNeuronID();
		int to = splitLink.getToNeuronID();
		
		NEATNeuronGene fromGene = (NEATNeuronGene)this.getNeurons().get(getElementPos(from));
		NEATNeuronGene toGene = (NEATNeuronGene)this.getNeurons().get(getElementPos(to));

		double newDepth = (fromGene.getSplitY() + toGene.getSplitY()) / 2;
		double newWidth = (fromGene.getSplitX() + toGene.getSplitX()) / 2;

		// has this innovation already been tried?
		NEATInnovation innovation = this.training.getInnovations().checkInnovation(from, to,
				NEATInnovationType.NewNeuron);

		// prevent chaining
		if (innovation!=null) {
			int neuronID = innovation.getNeuronID();

			if (alreadyHaveThisNeuronID(neuronID)) {
				innovation = null;
			}
		}
		
		
		if (innovation==null) {
			// this innovation has not been tried, create it
			int newNeuronID = this.training.getInnovations()
					.createNewInnovation(from, to,
							NEATInnovationType.NewNeuron,
							NEATNeuronType.Hidden, newWidth, newDepth);

			this.neuronsChromosome.add(new NEATNeuronGene(NEATNeuronType.Hidden,
					newNeuronID, newDepth, newWidth));

			// add the first link
			int link1ID = this.training.getInnovations()
					.assignInnovationNumber();

			this.training.getInnovations().createNewInnovation(from,
					newNeuronID, NEATInnovationType.NewLink);

			NEATLinkGene link1 = new NEATLinkGene(from, newNeuronID, true,
					link1ID, 1.0, false);

			this.linksChromosome.add(link1);

			// add the second link
			int link2ID = this.training.getInnovations()
					.assignInnovationNumber();

			this.training.getInnovations().createNewInnovation(newNeuronID, to,
					NEATInnovationType.NewLink);

			NEATLinkGene link2 = new NEATLinkGene(newNeuronID, to, true,
					link2ID, originalWeight, false);

			this.linksChromosome.add(link2);
		}

		else {
			// existing innovation
			int newNeuronID = innovation.getNeuronID();

			NEATInnovation innovationLink1 = this.training.getInnovations().checkInnovation(from,
					newNeuronID, NEATInnovationType.NewLink);
			NEATInnovation innovationLink2 = this.training.getInnovations().checkInnovation(
					newNeuronID, to, NEATInnovationType.NewLink);

			if ((innovationLink1==null) || (innovationLink2==null)) {
				throw new NeuralNetworkError("NEAT Error");
			}

			NEATLinkGene link1 = new NEATLinkGene(from, newNeuronID, true,
					innovationLink1.getInnovationID(), 1.0, false);
			NEATLinkGene link2 = new NEATLinkGene(newNeuronID, to, true,
					innovationLink2.getInnovationID(), originalWeight, false);

			this.linksChromosome.add(link1);
			this.linksChromosome.add(link2);

			NEATNeuronGene newNeuron = new NEATNeuronGene(
					NEATNeuronType.Hidden, newNeuronID, newDepth, newWidth);

			this.neuronsChromosome.add(newNeuron);
		}

		return;
	}

	public boolean alreadyHaveThisNeuronID(int id) {
		for (Gene gene : this.neuronsChromosome.getGenes()) {
			
			NEATNeuronGene neuronGene = (NEATNeuronGene)gene;
			
			if (neuronGene.getId() == id)
				return true;
		}

		return false;
	}

	void mutateWeights(double mutateRate, double probNewMutate,
			double maxPertubation) {
		for (Gene gene : this.linksChromosome.getGenes()) {
			NEATLinkGene linkGene = (NEATLinkGene)gene;
			if (Math.random() < mutateRate) {
				if (Math.random() < probNewMutate) {
					linkGene.setWeight(RangeRandomizer.randomize(-1, 1));
				} else {
					linkGene.setWeight( linkGene.getWeight() +
							RangeRandomizer.randomize(-1, 1)
							* maxPertubation);
				}
			}
		}
	}

	void mutateActivationResponse(double mutateRate, double maxPertubation) {
		for (Gene gene : this.neuronsChromosome.getGenes()) {
			if (Math.random() < mutateRate) {
				NEATNeuronGene neuronGene = (NEATNeuronGene)gene;
				neuronGene.setActivationResponse(neuronGene
						.getActivationResponse()
						+ RangeRandomizer.randomize(-1, 1) * maxPertubation);
			}
		}
	}

	double getCompatibilityScore(NEATGenome genome) {
		double numDisjoint = 0;
		double numExcess = 0;
		double numMatched = 0;
		double weightDifference = 0;

		int g1 = 0;
		int g2 = 0;

		while ((g1 < this.linksChromosome.size() - 1) || (g2 < this.linksChromosome.size() - 1)) {

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
			int id1 = ((NEATLinkGene)this.linksChromosome.get(g1)).getInnovationID();
			int id2 = ((NEATLinkGene)genome.getLinks().get(g2)).getInnovationID();

			// innovation numbers are identical so increase the matched score
			if (id1 == id2) {
				g1++;
				g2++;
				numMatched++;

				// get the weight difference between these two genes
				weightDifference += Math.abs(
						((NEATLinkGene)this.linksChromosome.get(g1)).getWeight()
						- ((NEATLinkGene)genome.getLinks().get(g2)).getWeight());
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

		double score = (NEATGenome.TWEAK_EXCESS * numExcess / (double) longest)
				+ (NEATGenome.TWEAK_DISJOINT * numDisjoint / (double) longest)
				+ (NEATGenome.TWEAK_MATCHED * weightDifference / numMatched);

		return score;
	}

	public int getNumGenes() {
		return this.linksChromosome.size();
	}

	public int getGenomeID() {
		return genomeID;
	}

	public Chromosome getNeurons() {
		return this.neuronsChromosome;
	}

	public Chromosome getLinks() {
		return this.linksChromosome;
	}

	public BasicNetwork getNetwork() {
		return network;
	}

	public int getNetworkDepth() {
		return networkDepth;
	}

	public double getAdjustedFitness() {
		return adjustedScore;
	}

	public double getAmountToSpawn() {
		return amountToSpawn;
	}

	public int getSpeciesID() {
		return speciesID;
	}

	public int getInputCount() {
		return inputCount;
	}

	public int getOutputCount() {
		return outputCount;
	}

	public void setSpeciesID(int species) {
		this.speciesID = species;
	}

	public double getSplitY(int nd) {
		return ((NEATNeuronGene)this.neuronsChromosome.get(nd)).getSplitY();
	}

	/**
	 * @param networkDepth the networkDepth to set
	 */
	public void setNetworkDepth(int networkDepth) {
		this.networkDepth = networkDepth;
	}

	public void setAmountToSpan(double toSpawn) {
		this.amountToSpawn = toSpawn;
		
	}

	public void deleteNetwork() {
		this.network = null;
	}

	public void setGenomeID(int id) {
		this.genomeID = id;
		
	}

	public void sortGenes() {
		Collections.sort(this.linksChromosome.getGenes());
	}

	public void setAdjustedFitness(double adjustedFitness) {
		this.adjustedScore = adjustedFitness;
		
	}
	
	public String toString()
	{
		StringBuilder result = new StringBuilder();
		result.append("[NEATGenome:");
		result.append(this.genomeID);
		result.append(",fitness=");
		result.append(this.getScore());
		result.append(")");
		return result.toString();
	}

	@Override
	public void calculateScore() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void decode() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void encode() {
		// TODO Auto-generated method stub
		
	}

	

}
