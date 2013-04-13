package org.encog.ml.prg.opp;

import java.util.Random;

import org.encog.ml.ea.genome.Genome;
import org.encog.ml.ea.opp.EvolutionaryOperator;
import org.encog.ml.ea.train.EvolutionaryAlgorithm;
import org.encog.ml.prg.EncogProgram;
import org.encog.ml.prg.EncogProgramContext;
import org.encog.ml.prg.ProgramNode;
import org.encog.ml.prg.expvalue.ExpressionValue;
import org.encog.ml.prg.extension.StandardExtensions;
import org.encog.ml.tree.TreeNode;

public class ConstMutation implements EvolutionaryOperator {

	private final double frequency;
	private final double sigma;

	public ConstMutation(final EncogProgramContext theContext,
			final double theFrequency, final double theSigma) {
		this.frequency = theFrequency;
		this.sigma = theSigma;
	}

	@Override
	public void init(final EvolutionaryAlgorithm theOwner) {
		// TODO Auto-generated method stub

	}

	private void mutateNode(final Random rnd, final ProgramNode node) {
		if (node.getTemplate() == StandardExtensions.EXTENSION_CONST_SUPPORT) {
			if (rnd.nextDouble() < this.frequency) {
				final ExpressionValue v = node.getData()[0];
				if (v.isFloat()) {
					final double adj = rnd.nextGaussian() * this.sigma;
					node.getData()[0] = new ExpressionValue(v.toFloatValue()
							+ adj);
				}
			}
		}

		for (final TreeNode n : node.getChildNodes()) {
			final ProgramNode childNode = (ProgramNode) n;
			mutateNode(rnd, childNode);
		}
	}

	/**
	 * @return Returns the number of offspring produced. In this case, one.
	 */
	@Override
	public int offspringProduced() {
		return 1;
	}

	/**
	 * @return Returns the number of parents needed. In this case, one.
	 */
	@Override
	public int parentsNeeded() {
		return 1;
	}

	@Override
	public void performOperation(final Random rnd, final Genome[] parents,
			final int parentIndex, final Genome[] offspring,
			final int offspringIndex) {
		final EncogProgram program = (EncogProgram) parents[0];
		final EncogProgramContext context = program.getContext();
		final EncogProgram result = context.cloneProgram(program);
		mutateNode(rnd, result.getRootNode());
		offspring[0] = result;
	}
}
