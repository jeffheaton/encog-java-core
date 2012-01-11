/*
 * Encog(tm) Core v3.1 - Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 
 * Copyright 2008-2012 Heaton Research, Inc.
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
package org.encog.util.arrayutil;


/**
 * This class specifies how fields are to be used by the TemporalWindowCSV
 * class.
 */
public class TemporalWindowField {

	/**
	 * The action that is to be taken on this field.
	 */
	private TemporalType action;

	/**
	 * The name of this field.
	 */
	private String name;

	/**
	 * The last value of this field. Used internally.
	 */
	private String lastValue;

	/**
	 * Construct the object.
	 * 
	 * @param theName
	 *            The name of the field to be considered.
	 */
	public TemporalWindowField(final String theName) {
		this.name = theName;
	}

	/**
	 * @return the action
	 */
	public final TemporalType getAction() {
		return this.action;
	}

	/**
	 * @return Returns true, if this field is to be used as part of the input
	 *         for a prediction.
	 */
	public final boolean getInput() {
		return ((this.action == TemporalType.Input) 
				|| (this.action == TemporalType.InputAndPredict));
	}

	/**
	 * @return the lastValue
	 */
	public final String getLastValue() {
		return this.lastValue;
	}

	/**
	 * @return the name
	 */
	public final String getName() {
		return this.name;
	}

	/**
	 * @return Returns true, if this field is part of what is being predicted.
	 */
	public final boolean getPredict() {
		return ((this.action == TemporalType.Predict) 
				|| (this.action == TemporalType.InputAndPredict));
	}

	/**
	 * @param theAction
	 *            the action to set
	 */
	public final void setAction(final TemporalType theAction) {
		this.action = theAction;
	}

	/**
	 * @param theLastValue
	 *            the lastValue to set
	 */
	public final void setLastValue(final String theLastValue) {
		this.lastValue = theLastValue;
	}

	/**
	 * @param theName
	 *            the name to set
	 */
	public final void setName(final String theName) {
		this.name = theName;
	}

	/** {@inheritDoc} */
	@Override
	public final String toString() {
		final StringBuilder result = new StringBuilder("[");
		result.append(getClass().getSimpleName());
		result.append(" name=");
		result.append(this.name);
		result.append(", action=");
		result.append(this.action);

		result.append("]");
		return result.toString();
	}
}
