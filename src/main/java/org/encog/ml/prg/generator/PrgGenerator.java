package org.encog.ml.prg.generator;

import java.util.List;
import java.util.Random;

import org.encog.ml.ea.population.PopulationGenerator;
import org.encog.ml.prg.EncogProgram;
import org.encog.ml.prg.ProgramNode;
import org.encog.ml.prg.expvalue.ValueType;

/**
 * Generate a random Encog Program.
 */
public interface PrgGenerator extends PopulationGenerator {
	
	/**
	 * Create a random node for an Encog Program.
	 * @param rnd Random number generator.
	 * @param program The program that the node should be generated for.
	 * @param depthRemaining The depth remaining to generate.
	 * @param types The types to generate.
	 * @return The newly created node.
	 */
	ProgramNode createNode(Random rnd, EncogProgram program, int depthRemaining,
			List<ValueType> types);

	/**
	 * @return The maximum number of errors to allow during generation.
	 */
	int getMaxGenerationErrors();

	/**
	 * Set the maximum errors to allow during generation.
	 * @param maxGenerationErrors The max errors.
	 */
	void setMaxGenerationErrors(int maxGenerationErrors);

}
