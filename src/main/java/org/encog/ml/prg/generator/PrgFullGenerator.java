package org.encog.ml.prg.generator;

import java.util.Random;

import org.encog.ml.CalculateScore;
import org.encog.ml.prg.EncogProgram;
import org.encog.ml.prg.EncogProgramContext;
import org.encog.ml.prg.extension.ProgramExtensionTemplate;

public class PrgFullGenerator extends PrgAbstractGenerate {

	public PrgFullGenerator(final EncogProgramContext theContext,
			final CalculateScore theScoreFunction, final int theMaxDepth) {
		super(theContext, theScoreFunction, theMaxDepth);
	}

	@Override
	public void createNode(final Random random, final EncogProgram program,
			final int currentDepth, final int desiredDepth) {
		if (currentDepth >= desiredDepth) {
			createLeafNode(random, program);
			return;
		}

		final int opCode = random.nextInt(getBranchNodes().size());
		final ProgramExtensionTemplate temp = getBranchNodes().get(opCode);

		final int childNodeCount = temp.getChildNodeCount();

		for (int i = 0; i < childNodeCount; i++) {
			createNode(random, program, currentDepth + 1, desiredDepth);
		}

		// write the node with random params
		temp.randomize(random, program, 1.0);
	}

}
