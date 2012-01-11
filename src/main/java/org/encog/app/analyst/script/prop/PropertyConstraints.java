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

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.encog.EncogError;
import org.encog.app.analyst.AnalystError;
import org.encog.util.csv.CSVFormat;
import org.encog.util.csv.ReadCSV;
import org.encog.util.file.ResourceInputStream;

/**
 * Holds constant type information for each of the properties that the script
 * might have. This constant information allows values to be validated.  
 * This class is a singleton.
 * 
 */
public final class PropertyConstraints {

	/**
	 * The instance.
	 */
	private static PropertyConstraints instance;


	/**
	 * @return The instance.
	 */
	public static PropertyConstraints getInstance() {
		if (PropertyConstraints.instance == null) {
			PropertyConstraints.instance = new PropertyConstraints();
		}

		return PropertyConstraints.instance;
	}

	/**
	 * The property data.
	 */
	private final Map<String, List<PropertyEntry>> data 
		= new HashMap<String, List<PropertyEntry>>();

	/**
	 * Private constructor.
	 */
	private PropertyConstraints() {

		try {

			final InputStream is = ResourceInputStream
					.openResourceInputStream("org/encog/data/analyst.csv");
			final ReadCSV csv = new ReadCSV(is, false, CSVFormat.EG_FORMAT);

			while (csv.next()) {
				final String sectionStr = csv.get(0);
				final String nameStr = csv.get(1);
				final String typeStr = csv.get(2);

				// determine type
				PropertyType t = null;
				if ("boolean".equalsIgnoreCase(typeStr)) {
					t = PropertyType.TypeBoolean;
				} else if ("real".equalsIgnoreCase(typeStr)) {
					t = PropertyType.TypeDouble;
				} else if ("format".equalsIgnoreCase(typeStr)) {
					t = PropertyType.typeFormat;
				} else if ("int".equalsIgnoreCase(typeStr)) {
					t = PropertyType.TypeInteger;
				} else if ("list-string".equalsIgnoreCase(typeStr)) {
					t = PropertyType.TypeListString;
				} else if ("string".equalsIgnoreCase(typeStr)) {
					t = PropertyType.TypeString;
				} else {
					throw new AnalystError("Unknown type constraint: "
							+ typeStr);
				}

				final PropertyEntry entry = new PropertyEntry(t, nameStr,
						sectionStr);
				List<PropertyEntry> list;

				if (this.data.containsKey(sectionStr)) {
					list = this.data.get(sectionStr);
				} else {
					list = new ArrayList<PropertyEntry>();
					this.data.put(sectionStr, list);
				}

				list.add(entry);
			}

			csv.close();
			is.close();
		} catch (final IOException e) {
			throw new EncogError(e);
		}
	}

	/**
	 * Find an entry based on a string.
	 * @param v The property to find.
	 * @return The property entry data.
	 */
	public PropertyEntry findEntry(final String v) {
		final String[] cols = v.split("\\.");
		final String section = cols[0];
		final String subSection = cols[1];
		final String name = cols[2];
		return getEntry(section, subSection, name);
	}

	/**
	 * Get all entries for a section/subsection.
	 * @param section The section to find.
	 * @param subSection The subsection to find.
	 * @return A list of property entries.
	 */
	public List<PropertyEntry> getEntries(final String section,
			final String subSection) {
		final String key = section + ":" + subSection;
		return this.data.get(key);
	}

	/**
	 * Get a single property entry.  If the section and subsection do 
	 * not exist, an error is thrown.
	 * @param section The section.
	 * @param subSection The subsection.
	 * @param name The name of the property.
	 * @return The property entry, or null if not found.
	 */
	public PropertyEntry getEntry(final String section,
			final String subSection, final String name) {
		final String key = section.toUpperCase() + ":"
				+ subSection.toUpperCase();
		final List<PropertyEntry> list = this.data.get(key);
		if (list == null) {
			throw new AnalystError("Unknown section and subsection: " + section
					+ "." + subSection);
		}
		for (final PropertyEntry entry : list) {
			if (entry.getName().equalsIgnoreCase(name)) {
				return entry;
			}
		}

		return null;
	}

}
