package org.encog.app.quant.normalize;

/**
 * Normalization is the process where data is adjusted to be inside a range.  This
 * range is typically -1 to 1.  For more information about normalization, refer to 
 * the following page.
 * 
 * http://www.heatonresearch.com/content/really-simple-introduction-normalization
 * 
 * This class is used to normalize an array.  Sometimes you would like to normalize
 * an array, rather than an entire CSV file.  If you would like to normalize an entire
 * CSV file, you should make use of the class NormalizeCSV.
 */
public class NormalizeArray {

    /**
     * Contains stats about the array normalized.
     */
    private NormalizedFieldStats stats;

    /**
     * @return Contains stats about the array normalized.
     */
    public NormalizedFieldStats getStats()
    { 
    	return this.stats; 
    }

    /**
     * The high end of the range that the values are normalized into.  Typically 1.
     */
    private double normalizedHigh;

    /**
     * The low end of the range that the values are normalized into.  Typically 1.
     */
    private double normalizedLow;

    /**
     * Construct the object, default NormalizedHigh and NormalizedLow to 1 and -1.
     */
    public NormalizeArray()
    {
        this.normalizedHigh = 1;
        this.normalizedLow = -1;
    }

    /**
     * Normalize the array.  Return the new normalized array.
     * @param inputArray The input array.
     * @return The normalized array.
     */
    public double[] process(double[] inputArray)
    {
        this.stats = new NormalizedFieldStats();
        this.stats.setNormalizedHigh( normalizedHigh );
        this.stats.setNormalizedLow( normalizedLow );

        for (int i = 0; i < inputArray.length; i++)
        {
            stats.analyze(inputArray[i]);
        }

        double[] result = new double[inputArray.length];

        for (int i = 0; i < inputArray.length; i++)
        {
            result[i] = this.stats.normalize(inputArray[i]);
        }

        return result;
    }

	
}
