package org.encog.app.analyst.script.segregate;

import org.encog.app.analyst.script.normalize.NormalizedField;

public class AnalystSegregate {
	
	private AnalystSegregateTarget[] segregateTargets;
	private String sourceFile = "";
		
	/**
	 * @return the sourceFile
	 */
	public String getSourceFile() {
		return sourceFile;
	}

	/**
	 * @param sourceFile the sourceFile to set
	 */
	public void setSourceFile(String sourceFile) {
		this.sourceFile = sourceFile;
	}

	/**
	 * @return the segregateTargets
	 */
	public AnalystSegregateTarget[] getSegregateTargets() {
		return segregateTargets;
	}

	/**
	 * @param segregateTargets the segregateTargets to set
	 */
	public void setSegregateTargets(AnalystSegregateTarget[] segregateTargets) {
		this.segregateTargets = segregateTargets;
	}

	

	
}
