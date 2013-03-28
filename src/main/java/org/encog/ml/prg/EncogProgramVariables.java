package org.encog.ml.prg;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.encog.ml.prg.expvalue.ExpressionValue;

public class EncogProgramVariables implements Serializable {
	/**
	 * The serial id.
	 */
	private static final long serialVersionUID = 1L;
	private final Map<String, Integer> varMap = new HashMap<String, Integer>();	
	private final List<ExpressionValue> variables = new ArrayList<ExpressionValue>();
	
	public ExpressionValue getVariable(final String name) {
		if( this.varMap.containsKey(name)) {
			int index = this.varMap.get(name);
			return this.variables.get(index);
		} else {
			return null;
		}
	}

	public synchronized void setVariable(final String name, final ExpressionValue value) {
		if( this.varMap.containsKey(name)) {
			int index = this.varMap.get(name);
			this.variables.set(index, value);
		} else {
			this.varMap.put(name, this.variables.size());
			this.variables.add(value);
		}
	}

	public boolean variableExists(final String name) {
		return this.varMap.containsKey(name);
	}

	public ExpressionValue getVariable(int i) {
		return this.variables.get(i);
	}

	public int getVariableIndex(String varName) {
		if( !variableExists(varName) ) {
			throw new ExpressionError("Undefined variable: " + varName);
		}
		
		return this.varMap.get(varName);
	}

	public String getVariableName(int idx) {
		for( Entry<String, Integer> entry: this.varMap.entrySet()) {
			if( entry.getValue()==idx) {
				return entry.getKey();
			}
		}
		
		throw new ExpressionError("No variable defined for index " + idx);
	}
	
	public void setVariable(final String name, final double d) {
		setVariable(name, new ExpressionValue(d));

	}
	
	public int size() {
		return this.varMap.size();
	}

	public void defineVariable(VariableMapping mapping) {
		if( this.varMap.containsKey(mapping.getName())) {
			throw new ExpressionError("Variable " + mapping.getName() + " already defined, simply set its value, do not redefine.");
		} else {
			this.varMap.put(mapping.getName(), this.variables.size());
			this.variables.add(new ExpressionValue(mapping.getVariableType()));
		}
	}

	public void setVariable(int index, double value) {
		this.variables.set(index, new ExpressionValue(value));
		
	}
}
