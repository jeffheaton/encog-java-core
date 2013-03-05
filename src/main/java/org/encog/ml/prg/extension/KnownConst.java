package org.encog.ml.prg.extension;

import java.io.Serializable;
import java.util.Random;

import org.encog.ml.prg.EncogProgram;
import org.encog.ml.prg.epl.OpCodeHeader;
import org.encog.ml.prg.expvalue.ExpressionValue;

public class KnownConst implements ProgramExtensionTemplate, Serializable {

	private final String name;
	private final ExpressionValue value;
	private final short opcode;

	public KnownConst(final short theOpcode, final String theName,
			final ExpressionValue theValue) {
		this.name = theName;
		this.value = theValue;
		this.opcode = theOpcode;
	}

	@Override
	public void evaluate(final EncogProgram prg) {
		prg.getStack().push(this.value);
	}

	@Override
	public int getChildNodeCount() {
		return 0;
	}

	@Override
	public int getInstructionSize(final OpCodeHeader header) {
		return 1;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public short getOpcode() {
		return this.opcode;
	}

	@Override
	public boolean isOperator() {
		return false;
	}

	@Override
	public boolean isVariableValue() {
		return false;
	}

	@Override
	public void randomize(final Random r, final EncogProgram program,
			final double degree) {
		program.writeNode(this.opcode, 0, (short) 0);
	}

}
