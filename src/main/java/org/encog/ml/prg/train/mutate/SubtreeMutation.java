package org.encog.ml.prg.train.mutate;

import org.encog.mathutil.randomize.RangeRandomizer;
import org.encog.ml.prg.EncogProgram;
import org.encog.ml.prg.EncogProgramContext;
import org.encog.ml.prg.ProgramNode;
import org.encog.ml.prg.train.CreateRandom;

public class SubtreeMutation implements PrgMutate {

	private CreateRandom rnd;
	
	public SubtreeMutation(EncogProgramContext theContext, int theMaxDepth) {
		this.rnd = new CreateRandom(theContext, theMaxDepth);
	}
	
	@Override
	public void mutateSelf(EncogProgram program) {
		int index = RangeRandomizer.randomInt(0, program.getRootNode().size());
		ProgramNode node = program.findNode(index);
		ProgramNode newInsert = this.rnd.generate(program);
		program.replaceNode(node,newInsert);
	}

	@Override
	public EncogProgram mutate(EncogProgram program) {
		EncogProgramContext context = program.getContext();
		EncogProgram result = context.cloneProgram(program);
		mutateSelf(result);
		return result;
	}

}
