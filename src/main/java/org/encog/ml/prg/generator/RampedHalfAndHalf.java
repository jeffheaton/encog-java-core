package org.encog.ml.prg.generator;

import java.util.List;
import java.util.Random;

import org.encog.ml.prg.EncogProgram;
import org.encog.ml.prg.EncogProgramContext;
import org.encog.ml.prg.ProgramNode;
import org.encog.ml.prg.expvalue.ValueType;

public class RampedHalfAndHalf extends AbstractPrgGenerator {

	private final int minDepth;
	private final PrgFullGenerator fullGenerator;
	private final PrgGrowGenerator growGenerator;

	public RampedHalfAndHalf(final EncogProgramContext theContext,
			final int theMinDepth, final int theMaxDepth) {
		super(theContext, theMaxDepth);
		this.minDepth = theMinDepth;

		this.fullGenerator = new PrgFullGenerator(theContext, theMaxDepth);
		this.growGenerator = new PrgGrowGenerator(theContext, theMaxDepth);
	}

	@Override
	public ProgramNode createNode(final Random rnd, final EncogProgram program,
			final int depthRemaining, final List<ValueType> types) {
		final int actualDepthRemaining = depthRemaining;

		if (rnd.nextBoolean()) {
			return this.fullGenerator.createNode(rnd, program,
					actualDepthRemaining, types);
		} else {
			return this.growGenerator.createNode(rnd, program,
					actualDepthRemaining, types);
		}
	}

	@Override
	public int determineMaxDepth(final Random rnd) {
		final int range = getMaxDepth() - this.minDepth;
		return rnd.nextInt(range) + this.minDepth;
	}

	public int getMinDepth() {
		return this.minDepth;
	}

}
