package org.encog.ml.prg.opp;

import java.util.Random;

import org.encog.ml.ea.genome.Genome;
import org.encog.ml.ea.opp.EvolutionaryOperator;
import org.encog.ml.ea.train.EvolutionaryAlgorithm;
import org.encog.ml.prg.EncogProgram;
import org.encog.ml.prg.EncogProgramContext;
import org.encog.ml.prg.ProgramNode;

public class SubtreeCrossover implements EvolutionaryOperator {

	@Override
	public void init(EvolutionaryAlgorithm theOwner) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * @return Returns the number of offspring produced.  In this case, one.
	 */
	@Override
	public int offspringProduced() {
		return 1;
	}

	/**
	 * @return Returns the number of parents needed.  In this case, two.
	 */
	@Override
	public int parentsNeeded() {
		return 2;
	}

	@Override
	public void performOperation(Random rnd, Genome[] parents, int parentIndex,
			Genome[] offspring, int offspringIndex) {
		EncogProgram parent1 = (EncogProgram)parents[0];
		EncogProgram parent2 = (EncogProgram)parents[1];
		
		EncogProgramContext context = parent1.getContext();
		EncogProgram result = context.cloneProgram(parent1);
		int p1Index = rnd.nextInt(parent1.getRootNode().size());
		int p2Index = rnd.nextInt(parent2.getRootNode().size());
		ProgramNode resultNode = parent1.findNode(p1Index);
		ProgramNode p2Node = parent2.findNode(p2Index);
		ProgramNode newInsert = context.cloneBranch(result, p2Node);
		result.replaceNode(resultNode,newInsert);
		offspring[0] = result;
	}



}
