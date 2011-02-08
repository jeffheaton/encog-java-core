package org.encog.app.quant.basic;

/**
 * A basic cached column.  Used internally by some of the Encog CSV quant classes.
 * All of the file contents for this column are loaded into memory.
 */
public class BaseCachedColumn {

	/**
	 * The data for this column.
	 */
    private double[] data;
    
    /**
     * The name of this column.
     */
    private String name;
    
    /**
     * Is this column used for output?
     */
    private boolean output;
    
    /**
     * Is this column used for input?
     */
    private boolean input;
    
    /**
     * Should this column be ignored.
     */
    private boolean ignore;


    /**
     * @return The data for this column.
     */
    public double[] getData() 
    { 
    	return data; 
    } 

    /**
     * Construct the cached column.
     * @param name The name of the column.
     * @param input Is this column used for input?
     * @param output Is this column used for output?
     */
    public BaseCachedColumn(String name, boolean input, boolean output)
    {
        this.name = name;
        this.input = input;
        this.output = output;
        this.ignore = false;
    }

    /**
     * Allocate enough space for this column.
     * @param length The length of this column.
     */
    public void allocate(int length)
    {
        this.data = new double[length];
    }

    /**
     * @return The name of this column
     */
	public String getName() {
		return name;
	}

	/**
	 * Set the name of this column.
	 * @param name The name of this column.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return Is this column used for output?
	 */
	public boolean isOutput() {
		return output;
	}

	/**
	 * Set if this column is used for output.
	 * @param output Is this column used for output.
	 */
	public void setOutput(boolean output) {
		this.output = output;
	}

	/**
	 * @return Is this column used for input?
	 */
	public boolean isInput() {
		return input;
	}

	/**
	 * Set if this column is used for input.
	 * @param input Is this column used for input.
	 */
	public void setInput(boolean input) {
		this.input = input;
	}

	/**
	 * @return Is this column ignored?
	 */
	public boolean isIgnore() {
		return ignore;
	}

	/**
	 * Set if this column is to be ignored?
	 * @param ignore True, if this column is to be ignored.
	 */
	public void setIgnore(boolean ignore) {
		this.ignore = ignore;
	}
   
}
