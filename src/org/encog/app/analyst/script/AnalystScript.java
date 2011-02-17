package org.encog.app.analyst.script;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.Set;

import org.encog.app.analyst.script.classify.AnalystClassify;
import org.encog.app.analyst.script.generate.AnalystGenerate;
import org.encog.app.analyst.script.normalize.AnalystNormalize;
import org.encog.app.analyst.script.normalize.NormalizedField;
import org.encog.app.analyst.script.randomize.AnalystRandomize;
import org.encog.app.analyst.script.segregate.AnalystSegregate;

public class AnalystScript {

	private final EncogAnalystConfig config = new EncogAnalystConfig();
	private DataField[] fields;
	private final AnalystNormalize normalize = new AnalystNormalize();
	private final AnalystClassify classify = new AnalystClassify();
	private final AnalystRandomize randomize = new AnalystRandomize();
	private final AnalystSegregate segregate = new AnalystSegregate();
	private final AnalystGenerate generate = new AnalystGenerate();
	private final Set<String> generated = new HashSet<String>();
	

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
	 * @return the normalize
	 */
	public AnalystNormalize getNormalize() {
		return normalize;
	}

	public DataField findDataField(String name) {
		for(DataField dataField: this.fields) {
			if( dataField.getName().equals(name))
				return dataField;
		}
		
		return null;
	}

	/**
	 * @return the classify
	 */
	public AnalystClassify getClassify() {
		return classify;
	}
		
	
	public void markGenerated(String filename) {
		this.generated.add(filename);
	}
	
	public boolean isGenerated(String filename) {
		return this.generated.contains(filename);
	}
	
	/**
	 * @return the randomize
	 */
	public AnalystRandomize getRandomize() {
		return randomize;
	}
	
	/**
	 * @return the generate
	 */
	public AnalystGenerate getGenerate() {
		return generate;
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
			return this.config.isOutputHeaders();
		else
			return this.config.isInputHeaders();
	}
}
