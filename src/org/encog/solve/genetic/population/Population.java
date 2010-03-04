package org.encog.solve.genetic.population;

import java.util.List;

import org.encog.neural.networks.training.neat.NEATGenome;
import org.encog.solve.genetic.Genome;

public interface Population {
	int getPopulationSize();
	void setPopulationSize(final int populationSize);
	void sort();
	List<Genome> getGenomes();
	Genome getBest();
	void add(Genome genome);
	long assignGenomeID();
	long assignSpeciesID();
	long assignInnovationID();
	long assignGeneID();
	int size();
	void clear();
	void addAll(List<? extends Genome> newPop);
	Genome get(int i);
}
