package org.encog.ml.prg.extension;

import org.encog.ml.prg.EncogProgram;
import org.encog.ml.prg.ProgramNode;

public interface ProgramExtensionTemplate {
	String getName();
	int getChildNodeCount();
	ProgramNode factorFunction(EncogProgram theOwner,
			String theName, ProgramNode[] theArgs);
}
