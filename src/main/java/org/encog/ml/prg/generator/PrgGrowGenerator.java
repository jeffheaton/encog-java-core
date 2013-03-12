package org.encog.ml.prg.generator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.encog.mathutil.randomize.RangeRandomizer;
import org.encog.ml.ea.species.Species;
import org.encog.ml.fitness.ZeroEvalScoreFunction;
import org.encog.ml.prg.EncogProgram;
import org.encog.ml.prg.EncogProgramContext;
import org.encog.ml.prg.EncogProgramVariables;
import org.encog.ml.prg.ProgramNode;
import org.encog.ml.prg.extension.ProgramExtensionTemplate;
import org.encog.ml.prg.train.PrgPopulation;

public class PrgGrowGenerator {
	
	private EncogProgramContext context;
	private int maxDepth;
	private List<ProgramExtensionTemplate> leaves = new ArrayList<ProgramExtensionTemplate>();
	
	public PrgGrowGenerator(EncogProgramContext theContext, int theMaxDepth) {
		this.context = theContext;
		this.maxDepth = theMaxDepth;
		
		for(ProgramExtensionTemplate temp : this.context.getFunctions().getOpCodes() ) {
			if( temp.getChildNodeCount()==0 ) {
				this.leaves.add(temp);
			}
		}
	}
	
	public EncogProgram generate(Random rnd) {
		EncogProgram program = new EncogProgram(context);
		program.setRootNode(createNode(rnd, program,0));
		return program;
	}
	
	public ProgramNode generate(Random rnd, EncogProgram program) {
		return createNode(rnd, program,0);
	}
	
	private ProgramNode createLeafNode(Random rnd, EncogProgram program) {
		int opCode = rnd.nextInt(this.leaves.size());
		ProgramExtensionTemplate temp = this.leaves.get(opCode);
		ProgramNode result = temp.factorFunction(program, temp.getName(), new ProgramNode[] {});
		result.randomize(program, 1.0);
		return result;
	}
	
	private ProgramNode createNode(Random rnd, EncogProgram program, int depth) {
		int maxOpCode = context.getFunctions().size();
		
		if( depth>=this.maxDepth ) {
			return createLeafNode(rnd, program);
		}
		
		int opCode = RangeRandomizer.randomInt(0, maxOpCode-1);
		ProgramExtensionTemplate temp = context.getFunctions().getOpCode(opCode);
		int childNodeCount = temp.getChildNodeCount();
		
		ProgramNode[] children = new ProgramNode[childNodeCount];
		for(int i=0;i<children.length;i++) {
			children[i] = createNode(rnd, program, depth+1);
		}
		
		ProgramNode result = temp.factorFunction(program, temp.getName(), children);
		result.randomize(program, 1.0);
		return result;
	}
	
	private EncogProgram attemptCreateGenome(Random rnd, ZeroEvalScoreFunction score, Set<String> contents) {
		boolean done = false;
		EncogProgram result = null;
		int tries = 0;
		
		while(!done) {
			result = generate(rnd);
			
			double s = score.calculateScore(result);
			
			if( tries>100 ) {
				done = true;
			} else if( !Double.isNaN(s) && !Double.isInfinite(s) && !contents.contains(result.dumpAsCommonExpression()) ) {
				done = true;
			}
		}
		
		return result;
	}

	public void generate(Random rnd, PrgPopulation pop, ZeroEvalScoreFunction score) {
		Set<String> contents = new HashSet<String>();
		
		pop.getSpecies().clear();
		final Species defaultSpecies = pop.createSpecies();

		for (int i = 0; i < pop.getPopulationSize(); i++) {
			final EncogProgram prg = attemptCreateGenome(rnd,score,contents);
			defaultSpecies.add(prg);
			contents.add(prg.dumpAsCommonExpression());
		}
	}
}
