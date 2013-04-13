package org.encog.ml.prg.opp;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.encog.ml.ea.genome.Genome;
import org.encog.ml.ea.opp.EvolutionaryOperator;
import org.encog.ml.ea.train.EvolutionaryAlgorithm;
import org.encog.ml.prg.EncogProgram;
import org.encog.ml.prg.EncogProgramContext;
import org.encog.ml.prg.ProgramNode;
import org.encog.ml.prg.expvalue.ValueType;
import org.encog.ml.prg.generator.PrgGenerator;
import org.encog.ml.prg.generator.PrgGrowGenerator;

public class SubtreeMutation implements EvolutionaryOperator {

	private PrgGenerator generator;
	private final int maxDepth;

	public SubtreeMutation(final EncogProgramContext theContext,
			final int theMaxDepth) {
		this.generator = new PrgGrowGenerator(theContext, theMaxDepth);
		this.maxDepth = theMaxDepth;
	}

	private List<ValueType> determineValueType(final ProgramNode node) {
		final List<ValueType> result = new ArrayList<ValueType>();
		result.addAll(node.getTemplate().getReturnValue().getPossibleTypes());
		return result;
	}

	private void findNode(final Random rnd, final EncogProgram result,
			final ProgramNode parentNode, final List<ValueType> types,
			final int[] globalIndex) {
		if (globalIndex[0] == 0) {
			globalIndex[0]--;

			final ProgramNode newInsert = this.generator.createNode(rnd,
					result, this.maxDepth, types);
			result.replaceNode(parentNode, newInsert);
		} else {
			globalIndex[0]--;
			for (int i = 0; i < parentNode.getTemplate().getChildNodeCount(); i++) {
				final ProgramNode childNode = parentNode.getChildNode(i);
				final List<ValueType> childTypes = parentNode.getTemplate()
						.getParams().get(i).determineArgumentTypes(types);
				findNode(rnd, result, childNode, childTypes, globalIndex);
			}
		}
	}

	public PrgGenerator getGenerator() {
		return this.generator;
	}

	@Override
	public void init(final EvolutionaryAlgorithm theOwner) {
		// TODO Auto-generated method stub

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

		final List<ValueType> types = new ArrayList<ValueType>();
		types.add(context.getResult().getVariableType());
		final int[] globalIndex = new int[1];
		globalIndex[0] = rnd.nextInt(result.getRootNode().size());
		findNode(rnd, result, result.getRootNode(), types, globalIndex);

		offspring[0] = result;
	}

	public void setGenerator(final PrgGenerator generator) {
		this.generator = generator;
	}

}
