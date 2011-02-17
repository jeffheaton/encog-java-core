package org.encog.app.analyst.script.generate;

public class AnalystGenerate {
	private String sourceFile = "";
	private String targetFile = "";
	private int input;
	private int ideal;
		
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
	 * @return the input
	 */
	public int getInput() {
		return input;
	}

	/**
	 * @param input the input to set
	 */
	public void setInput(int input) {
		this.input = input;
	}

	/**
	 * @return the ideal
	 */
	public int getIdeal() {
		return ideal;
	}

	/**
	 * @param ideal the ideal to set
	 */
	public void setIdeal(int ideal) {
		this.ideal = ideal;
	}
	
	
}
