package org.encog.ml.prg.generator;

import java.util.List;
import java.util.Random;

import org.encog.ml.prg.EncogProgram;
import org.encog.ml.prg.EncogProgramContext;
import org.encog.ml.prg.ProgramNode;
import org.encog.ml.prg.expvalue.ValueType;

/**
 * Because neither the grow or full method provide a very wide array of sizes or
 * shapes on their own, Koza (1992) proposed a combination called ramped
 * half-and-half. Half the initial population is constructed using full and half
 * is constructed using grow. This is done using a range of depth limits (hence
 * the term "ramped") to help ensure that we generate trees having a variety of
 * sizes and shapes. (from: A field guide to genetic programming)
 * 
 * This algorithm was implemented as described in the following publication:
 * 
 * Genetic programming: on the programming of computers by means of natural
 * selection MIT Press Cambridge, MA, USA (c)1992 ISBN:0-262-11170-5
 */
public class RampedHalfAndHalf extends AbstractPrgGenerator {

	/**
	 * The minimum depth.
	 */
	private final int minDepth;
	
	/**
	 * The full generator.
	 */
	private final PrgFullGenerator fullGenerator;
	
	/**
	 * The grow generator.
	 */
	private final PrgGrowGenerator growGenerator;

	/**
	 * Construct the ramped half-and-half generator.
	 * @param theContext The context.
	 * @param theMinDepth The minimum depth.
	 * @param theMaxDepth The maximum depth.
	 */
	public RampedHalfAndHalf(final EncogProgramContext theContext,
			final int theMinDepth, final int theMaxDepth) {
		super(theContext, theMaxDepth);
		this.minDepth = theMinDepth;

		this.fullGenerator = new PrgFullGenerator(theContext, theMaxDepth);
		this.growGenerator = new PrgGrowGenerator(theContext, theMaxDepth);
	}

	/**
	 * {@inheritDoc}
	 */
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

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int determineMaxDepth(final Random rnd) {
		final int range = getMaxDepth() - this.minDepth;
		return rnd.nextInt(range) + this.minDepth;
	}

	/**
	 * @return The minimum depth.
	 */
	public int getMinDepth() {
		return this.minDepth;
	}

}
