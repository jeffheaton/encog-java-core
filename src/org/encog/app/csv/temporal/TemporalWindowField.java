package org.encog.app.csv.temporal;

/**
 * This class specifies how fields are to be used by the TemporalWindowCSV class.
 */
public class TemporalWindowField {

	/**
	 * Construct the object.
	 * @param name The name of the field to be considered.
	 */
    public TemporalWindowField(String name)
    {
        this.name = name;
    }

    /**
     * @return Returns true, if this field is to be used as part of the input for a prediction.
     */
    public boolean getInput()
    {
            return( action==TemporalType.Input || action==TemporalType.InputAndPredict );     
    }

    /**
     * @return Returns true, if this field is part of what is being predicted.
     */
    public boolean getPredict()
    {
            return (action == TemporalType.Predict || action == TemporalType.InputAndPredict);     
    }

    /**
     * The action that is to be taken on this field.
     */
    private TemporalType action;

    /**
     * The name of this field.
     */
    private String name;

    /**
     * The last value of this field.  Used internally.
     */
    private String lastValue;

	/**
	 * @return the action
	 */
	public TemporalType getAction() {
		return action;
	}

	/**
	 * @param action the action to set
	 */
	public void setAction(TemporalType action) {
		this.action = action;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the lastValue
	 */
	public String getLastValue() {
		return lastValue;
	}

	/**
	 * @param lastValue the lastValue to set
	 */
	public void setLastValue(String lastValue) {
		this.lastValue = lastValue;
	}
	
	/** {@inheritDoc} */
	public String toString() {
		StringBuilder result = new StringBuilder("[");
		result.append(getClass().getSimpleName());
		result.append(" name=");
		result.append(this.name);
		result.append(", action=");
		result.append(this.action);

		result.append("]");
		return result.toString();
	}
}
