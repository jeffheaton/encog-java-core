package org.encog.neural.networks.training.neat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.training.Strategy;
import org.encog.neural.networks.training.Train;
import org.encog.neural.networks.training.neat.NEATInnovationDB;

public class NEATTraining implements Train {

	private final NeuralDataSet training;
	private final int inputCount;
	private final int outputCount;
	private final List<NEATGenome> population = new ArrayList<NEATGenome>();
	private final List<NEATGenome> bestGenomes = new ArrayList<NEATGenome>();
	private final NEATInnovationDB innovations;
	private final List<SplitDepth> splits;
	private final List<NEATSpecies> species = new ArrayList<NEATSpecies>();
	private double bestEverFitness;
	private double totalFitAdjustment;
	private double averageFitAdjustment;

	private int currentGenomeID = 1;

	private int paramNumBestGenomes = 4;
	private double paramCompatibilityThreshold = 0;
	private int paramMaxNumberOfSpecies = 0;

	public NEATTraining(NeuralDataSet training, int inputCount,
			int outputCount, int populationSize) {
		this.training = training;
		this.inputCount = inputCount;
		this.outputCount = outputCount;

		// create the initial population
		for (int i = 0; i < populationSize; i++) {
			population.add(new NEATGenome(assignGenomeID(), inputCount,
					outputCount));
		}

		NEATGenome genome = new NEATGenome(1, inputCount, outputCount);

		this.innovations = new NEATInnovationDB(genome.getLinks(), genome
				.getNeurons());

		this.splits = split(null, 0, 1, 0);
	}

	public List<BasicNetwork> createNetworks() {
		List<BasicNetwork> result = new ArrayList<BasicNetwork>();

		for (NEATGenome genome : this.population) {
			calculateNetDepth(genome);
			BasicNetwork net = genome.createNetwork();

			result.add(net);
		}

		return result;
	}

	private void calculateNetDepth(NEATGenome genome) {
		int maxSoFar = 0;

		for (int nd = 0; nd < genome.getNeurons().size(); ++nd) {
			for (SplitDepth split : this.splits) {

				if ((genome.getSplitY(nd) == split.getValue())
						&& (split.getDepth() > maxSoFar)) {
					maxSoFar = split.getDepth();
				}
			}
		}

		genome.setNetworkDepth(maxSoFar + 2);
	}

	public void addNeuronID(int nodeID, List<Integer> vec) {
		for (int i = 0; i < vec.size(); i++) {
			if (vec.get(i) == nodeID) {
				return;
			}
		}

		vec.add(nodeID);

		return;
	}

	public int assignGenomeID() {
		return (this.currentGenomeID++);
	}

	public NeuralDataSet getTraining() {
		return training;
	}

	public int getInputCount() {
		return inputCount;
	}

	public int getOutputCount() {
		return outputCount;
	}

	public void addStrategy(Strategy strategy) {
		// TODO Auto-generated method stub

	}

	public void finishTraining() {
		// TODO Auto-generated method stub

	}

	public double getError() {
		// TODO Auto-generated method stub
		return 0;
	}

	public BasicNetwork getNetwork() {
		// TODO Auto-generated method stub
		return null;
	}

	public List<Strategy> getStrategies() {
		// TODO Auto-generated method stub
		return null;
	}

	public void iteration() {
		// TODO Auto-generated method stub

	}

	public void setError(double error) {
		// TODO Auto-generated method stub

	}

	private List<SplitDepth> split(List<SplitDepth> result, double low,
			double high, int depth) {
		if (result == null)
			result = new ArrayList<SplitDepth>();

		double span = high - low;

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

	public NEATInnovationDB getInnovations() {
		return this.innovations;
	}

	public void sortAndRecord() {
		Collections.sort(this.population);

		if (this.population.get(0).getFitness() > this.bestEverFitness) {
			this.bestEverFitness = this.population.get(0).getFitness();
		}

		storeBestGenomes();
	}

	public void storeBestGenomes() {
		// clear old record
		this.bestGenomes.clear();

		for (int i = 0; i < this.paramNumBestGenomes; ++i) {
			this.bestGenomes.add(this.population.get(i));
		}
	}

	public List<BasicNetwork> getBestNetworksFromLastGeneration() {
		List<BasicNetwork> result = new ArrayList<BasicNetwork>();

		for (NEATGenome genome : this.population) {
			calculateNetDepth(genome);
			result.add(genome.createNetwork());
		}

		return result;
	}

	public void adjustSpeciesFitnesses() {
		for (NEATSpecies s : this.species) {
			s.adjustFitness();
		}
	}

	public void speciateAndCalculateSpawnLevels() {
		boolean added = false;

		adjustCompatibilityThreshold();

		for (NEATGenome genome : this.population) {
			for (NEATSpecies s : this.species) {
				double compatibility = genome.getCompatibilityScore(s
						.getLeader());

				if (compatibility <= this.paramCompatibilityThreshold) {
					s.addMember(genome);
					genome.setSpeciesID(s.getSpeciesID());
					added = true;
					break;
				}
			}

			if (!added) {
				this.species.add(new NEATSpecies(genome, assignSpeciesID()));
			}

			added = false;
		}

		adjustSpeciesFitnesses();

		for (NEATGenome genome : this.population) {
			this.totalFitAdjustment += genome.getAdjustedFitness();
		}

		this.averageFitAdjustment = this.totalFitAdjustment
				/ this.population.size();

		for (NEATGenome genome : this.population) {
			double toSpawn = genome.getAdjustedFitness()
					/ this.averageFitAdjustment;
			genome.setAmountToSpan(toSpawn);
		}

		for (NEATSpecies species : this.species) {
			species.calculateSpawnAmount();
		}
	}

	public void adjustCompatibilityThreshold() {

		if (this.paramMaxNumberOfSpecies < 1)
			return;

		double thresholdIncrement = 0.01;

		if (this.species.size() > this.paramMaxNumberOfSpecies) {
			this.paramCompatibilityThreshold += thresholdIncrement;
		}

		else if (this.species.size() < 2) {
			this.paramCompatibilityThreshold -= thresholdIncrement;
		}

	}

	private int assignSpeciesID() {
		// TODO Auto-generated method stub
		return 0;
	}

}
