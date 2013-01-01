package org.encog.ml.prg.generator;

import java.util.Random;

import org.encog.ml.ea.population.PopulationGenerator;
import org.encog.ml.prg.EncogProgram;

public interface PrgPopulationGenerator extends PopulationGenerator {
	void createNode(Random rnd, EncogProgram prg, int currentDepth, int desiredDepth);
	int getMaxDepth();
}
