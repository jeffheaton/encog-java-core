package org.encog.app.csv.normalize;

import org.encog.Encog;
import org.encog.util.csv.CSVFormat;

/**
 * This object holds the normalization stats for a column.  This includes
   the actual and desired high-low range for this column.
 */
public class NormalizationStats {

	private CSVFormat format;
	private int precision;
	
	public NormalizationStats() {
		this.format = CSVFormat.DECIMAL_POINT;
		this.precision = Encog.DEFAULT_PRECISION;
	}
	
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
    	this();
        this.stats = new NormalizedField[count];
    }

    public NormalizationStats(NormalizedField[] normFields) {
		this();
		this.stats = normFields;
	}

	/**
     *  Scan all columns and fix any columns where the min/max are the same value.
     * You cannot normalize when the min/max are the same values.
     * @param owner 
     */
    public void init()
    {
        for(NormalizedField stat : this.stats)
        {
            stat.fixSingleValue();
            stat.init(this);
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

	/**
	 * @return the format
	 */
	public CSVFormat getFormat() {
		return format;
	}

	/**
	 * @param format the format to set
	 */
	public void setFormat(CSVFormat format) {
		this.format = format;
	}

	/**
	 * @return the precision
	 */
	public int getPrecision() {
		return precision;
	}

	/**
	 * @param precision the precision to set
	 */
	public void setPrecision(int precision) {
		this.precision = precision;
	}   	
}
