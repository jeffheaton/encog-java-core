package org.encog.app.quant.temporal;

/**
 * Operations that the temporal class may perform on fields.
 */
public enum TemporalType
{
    /**
     * This field is used as part of the input.  However, if you wish to use the field
     * for prediction as well, specify InputAndPredict.
     */
    Input,

    /**
     * This field is used as part of the prediction.  However, if you wish to use the field
     * for input as well, specify InputAndPredict.
     */
    Predict,

    /**
     * This field is used for both input and prediction.
     */
    InputAndPredict,

    /**
     * This field should be ignored.
     */
    Ignore,

    /**
     * This field should pass through, to the output file, without modification.
     */
    PassThrough
}
