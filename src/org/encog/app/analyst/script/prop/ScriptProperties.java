package org.encog.app.analyst.script.prop;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.encog.app.analyst.AnalystError;
import org.encog.util.csv.CSVFormat;

public class ScriptProperties {

	public static final String HEADER_DATASOURCE_sourceFile = "HEADER:DATASOURCE_sourceFile";
	public static final String HEADER_DATASOURCE_rawFile = "HEADER:DATASOURCE_rawFile";
	public static final String HEADER_DATASOURCE_sourceFormat = "HEADER:DATASOURCE_sourceFormat";
	public static final String HEADER_DATASOURCE_sourceHeaders = "HEADER:DATASOURCE_sourceHeaders";
	public static final String SETUP_CONFIG_maxClassCount = "SETUP:CONFIG_maxClassCount";
	public static final String SETUP_CONFIG_allowedClasses = "SETUP:CONFIG_allowedClasses";
	public static final String SETUP_CONFIG_outputHeaders = "SETUP:CONFIG_outputHeaders";
	public static final String SETUP_CONFIG_inputHeaders = "SETUP:CONFIG_inputHeaders";
	public static final String SETUP_CONFIG_csvFormat = "SETUP:CONFIG_csvFormat";
	public static final String NORMALIZE_CONFIG_sourceFile = "NORMALIZE:CONFIG_sourceFile";
	public static final String NORMALIZE_CONFIG_targetFile = "NORMALIZE:CONFIG_targetFile";
	public static final String RANDOMIZE_CONFIG_sourceFile = "RANDOMIZE:CONFIG_sourceFile";
	public static final String RANDOMIZE_CONFIG_targetFile = "RANDOMIZE:CONFIG_targetFile";
	public static final String SEGREGATE_CONFIG_sourceFile = "SEGREGATE:CONFIG_sourceFile";
	public static final String GENERATE_CONFIG_sourceFile = "GENERATE:CONFIG_sourceFile";
	public static final String GENERATE_CONFIG_targetFile = "GENERATE:CONFIG_targetFile";
	public static final String GENERATE_CONFIG_input = "GENERATE:CONFIG_input";
	public static final String GENERATE_CONFIG_ideal = "GENERATE:CONFIG_ideal";
	public static final String ML_CONFIG_trainingFile = "ML:CONFIG_trainingFile";
	public static final String ML_CONFIG_evalFile = "ML:CONFIG_evalFile";
	public static final String ML_CONFIG_resourceFile = "ML:CONFIG_resourceFile";
	public static final String ML_CONFIG_outputFile = "ML:CONFIG_outputFile";
	public static final String ML_CONFIG_type = "ML:CONFIG_type";
	public static final String ML_CONFIG_architecture = "ML:CONFIG_architecture";
	public static final String ML_CONFIG_resourceName = "ML:CONFIG_resourceName";
	public static final String ML_TRAIN_type = "ML:TRAIN_type";
	public static final String ML_TRAIN_arguments = "ML:TRAIN_arguments";
	public static final String ML_TRAIN_targetError = "ML:TRAIN_targetError";

	private final Map<String, String> data = new HashMap<String, String>();

	public void setProperty(String name, String value) {
		data.put(name, value);
	}

	public Object getProperty(String name) {
		return data.get(name);
	}

	public String getPropertyString(String name) {
		if (!data.containsKey(name)) {
			return null;
		}
		return data.get(name).toString();
	}

	public void setProperty(String name, CSVFormat format) {
		
		if( format.getDecimal()=='.' ) {
			data.put(name, "decpnt");
		} else if( format.getDecimal()==',' ) {
			data.put(name, "deccomma");
		}
	}

	public void setProperty(String name, boolean b) {
		data.put(name, b?"t":"f");
	}

	public void setProperty(String name, File analyzeFile) {
		data.put(name, analyzeFile.toString());

	}

	public void setProperty(String name, URL url) {
		data.put(name, url.toExternalForm());

	}

	public String getPropertyFile(String name) {
		return (String) data.get(name);

	}
	
	public CSVFormat getPropertyFormat(String name) {
		String value = data.get(name);
		
		if( value.equals("deccomma") ) {
			return CSVFormat.DECIMAL_COMMA;
		} else if( value.equals("decpnt") ) {
			return CSVFormat.DECIMAL_POINT;
		} else {
			return null;
		}
	}

	public URL getPropertyURL(String name) {
		try {
			return new URL(data.get(name));
		} catch (MalformedURLException e) {
			throw new AnalystError(e);
		}
	}

	public void clearFilenames() {
		Object[] array = this.data.keySet().toArray();
		for (int i = 0; i < array.length; i++) {
			String key = (String) array[i];
			if (key.startsWith("SETUP:FILENAMES")) {
				this.data.remove(key);
			}
		}
	}

	public List<String> getFilenames() {
		List<String> result = new ArrayList<String>();
		for (String key : this.data.keySet()) {
			if (key.startsWith("SETUP:FILENAMES")) {
				int index = key.indexOf('_');
				if (index != -1) {
					result.add(key.substring(index + 1));
				}
			}
		}
		return result;
	}
	
	public void setFilename(String key, String value) {
		String key2 = "SETUP:FILENAMES_" + key;
		this.data.put(key2, value);
		
	}
	public String getFilename(String file) {
		String key2 = "SETUP:FILENAMES_" + file;
		
		if( !this.data.containsKey(key2) ) {
			throw new AnalystError("Undefined file: " + file);
		}
		
		return (String)this.data.get(key2);
	}

	public boolean getPropertyBoolean(String name) {
		if( !data.containsKey(name) )
			return false;
		else
			return data.get(name).toLowerCase().startsWith("t");
	}

	public int getPropertyInt(String name) {
		try {
			String value = this.data.get(name);
			if( value==null ) {
				return 0;
			}
			return Integer.parseInt(value);
		} catch(NumberFormatException ex)
		{
			throw new AnalystError(ex);
		}
	}

	public void setProperty(String name, int i) {
		this.data.put(name, ""+i);
		
	}

	public void setProperty(String name, double d) {
		this.data.put(name, CSVFormat.EG_FORMAT.format(d, 5));		
	}
}
