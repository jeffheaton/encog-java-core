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
			int mutationEnd = program.findFrame(random.nextInt(programSize));
			int mutationStart = program.findNodeStart(mutationEnd);
			mutationEnd++;
			//int mutationSize = program.size(mutationPoint);
			result.copy(program,0,0,mutationStart);
			result.advanceProgramCounter(mutationStart,true);
			this.rnd.createNode(random, result, 0);
			int sz = programSize - mutationEnd;
			result.copy(program,mutationEnd,result.getProgramCounter(),sz);
			result.advanceProgramCounter(sz, true);
			
			try {
				offspring[i].evaluate();
			} catch(Throwable t) {
				System.out.println("Stop");
			}
		}		
	}
}
