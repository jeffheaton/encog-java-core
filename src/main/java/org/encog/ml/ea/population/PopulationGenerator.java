package org.encog.ml.ea.population;

import java.util.Random;

import org.encog.ml.ea.genome.Genome;

public interface PopulationGenerator {
	void generate(Random rnd, Population pop);
	void generate(Random rnd, Genome genome);
}
