package org.encog.app.analyst.script.normalize;

import org.encog.app.quant.normalize.NormalizedField;

public class AnalystNormalize {
	private NormalizedField[] normalizedFields;
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
	 * @return the normalizedFields
	 */
	public NormalizedField[] getNormalizedFields() {
		return normalizedFields;
	}

	/**
	 * @param normalizedFields the normalizedFields to set
	 */
	public void setNormalizedFields(NormalizedField[] normalizedFields) {
		this.normalizedFields = normalizedFields;
	}

	public int calculateInputColumns(NormalizedField targetField) {
		int result = 0;
		for( NormalizedField field: this.normalizedFields ) {
			if( field!=targetField )
			result+=field.getColumnsNeeded();
		}
		return result;
	}

	public int calculateOutputColumns(NormalizedField targetField) {
		return targetField.getColumnsNeeded();
	}
}
