package org.encog.ml.prg.extension;

import org.encog.ml.prg.EncogProgram;
import org.encog.ml.prg.NodeFunction;
import org.encog.ml.prg.ProgramNode;

public interface ProgramExtensionTemplate {
	String getName();
	int getChildNodeCount();
	NodeFunction factorFunction(EncogProgram theOwner,
			String theName, ProgramNode[] theArgs);
}
