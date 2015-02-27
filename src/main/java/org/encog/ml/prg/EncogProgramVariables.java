/*
 * Encog(tm) Core v3.3 - Java Version
 * http://www.heatonresearch.com/encog/
 * https://github.com/encog/encog-java-core
 
 * Copyright 2008-2014 Heaton Research, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *   
 * For more information on Heaton Research copyrights, licenses 
 * and trademarks visit:
 * http://www.heatonresearch.com/copyright
 */
package org.encog.ml.prg;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.encog.ml.ea.exception.EARuntimeError;
import org.encog.ml.prg.expvalue.ExpressionValue;

/**
 * This class stores the actual variable values for an Encog Program. The
 * definitions for the variables are stored in the context.
 */
public class EncogProgramVariables implements Serializable {
	/**
	 * The serial id.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * A map to the index values of each variable name.
	 */
	private final Map<String, Integer> varMap = new HashMap<String, Integer>();

	/**
	 * The values of each variable. The order lines up to the order defined in
	 * the context.
	 */
	private final List<ExpressionValue> variables = new ArrayList<ExpressionValue>();

	/**
	 * Define the specified variable mapping. This is to be used by the context
	 * to setup the variable definitions. Do not call it directly. You will have
	 * unexpected results if you have a variable defined in this class, but not
	 * in the context.
	 * 
	 * @param mapping
	 *            The variable mapping.
	 */
	public void defineVariable(final VariableMapping mapping) {
		if (this.varMap.containsKey(mapping.getName())) {
			throw new EARuntimeError(
					"Variable "
							+ mapping.getName()
							+ " already defined, simply set its value, do not redefine.");
		} else {
			this.varMap.put(mapping.getName(), this.variables.size());
			this.variables.add(new ExpressionValue(mapping.getVariableType()));
		}
	}

	/**
	 * Get a variable value by index.
	 * 
	 * @param i
	 *            The index of the variable we are using.
	 * @return The variable at the specified index.
	 */
	public ExpressionValue getVariable(final int i) {
		return this.variables.get(i);
	}

	/**
	 * Get a variable value by name.
	 * 
	 * @param name
	 *            The name of the variable we are using.
	 * @return The variable at the specified index.
	 */
	public ExpressionValue getVariable(final String name) {
		if (this.varMap.containsKey(name)) {
			final int index = this.varMap.get(name);
			return this.variables.get(index);
		} else {
			return null;
		}
	}

	/**
	 * Get a variable index by name.
	 * 
	 * @param varName
	 *            The variable name.
	 * @return The index of the specified variable.
	 */
	public int getVariableIndex(final String varName) {
		if (!variableExists(varName)) {
			throw new EARuntimeError("Undefined variable: " + varName);
		}

		return this.varMap.get(varName);
	}

	/**
	 * Get a variable name by index.
	 * 
	 * @param idx
	 *            The variable index.
	 * @return The variable name.
	 */
	public String getVariableName(final int idx) {
		for (final Entry<String, Integer> entry : this.varMap.entrySet()) {
			if (entry.getValue() == idx) {
				return entry.getKey();
			}
		}

		throw new EARuntimeError("No variable defined for index " + idx);
	}

	/**
	 * Set a variable floating point value by index.
	 * 
	 * @param index
	 *            The index.
	 * @param value
	 *            The value.
	 */
	public void setVariable(final int index, final double value) {
		this.variables.set(index, new ExpressionValue(value));

	}

	/**
	 * Set a floating point variable value by name.
	 * 
	 * @param name
	 *            The name.
	 * @param d
	 *            The value.
	 */
	public void setVariable(final String name, final double d) {
		setVariable(name, new ExpressionValue(d));
	}

	/**
	 * Set a variable value by name.
	 * 
	 * @param name
	 *            The variable name.
	 * @param value
	 *            The value.
	 */
	public synchronized void setVariable(final String name,
			final ExpressionValue value) {
		if (this.varMap.containsKey(name)) {
			final int index = this.varMap.get(name);
			this.variables.set(index, value);
		} else {
			this.varMap.put(name, this.variables.size());
			this.variables.add(value);
		}
	}

	/**
	 * @return Get the number of variables defined.
	 */
	public int size() {
		return this.varMap.size();
	}

	/**
	 * Determine if the specified variable name exists.
	 * 
	 * @param name The name of the variable.
	 * @return True if the name already exists.
	 */
	public boolean variableExists(final String name) {
		return this.varMap.containsKey(name);
	}
}
