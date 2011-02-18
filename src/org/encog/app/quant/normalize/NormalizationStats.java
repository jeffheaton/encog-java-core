package org.encog.app.quant.normalize;

/**
 * This object holds the normalization stats for a column.  This includes
   the actual and desired high-low range for this column.
 */
public class NormalizationStats {

    /**
     * @return Get the number of columns.
     */
    public int size()
    {
            return this.stats.length;
    }

    /**
     * Access the normalized column data.
     */
    private NormalizedField[] stats;

    /**
     * Create a new object.
     * @param count The number of columns.
     */
    public NormalizationStats(int count)
    {
        this.stats = new NormalizedField[count];
    }

    /**
     *  Scan all columns and fix any columns where the min/max are the same value.
     * You cannot normalize when the min/max are the same values.
     */
    public void fixSingleValue()
    {
        for(NormalizedField stat : this.stats)
        {
            stat.fixSingleValue();
        }
    }

    /**
     * @return The field stats.
     */
	public NormalizedField[] getStats() {
		return stats;
	}

	/**
	 * Set the field stats.
	 * @param stats The field stats.
	 */
	public void setStats(NormalizedField[] stats) {
		this.stats = stats;
	}
   	
}
