package org.encog.ml.ea.species;

import java.util.List;

import org.encog.ml.ea.genome.Genome;
import org.encog.ml.ea.population.Population;
import org.encog.neural.neat.NEATPopulation;
import org.encog.neural.neat.training.NEATGenome;

public interface Species {

	double calculateShare(boolean shouldMinimize, double maxScore);

	int getAge();

	double getBestScore();

	int getGensNoImprovement();

	Genome getLeader();

	List<Genome> getMembers();

	int getOffspringCount();

	double getOffspringShare();

	Population getPopulation();

	void setAge(int theAge);

	void setBestScore(double theBestScore);

	void setGensNoImprovement(int theGensNoImprovement);

	void setLeader(Genome theLeader);

	void setOffspringCount(int offspringCount);

	void setPopulation(NEATPopulation thePopulation);

}
