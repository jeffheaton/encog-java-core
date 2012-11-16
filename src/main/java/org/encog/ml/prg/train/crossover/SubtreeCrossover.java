package org.encog.ml.prg.train.crossover;

import org.encog.mathutil.randomize.RangeRandomizer;
import org.encog.ml.prg.EncogProgram;
import org.encog.ml.prg.EncogProgramContext;
import org.encog.ml.prg.ProgramNode;

public class SubtreeCrossover implements PrgCrossover {

	@Override
	public EncogProgram crossover(EncogProgram mother, EncogProgram father) {
		EncogProgramContext context = mother.getContext();
		EncogProgram result = context.cloneProgram(mother);
		int motherIndex = RangeRandomizer.randomInt(0, mother.getRootNode().size());
		int fatherIndex = RangeRandomizer.randomInt(0, father.getRootNode().size());
		ProgramNode resultNode = mother.findNode(motherIndex);
		ProgramNode fatherNode = mother.findNode(fatherIndex);
		ProgramNode newInsert = context.cloneBranch(result, fatherNode);
		result.replaceNode(resultNode,newInsert);
		return result;
	}



}
