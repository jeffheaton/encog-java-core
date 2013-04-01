package org.encog.ml.prg.generator;

import java.util.Random;

import org.encog.ml.prg.EncogProgram;
import org.encog.ml.prg.EncogProgramContext;
import org.encog.ml.prg.ProgramNode;
import org.encog.ml.prg.extension.ProgramExtensionTemplate;

public class PrgGrowGenerator extends AbstractPrgGenerator {
		
	public PrgGrowGenerator(EncogProgramContext theContext, int theMaxDepth) {
		super(theContext, theMaxDepth);
	}

	public ProgramNode createNode(Random rnd, EncogProgram program, int depth) {
				
		if( depth>=getMaxDepth() ) {
			return createLeafNode(rnd, program);
		}
		
		ProgramExtensionTemplate temp = generateRandomOpcode(rnd, getContext().getFunctions().getOpCodes());
		int childNodeCount = temp.getChildNodeCount();
		
		ProgramNode[] children = new ProgramNode[childNodeCount];
		for(int i=0;i<children.length;i++) {
			children[i] = createNode(rnd, program, depth+1);
		}
		
		ProgramNode result = new ProgramNode(program, temp, children);
		temp.randomize(rnd, result, getMinConst(), getMaxConst());
		return result;
	}
	
}
