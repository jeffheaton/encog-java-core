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
package org.encog.util.normalize.output;

import org.encog.util.normalize.input.InputField;

/**
 * Defines an output field that can be grouped.  Groupable classes
 * will extend this class.
 *
 */
public abstract class OutputFieldGrouped extends BasicOutputField {

	/**
	 * The group that this field is a member of.
	 */
	private OutputFieldGroup group;

	/**
	 * The source field, this is the input field that provides data
	 * for this output field.
	 */
	private InputField sourceField;

	/**
	 * Default constructor, used mainly for reflection.
	 */
	public OutputFieldGrouped() {
	}

	/**
	 * Construct a grouped output field.
	 * @param group The group that this field belongs to.
	 * @param sourceField The source field for this output field.
	 */
	public OutputFieldGrouped(final OutputFieldGroup group,
			final InputField sourceField) {
		this.group = group;
		this.sourceField = sourceField;
		this.group.getGroupedFields().add(this);
	}

	/**
	 * @return The group that this field belongs to.
	 */
	public OutputFieldGroup getGroup() {
		return this.group;
	}

	/**
	 * @return The source field for this output field.
	 */
	public InputField getSourceField() {
		return this.sourceField;
	}

}
