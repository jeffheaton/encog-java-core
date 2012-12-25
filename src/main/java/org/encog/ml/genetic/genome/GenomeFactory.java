package org.encog.ml.genetic.genome;

import java.util.Random;

import org.encog.ml.genetic.population.Population;

public interface GenomeFactory {
	Genome factor();

	void factorRandomPopulation(Random random, Population population,
			CalculateGenomeScore scoreFunction, int maxDepth);
}
