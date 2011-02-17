package org.encog.app.analyst.script.ml;

public class AnalystMachineLearning {
	private String MLType = "";
	private String MLArchitecture = "";
	private String sourceFile = "";
	private String targetFile = "";
	private String resourceName = "";
	
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
	 * @return the resourceName
	 */
	public String getResourceName() {
		return resourceName;
	}
	/**
	 * @param resourceName the resourceName to set
	 */
	public void setResourceName(String resourceName) {
		this.resourceName = resourceName;
	}
	/**
	 * @return the mLType
	 */
	public String getMLType() {
		return MLType;
	}
	/**
	 * @param mLType the mLType to set
	 */
	public void setMLType(String mLType) {
		MLType = mLType;
	}
	/**
	 * @return the mLArchitecture
	 */
	public String getMLArchitecture() {
		return MLArchitecture;
	}
	/**
	 * @param mLArchitecture the mLArchitecture to set
	 */
	public void setMLArchitecture(String mLArchitecture) {
		MLArchitecture = mLArchitecture;
	}
	
	
	
}
