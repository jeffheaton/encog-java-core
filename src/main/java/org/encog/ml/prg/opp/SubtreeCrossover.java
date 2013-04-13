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

public class SubtreeCrossover implements EvolutionaryOperator {

	private void findNode(final Random rnd, final ProgramNode parentNode,
			final List<ValueType> types, final LevelHolder holder) {
		if (holder.getCurrentLevel() == 0) {
			holder.decreaseLevel();
			holder.setTypes(types);
			holder.setNodeFound(parentNode);
		} else {
			holder.decreaseLevel();
			for (int i = 0; i < parentNode.getTemplate().getChildNodeCount(); i++) {
				final ProgramNode childNode = parentNode.getChildNode(i);
				final List<ValueType> childTypes = parentNode.getTemplate()
						.getParams().get(i).determineArgumentTypes(types);
				findNode(rnd, childNode, childTypes, holder);
			}
		}
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
	 * @return Returns the number of parents needed. In this case, two.
	 */
	@Override
	public int parentsNeeded() {
		return 2;
	}

	@Override
	public void performOperation(final Random rnd, final Genome[] parents,
			final int parentIndex, final Genome[] offspring,
			final int offspringIndex) {
		final EncogProgram parent1 = (EncogProgram) parents[0];
		final EncogProgram parent2 = (EncogProgram) parents[1];
		offspring[0] = null;

		final EncogProgramContext context = parent1.getContext();
		final int size1 = parent1.getRootNode().size();
		final int size2 = parent2.getRootNode().size();

		boolean done = false;
		int tries = 100;

		while (!done) {
			final int p1Index = rnd.nextInt(size1);
			final int p2Index = rnd.nextInt(size2);

			final LevelHolder holder1 = new LevelHolder(p1Index);
			final LevelHolder holder2 = new LevelHolder(p2Index);

			final List<ValueType> types = new ArrayList<ValueType>();
			types.add(context.getResult().getVariableType());

			findNode(rnd, parent1.getRootNode(), types, holder1);
			findNode(rnd, parent2.getRootNode(), types, holder2);

			if (LevelHolder.compatibleTypes(holder1.getTypes(),
					holder2.getTypes())) {
				final EncogProgram result = context.cloneProgram(parent1);
				final ProgramNode resultNode = parent1.findNode(p1Index);
				final ProgramNode p2Node = parent2.findNode(p2Index);
				final ProgramNode newInsert = context.cloneBranch(result,
						p2Node);
				result.replaceNode(resultNode, newInsert);
				offspring[0] = result;
				done = true;
			} else {
				tries--;
				if (tries < 0) {
					done = true;
				}
			}

		}
	}

}
