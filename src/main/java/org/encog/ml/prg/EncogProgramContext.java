package org.encog.ml.prg;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.encog.EncogError;
import org.encog.ml.prg.expvalue.ExpressionValue;
import org.encog.ml.prg.expvalue.ValueType;
import org.encog.ml.prg.extension.FunctionFactory;
import org.encog.ml.prg.extension.StandardExtensions;
import org.encog.util.csv.CSVFormat;

public class EncogProgramContext implements Serializable {
	/**
	 * The serial id.
	 */
	private static final long serialVersionUID = 1L;
	private final CSVFormat format;
	private final FunctionFactory functions;
	private final List<VariableMapping> definedVariables = new ArrayList<VariableMapping>();
	private final Map<String, VariableMapping> map = new HashMap<String, VariableMapping>();
	private VariableMapping result = new VariableMapping(null,ValueType.floatingType);

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

	public void defineVariable(String theName) {
		defineVariable(theName, ValueType.floatingType, false, 0, 0);
	}

	public void defineVariable(String theName, ValueType theVariableType) {
		defineVariable(theName, theVariableType, false, 0, 0);
	}

	public void defineVariable(String theName, ValueType theVariableType,
			boolean theIsEnum, int theEnumType, int theEnumValueCount) {
		VariableMapping mapping = new VariableMapping(theName, theVariableType,
				theIsEnum, theEnumType, theEnumValueCount);
		defineVariable(mapping);

	}

	public void defineVariable(VariableMapping mapping) {
		if (this.map.containsKey(mapping.getName())) {
			throw new ExpressionError("Variable " + mapping.getName()
					+ " already defined.");
		}
		this.map.put(mapping.getName(), mapping);
		definedVariables.add(mapping);
	}

	public List<VariableMapping> getDefinedVariables() {
		return this.definedVariables;
	}

	public EncogProgram cloneProgram(EncogProgram sourceProgram) {
		ProgramNode rootNode = sourceProgram.getRootNode();
		EncogProgram result = new EncogProgram(this);
		result.setRootNode(cloneBranch(result, rootNode));
		return result;
	}

	public ProgramNode cloneBranch(EncogProgram targetProgram,
			ProgramNode sourceBranch) {
		if (sourceBranch == null) {
			throw new EncogError("Can't clone null branch.");
		}

		String name = sourceBranch.getName();

		// create any subnodes
		ProgramNode[] args = new ProgramNode[sourceBranch.getChildNodes()
				.size()];
		for (int i = 0; i < args.length; i++) {
			args[i] = cloneBranch(targetProgram, (ProgramNode) sourceBranch
					.getChildNodes().get(i));
		}

		ProgramNode result = targetProgram.getContext().getFunctions()
				.factorFunction(name, targetProgram, args);

		// now copy the expression data for the node
		for (int i = 0; i < sourceBranch.getData().length; i++) {
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

	public void clearDefinedVariables() {
		this.definedVariables.clear();
		this.map.clear();
	}

	/**
	 * @return the result
	 */
	public VariableMapping getResult() {
		return result;
	}

	/**
	 * @param result the result to set
	 */
	public void setResult(VariableMapping result) {
		this.result = result;
	}

	public int getMaxEnumType() {
		int r = -1;
		
		// make sure we consider the result
		if( this.result.isEnum() ) {
			r = this.result.getEnumType();
		}
		
		// loop over all mappings and find the max enum type
		for(VariableMapping mapping: this.definedVariables) {
			if( mapping.isEnum()  ) {
				r = Math.max(r, mapping.getEnumType());
			}
		}
		
		// if we did not find one then there are no enum types
		if( r==-1) {
			throw new ExpressionError("No enum types defined in context.");
		}
		
		return r;
	}

	public int getEnumCount(int enumType) {
		
		// make sure we consider the result
		if( this.result.isEnum() && this.result.getEnumType()==enumType ) {
			return this.result.getEnumValueCount();
		}
		
		for(VariableMapping mapping: this.definedVariables) {
			if( mapping.isEnum() ) {
				if( mapping.getEnumType()==enumType ) {
					return mapping.getEnumValueCount();
				}
			}
		}
		throw new ExpressionError("Undefined enum type: " + enumType);
	}
	
	public boolean hasEnum() {
		if( this.result.isEnum() ) {
			return true;
		}
		
		for(VariableMapping mapping: this.definedVariables) {
			if( mapping.isEnum() ) {
				return true;
			}
		}
		
		return false;
	}

}
