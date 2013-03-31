package org.encog.ml.prg.generator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.encog.EncogError;
import org.encog.ml.CalculateScore;
import org.encog.ml.ea.exception.EACompileError;
import org.encog.ml.ea.exception.EARuntimeError;
import org.encog.ml.ea.population.Population;
import org.encog.ml.ea.species.Species;
import org.encog.ml.prg.EncogProgram;
import org.encog.ml.prg.EncogProgramContext;
import org.encog.ml.prg.ProgramNode;
import org.encog.ml.prg.extension.ProgramExtensionTemplate;
import org.encog.ml.prg.extension.StandardExtensions;
import org.encog.ml.prg.train.ZeroEvalScoreFunction;

public class PrgGrowGenerator extends AbstractGenerator {
		
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
