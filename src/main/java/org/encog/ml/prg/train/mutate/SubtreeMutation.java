package org.encog.ml.prg.train.mutate;

import java.util.Random;

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
	public void mutateSelf(Random rnd, EncogProgram program) {
		int index = rnd.nextInt(program.getRootNode().size());
		ProgramNode node = program.findNode(index);
		ProgramNode newInsert = this.rnd.generate(program);
		program.replaceNode(node,newInsert);
	}

	@Override
	public EncogProgram mutate(Random rnd, EncogProgram program) {
		EncogProgramContext context = program.getContext();
		EncogProgram result = context.cloneProgram(program);
		mutateSelf(rnd, result);
		return result;
	}

}
