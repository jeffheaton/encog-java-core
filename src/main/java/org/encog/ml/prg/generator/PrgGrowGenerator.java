package org.encog.ml.prg.generator;

import java.util.List;
import java.util.Random;

import org.encog.ml.prg.EncogProgram;
import org.encog.ml.prg.EncogProgramContext;
import org.encog.ml.prg.ProgramNode;
import org.encog.ml.prg.expvalue.ValueType;

/**
 * The grow generator creates a random program by choosing a random node from
 * both the "function and terminal" sets until the maximum depth is reached.
 * Once the maximum depth is reached only nodes from terminal set are chosen.
 * 
 * This algorithm was implemented as described in the following publication:
 * 
 * Genetic programming: on the programming of computers by means of natural
 * selection MIT Press Cambridge, MA, USA (c)1992 ISBN:0-262-11170-5
 */
public class PrgGrowGenerator extends AbstractPrgGenerator {

	/**
	 * Construct the grow generator.
	 * @param theContext The program context.
	 * @param theMaxDepth The max depth.
	 */
	public PrgGrowGenerator(final EncogProgramContext theContext,
			final int theMaxDepth) {
		super(theContext, theMaxDepth);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ProgramNode createNode(final Random rnd, final EncogProgram program,
			final int depthRemaining, final List<ValueType> types) {
		return createRandomNode(rnd, program, depthRemaining, types, true, true);
	}

}
