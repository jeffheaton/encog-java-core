package org.encog.ml.prg.extension;

import java.util.Random;

import org.encog.ml.prg.EncogProgram;
import org.encog.ml.prg.epl.EPLHolder;
import org.encog.ml.prg.epl.OpCodeHeader;

public interface ProgramExtensionTemplate {
	String getName();
	int getChildNodeCount();
	void evaluate(EncogProgram prg);
	short getOpcode();
	boolean isVariableValue();
	void randomize(Random r, EncogProgram program, double degree);
	int getInstructionSize(EPLHolder holder, int individual, int index);
}
