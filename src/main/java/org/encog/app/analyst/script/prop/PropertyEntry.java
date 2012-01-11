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
package org.encog.app.analyst.script.prop;

import org.encog.app.analyst.AnalystError;
import org.encog.app.analyst.util.ConvertStringConst;

/**
 * A property entry for the Encog Analyst. Properties have a name and section.
 * 
 */
public class PropertyEntry implements Comparable<PropertyEntry> {

	/**
	 * Put a property in dot form, which is "section.subsection.name".
	 * @param section The section.
	 * @param subSection The subsection.
	 * @param name The name.
	 * @return The property in dot form.
	 */
	public static String dotForm(final String section, final String subSection,
			final String name) {
		final StringBuilder result = new StringBuilder();
		result.append(section);
		result.append('.');
		result.append(subSection);
		result.append('.');
		result.append(name);
		return result.toString();
	}

	/**
	 * The type of property.
	 */
	private final PropertyType entryType;
	
	/**
	 * The name of the property.
	 */
	private final String name;

	/**
	 * The section of the property.
	 */
	private final String section;

	/**
	 * Construct a property entry.
	 * @param theEntryType The entry type.
	 * @param theName The name of the property.
	 * @param theSection The section of the property.
	 */
	public PropertyEntry(final PropertyType theEntryType, final String theName,
			final String theSection) {
		super();
		this.entryType = theEntryType;
		this.name = theName;
		this.section = theSection;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final int compareTo(final PropertyEntry o) {
		return this.name.compareTo(o.name);
	}

	/**
	 * @return the entryType
	 */
	public final PropertyType getEntryType() {
		return this.entryType;
	}

	/**
	 * @return The key.
	 */
	public final String getKey() {
		return this.section + "_" + this.name;
	}

	/**
	 * @return the name
	 */
	public final String getName() {
		return this.name;
	}

	/**
	 * @return the section
	 */
	public final String getSection() {
		return this.section;
	}

	/** {@inheritDoc} */
	@Override
	public final String toString() {
		final StringBuilder result = new StringBuilder("[");
		result.append(getClass().getSimpleName());
		result.append(" name=");
		result.append(this.name);
		result.append(", section=");
		result.append(this.section);
		result.append("]");
		return result.toString();
	}

	/**
	 * Validate the specified property.
	 * @param theSection The section.
	 * @param subSection The sub section.
	 * @param theName The name of the property.
	 * @param value The value of the property.
	 */
	public final void validate(final String theSection, final String subSection,
			final String theName, final String value) {
		if ((value == null) || (value.length() == 0)) {
			return;
		}

		try {
			switch (getEntryType()) {
			case TypeBoolean:
				if ((Character.toUpperCase(value.charAt(0)) != 'T')
						&& (Character.toUpperCase(value.charAt(0)) != 'F')) {
					final StringBuilder result = new StringBuilder();
					result.append("Illegal boolean for ");
					result.append(PropertyEntry.dotForm(section, subSection,
							name));
					result.append(", value is ");
					result.append(value);
					result.append(".");
					throw new AnalystError(result.toString());
				}
				break;
			case TypeDouble:
				Double.parseDouble(value);
				break;
			case typeFormat:
				if (ConvertStringConst.string2AnalystFileFormat(value) 
						== null) {
					final StringBuilder result = new StringBuilder();
					result.append("Invalid file format for ");
					result.append(PropertyEntry.dotForm(section, subSection,
							name));
					result.append(", value is ");
					result.append(value);
					result.append(".");
					throw new AnalystError(result.toString());
				}
				break;
			case TypeInteger:
				Integer.parseInt(value);
				break;
			case TypeListString:
				break;
			case TypeString:
				break;
			default:
				throw new AnalystError("Unsupported property type.");
			}
		} catch (final NumberFormatException ex) {
			final StringBuilder result = new StringBuilder();
			result.append("Illegal value for ");
			result.append(PropertyEntry.dotForm(section, subSection, name));
			result.append(", expecting a ");
			result.append(getEntryType().toString());
			result.append(", but got ");
			result.append(value);
			result.append(".");
			throw new AnalystError(result.toString());
		}
	}
}
