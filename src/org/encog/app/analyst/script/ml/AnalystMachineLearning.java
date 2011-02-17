package org.encog.app.analyst.script.ml;

public class AnalystMachineLearning {
	private String MLType = "";
	private String MLArchitecture = "";
	private String trainingFile = "";
	private String resourceFile = "";
	private String outputFile = "";
	private String evalFile = "";
	private String resourceName = "";
	
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
	/**
	 * @return the resourceFile
	 */
	public String getResourceFile() {
		return resourceFile;
	}
	/**
	 * @param resourceFile the resourceFile to set
	 */
	public void setResourceFile(String resourceFile) {
		this.resourceFile = resourceFile;
	}
	/**
	 * @return the outputFile
	 */
	public String getOutputFile() {
		return outputFile;
	}
	/**
	 * @param outputFile the outputFile to set
	 */
	public void setOutputFile(String outputFile) {
		this.outputFile = outputFile;
	}
	/**
	 * @return the evalFile
	 */
	public String getEvalFile() {
		return evalFile;
	}
	/**
	 * @param evalFile the evalFile to set
	 */
	public void setEvalFile(String evalFile) {
		this.evalFile = evalFile;
	}
	/**
	 * @return the trainingFile
	 */
	public String getTrainingFile() {
		return trainingFile;
	}
	/**
	 * @param trainingFile the trainingFile to set
	 */
	public void setTrainingFile(String trainingFile) {
		this.trainingFile = trainingFile;
	}
	
	
	
}
