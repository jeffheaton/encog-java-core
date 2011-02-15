package org.encog.app.analyst.script.classify;

public class AnalystClassify {

	private ClassifyField[] classifiedFields;
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
	 * @return the classifiedFields
	 */
	public ClassifyField[] getClassifiedFields() {
		return classifiedFields;
	}
	/**
	 * @param classifiedFields the classifiedFields to set
	 */
	public void setClassifiedFields(ClassifyField[] classifiedFields) {
		this.classifiedFields = classifiedFields;
	}
	
	
}
