package org.encog.solve.genetic.population;

import java.util.List;

import org.encog.solve.genetic.Genome;

public interface Population {
	int getPopulationSize();
	void setPopulationSize(final int populationSize);
	void sort();
	List<Genome> getGenomes();
	Genome getBest();
	void add(Genome genome);
}
