package org.encog.ml.genetic.genome;

import java.util.Random;

import org.encog.ml.genetic.population.Population;
import org.encog.neural.networks.training.CalculateScore;

public interface GenomeFactory {
	Genome factor();

	void factorRandomPopulation(Random random, Population population,
			CalculateScore scoreFunction, int maxDepth);
}
