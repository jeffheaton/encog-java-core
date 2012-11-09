package org.encog.ml.prg.train;

import java.util.ArrayList;
import java.util.List;

import org.encog.mathutil.randomize.RangeRandomizer;
import org.encog.ml.prg.EncogProgram;
import org.encog.ml.prg.EncogProgramContext;
import org.encog.ml.prg.ProgramNode;
import org.encog.ml.prg.extension.ProgramExtensionTemplate;

public class CreateRandom {
	
	private EncogProgramContext context;
	private int maxDepth;
	private List<ProgramExtensionTemplate> leaves = new ArrayList<ProgramExtensionTemplate>();
	
	public CreateRandom(EncogProgramContext theContext, int theMaxDepth) {
		this.context = theContext;
		this.maxDepth = theMaxDepth;
		
		for(ProgramExtensionTemplate temp : this.context.getFunctions().getOpCodes() ) {
			if( temp.getChildNodeCount()==0 ) {
				this.leaves.add(temp);
			}
		}
	}
	
	public EncogProgram generate() {
		EncogProgram program = new EncogProgram(context);
		program.setRootNode(createNode(program,0));
		return program;
	}
	
	private ProgramNode createLeafNode(EncogProgram program) {
		int opCode = RangeRandomizer.randomInt(0, this.leaves.size()-1);
		ProgramExtensionTemplate temp = this.leaves.get(opCode);
		ProgramNode result = temp.factorFunction(program, temp.getName(), new ProgramNode[] {});
		result.randomize(program, 1.0);
		return result;
	}
	
	private ProgramNode createNode(EncogProgram program, int depth) {
		int maxOpCode = context.getFunctions().size();
		
		if( depth>=this.maxDepth ) {
			return createLeafNode(program);
		}
		
		int opCode = RangeRandomizer.randomInt(0, maxOpCode-1);
		ProgramExtensionTemplate temp = context.getFunctions().getOpCode(opCode);
		int childNodeCount = temp.getChildNodeCount();
		
		ProgramNode[] children = new ProgramNode[childNodeCount];
		for(int i=0;i<children.length;i++) {
			children[i] = createNode(program, depth+1);
		}
		
		ProgramNode result = temp.factorFunction(program, temp.getName(), children);
		result.randomize(program, 1.0);
		return result;
	}
}
