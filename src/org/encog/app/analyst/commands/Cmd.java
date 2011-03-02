package org.encog.app.analyst.commands;

import org.encog.app.analyst.EncogAnalyst;
import org.encog.app.analyst.script.AnalystScript;
import org.encog.app.analyst.script.prop.ScriptProperties;

public abstract class Cmd {
	private final EncogAnalyst analyst;
	private final AnalystScript script;
	private final ScriptProperties properties;

	public Cmd(EncogAnalyst analyst) {
		this.analyst = analyst;
		this.script = analyst.getScript();
		this.properties = this.script.getProperties();
	}

	public EncogAnalyst getAnalyst() {
		return analyst;
	}

	public AnalystScript getScript() {
		return script;
	}

	public ScriptProperties getProp() {
		return this.properties;
	}
	
	public abstract boolean executeCommand();
	public abstract String getName();

}
