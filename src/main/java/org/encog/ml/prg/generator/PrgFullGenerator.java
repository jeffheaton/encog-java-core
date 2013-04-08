package org.encog.ml.prg.generator;

import java.util.List;
import java.util.Random;

import org.encog.ml.ea.exception.EACompileError;
import org.encog.ml.prg.EncogProgram;
import org.encog.ml.prg.EncogProgramContext;
import org.encog.ml.prg.ProgramNode;
import org.encog.ml.prg.expvalue.ValueType;
import org.encog.ml.prg.extension.ParamTemplate;
import org.encog.ml.prg.extension.ProgramExtensionTemplate;

public class PrgFullGenerator extends AbstractPrgGenerator {
		
	public PrgFullGenerator(EncogProgramContext theContext, int theMaxDepth) {
		super(theContext, theMaxDepth);
	}

	@Override
	public ProgramNode createNode(Random rnd, EncogProgram program, int depthRemaining, List<ValueType> types) {
				
		if( depthRemaining==0 ) {
			return createTerminalNode(rnd, program, types);
		}
		
		List<ProgramExtensionTemplate> opcodeSet = getContext().getFunctions().findOpcodes(types, getContext(), false, true);
		ProgramExtensionTemplate temp = generateRandomOpcode(rnd, opcodeSet);
		if( temp==null ) {
			throw new EACompileError("Trying to generate a random opcode when no opcodes exist.");
		}
		
		int childNodeCount = temp.getChildNodeCount();
		
		ProgramNode[] children = new ProgramNode[childNodeCount];
		for(int i=0;i<children.length;i++) {
			List<ValueType> childTypes = temp.getParams().get(i).determineArgumentTypes(types);
			children[i] = createNode(rnd, program, depthRemaining-1, childTypes);
		}
		
		ProgramNode result = new ProgramNode(program, temp, children);
		temp.randomize(rnd, types, result, getMinConst(), getMaxConst());
		return result;
	}
	
}
