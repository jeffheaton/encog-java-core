package org.encog.ml.prg.extension;

import java.io.Serializable;
import java.util.Random;

import org.encog.ml.prg.EncogProgram;
import org.encog.ml.prg.epl.OpCodeHeader;
import org.encog.ml.prg.expvalue.ExpressionValue;

public class KnownConst implements ProgramExtensionTemplate, Serializable {

	private String name;
	private ExpressionValue value;
	private short opcode;
	
	public KnownConst(short theOpcode, String theName, ExpressionValue theValue) {
		this.name = theName;
		this.value = theValue;
		this.opcode = theOpcode;
	}
	
	
	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public int getInstructionSize(OpCodeHeader header) {
		return 1;
	}

	@Override
	public int getChildNodeCount() {
		return 0;
	}

	@Override
	public void evaluate(EncogProgram prg) {
		prg.getStack().push(this.value);
	}

	@Override
	public short getOpcode() {
		return this.opcode;
	}

	@Override
	public boolean isVariableValue() {
		return false;
	}

	@Override
	public void randomize(Random r, EncogProgram program, double degree) {
		program.writeNode(this.opcode,0,(short) 0);
	}


	@Override
	public boolean isOperator() {
		return false;
	}

}
