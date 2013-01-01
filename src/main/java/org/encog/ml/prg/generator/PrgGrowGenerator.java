package org.encog.ml.prg.generator;

import java.util.Random;

import org.encog.ml.ea.score.CalculateGenomeScore;
import org.encog.ml.prg.EncogProgram;
import org.encog.ml.prg.EncogProgramContext;
import org.encog.ml.prg.extension.ProgramExtensionTemplate;

public class PrgGrowGenerator extends PrgAbstractGenerate {

	public PrgGrowGenerator(EncogProgramContext theContext,
			CalculateGenomeScore theScoreFunction, int theMaxDepth) {
		super(theContext, theScoreFunction, theMaxDepth);
	}
	
	public void createNode(Random random, EncogProgram program, int depth) {		
		if( depth>=this.getMaxDepth() ) {
			createLeafNode(random, program);
			return;
		}
		
		int opCode = random.nextInt(this.getAllNodes().size());
		ProgramExtensionTemplate temp = this.getAllNodes().get(opCode);
		
		
		int childNodeCount = temp.getChildNodeCount();

		for(int i=0;i<childNodeCount;i++) {
			createNode(random, program, depth+1);	
		}
		
		// write the node with random params
		temp.randomize(random, program, 1.0);
	}
	
}
