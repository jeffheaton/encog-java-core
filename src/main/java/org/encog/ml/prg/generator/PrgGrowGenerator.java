package org.encog.ml.prg.generator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.encog.EncogError;
import org.encog.mathutil.randomize.RangeRandomizer;
import org.encog.ml.CalculateScore;
import org.encog.ml.ea.species.Species;
import org.encog.ml.prg.EncogProgram;
import org.encog.ml.prg.EncogProgramContext;
import org.encog.ml.prg.ProgramNode;
import org.encog.ml.prg.exception.EncogEPLError;
import org.encog.ml.prg.extension.ProgramExtensionTemplate;
import org.encog.ml.prg.extension.StandardExtensions;
import org.encog.ml.prg.train.PrgPopulation;

public class PrgGrowGenerator {
	
	private EncogProgramContext context;
	private int maxDepth;
	private List<ProgramExtensionTemplate> leaves = new ArrayList<ProgramExtensionTemplate>();
	private double minConst = -10;
	private double maxConst = 10;
	private boolean hasEnum;
	
	public PrgGrowGenerator(EncogProgramContext theContext, int theMaxDepth) {
		if( theContext.getFunctions().size()==0 ) {
			throw new EncogError("There are no opcodes defined");
		}
		
		this.context = theContext;
		this.maxDepth = theMaxDepth;
		this.hasEnum = this.context.hasEnum();
				
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
	
	/**
	 * Generate a new random branch that can be used with the specified program.
	 * Does not actually attach the new branch anywhere.
	 * @param rnd Random number generator.
	 * @param program The program to generate a branch for.
	 * @return The new branch.
	 */
	public ProgramNode generate(Random rnd, EncogProgram program) {
		return createNode(rnd, program,0);
	}
	
	private ProgramNode createLeafNode(Random rnd, EncogProgram program) {
		ProgramExtensionTemplate temp = generateRandomOpcode(rnd, this.leaves);
		ProgramNode result = new ProgramNode(program, temp, new ProgramNode[] {});
		temp.randomize(rnd, result, this.minConst, this.maxConst);
		return result;
	}
	
	private ProgramExtensionTemplate generateRandomOpcode(Random rnd, List<ProgramExtensionTemplate> opcodes) {
		int maxOpCode = opcodes.size();
		int tries = 10000;
		
		ProgramExtensionTemplate result = null;
		
		while(result==null) {
			int opcode = rnd.nextInt(maxOpCode);
			result = opcodes.get(opcode);
			if( !this.hasEnum && result==StandardExtensions.EXTENSION_CONST_ENUM_SUPPORT ) {
				result=null;
			}
			tries--;
			if( tries<0) {
				throw new EncogEPLError("Could not generate an opcode.  Make sure you have valid opcodes defined.");
			}
		}
		return result;
	}
	
	private ProgramNode createNode(Random rnd, EncogProgram program, int depth) {
				
		if( depth>=this.maxDepth ) {
			return createLeafNode(rnd, program);
		}
		
		ProgramExtensionTemplate temp = generateRandomOpcode(rnd, context.getFunctions().getOpCodes());
		int childNodeCount = temp.getChildNodeCount();
		
		ProgramNode[] children = new ProgramNode[childNodeCount];
		for(int i=0;i<children.length;i++) {
			children[i] = createNode(rnd, program, depth+1);
		}
		
		ProgramNode result = new ProgramNode(program, temp, children);
		temp.randomize(rnd, result, this.minConst, this.maxConst);
		return result;
	}
	
	private EncogProgram attemptCreateGenome(Random rnd, CalculateScore score, Set<String> contents) {
		boolean done = false;
		EncogProgram result = null;
		int tries = 0;
		
		while(!done) {
			result = generate(rnd);
			
			double s;
			try {
				s = score.calculateScore(result);
			} catch(EncogEPLError e) {
				s = Double.NaN;
			}
			
			if( tries>100 ) {
				done = true;
			} else if( !Double.isNaN(s) && !Double.isInfinite(s) && !contents.contains(result.dumpAsCommonExpression()) ) {
				done = true;
			}
		}
		
		return result;
	}

	public void generate(Random rnd, PrgPopulation pop, CalculateScore score) {
		Set<String> contents = new HashSet<String>();
		
		pop.getSpecies().clear();
		final Species defaultSpecies = pop.createSpecies();

		for (int i = 0; i < pop.getPopulationSize(); i++) {
			final EncogProgram prg = attemptCreateGenome(rnd,score,contents);
			prg.setSpecies(defaultSpecies);
			defaultSpecies.add(prg);
			contents.add(prg.dumpAsCommonExpression());
		}
		
		defaultSpecies.setLeader(defaultSpecies.getMembers().get(0));
	}
}
