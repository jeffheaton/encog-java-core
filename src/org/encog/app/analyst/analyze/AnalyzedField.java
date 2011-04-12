/*
 * Encog(tm) Core v3.0 - Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 
 * Copyright 2008-2011 Heaton Research, Inc.
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
package org.encog.app.analyst.analyze;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.encog.app.analyst.script.AnalystClassItem;
import org.encog.app.analyst.script.AnalystScript;
import org.encog.app.analyst.script.DataField;
import org.encog.app.analyst.script.prop.ScriptProperties;

/**
 * This class represents a field that the Encog Analyst is in the process of
 * analyzing. This class is used to track statistical information on the field
 * that will help the Encog analyst determine what type of field this is, and
 * how to normalize it.
 * 
 */
public class AnalyzedField extends DataField {

	/**
	 * Tge sum of all values of this field.
	 */
	private double total;
	
	/**
	 * The number of instances of this field.
	 */
	private int instances;
	
	/**
	 * The total for standard deviation calculation.
	 */
	private double devTotal;
	
	/**
	 * A mapping between the class names that the class items.
	 */
	private final Map<String, AnalystClassItem> classMap 
		= new HashMap<String, AnalystClassItem>();
	
	/**
	 * The analyst script that the results are saved to.
	 */
	private final AnalystScript script;

	/**
	 * Construct an analyzed field.
	 * @param theScript The script being analyzed.
	 * @param name The name of the field.
	 */
	public AnalyzedField(final AnalystScript theScript, final String name) {
		super(name);
		this.instances = 0;
		this.script = theScript;
	}

	/**
	 * Perform a pass one analysis of this field.
	 * @param str The current value.
	 */
	public final void analyze1(final String str) {

		boolean accountedFor = false;

		if (str.trim().length() == 0) {
			setComplete(false);
			return;
		}

		this.instances++;

		if (isReal()) {
			try {
				final double d = Double.parseDouble(str);
				setMax(Math.max(d, getMax()));
				setMin(Math.min(d, getMin()));
				this.total += d;
				accountedFor = true;
			} catch (final NumberFormatException ex) {
				setReal(false);
				if (!isInteger()) {
					setMax(0);
					setMin(0);
					setStandardDeviation(0);
				}
			}
		}

		if (isInteger()) {
			try {
				final int i = Integer.parseInt(str);
				setMax(Math.max(i, getMax()));
				setMin(Math.min(i, getMin()));
				if (!accountedFor) {
					this.total += i;
				}
			} catch (final NumberFormatException ex) {
				setInteger(false);
				if (!isReal()) {
					setMax(0);
					setMin(0);
					setStandardDeviation(0);
				}
			}
		}

		if (isClass()) {
			AnalystClassItem item;

			// is this a new class?
			if (!this.classMap.containsKey(str)) {
				item = new AnalystClassItem(str, str, 1);
				this.classMap.put(str, item);

				// do we have too many different classes?
				final int max = this.script.getProperties().getPropertyInt(
						ScriptProperties.SETUP_CONFIG_maxClassCount);
				if (this.classMap.size() > max) {
					setClass(false);
				}
			} else {
				item = this.classMap.get(str);
				item.increaseCount();
			}

		}
	}

	/**
	 * Perform a pass two analysis of this field.
	 * @param str The current value.
	 */
	public final void analyze2(final String str) {
		if (str.trim().length() == 0) {
			return;
		}

		if (isReal() || isInteger()) {
			final double d = Double.parseDouble(str);
			this.devTotal += Math.pow((d - getMean()), 2);
		}
	}

	/**
	 * Complete pass 1.
	 */
	public final void completePass1() {

		this.devTotal = 0;

		if (this.instances == 0) {
			setMean(0);
		} else {
			setMean(this.total / this.instances);
		}
	}

	/**
	 * Complete pass 2.
	 */
	public final void completePass2() {
		setStandardDeviation(Math.sqrt(this.devTotal / this.instances));
	}

	/**
	 * Finalize the field, and create a DataField.
	 * @return The new DataField.
	 */
	public final DataField finalizeField() {
		final DataField result = new DataField(getName());

		result.setName(getName());
		result.setMin(getMin());
		result.setMax(getMax());
		result.setMean(getMean());
		result.setStandardDeviation(getStandardDeviation());
		result.setInteger(isInteger());
		result.setReal(isReal());
		result.setClass(isClass());
		result.setComplete(isComplete());

		result.getClassMembers().clear();

		if (result.isClass()) {
			final List<AnalystClassItem> list = getClassMembers();
			result.getClassMembers().addAll(list);
		}

		return result;
	}

	/**
	 * Get the class members.
	 * @return The class members.
	 */
	@Override
	public final List<AnalystClassItem> getClassMembers() {
		final List<String> sorted = new ArrayList<String>();
		sorted.addAll(this.classMap.keySet());
		Collections.sort(sorted);

		final List<AnalystClassItem> result = new ArrayList<AnalystClassItem>();
		for (final String str : sorted) {
			result.add(this.classMap.get(str));
		}

		return result;
	}

	/** {@inheritDoc} */
	@Override
	public final String toString() {
		final StringBuilder result = new StringBuilder("[");
		result.append(getClass().getSimpleName());
		result.append(" total=");
		result.append(this.total);
		result.append(", instances=");
		result.append(this.instances);
		result.append("]");
		return result.toString();
	}
}
