package org.encog.app.quant.filter;

/**
 * Used internally to track excluded fields from the FilterCSV.
 */
public class ExcludedField {

	/**
	 * The field number.
	 */
    private int fieldNumber;
    
    /**
     * The field value to filter on.
     */
    private String fieldValue;

    /**
     * Construct the object.
     * @param fieldNumber The field number.
     * @param fieldValue The field value to filter on.
     */
    public ExcludedField(int fieldNumber, String fieldValue)
    {
        this.fieldNumber = fieldNumber;
        this.fieldValue = fieldValue;
    }

	/**
	 * @return the fieldNumber
	 */
	public int getFieldNumber() {
		return fieldNumber;
	}

	/**
	 * @param fieldNumber the fieldNumber to set
	 */
	public void setFieldNumber(int fieldNumber) {
		this.fieldNumber = fieldNumber;
	}

	/**
	 * @return the fieldValue
	 */
	public String getFieldValue() {
		return fieldValue;
	}

	/**
	 * @param fieldValue the fieldValue to set
	 */
	public void setFieldValue(String fieldValue) {
		this.fieldValue = fieldValue;
	}
	
	/** {@inheritDoc} */
	public String toString() {
		StringBuilder result = new StringBuilder("[");
		result.append(getClass().getSimpleName());
		result.append(" fieldNumber=");
		result.append(this.fieldNumber);
		result.append(", value=");
		result.append(this.fieldValue);

		result.append("]");
		return result.toString();
	}
	
}
