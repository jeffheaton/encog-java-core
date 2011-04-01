package org.encog.app.analyst.commands;

import org.encog.app.analyst.EncogAnalyst;
import org.encog.app.analyst.script.AnalystScript;
import org.encog.app.analyst.script.prop.ScriptProperties;

/**
 * Base class for Encog Analyst commands. This class defines the properties sent
 * to a command.
 * 
 */
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

	/** {@inheritDoc} */
	public String toString() {
		StringBuilder result = new StringBuilder("[");
		result.append(getClass().getSimpleName());
		result.append(" name=");
		result.append(this.getName());
		result.append("]");
		return result.toString();
	}

}
