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
package org.encog.neural.neat.training;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.encog.EncogError;
import org.encog.engine.network.activation.ActivationFunction;
import org.encog.mathutil.randomize.RangeRandomizer;
import org.encog.ml.ea.genome.BasicGenome;
import org.encog.ml.ea.genome.Genome;
import org.encog.neural.NeuralNetworkError;
import org.encog.neural.neat.NEATLink;
import org.encog.neural.neat.NEATNetwork;
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
	private final List<NEATLinkGene> linksChromosome = new ArrayList<NEATLinkGene>();

	/**
	 * THe network depth.
	 */
	private int networkDepth;

	/**
	 * The chromosome that holds the neurons.
	 */
	private final List<NEATNeuronGene> neuronsChromosome = new ArrayList<NEATNeuronGene>();

	/**
	 * The number of outputs.
	 */
	private int outputCount;

	/**
	 * The species id.
	 */
	private long speciesID;
	
	/**
	 * The genome id.
	 */
	private long genomeID;
	
	/**
	 * The amount to spawn.
	 */
	private double amountToSpawn;

	/**
	 * Construct a genome by copying another.
	 *
	 * @param other
	 *            The other genome.
	 */
	public NEATGenome(final NEATGenome other) {
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
		for (final NEATNeuronGene oldGene : other.getNeuronsChromosome()) {
			final NEATNeuronGene newGene = new NEATNeuronGene(oldGene
					.getNeuronType(), oldGene.getActivationFunction(), 
					oldGene.getId(), oldGene.getSplitY(),
					oldGene.getSplitX());
			this.neuronsChromosome.add(newGene);
		}

		// copy links
		for (final NEATLinkGene oldGene : other.getLinksChromosome() ) {
			final NEATLinkGene newGene = new NEATLinkGene(oldGene
					.getFromNeuronID(), oldGene.getToNeuronID(), oldGene
					.isEnabled(), oldGene.getInnovationId(), oldGene
					.getWeight(), oldGene.isRecurrent());
			this.linksChromosome.add(newGene);
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
			final List<NEATNeuronGene> neurons, final List<NEATLinkGene> links,
			final int inputCount, final int outputCount) {
		setGenomeID(genomeID);
		setAmountToSpawn(0);
		setAdjustedScore(0);
		this.inputCount = inputCount;
		this.outputCount = outputCount;

		this.linksChromosome.addAll(links);
		this.neuronsChromosome.addAll(neurons);
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
	public NEATGenome(final NEATPopulation pop, final long id,
			final int inputCount, final int outputCount) {
		setGenomeID(id);
		setAdjustedScore(0);
		this.inputCount = inputCount;
		this.outputCount = outputCount;
		setAmountToSpawn(0);
		this.speciesID = 0;

		final double inputRowSlice = 0.8 / (inputCount);
		
		// get the activation function
		ActivationFunction af = pop.getActivationFunctions().pickFirst();
		
		// first bias
		this.neuronsChromosome.add(new NEATNeuronGene(NEATNeuronType.Bias, af, 
				inputCount, 0, 0.9));

		// then inputs

		for (int i = 0; i < inputCount; i++) {
			this.neuronsChromosome.add(new NEATNeuronGene(NEATNeuronType.Input, af, 
					i, 0, 0.1 + i * inputRowSlice));
		}

		// then the other stuff

		final double outputRowSlice = 1 / (double) (outputCount + 1);

		for (int i = 0; i < outputCount; i++) {
			this.neuronsChromosome.add(new NEATNeuronGene(
					NEATNeuronType.Output, af, i + inputCount + 1, 1, (i + 1)
							* outputRowSlice));
		}

		for (int i = 0; i < inputCount + 1; i++) {
			for (int j = 0; j < outputCount; j++) {
				this.linksChromosome.add(new NEATLinkGene(
						((NEATNeuronGene) this.neuronsChromosome.get(i))
								.getId(), ((NEATNeuronGene)this.neuronsChromosome.get(
								inputCount + j + 1)).getId(), true, inputCount
								+ outputCount + 2 + getNumGenes(),
						RangeRandomizer.randomize(-1, 1), false));
			}
		}

	}
	
	public NEATGenome() {
		
	}


	/**
	 * Convert the genes to an actual network.
	 */
	public void decode() {
		NEATPopulation pop = (NEATPopulation)this.getPopulation();
		
		if( ((NEATNeuronGene)this.neuronsChromosome.get(0)).getNeuronType() != NEATNeuronType.Bias ) {
			throw new NeuralNetworkError("The first neuron must be the bias neuron, this genome is invalid.");
		}
		
		NEATLink[] links = new NEATLink[this.linksChromosome.size()];
		ActivationFunction[] afs = new ActivationFunction[this.neuronsChromosome.size()];
		
		for(int i=0;i<afs.length;i++) {
			afs[i] = this.getNeuronsChromosome().get(i).getActivationFunction();
		}
		
		Map<Long,Integer> lookup = new HashMap<Long,Integer>();
		for(int i=0;i<this.neuronsChromosome.size();i++) {
			NEATNeuronGene neuronGene = (NEATNeuronGene)this.neuronsChromosome.get(i);
			lookup.put(neuronGene.getId(), i);
		}
		
		// loop over connections
		for(int i=0;i<this.linksChromosome.size();i++) {
			NEATLinkGene linkGene = (NEATLinkGene)this.linksChromosome.get(i);
			links[i] = new NEATLink(
					lookup.get(linkGene.getFromNeuronID()),
					lookup.get(linkGene.getToNeuronID()),
					linkGene.getWeight());
			
		}
		
		Arrays.sort(links);
		
	    NEATNetwork network = new NEATNetwork(
	    		this.inputCount,
                this.outputCount,
	    		links,	    		
                afs);
		
		
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

			if (g2 == genome.getLinksChromosome().size() - 1) {
				g1++;
				numExcess++;

				continue;
			}

			// get innovation numbers for each gene at this point
			final long id1 = ((NEATLinkGene) this.linksChromosome.get(g1))
					.getInnovationId();
			final long id2 = ((NEATLinkGene) genome.getLinksChromosome().get(g2))
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
								- ((NEATLinkGene) genome.getLinksChromosome().get(g2))
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
	 * @return The number of input neurons.
	 */
	public int getInputCount() {
		return this.inputCount;
	}

	/**
	 * @return The network depth.
	 */
	public int getNetworkDepth() {
		return this.networkDepth;
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
		Collections.sort(this.linksChromosome);
	}

	/**
	 * @return the linksChromosome
	 */
	public List<NEATLinkGene> getLinksChromosome() {
		return this.linksChromosome;
	}

	/**
	 * @return the neuronsChromosome
	 */
	public List<NEATNeuronGene> getNeuronsChromosome() {
		return this.neuronsChromosome;
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
		
		// make sure that the bias neuron is where it should be
		NEATNeuronGene g = (NEATNeuronGene)this.neuronsChromosome.get(0);
		if( g.getNeuronType()!=NEATNeuronType.Bias ) {
			throw new EncogError("NEAT Neuron Gene 0 should be a bias gene.");
		}
		
		// make sure all input neurons are at the beginning
		for(int i=1;i<=this.inputCount;i++) {
			NEATNeuronGene gene = (NEATNeuronGene)this.neuronsChromosome.get(i);
			if( gene.getNeuronType()!=NEATNeuronType.Input ) {
				throw new EncogError("NEAT Neuron Gene " + i + " should be an input gene.");
			}
		}
		
		// make sure that there are no double links
		Map<String,NEATLinkGene> map = new HashMap<String,NEATLinkGene>();
		for(NEATLinkGene nlg: this.linksChromosome) {
			String key = nlg.getFromNeuronID() + "->" + nlg.getToNeuronID();
			if(map.containsKey(key)) {
				throw new EncogError("Double link found: " + key);
			} 
			map.put(key, nlg);
		}
	}
	
	
	

	public long getGenomeID() {
		return this.genomeID;
	}

	/**
	 * Set the genome id.
	 * 
	 * @param theGenomeID
	 *            the genome id.
	 */
	public void setGenomeID(final long theGenomeID) {
		this.genomeID = theGenomeID;
	}
	
	/**
	 * @return The amount this genome will spawn.
	 */
	public double getAmountToSpawn() {
		return this.amountToSpawn;
	}
	
	/**
	 * Set the amount to spawn.
	 * 
	 * @param theAmountToSpawn
	 *            The amount to spawn.
	 */
	public void setAmountToSpawn(final double theAmountToSpawn) {
		this.amountToSpawn = theAmountToSpawn;
	}

	@Override
	public void copy(Genome source) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int size() {
		throw new UnsupportedOperationException();
	}
	
}
