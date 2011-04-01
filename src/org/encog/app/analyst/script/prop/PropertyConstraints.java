package org.encog.app.analyst.script.prop;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.encog.EncogError;
import org.encog.util.csv.CSVFormat;
import org.encog.util.csv.ReadCSV;
import org.encog.util.file.ResourceInputStream;

/**
 * Holds constant type information for each of the properties that the script
 * might have. This constant information allows values to be validated.
 * 
 */
public class PropertyConstraints {

	private static PropertyConstraints instance;
	private final Map<String, List<PropertyEntry>> data = new HashMap<String, List<PropertyEntry>>();

	private PropertyConstraints() {

		try {

			InputStream is = ResourceInputStream
					.openResourceInputStream("org/encog/data/analyst.csv");
			ReadCSV csv = new ReadCSV(is, false, CSVFormat.EG_FORMAT);

			while (csv.next()) {
				String sectionStr = csv.get(0);
				String nameStr = csv.get(1);
				String typeStr = csv.get(2);

				// determine type
				PropertyType t = null;
				if ("boolean".equalsIgnoreCase(typeStr)) {
					t = PropertyType.TypeBoolean;
				} else if ("double".equalsIgnoreCase(typeStr)) {
					t = PropertyType.TypeDouble;
				} else if ("format".equalsIgnoreCase(typeStr)) {
					t = PropertyType.typeFormat;
				} else if ("integer".equalsIgnoreCase(typeStr)) {
					t = PropertyType.TypeInteger;
				} else if ("double".equalsIgnoreCase(typeStr)) {
					t = PropertyType.TypeListString;
				} else if ("string".equalsIgnoreCase(typeStr)) {
					t = PropertyType.TypeString;
				}

				PropertyEntry entry = new PropertyEntry(t, nameStr, sectionStr);
				List<PropertyEntry> list;

				if (this.data.containsKey(sectionStr)) {
					list = data.get(sectionStr);
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

	public static PropertyConstraints getInstance() {
		if (instance == null) {
			instance = new PropertyConstraints();
		}

		return instance;
	}

	public List<PropertyEntry> getEntries(String section, String subSection) {
		String key = section + ":" + subSection;
		return this.data.get(key);
	}

}
