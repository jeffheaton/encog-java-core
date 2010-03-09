package org.encog.solve.genetic.species;

import java.util.List;

import org.encog.mathutil.randomize.RangeRandomizer;
import org.encog.neural.networks.training.neat.NEATGenome;
import org.encog.solve.genetic.genome.Genome;

public interface Species {
	
	void adjustFitness();
	Genome getLeader();
	void setLeader(NEATGenome leader);
	double getBestFitness();
	void setBestFitness(double bestFitness);
	int getGensNoImprovement();
	void setGensNoImprovement(int gensNoImprovement);
	int getAge();
	void setAge(int age);
	double getSpawnsRequired();
	void setSpawnsRequired(double spawnsRequired);
	List<Genome> getMembers();
	long getSpeciesID();
	void addMember(NEATGenome genome);
	void calculateSpawnAmount();
	void purge();
	double getNumToSpawn();
	Genome chooseParent();
}
