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

	@Override
	public void init(EvolutionaryAlgorithm theOwner) {
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
	public void performOperation(Random rnd, Genome[] parents, int parentIndex,
			Genome[] offspring, int offspringIndex) {
		EncogProgram parent1 = (EncogProgram) parents[0];
		EncogProgram parent2 = (EncogProgram) parents[1];
		offspring[0] = null;

		EncogProgramContext context = parent1.getContext();
		int size1 = parent1.getRootNode().size();
		int size2 = parent2.getRootNode().size();

		boolean done = false;
		int tries = 100;

		while (!done) {
			int p1Index = rnd.nextInt(size1);
			int p2Index = rnd.nextInt(size2);
			
			LevelHolder holder1 = new LevelHolder(p1Index);
			LevelHolder holder2 = new LevelHolder(p2Index);
			
			List<ValueType> types = new ArrayList<ValueType>();
			types.add(context.getResult().getVariableType());
			
			findNode(rnd,parent1.getRootNode(),types,holder1);
			findNode(rnd,parent2.getRootNode(),types,holder2);
			
			if( LevelHolder.compatibleTypes(holder1.getTypes(),holder2.getTypes()) ) {
				EncogProgram result = context.cloneProgram(parent1);
				ProgramNode resultNode = parent1.findNode(p1Index);
				ProgramNode p2Node = parent2.findNode(p2Index);
				ProgramNode newInsert = context.cloneBranch(result, p2Node);
				result.replaceNode(resultNode, newInsert);
				offspring[0] = result;
				done = true;
			} else {
				tries--;
				if( tries<0) {
					done = true;
				}
			}
			
		}
	}

	private void findNode(Random rnd,
			ProgramNode parentNode, List<ValueType> types, LevelHolder holder) {
		if (holder.getCurrentLevel() == 0) {
			holder.decreaseLevel();
			holder.setTypes(types);
			holder.setNodeFound(parentNode);
		} else {
			holder.decreaseLevel();
			for (int i = 0; i < parentNode.getTemplate().getChildNodeCount(); i++) {
				ProgramNode childNode = parentNode.getChildNode(i);
				List<ValueType> childTypes = parentNode.getTemplate()
						.getParams().get(i).determineArgumentTypes(types);
				findNode(rnd, childNode, childTypes, holder);
			}
		}
	}

}
