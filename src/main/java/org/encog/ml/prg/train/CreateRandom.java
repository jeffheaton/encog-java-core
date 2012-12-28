package org.encog.ml.prg.train;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.encog.ml.prg.EncogProgram;
import org.encog.ml.prg.EncogProgramContext;
import org.encog.ml.prg.extension.ProgramExtensionTemplate;

public class CreateRandom implements Serializable {
	
	private EncogProgramContext context;
	private int maxDepth;
	private List<ProgramExtensionTemplate> leafNodes = new ArrayList<ProgramExtensionTemplate>();
	private List<ProgramExtensionTemplate> branchNodes = new ArrayList<ProgramExtensionTemplate>();
	private List<ProgramExtensionTemplate> allNodes = new ArrayList<ProgramExtensionTemplate>();
	
	public CreateRandom(EncogProgramContext theContext, int theMaxDepth) {
		this.context = theContext;
		this.maxDepth = theMaxDepth;
		
		for(ProgramExtensionTemplate temp : this.context.getFunctions().getOpCodes() ) {
			this.allNodes.add(temp);
			if( temp.getChildNodeCount()==0 ) {
				this.leafNodes.add(temp);
			} else {
				this.branchNodes.add(temp);
			}
		}
	}
	
	public EncogProgram generate(Random random) {
		EncogProgram program = new EncogProgram(context);
		createNode(random, program,0);
		return program;
	}
	
	public void generate(Random random, EncogProgram program) {
		createNode(random, program,0);
	}
	
	private void createLeafNode(Random random, EncogProgram program) {
		int opCode = random.nextInt(this.leafNodes.size());
		ProgramExtensionTemplate temp = this.leafNodes.get(opCode);
		temp.randomize(random, program, 1.0);
	}
	
	public void createNode(Random random, EncogProgram program, int depth) {		
		if( depth>=this.maxDepth ) {
			createLeafNode(random, program);
			return;
		}
		
		int opCode = random.nextInt(this.allNodes.size());
		ProgramExtensionTemplate temp = this.allNodes.get(opCode);
		
		
		int childNodeCount = temp.getChildNodeCount();

		for(int i=0;i<childNodeCount;i++) {
			createNode(random, program, depth+1);	
		}
		
		// write the noe with random params
		temp.randomize(random, program, 1.0);
	}
}
