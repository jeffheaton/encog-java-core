package org.encog.app.quant.normalize;

/**
 * Normalization actions desired.
 */
public enum NormalizationDesired
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
    Ignore
}
