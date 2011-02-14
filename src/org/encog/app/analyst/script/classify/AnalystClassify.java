package org.encog.app.analyst.script.classify;

public class AnalystClassify {

	private ClassifyField[] fields;
	private String sourceFile = "";
	private String targetFile = "";
	
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
	 * @return the targetFile
	 */
	public String getTargetFile() {
		return targetFile;
	}
	/**
	 * @param targetFile the targetFile to set
	 */
	public void setTargetFile(String targetFile) {
		this.targetFile = targetFile;
	}
	/**
	 * @return the fields
	 */
	public ClassifyField[] getFields() {
		return fields;
	}
	/**
	 * @param fields the fields to set
	 */
	public void setFields(ClassifyField[] fields) {
		this.fields = fields;
	}
	
	
	
}
