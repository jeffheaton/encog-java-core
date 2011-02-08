package org.encog.app.quant.classify;

/**
 * A class item.
 */
public class ClassItem {
    /**
     * The name of the class.
     */
    public String name;

    /**
     * The index of the class.
     */
    public int index;

    /**
     * Construct the object.
     * @param name The name of the class.
     * @param index The index of the class.
     */
    public ClassItem(String name, int index)
    {
        this.name = name;
        this.index = index;
    }

    /**
     * @return The name of the class.
     */
	public String getName() {
		return name;
	}

	/**
	 * Set the name of the class.
	 * @param name The name of the class.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return The index of the class.
	 */
	public int getIndex() {
		return index;
	}

	/**
	 * Set the idnex of the class.
	 * @param index The index of the class.
	 */
	public void setIndex(int index) {
		this.index = index;
	}
    
    
}
