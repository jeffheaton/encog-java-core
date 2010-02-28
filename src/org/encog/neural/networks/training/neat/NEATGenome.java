package org.encog.neural.networks.training.neat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.encog.math.randomize.RangeRandomizer;
import org.encog.neural.NeuralNetworkError;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.synapse.neat.NEATLink;
import org.encog.neural.networks.synapse.neat.NEATNeuron;
import org.encog.neural.networks.synapse.neat.NEATNeuronType;
import org.encog.neural.networks.synapse.neat.NEATSynapse;

public class NEATGenome implements Comparable<NEATGenome> {

	public static final double TWEAK_DISJOINT = 1;
	public static final double TWEAK_EXCESS = 1;
	public static final double TWEAK_MATCHED = 0.4;

	private int genomeID;
	private List<NEATNeuronGene> neurons = new ArrayList<NEATNeuronGene>();
	private List<NEATLinkGene> links = new ArrayList<NEATLinkGene>();
	private BasicNetwork network;
	private int networkDepth;
	private double fitness;
	private double adjustedFitness;
	private double amountToSpawn;
	private final int inputCount;
	private final int outputCount;
	private int speciesID;
	private NEATTraining training;

	public NEATGenome(int id, int inputCount, int outputCount) {
		this.network = null;
		this.genomeID = id;
		this.fitness = 0;
		this.adjustedFitness = 0;
		this.inputCount = inputCount;
		this.outputCount = outputCount;
		this.amountToSpawn = 0;
		this.speciesID = 0;

		double inputRowSlice = 0.8 / (double) (inputCount);

		for (int i = 0; i < inputCount; i++) {
			neurons.add(new NEATNeuronGene(NEATNeuronType.Input, i, 0, 0.1 + i
					* inputRowSlice));
		}

		neurons
				.add(new NEATNeuronGene(NEATNeuronType.Bias, inputCount, 0, 0.9));

		double outputRowSlice = 1 / (double) (outputCount + 1);

		for (int i = 0; i < outputCount; i++) {
			neurons.add(new NEATNeuronGene(NEATNeuronType.Output, i
					+ inputCount + 1, 1, (i + 1) * outputRowSlice));
		}

		for (int i = 0; i < inputCount + 1; i++) {
			for (int j = 0; j < outputCount; j++) {
				this.links.add(new NEATLinkGene(this.neurons.get(i).getId(),
						this.getNeurons().get(inputCount + j + 1).getId(),
						true, inputCount + outputCount + 1 + getNumGenes(),
						RangeRandomizer.randomize(-1, 1), false));
			}
		}

	}

	public NEATGenome(int genomeID, List<NEATNeuronGene> neurons,
			List<NEATLinkGene> links, int inputCount, int outputCount) {
		this.genomeID = genomeID;
		this.network = null;
		this.links = links;
		this.neurons = neurons;
		this.amountToSpawn = 0;
		this.fitness = 0;
		this.adjustedFitness = 0;
		this.inputCount = inputCount;
		this.outputCount = outputCount;
	}

	public NEATGenome(final NEATGenome other) {
		this.genomeID = other.genomeID;
		this.neurons = other.neurons;
		this.links = other.links;
		this.network = null; // no need to perform a deep copy
		this.fitness = other.fitness;
		this.adjustedFitness = other.adjustedFitness;
		this.inputCount = other.inputCount;
		this.outputCount = other.outputCount;
		this.amountToSpawn = other.amountToSpawn;
	}

	void randomize() {
		for (NEATLinkGene link : this.links) {
			link.setWeight(RangeRandomizer.randomize(-1, 1));
		}
	}

	public BasicNetwork createNetwork() {

		List<NEATNeuron> neurons = new ArrayList<NEATNeuron>();

		for (NEATNeuronGene neuronGene : this.neurons) {
			NEATNeuron neuron = new NEATNeuron(neuronGene.getNeuronType(),
					neuronGene.getId(), neuronGene.getSplitY(), neuronGene
							.getSplitX(), neuronGene.getActivationResponse());

			neurons.add(neuron);
		}

		// now to create the links.
		for (NEATLinkGene linkGene : this.links) {
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

		BasicLayer inputLayer = new BasicLayer(this.inputCount);
		BasicLayer outputLayer = new BasicLayer(this.outputCount);
		NEATSynapse synapse = new NEATSynapse(inputLayer, outputLayer, neurons,
				this.networkDepth);
		inputLayer.addSynapse(synapse);
		this.network = new BasicNetwork();
		this.network.tagLayer(BasicNetwork.TAG_INPUT, inputLayer);
		this.network.tagLayer(BasicNetwork.TAG_OUTPUT, outputLayer);
		this.network.getStructure().finalizeStructure();

		return this.network;
	}

	int getElementPos(int neuronID) {
		for (int i = 0; i < this.neurons.size(); i++) {
			NEATNeuronGene neuronGene = this.neurons.get(i);
			if (neuronGene.getId() == neuronID)
				return i;
		}

		return -1;
	}

	public boolean isDuplicateLink(int fromNeuronID, int toNeuronID) {
		for (NEATLinkGene linkGene : this.links) {
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

		int neuronPos = (int) RangeRandomizer.randomize(start, this.neurons
				.size() - 1);
		NEATNeuronGene neuronGene = this.neurons.get(neuronPos);
		return neuronGene;

	}

	void addLink(double mutationRate, double chanceOfLooped,
			int numTrysToFindLoop,
			int numTrysToAddLink) {

		if (Math.random() > mutationRate)
			return;

		int neuron1ID = -1;
		int neuron2ID = -1;

		boolean recurrent = false;

		if (Math.random() < chanceOfLooped) {
			while ((numTrysToFindLoop--) > 0) {
				NEATNeuronGene neuronGene = chooseRandomNeuron(false);

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
			while ((numTrysToAddLink--) > 0) {
				neuron1ID = chooseRandomNeuron(true).getId();
				neuron2ID = chooseRandomNeuron(false).getId();

				if (neuron2ID == 2) {
					continue;
				}

				if (this.isDuplicateLink(neuron1ID, neuron2ID)
						|| (neuron1ID == neuron2ID)) {

					neuron1ID = -1;
					neuron2ID = -1;
				}

				else {
					numTrysToAddLink = 0;
				}
			}
		}

		if ((neuron1ID < 0) || (neuron2ID < 0)) {
			return;
		}

		int id = this.training.getInnovations().checkInnovation(neuron1ID,
				neuron1ID, NEATInnovationType.NewLink);

		if (neurons.get(getElementPos(neuron1ID)).getSplitY() > neurons.get(
				getElementPos(neuron2ID)).getSplitY()) {
			recurrent = true;
		}

		if (id < 0) {
			this.training.getInnovations().createNewInnovation(neuron1ID,
					neuron2ID, NEATInnovationType.NewLink);

			int id2 = this.training.getInnovations().assignInnovationNumber();

			NEATLinkGene linkGene = new NEATLinkGene(neuron1ID, neuron2ID,
					true, id2, RangeRandomizer.randomize(-1, 1), recurrent);
			this.links.add(linkGene);
		} else {
			NEATLinkGene linkGene = new NEATLinkGene(neuron1ID, neuron2ID,
					true, id, RangeRandomizer.randomize(-1, 1), recurrent);
			this.links.add(linkGene);
		}
	}

	void addNeuron(double mutationRate,
			int numTrysToFindOldLink) {
		if (Math.random() > mutationRate)
			return;

		boolean done = false;

		int ChosenLink = 0;

		final int sizeThreshold = this.inputCount + this.outputCount + 10;

		if (this.links.size() < sizeThreshold) {
			while ((numTrysToFindOldLink--) > 0) {
				int chosenLink = (int) RangeRandomizer.randomize(0,
						getNumGenes() - 1 - (int) Math.sqrt(getNumGenes()));

				int FromNeuron = this.links.get(chosenLink).getFromNeuronID();

				if ((this.links.get(ChosenLink).isEnabled())
						&& (!this.links.get(ChosenLink).isRecurrent())
						&& (this.neurons.get(getElementPos(FromNeuron))
								.getNeuronType() != NEATNeuronType.Bias)) {
					done = true;

					numTrysToFindOldLink = 0;
				}
			}

			if (!done) {
				return;
			}
		}

		else {
			while (!done) {
				int chosenLink = (int) RangeRandomizer.randomize(0,
						getNumGenes() - 1);

				int FromNeuron = this.links.get(ChosenLink).getFromNeuronID();

				if ((links.get(ChosenLink).isEnabled())
						&& (!this.links.get(chosenLink).isRecurrent())
						&& (this.neurons.get(getElementPos(FromNeuron))
								.getNeuronType() != NEATNeuronType.Bias)) {
					done = true;
				}
			}
		}

		this.links.get(ChosenLink).setEnabled(false);

		double originalWeight = this.links.get(ChosenLink).getWeight();

		int from = this.links.get(ChosenLink).getFromNeuronID();
		int to = this.links.get(ChosenLink).getToNeuronID();

		double newDepth = (this.neurons.get(getElementPos(from)).getSplitY() + this.neurons
				.get(getElementPos(to)).getSplitY()) / 2;

		double newWidth = (this.neurons.get(getElementPos(from)).getSplitX() + this.neurons
				.get(getElementPos(to)).getSplitX()) / 2;

		int id = this.training.getInnovations().checkInnovation(from, to,
				NEATInnovationType.NewNeuron);

		if (id >= 0) {
			int neuronID = this.training.getInnovations().getNeuronID(id);

			if (alreadyHaveThisNeuronID(neuronID)) {
				id = -1;
			}
		}

		if (id < 0) {
			int newNeuronID = this.training.getInnovations()
					.createNewInnovation(from, to,
							NEATInnovationType.NewNeuron,
							NEATNeuronType.Hidden, newWidth, newDepth);

			this.neurons.add(new NEATNeuronGene(NEATNeuronType.Hidden,
					newNeuronID, newDepth, newWidth));

			int idLink1 = this.training.getInnovations()
					.assignInnovationNumber();

			this.training.getInnovations().createNewInnovation(from,
					newNeuronID, NEATInnovationType.NewLink);

			NEATLinkGene link1 = new NEATLinkGene(from, newNeuronID, true,
					idLink1, 1.0, false);

			this.links.add(link1);

			int idLink2 = this.training.getInnovations()
					.assignInnovationNumber();

			this.training.getInnovations().createNewInnovation(newNeuronID, to,
					NEATInnovationType.NewLink);

			NEATLinkGene link2 = new NEATLinkGene(newNeuronID, to, true,
					idLink2, originalWeight, false);

			this.links.add(link2);
		}

		else {
			int NewNeuronID = this.training.getInnovations().getNeuronID(id);

			int idLink1 = this.training.getInnovations().checkInnovation(from,
					NewNeuronID, NEATInnovationType.NewLink);
			int idLink2 = this.training.getInnovations().checkInnovation(
					NewNeuronID, to, NEATInnovationType.NewLink);

			if ((idLink1 < 0) || (idLink2 < 0)) {
				throw new NeuralNetworkError("NEAT Error");
			}

			NEATLinkGene link1 = new NEATLinkGene(from, NewNeuronID, true,
					idLink1, 1.0, false);
			NEATLinkGene link2 = new NEATLinkGene(NewNeuronID, to, true,
					idLink2, originalWeight, false);

			this.links.add(link1);
			this.links.add(link2);

			NEATNeuronGene newNeuron = new NEATNeuronGene(
					NEATNeuronType.Hidden, NewNeuronID, newDepth, newWidth);

			this.neurons.add(newNeuron);
		}

		return;
	}

	public boolean alreadyHaveThisNeuronID(int id) {
		for (NEATNeuronGene neuronGene : this.neurons) {
			if (neuronGene.getId() == id)
				return true;
		}

		return false;
	}

	void mutateWeights(double mutateRate, double probNewMutate,
			double maxPertubation) {
		for (NEATLinkGene linkGene : this.links) {
			if (Math.random() < mutateRate) {
				if (Math.random() < probNewMutate) {
					linkGene.setWeight(RangeRandomizer.randomize(-1, 1));
				} else {
					linkGene.setWeight(RangeRandomizer.randomize(-1, 1)
							* maxPertubation);
				}
			}
		}
	}

	void mutateActivationResponse(double mutateRate, double maxPertubation) {
		for (NEATNeuronGene neuronGene : this.neurons) {
			if (Math.random() < mutateRate) {
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

		while ((g1 < this.links.size() - 1) || (g2 < this.links.size() - 1)) {

			if (g1 == this.links.size() - 1) {
				g2++;
				numExcess++;

				continue;
			}

			// and vice versa
			if (g2 == this.links.size() - 1) {
				g1++;
				numExcess++;

				continue;
			}

			// get innovation numbers for each gene at this point
			int id1 = this.links.get(g1).getInnovationID();
			int id2 = genome.getLinks().get(g2).getInnovationID();

			// innovation numbers are identical so increase the matched score
			if (id1 == id2) {
				g1++;
				g2++;
				numMatched++;

				// get the weight difference between these two genes
				weightDifference += Math.abs(this.links.get(g1).getWeight()
						- genome.getLinks().get(g2).getWeight());
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
		return this.links.size();
	}

	public int getGenomeID() {
		return genomeID;
	}

	public List<NEATNeuronGene> getNeurons() {
		return neurons;
	}

	public List<NEATLinkGene> getLinks() {
		return links;
	}

	public BasicNetwork getNetwork() {
		return network;
	}

	public int getNetworkDepth() {
		return networkDepth;
	}

	public double getFitness() {
		return fitness;
	}

	public double getAdjustedFitness() {
		return adjustedFitness;
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
		return this.neurons.get(nd).getSplitY();
	}

	/**
	 * @param networkDepth the networkDepth to set
	 */
	public void setNetworkDepth(int networkDepth) {
		this.networkDepth = networkDepth;
	}

	public int compareTo(NEATGenome other) {
		return Double.compare(this.getFitness(), other.getFitness());
	}

	public void setAmountToSpan(double toSpawn) {
		this.amountToSpawn = toSpawn;
		
	}

	public void deleteNetwork() {
		this.network = null;
	}

	public void setFitness(double score) {
		this.fitness = score;
		
	}

	public void setGenomeID(int id) {
		this.genomeID = id;
		
	}

	public void sortGenes() {
		Collections.sort(this.links);
	}

	public void setAdjustedFitness(double adjustedFitness) {
		this.adjustedFitness = adjustedFitness;
		
	}
	
	public String toString()
	{
		StringBuilder result = new StringBuilder();
		result.append("[NEATGenome:");
		result.append(this.genomeID);
		result.append(",fitness=");
		result.append(this.fitness);
		result.append(")");
		return result.toString();
	}

}
