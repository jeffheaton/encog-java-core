package org.encog.app.analyst.script;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.encog.app.analyst.AnalystFileFormat;
import org.encog.app.analyst.script.normalize.AnalystNormalize;
import org.encog.app.analyst.script.prop.ScriptProperties;
import org.encog.app.analyst.script.segregate.AnalystSegregate;
import org.encog.app.analyst.script.task.AnalystTask;
import org.encog.app.quant.normalize.NormalizedField;
import org.encog.util.csv.CSVFormat;

public class AnalystScript {

	private DataField[] fields;
	private final AnalystNormalize normalize = new AnalystNormalize();
	private final AnalystSegregate segregate = new AnalystSegregate();
	private final Set<String> generated = new HashSet<String>();
	private final Map<String,AnalystTask> tasks = new HashMap<String,AnalystTask>();
	private final ScriptProperties properties = new ScriptProperties();
	private String basePath;

	public AnalystScript() {
		this.properties.setProperty(ScriptProperties.SETUP_CONFIG_csvFormat, AnalystFileFormat.DECPNT_COMMA);
		this.properties.setProperty(ScriptProperties.SETUP_CONFIG_maxClassCount, 50);
		this.properties.setProperty(ScriptProperties.SETUP_CONFIG_allowedClasses, "integer,string");
		this.properties.setProperty(ScriptProperties.SETUP_CONFIG_outputHeaders, true);
	}
	
	/**
	 * @return the fields
	 */
	public DataField[] getFields() {
		return fields;
	}

	/**
	 * @param fields the fields to set
	 */
	public void setFields(DataField[] fields) {
		this.fields = fields;
	}


	public void save(OutputStream stream) {
		ScriptSave s = new ScriptSave(this);
		s.save(stream);
	}
	
	public void load(InputStream stream) {
		ScriptLoad s = new ScriptLoad(this);
		s.load(stream);
	}

	/**
	 * @return the normalize
	 */
	public AnalystNormalize getNormalize() {
		return normalize;
	}

	public DataField findDataField(String name) {
		for(DataField dataField: this.fields) {
			if( dataField.getName().equalsIgnoreCase(name))
				return dataField;
		}
		
		return null;
	}
		
	
	public void markGenerated(String filename) {
		this.generated.add(filename);
	}
	
	public boolean isGenerated(String filename) {
		return this.generated.contains(filename);
	}

	/**
	 * @return the segregate
	 */
	public AnalystSegregate getSegregate() {
		return segregate;
	}

	public boolean expectInputHeaders(String filename)
	{
		if( isGenerated(filename) )
			return this.properties.getPropertyBoolean(ScriptProperties.SETUP_CONFIG_outputHeaders); 
		else
			return this.properties.getPropertyBoolean(ScriptProperties.SETUP_CONFIG_inputHeaders);
	}

	public void clearTasks()
	{
		this.tasks.clear();
	}
	
	public AnalystTask getTask(String name) {
		if( !this.tasks.containsKey(name) ) {
			return null;
		}
		return this.tasks.get(name);
	}
	
	public void addTask(AnalystTask task) {
		this.tasks.put(task.getName(), task);
	}

	public Map<String, AnalystTask> getTasks() {
		return this.tasks;		
	}

	public void init() {
		this.normalize.init(this);		
	}

	/**
	 * @return the properties
	 */
	public ScriptProperties getProperties() {
		return properties;
	}

	public CSVFormat determineInputFormat(String sourceID) {
		String rawID = getProperties().getPropertyString(ScriptProperties.HEADER_DATASOURCE_rawFile);
		CSVFormat result;
		
		if( sourceID.equals(rawID)) {
			result = getProperties().getPropertyCSVFormat(ScriptProperties.HEADER_DATASOURCE_sourceFormat);
		} else {
			result = getProperties().getPropertyCSVFormat(ScriptProperties.SETUP_CONFIG_csvFormat);
		}
		
		return result;
	}
	
	public CSVFormat determineOutputFormat() {		
		return getProperties().getPropertyCSVFormat(ScriptProperties.SETUP_CONFIG_csvFormat);
	}

	public NormalizedField findNormalizedField(String name) {
		for(NormalizedField field: this.getNormalize().getNormalizedFields()) {
			if( field.getName().equalsIgnoreCase(name))
				return field;
		}
		
		return null;
	}

	public String getBasePath() {
		return basePath;
	}

	public void setBasePath(String basePath) {
		this.basePath = basePath;
	}

	public File resolveFilename(String sourceID) {
		String name = this.getProperties().getFilename(sourceID);
		
		if( this.basePath!=null )
			return new File(this.basePath,name);
		else
			return new File(name);
	}
	
	
	
}
