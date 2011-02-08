package org.encog.app.quant.classify;

/**
 * The classification method to use.
 */
public enum ClassifyMethod {
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
    SingleField
}
