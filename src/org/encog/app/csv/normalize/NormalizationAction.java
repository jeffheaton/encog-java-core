package org.encog.app.csv.normalize;

/**
 * Normalization actions desired.
 */
public enum NormalizationAction
{
    /**
     * Do not normalize the column, just allow it to pass through.  This allows 
     * string fields to pass through as well.
     */
    PassThrough,

    /**
     * Normalize this column.
     */
    Normalize,

    /**
     * Ignore this column, do not include in the output.
     */
    Ignore,
    
	/// <summary>
    /// Use the "one-of" classification method.
    /// </summary>
    OneOf,

    /// <summary>
    /// Use the equilateral classification method.
    /// </summary>
    Equilateral,

    /// <summary>
    /// Use a single-field classification method.
    /// </summary>
    SingleField;

	public boolean isClassify() {		
		return (this == OneOf) || (this==SingleField) || (this==Equilateral) ;
	}

}
