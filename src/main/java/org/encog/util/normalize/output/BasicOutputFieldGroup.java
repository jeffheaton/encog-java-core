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

import java.util.ArrayList;
import java.util.Collection;

/**
 * Provides very basic functionality that other output field groups
 * will use.  Mainly provides the list of fields that are grouped.
 *
 */
public abstract class BasicOutputFieldGroup implements OutputFieldGroup {

	/**
	 * The fields in this group.
	 */
	private final Collection<OutputFieldGrouped> fields = 
		new ArrayList<OutputFieldGrouped>();

	/**
	 * Add a field to this group.
	 * @param field The field to add to the group.
	 */
	public void addField(final OutputFieldGrouped field) {
		this.fields.add(field);
	}

	/**
	 * @return The list of grouped fields.
	 */
	public Collection<OutputFieldGrouped> getGroupedFields() {
		return this.fields;
	}
}
