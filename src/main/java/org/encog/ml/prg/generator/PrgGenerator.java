/*
 * Encog(tm) Core v3.3 - Java Version
 * http://www.heatonresearch.com/encog/
 * https://github.com/encog/encog-java-core
 
 * Copyright 2008-2014 Heaton Research, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *   
 * For more information on Heaton Research copyrights, licenses 
 * and trademarks visit:
 * http://www.heatonresearch.com/copyright
 */
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
