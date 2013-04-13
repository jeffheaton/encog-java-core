package org.encog.ml.prg.generator;

import java.util.List;
import java.util.Random;

import org.encog.ml.prg.EncogProgram;
import org.encog.ml.prg.EncogProgramContext;
import org.encog.ml.prg.ProgramNode;
import org.encog.ml.prg.expvalue.ValueType;

public class PrgFullGenerator extends AbstractPrgGenerator {

	public PrgFullGenerator(final EncogProgramContext theContext,
			final int theMaxDepth) {
		super(theContext, theMaxDepth);
	}

	@Override
	public ProgramNode createNode(final Random rnd, final EncogProgram program,
			final int depthRemaining, final List<ValueType> types) {
		return createRandomNode(rnd, program, depthRemaining, types, false,
				true);
	}

}
