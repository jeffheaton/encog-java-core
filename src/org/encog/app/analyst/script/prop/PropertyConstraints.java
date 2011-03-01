package org.encog.app.analyst.script.prop;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.encog.EncogError;
import org.encog.persist.location.ResourcePersistence;
import org.encog.util.csv.CSVFormat;
import org.encog.util.csv.ReadCSV;

public class PropertyConstraints {

	public final String HEADER_DATASOURCE_sourceFile = "HEADER_DATASOURCE_sourceFile";
	public final String HEADER_DATASOURCE_rawFile = "HEADER_DATASOURCE_rawFile";
	public final String HEADER_DATASOURCE_sourceFormat = "HEADER_DATASOURCE_sourceFormat";
	public final String HEADER_DATASOURCE_sourceHeaders = "HEADER_DATASOURCE_sourceHeaders";
	public final String SETUP_CONFIG_maxClassCount = "SETUP_CONFIG_maxClassCount";
	public final String SETUP_CONFIG_allowedClasses = "SETUP_CONFIG_allowedClasses";
	public final String SETUP_CONFIG_outputHeaders = "SETUP_CONFIG_outputHeaders";
	public final String SETUP_CONFIG_inputHeaders = "SETUP_CONFIG_inputHeaders";
	public final String SETUP_CONFIG_csvFormat = "SETUP_CONFIG_csvFormat";
	public final String SETUP_FILENAMES_FILE_EVAL = "SETUP_FILENAMES_FILE_EVAL";
	public final String SETUP_FILENAMES_FILE_NORMALIZE = "SETUP_FILENAMES_FILE_NORMALIZE";
	public final String SETUP_FILENAMES_FILE_TRAINSET = "SETUP_FILENAMES_FILE_TRAINSET";
	public final String SETUP_FILENAMES_FILE_TRAIN = "SETUP_FILENAMES_FILE_TRAIN";
	public final String SETUP_FILENAMES_FILE_EG = "SETUP_FILENAMES_FILE_EG";
	public final String SETUP_FILENAMES_FILE_RAW = "SETUP_FILENAMES_FILE_RAW";
	public final String SETUP_FILENAMES_FILE_OUTPUT = "SETUP_FILENAMES_FILE_OUTPUT";
	public final String SETUP_FILENAMES_FILE_RANDOMIZE = "SETUP_FILENAMES_FILE_RANDOMIZE";
	public final String NORMALIZE_CONFIG_sourceFile = "NORMALIZE_CONFIG_sourceFile";
	public final String NORMALIZE_CONFIG_targetFile = "NORMALIZE_CONFIG_targetFile";
	public final String RANDOMIZE_CONFIG_sourceFile = "RANDOMIZE_CONFIG_sourceFile";
	public final String RANDOMIZE_CONFIG_targetFile = "RANDOMIZE_CONFIG_targetFile";
	public final String SEGREGATE_CONFIG_sourceFile = "SEGREGATE_CONFIG_sourceFile";
	public final String GENERATE_CONFIG_sourceFile = "GENERATE_CONFIG_sourceFile";
	public final String GENERATE_CONFIG_targetFile = "GENERATE_CONFIG_targetFile";
	public final String GENERATE_CONFIG_input = "GENERATE_CONFIG_input";
	public final String GENERATE_CONFIG_ideal = "GENERATE_CONFIG_ideal";
	public final String ML_CONFIG_trainingFile = "ML_CONFIG_trainingFile";
	public final String ML_CONFIG_evalFile = "ML_CONFIG_evalFile";
	public final String ML_CONFIG_resourceFile = "ML_CONFIG_resourceFile";
	public final String ML_CONFIG_outputFile = "ML_CONFIG_outputFile";
	public final String ML_CONFIG_type = "ML_CONFIG_type";
	public final String ML_CONFIG_architecture = "ML_CONFIG_architecture";
	public final String ML_CONFIG_resourceName = "ML_CONFIG_resourceName";

	private static PropertyConstraints instance;
	private final Map<String,List<PropertyEntry>> data = new HashMap<String,List<PropertyEntry>>();

	private PropertyConstraints() {
		String currentClass = null;
		try {
			final ResourcePersistence resource = new ResourcePersistence(
					"org/encog/data/analyst.csv");
			final InputStream is = resource.createInputStream();
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
				
				PropertyEntry entry = new PropertyEntry(t,nameStr,sectionStr);				
				List<PropertyEntry> list;
				
				if( this.data.containsKey(sectionStr)) {
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
