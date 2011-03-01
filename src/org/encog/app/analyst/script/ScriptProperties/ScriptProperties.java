package org.encog.app.analyst.script.ScriptProperties;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.encog.util.csv.CSVFormat;

public class ScriptProperties {

	public static final String HEADER_DATASOURCE_sourceFile = "HEADER_DATASOURCE_sourceFile";
	public static final String HEADER_DATASOURCE_rawFile = "HEADER_DATASOURCE_rawFile";
	public static final String HEADER_DATASOURCE_sourceFormat = "HEADER_DATASOURCE_sourceFormat";
	public static final String HEADER_DATASOURCE_sourceHeaders = "HEADER_DATASOURCE_sourceHeaders";
	public static final String SETUP_CONFIG_maxClassCount = "SETUP_CONFIG_maxClassCount";
	public static final String SETUP_CONFIG_allowedClasses = "SETUP_CONFIG_allowedClasses";
	public static final String SETUP_CONFIG_outputHeaders = "SETUP_CONFIG_outputHeaders";
	public static final String SETUP_CONFIG_inputHeaders = "SETUP_CONFIG_inputHeaders";
	public static final String SETUP_CONFIG_csvFormat = "SETUP_CONFIG_csvFormat";
	public static final String SETUP_FILENAMES_FILE_EVAL = "SETUP_FILENAMES_FILE_EVAL";
	public static final String SETUP_FILENAMES_FILE_NORMALIZE = "SETUP_FILENAMES_FILE_NORMALIZE";
	public static final String SETUP_FILENAMES_FILE_TRAINSET = "SETUP_FILENAMES_FILE_TRAINSET";
	public static final String SETUP_FILENAMES_FILE_TRAIN = "SETUP_FILENAMES_FILE_TRAIN";
	public static final String SETUP_FILENAMES_FILE_EG = "SETUP_FILENAMES_FILE_EG";
	public static final String SETUP_FILENAMES_FILE_RAW = "SETUP_FILENAMES_FILE_RAW";
	public static final String SETUP_FILENAMES_FILE_OUTPUT = "SETUP_FILENAMES_FILE_OUTPUT";
	public static final String SETUP_FILENAMES_FILE_RANDOMIZE = "SETUP_FILENAMES_FILE_RANDOMIZE";
	public static final String NORMALIZE_CONFIG_sourceFile = "NORMALIZE_CONFIG_sourceFile";
	public static final String NORMALIZE_CONFIG_targetFile = "NORMALIZE_CONFIG_targetFile";
	public static final String RANDOMIZE_CONFIG_sourceFile = "RANDOMIZE_CONFIG_sourceFile";
	public static final String RANDOMIZE_CONFIG_targetFile = "RANDOMIZE_CONFIG_targetFile";
	public static final String SEGREGATE_CONFIG_sourceFile = "SEGREGATE_CONFIG_sourceFile";
	public static final String GENERATE_CONFIG_sourceFile = "GENERATE_CONFIG_sourceFile";
	public static final String GENERATE_CONFIG_targetFile = "GENERATE_CONFIG_targetFile";
	public static final String GENERATE_CONFIG_input = "GENERATE_CONFIG_input";
	public static final String GENERATE_CONFIG_ideal = "GENERATE_CONFIG_ideal";
	public static final String ML_CONFIG_trainingFile = "ML_CONFIG_trainingFile";
	public static final String ML_CONFIG_evalFile = "ML_CONFIG_evalFile";
	public static final String ML_CONFIG_resourceFile = "ML_CONFIG_resourceFile";
	public static final String ML_CONFIG_outputFile = "ML_CONFIG_outputFile";
	public static final String ML_CONFIG_type = "ML_CONFIG_type";
	public static final String ML_CONFIG_architecture = "ML_CONFIG_architecture";
	public static final String ML_CONFIG_resourceName = "ML_CONFIG_resourceName";

	private final Map<String,Object> data = new HashMap<String,Object>();
	
	public void setProperty(String name,String value) {
		data.put(name, value);
	}
	
	public Object getProperty(String name) {
		return data.get(name);
	}
	
	public String getPropertyString(String name) {
		return data.get(name).toString();
	}

	public void setProperty(String name,
			CSVFormat format) {
		data.put(name, format);
		
	}

	public void setProperty(String name, boolean b) {
		data.put(name, b);		
	}

	public void setProperty(String name, File analyzeFile) {
		data.put(name, analyzeFile);
		
	}

	public void setProperty(String name, URL url) {
		data.put(name, url);
		
	}

	public String getPropertyFile(String name) {
		return (String)data.get(name);
		
	}

	public URL getPropertyURL(String name) {
		return (URL)data.get(name);
	}
}
