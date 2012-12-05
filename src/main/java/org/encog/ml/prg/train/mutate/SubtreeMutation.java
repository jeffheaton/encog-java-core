package org.encog.ml.prg.train.mutate;

import java.util.Random;

import org.encog.ml.prg.EncogProgram;
import org.encog.ml.prg.EncogProgramContext;
import org.encog.ml.prg.train.CreateRandom;

public class SubtreeMutation implements PrgMutate {

	private CreateRandom rnd;
	
	public SubtreeMutation(EncogProgramContext theContext, int theMaxDepth) {
		this.rnd = new CreateRandom(theContext, theMaxDepth);
	}
	
	@Override
	public void mutate(Random random, EncogProgram program,
			EncogProgram[] offspring, int index, int mutationCount) {
		for(int i=0;i<mutationCount;i++) {
			EncogProgram result = offspring[i];
			result.clear();
			int programSize = program.size();
			int mutationPoint = random.nextInt(programSize);
			int mutationSize = program.size(mutationPoint);
			result.copy(program,0,0,mutationPoint);
			result.setProgramCounter(mutationPoint);
			this.rnd.createNode(random, program, 0);
			int sz = programSize - (mutationPoint+mutationSize);
			result.copy(program,mutationPoint+mutationSize,result.getProgramCounter(),sz);
			result.advanceProgramCounter(mutationSize, true);
		}		
	}
}
