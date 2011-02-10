package org.encog.app.quant.normalize;

import org.encog.Encog;

/**
 * This object holds the normalization stats for a column.  This includes
 * the actual and desired high-low range for this column.
 */
public class NormalizedFieldStats {

    /**
     * The actual high from the sample data.
     */
    private double actualHigh;

    /**
     * The actual low from the sample data.
     */
    private double actualLow;

    /**
     * The desired normalized high.
     */
    private double normalizedHigh;

    /**
     * The desired normalized low from the sample data.
     */
    private double normalizedLow;

    /**
     * The action that should be taken on this column.
     */
    private NormalizationDesired action;

    /**
     * The name of this column.
     */
    private String name;
    
    /**
     * Construct an object.
     * @param action The desired action.
     * @param name The name of this column.
     */
    public NormalizedFieldStats(NormalizationDesired action, String name) 
    {
    	this(action, name, 0, 0, 0, 0);
    }

    /**
     * Construct the field, with no defaults.
     * @param action The normalization action to take.
     * @param name The name of this field.
     * @param ahigh The actual high.
     * @param alow The actual low.
     * @param nhigh The normalized high.
     * @param nlow The normalized low.
     */
    public NormalizedFieldStats(NormalizationDesired action, String name, double ahigh, double alow, double nhigh, double nlow)
    {
        this.action = action;
        this.actualHigh = ahigh;
        this.actualLow = alow;
        this.normalizedHigh = nhigh;
        this.normalizedLow = nlow;
        this.name = name;
    }

    /**
     * Construct the object.
     * @param normalizedHigh The normalized high.
     * @param normalizedLow The normalized low.
     */
    public NormalizedFieldStats(double normalizedHigh, double normalizedLow)
    {
        this.normalizedHigh = normalizedHigh;
        this.normalizedLow = normalizedLow;
        this.actualHigh = Double.MIN_VALUE;
        this.actualLow = Double.MAX_VALUE;
        this.action = NormalizationDesired.Normalize;
    }

    /**
     * Construct the object with a range of 1 and -1.
     */
    public NormalizedFieldStats() 
    {
    	this(1, -1);
    }

    /**
     * Make this a pass-through field.
     */
    public void makePassThrough()
    {
        this.normalizedHigh = 0;
        this.normalizedLow = 0;
        this.actualHigh = 0;
        this.actualLow = 0;
        this.action = NormalizationDesired.PassThrough;
    }

    /**
     * Analyze the specified value.  Adjust min/max as needed.  Usually used only internally.
     * @param d The value to analyze.
     */
    public void analyze(double d)
    {
        this.actualHigh = Math.max(this.actualHigh, d);
        this.actualLow = Math.min(this.actualLow, d);
    }
    
    /**
     * Normalize the specified value.
     * @param value The value to normalize.
     * @return The normalized value.
     */
    public double normalize(double value)
    {
        return ((value - actualLow)
                / (actualHigh - actualLow))
                * (normalizedHigh - normalizedLow) + normalizedLow;
    }

    /**
     * Denormalize the specified value.
     * @param value The value to normalize.
     * @return The normalized value.
     */
    public double deNormalize(double value)
    {
        double result = ((actualLow - actualHigh) * value - normalizedHigh
                * actualLow + actualHigh * normalizedLow)
                / (normalizedLow - normalizedHigh);
        return result;
    }

    /**
     * Fix normalized fields that have a single value for the min/max.  Separate them by 2 units.
     */
    public void fixSingleValue()
    {
        if (action == NormalizationDesired.Normalize)
        {
            if (Math.abs(actualHigh - actualLow) < Encog.DEFAULT_DOUBLE_EQUAL)
            {
                actualHigh += 1;
                actualLow -= 1;
            }
        }
    }

    /**
     * @return The actual high for the field.
     */
	public double getActualHigh() {
		return actualHigh;
	}

	/**
	 * Set the actual high for the field.
	 * @param actualHigh The actual high for the field.
	 */
	public void setActualHigh(double actualHigh) {
		this.actualHigh = actualHigh;
	}

	/**
	 * @return The actual low for the field.
	 */
	public double getActualLow() {
		return actualLow;
	}

	/**
	 * Set the actual low for the field.
	 * @param actualLow The actual low for the field.
	 */
	public void setActualLow(double actualLow) {
		this.actualLow = actualLow;
	}

	/**
	 * @return The normalized high for the field.
	 */
	public double getNormalizedHigh() {
		return normalizedHigh;
	}

	/**
	 * Set the normalized high for the field.
	 * @param normalizedHigh The normalized high for the field.
	 */
	public void setNormalizedHigh(double normalizedHigh) {
		this.normalizedHigh = normalizedHigh;
	}

	/**
	 * @return The normalized low for the neural network.
	 */
	public double getNormalizedLow() {
		return normalizedLow;
	}

	/**
	 * Set the normalized low for the field.
	 * @param normalizedLow The normalized low for the field.
	 */
	public void setNormalizedLow(double normalizedLow) {
		this.normalizedLow = normalizedLow;
	}

	/**
	 * @return The action for the field.
	 */
	public NormalizationDesired getAction() {
		return action;
	}

	/**
	 * Set the action for the field.
	 * @param action The action for the field.
	 */
	public void setAction(NormalizationDesired action) {
		this.action = action;
	}

	/**
	 * @return The name of the field.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Set the name of the field.
	 * @param name The name of the field.
	 */
	public void setName(String name) {
		this.name = name;
	}
   	
}
