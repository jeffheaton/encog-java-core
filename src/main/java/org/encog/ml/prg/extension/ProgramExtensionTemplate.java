package org.encog.ml.prg.extension;

import java.io.Serializable;
import java.util.List;
import java.util.Random;

import org.encog.ml.prg.EncogProgramContext;
import org.encog.ml.prg.ProgramNode;
import org.encog.ml.prg.expvalue.ExpressionValue;
import org.encog.ml.prg.expvalue.ValueType;

public interface ProgramExtensionTemplate extends Serializable {
	public static final int NO_PREC = 100;
	
	String getName();
	int getChildNodeCount();
	ExpressionValue evaluate(ProgramNode actual);
	boolean isVariable();
	void randomize(Random rnd, ValueType desiredType, ProgramNode actual, double minValue, double maxValue);
	int getDataSize();
	NodeType getNodeType();
	int getPrecedence();
	ParamTemplate getReturnValue();
	List<ParamTemplate> getParams();
	boolean isPossibleReturnType(EncogProgramContext context, ValueType rtn);
}
