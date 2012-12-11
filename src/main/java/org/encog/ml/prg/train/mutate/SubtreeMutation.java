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
			
			// find the mutation point, this is simply a node position based on the
			// node count, it does not take int account node-sizes. Also, because this
			// is RPN, the mutation point is the end of the mutation.
			int programSize = program.size();
			int mutationPosition = random.nextInt(programSize);
			
			// now find the actual frame index of the end of the mutation
			int mutationIndex = program.findFrame(mutationPosition);
			
			int mutationStart = program.findNodeStart(mutationIndex);
			int mutationSize = (program.nextIndex(mutationIndex) - mutationStart);
			int mutationEnd = mutationStart+mutationSize;
			
			// copy left of the mutation point
			result.copy(program, 0, 0, mutationStart);
			result.setProgramLength(mutationStart);
			result.setProgramCounter(mutationStart);
			
			// handle mutation point
			this.rnd.createNode(random, result, 0);
			
			// copy right of the mutation point
			int rightSize = program.getProgramLength()-mutationStart-mutationSize;
			int t = result.getProgramLength();
			result.setProgramLength(result.getProgramLength()+rightSize);
			result.copy(program, mutationEnd, t, rightSize);			
			
			result.size();
		}		
	}
}
