package org.encog.solve.genetic.species;

import java.util.ArrayList;
import java.util.List;

import org.encog.mathutil.randomize.RangeRandomizer;
import org.encog.neural.networks.training.neat.NEATGenome;
import org.encog.neural.networks.training.neat.NEATTraining;
import org.encog.solve.genetic.GeneticAlgorithm;
import org.encog.solve.genetic.genome.Genome;

public class BasicSpecies implements Species {
	private Genome leader;
	private final List<Genome> members = new ArrayList<Genome>();
	private long speciesID;
	private double bestFitness;
	private int gensNoImprovement;
	private int age;
	private double spawnsRequired;
	private final GeneticAlgorithm training;

	public BasicSpecies(GeneticAlgorithm training, NEATGenome first, long speciesID) {
		this.training = training;
		this.speciesID = speciesID;
		this.bestFitness = first.getScore();
		this.gensNoImprovement = 0;
		this.age = 0;
		this.leader = first;
		this.spawnsRequired = 0;
		this.members.add(first);
	}

	public void adjustFitness() {

		for (Genome member : this.members) {
			double fitness = member.getScore();

			if (this.age < training.getPopulation().getYoungBonusAgeThreshhold()) {
				fitness = training.getComparator().applyBonus(fitness,training.getPopulation().getYoungFitnessBonus());
			}

			if (this.age > this.training.getPopulation().getOldAgeThreshold()) {
				fitness = this.training.getComparator().applyPenalty(fitness, this.training.getPopulation().getOldAgePenalty());
			}

			double adjustedScore = fitness / this.members.size();

			member.setAdjustedScore(adjustedScore);

		}
	}

	public Genome getLeader() {
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

	public List<Genome> getMembers() {
		return members;
	}

	public long getSpeciesID() {
		return speciesID;
	}

	public void addMember(NEATGenome genome) {

		if( this.training.getComparator().isBetterThan(genome.getScore(), this.bestFitness)) {
			this.bestFitness = genome.getScore();
			this.gensNoImprovement = 0;
			this.leader = genome;
		}

		this.members.add(genome);

	}

	public void calculateSpawnAmount() {
		for (Genome genome : this.members) {
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

	public Genome chooseParent() {
		Genome baby;

		if (this.members.size() == 1) {
			baby = members.get(0);
		}

		else {
			int maxIndexSize = (int) (this.training.getPopulation().getSurvivalRate() * this.members
					.size()) + 1;
			int theOne = (int) RangeRandomizer.randomize(0, maxIndexSize);
			baby = this.members.get(theOne);
		}

		return baby;
	}

}
