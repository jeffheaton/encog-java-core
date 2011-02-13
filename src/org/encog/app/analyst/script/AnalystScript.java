package org.encog.app.analyst.script;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.encog.app.analyst.AnalystError;

public class AnalystScript {

	private final EncogAnalystConfig config = new EncogAnalystConfig();
	private DataField[] fields;
	private NormalizedField[] normalizedFields;

	/**
	 * @return the config
	 */
	public EncogAnalystConfig getConfig() {
		return config;
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
	 * @return the normalizedFields
	 */
	public NormalizedField[] getNormalizedFields() {
		return normalizedFields;
	}

	/**
	 * @param normalizedFields the normalizedFields to set
	 */
	public void setNormalizedFields(NormalizedField[] normalizedFields) {
		this.normalizedFields = normalizedFields;
	}	
}
