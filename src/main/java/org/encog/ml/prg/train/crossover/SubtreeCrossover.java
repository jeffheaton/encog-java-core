package org.encog.ml.prg.train.crossover;

import java.util.Random;

import org.encog.mathutil.randomize.RangeRandomizer;
import org.encog.ml.prg.EncogProgram;
import org.encog.ml.prg.EncogProgramContext;
import org.encog.ml.prg.ProgramNode;

public class SubtreeCrossover implements PrgCrossover {

	@Override
	public EncogProgram crossover(Random rnd, EncogProgram parent1, EncogProgram parent2) {
		EncogProgramContext context = parent1.getContext();
		EncogProgram result = context.cloneProgram(parent1);
		int p1Index = rnd.nextInt(parent1.getRootNode().size());
		int p2Index = rnd.nextInt(parent2.getRootNode().size());
		ProgramNode resultNode = parent1.findNode(p1Index);
		ProgramNode p2Node = parent2.findNode(p2Index);
		ProgramNode newInsert = context.cloneBranch(result, p2Node);
		result.replaceNode(resultNode,newInsert);
		return result;
	}



}
