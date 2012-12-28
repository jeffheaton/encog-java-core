package org.encog.ml.ea.genome;

import java.util.Random;

import org.encog.ml.ea.population.Population;
import org.encog.ml.ea.score.CalculateGenomeScore;

public interface GenomeFactory {
	Genome factor();

	void factorRandomPopulation(Random random, Population population,
			CalculateGenomeScore scoreFunction, int maxDepth);
}
