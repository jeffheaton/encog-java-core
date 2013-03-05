package org.encog.ml.prg.extension;

import java.util.Random;

import org.encog.ml.prg.EncogProgram;
import org.encog.ml.prg.epl.OpCodeHeader;

public interface ProgramExtensionTemplate {
	void evaluate(EncogProgram prg);

	int getChildNodeCount();

	int getInstructionSize(OpCodeHeader header);

	String getName();

	short getOpcode();

	boolean isOperator();

	boolean isVariableValue();

	void randomize(Random r, EncogProgram program, double degree);
}
