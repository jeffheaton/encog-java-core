package org.encog.app.analyst.script.preprocess;

import java.util.ArrayList;
import java.util.List;

import org.encog.app.analyst.script.AnalystScript;
import org.encog.app.analyst.script.normalize.AnalystField;

public class AnalystPreprocess {
	
	private final List<FieldPreprocess> calculatedFields 
		= new ArrayList<FieldPreprocess>();
	
	/**
	 * The parent script.
	 */
	private AnalystScript script;

	/**
	 * Construct the object.
	 * @param theScript The script.
	 */
	public AnalystPreprocess(AnalystScript theScript) {
		this.script = theScript;
	}

	public AnalystScript getScript() {
		return script;
	}

	public void setScript(AnalystScript script) {
		this.script = script;
	}

	public List<FieldPreprocess> getCalculatedFields() {
		return calculatedFields;
	}
	
	
}
