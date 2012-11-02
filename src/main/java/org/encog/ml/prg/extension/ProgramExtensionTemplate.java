package org.encog.ml.prg.extension;

public interface ProgramExtensionTemplate {
	String getName();
	int getChildNodeCount();
	int getOpCode();
	int setOpCode();
}
