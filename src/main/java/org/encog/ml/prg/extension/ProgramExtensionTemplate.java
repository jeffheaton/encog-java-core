package org.encog.ml.prg.extension;

import java.util.Random;

import org.encog.ml.prg.ProgramNode;
import org.encog.ml.prg.expvalue.ExpressionValue;

public interface ProgramExtensionTemplate {
	public static final int NO_PREC = 100;
	
	String getName();
	int getChildNodeCount();
	ExpressionValue evaluate(ProgramNode actual);
	boolean isVariable();
	void randomize(Random rnd, ProgramNode actual, double degree);
	int getDataSize();
	NodeType getNodeType();
	int getPrecedence();
}
