package org.encog.app.csv.sort;

/**
 *  Specifies how a field is to be sorted by SortCSV.
 */
public class SortedField {

	/**
	 * The index of the field.
	 */
    private int index;

    /**
     * True, if the field is to be sorted ascending.
     */
    private boolean ascending;

    /**
     * The type of sort.
     */
    private SortType sortType;

    /**
     * Construct the object.
     * @param index The index of the sorted field.
     * @param t The type of sort, the type of object.
     * @param ascending True, if this is an ascending sort.
     */
    public SortedField(int index, SortType t, boolean ascending)
    {
        this.index = index;
        this.ascending = ascending;
        this.sortType = t;
    }

	/**
	 * @return the index
	 */
	public int getIndex() {
		return index;
	}

	/**
	 * @param index the index to set
	 */
	public void setIndex(int index) {
		this.index = index;
	}

	/**
	 * @return the ascending
	 */
	public boolean isAscending() {
		return ascending;
	}

	/**
	 * @param ascending the ascending to set
	 */
	public void setAscending(boolean ascending) {
		this.ascending = ascending;
	}

	/**
	 * @return the sortType
	 */
	public SortType getSortType() {
		return sortType;
	}

	/**
	 * @param sortType the sortType to set
	 */
	public void setSortType(SortType sortType) {
		this.sortType = sortType;
	}
    
	/** {@inheritDoc} */
	public String toString() {
		StringBuilder result = new StringBuilder("[");
		result.append(getClass().getSimpleName());
		result.append(" index=");
		result.append(this.index);
		result.append(", type=");
		result.append(this.sortType);

		result.append("]");
		return result.toString();
	}
	
}
