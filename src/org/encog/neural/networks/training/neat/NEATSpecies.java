package org.encog.neural.networks.training.neat;

import java.util.ArrayList;
import java.util.List;

import org.encog.math.randomize.RangeRandomizer;

public class NEATSpecies {

	private NEATGenome leader;
	private final List<NEATGenome> members = new ArrayList<NEATGenome>();
	private int speciesID;
	private double bestFitness;
	private int gensNoImprovement;
	private int age;
	private double spawnsRequired;
	private final NEATTraining training;

	public NEATSpecies(NEATTraining training, NEATGenome first, int speciesID) {
		this.training = training;
		this.speciesID = speciesID;
		this.bestFitness = first.getFitness();
		this.gensNoImprovement = 0;
		this.age = 0;
		this.leader = first;
		this.spawnsRequired = 0;
		this.members.add(first);
	}

	public void adjustFitness() {

		for (NEATGenome member : this.members) {
			double fitness = member.getFitness();

			if (this.age < training.getParamYoungBonusAgeThreshhold()) {
				fitness = training.getComparator().applyBonus(fitness,training.getParamYoungFitnessBonus());
			}

			if (this.age > this.training.getParamOldAgeThreshold()) {
				fitness = this.training.getComparator().applyPenalty(fitness, this.training.getParamOldAgePenalty());
			}

			double adjustedFitness = fitness / this.members.size();

			member.setAdjustedFitness(adjustedFitness);

		}
	}

	public NEATGenome getLeader() {
		return leader;
	}

	public void setLeader(NEATGenome leader) {
		this.leader = leader;
	}

	public double getBestFitness() {
		return bestFitness;
	}

	public void setBestFitness(double bestFitness) {
		this.bestFitness = bestFitness;
	}

	public int getGensNoImprovement() {
		return gensNoImprovement;
	}

	public void setGensNoImprovement(int gensNoImprovement) {
		this.gensNoImprovement = gensNoImprovement;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public double getSpawnsRequired() {
		return spawnsRequired;
	}

	public void setSpawnsRequired(double spawnsRequired) {
		this.spawnsRequired = spawnsRequired;
	}

	public List<NEATGenome> getMembers() {
		return members;
	}

	public int getSpeciesID() {
		return speciesID;
	}

	public void addMember(NEATGenome genome) {

		if (genome.getFitness() > this.bestFitness) {
			this.bestFitness = genome.getFitness();

			this.gensNoImprovement = 0;

			this.leader = genome;
		}

		this.members.add(genome);

	}

	public void calculateSpawnAmount() {
		for (NEATGenome genome : this.members) {
			this.spawnsRequired += genome.getAmountToSpawn();
		}

	}

	public void purge() {
		this.members.clear();

		this.age++;

		this.gensNoImprovement++;

		this.spawnsRequired = 0;

	}

	public double getNumToSpawn() {
		return this.spawnsRequired;
	}

	public NEATGenome spawn() {
		NEATGenome baby;

		if (this.members.size() == 1) {
			baby = members.get(0);
		}

		else {
			int maxIndexSize = (int) (this.training.getParamSurvivalRate() * this.members
					.size()) + 1;
			int theOne = (int) RangeRandomizer.randomize(0, maxIndexSize);
			baby = this.members.get(theOne);
		}

		return baby;
	}

}
