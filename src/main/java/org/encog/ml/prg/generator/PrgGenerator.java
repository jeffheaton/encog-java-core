package org.encog.ml.prg.generator;

import java.util.Random;

import org.encog.ml.ea.population.PopulationGenerator;
import org.encog.ml.prg.EncogProgram;
import org.encog.ml.prg.ProgramNode;
import org.encog.ml.prg.expvalue.ValueType;

public interface PrgGenerator extends PopulationGenerator {
	ProgramNode createNode(Random rnd, EncogProgram program,
			int depth, ValueType t);

}
