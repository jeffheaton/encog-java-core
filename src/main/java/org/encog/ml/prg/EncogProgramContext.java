package org.encog.ml.prg;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.encog.EncogError;
import org.encog.ml.prg.expvalue.ExpressionValue;
import org.encog.ml.prg.extension.FunctionFactory;
import org.encog.ml.prg.extension.StandardExtensions;
import org.encog.util.csv.CSVFormat;

public class EncogProgramContext implements Serializable {

	private final CSVFormat format;
	private final FunctionFactory functions;
	private final List<String> definedVariables = new ArrayList<String>();
	private final Map<String,Double> config = new HashMap<String,Double>();
	private double constMin = -10;
	private double constMax = 10;
	
	public EncogProgramContext(CSVFormat theFormat, FunctionFactory theFunctions) {
		this.format = theFormat;
		this.functions = theFunctions;
	}
	
	public EncogProgramContext(CSVFormat format) {
		this(format, new FunctionFactory());
	}
	
	public EncogProgramContext() {
		this(CSVFormat.EG_FORMAT, new FunctionFactory());
	}

	public CSVFormat getFormat() {
		return format;
	}

	public FunctionFactory getFunctions() {
		return functions;
	}
	
	public void defineVariable(String v) {
		if( this.definedVariables.contains(v) ) {
			throw new ExpressionError("Variable " + v + " already defined.");
		}
		definedVariables.add(v);
		
	}

	public List<String> getDefinedVariables() {
		return this.definedVariables;
	}

	public Map<String, Double> getConfig() {
		return config;
	}

	public double getConstMin() {
		return constMin;
	}

	public void setConstMin(double constMin) {
		this.constMin = constMin;
	}

	public double getConstMax() {
		return constMax;
	}

	public void setConstMax(double constMax) {
		this.constMax = constMax;
	}
	
	public EncogProgram cloneProgram(EncogProgram sourceProgram) {
		ProgramNode rootNode = sourceProgram.getRootNode();
		EncogProgram result = new EncogProgram(this);
		result.setRootNode(cloneBranch(result,rootNode));
		return result;
	}
	
	public ProgramNode cloneBranch(EncogProgram targetProgram, ProgramNode sourceBranch) {
		if( sourceBranch==null ) {
			throw new EncogError("Can't clone null branch.");
		}
		
		
		String name = sourceBranch.getName();
	
		// create any subnodes
		ProgramNode[] args = new ProgramNode[sourceBranch.getChildNodes().size()];
		for(int i=0;i<args.length;i++) {
			args[i] = cloneBranch(targetProgram,(ProgramNode)sourceBranch.getChildNodes().get(i));
		}
		
		ProgramNode result = targetProgram.getContext().getFunctions().factorFunction(name, targetProgram, args);
		
		// now copy the expression data for the node
		for(int i=0;i<sourceBranch.getData().length;i++) {
			result.getData()[i] = new ExpressionValue(sourceBranch.getData()[i]);
		}
		
		// return the new node
		return result;
	}

	public EncogProgram createProgram(String expression) {
		EncogProgram result = new EncogProgram(this);
		result.compileExpression(expression);
		return result;
	}

	public void loadAllFunctions() {
		StandardExtensions.createAll(getFunctions());
	}
	
	
	
}
