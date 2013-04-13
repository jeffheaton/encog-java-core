package org.encog.ml.prg.generator;

import java.util.List;
import java.util.Random;

import org.encog.ml.prg.EncogProgram;
import org.encog.ml.prg.EncogProgramContext;
import org.encog.ml.prg.ProgramNode;
import org.encog.ml.prg.expvalue.ValueType;

/**
 * The full generator works by creating program trees that do not stop
 * prematurely. To do this a node is randomly selected from the "function set"
 * until the tree reaches the maximum depth. Once the tree reaches the maximum
 * depth only nodes from the "terminal set" are chosen.
 * 
 * This algorithm was implemented as described in the following publication:
 * 
 * Genetic programming: on the programming of computers by means of natural
 * selection MIT Press Cambridge, MA, USA (c)1992 ISBN:0-262-11170-5
 */
public class PrgFullGenerator extends AbstractPrgGenerator {

	/**
	 * Construct the full generator.
	 * @param theContext The context.
	 * @param theMaxDepth The max depth.
	 */
	public PrgFullGenerator(final EncogProgramContext theContext,
			final int theMaxDepth) {
		super(theContext, theMaxDepth);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ProgramNode createNode(final Random rnd, final EncogProgram program,
			final int depthRemaining, final List<ValueType> types) {
		return createRandomNode(rnd, program, depthRemaining, types, false,
				true);
	}

}
