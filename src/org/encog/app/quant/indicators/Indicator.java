package org.encog.app.quant.indicators;

import java.util.Map;

import org.encog.app.quant.QuantError;
import org.encog.app.quant.basic.BaseCachedColumn;

/**
 * An indicator, used by Encog.
 */
public abstract class Indicator extends BaseCachedColumn {

	/**
	 * Construct the indicator.
	 * @param name The indicator name.
	 * @param input Is this indicator used to predict?
	 * @param output Is this indicator what we are trying to predict.
	 */
    public Indicator(String name, boolean input, boolean output)        
    {
    	super(name,input,output);
    }

    /**
     * Require a specific type of underlying data.
     * @param data The data available.
     * @param item The type of data we are looking for.
     */
    public void Require(Map<String, BaseCachedColumn> data, String item)
    {
        if (!data.containsKey(item))
        {
            throw new QuantError("To use this indicator, the underlying data must contain: " + item);
        }
    }

    /**
     * @return The number of periods this indicator is for.
     */
    public abstract int getPeriods();

    /**
     * The beginning index.
     */
    private int beginningIndex;

    /**
     * The ending index.
     */
    private int endingIndex;

    /**
     * Calculate this indicator.
     * @param data The data available to this indicator.
     * @param length The length of data to use.
     */
    public abstract void calculate(Map<String, BaseCachedColumn> data, int length);

	/**
	 * @return the beginningIndex
	 */
	public int getBeginningIndex() {
		return beginningIndex;
	}

	/**
	 * @param beginningIndex the beginningIndex to set
	 */
	public void setBeginningIndex(int beginningIndex) {
		this.beginningIndex = beginningIndex;
	}

	/**
	 * @return the endingIndex
	 */
	public int getEndingIndex() {
		return endingIndex;
	}

	/**
	 * @param endingIndex the endingIndex to set
	 */
	public void setEndingIndex(int endingIndex) {
		this.endingIndex = endingIndex;
	}
	
    
}
