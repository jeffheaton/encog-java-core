package org.encog.app.csv.basic;

/**
 * A column that is based off of a column in a CSV file.
 */
public class FileData extends BaseCachedColumn {

	/**
	 * The date.
	 */
	public static final String DATE = "date";

	/**
	 * The time.
	 */
	public static final String TIME = "time";

	/**
	 * The high value.
	 */
	public static final String HIGH = "high";

	/**
	 * The low value.
	 */
	public static final String LOW = "low";

	/**
	 * The open value.
	 */
	public static final String OPEN = "open";

	/**
	 * The close value.
	 */
	public static final String CLOSE = "close";

	/**
	 * The volume.
	 */
	public static final String VOLUME = "volume";

	/**
	 * The index of this field.
	 */
	private int index;

	/**
	 * Construct the object.
	 * 
	 * @param theName
	 *            The name of the object.
	 * @param theIndex
	 *            The index of the field.
	 * @param theInput
	 *            Is this field for input?
	 * @param theOutput
	 *            Is this field for output?
	 */
	public FileData(final String theName, final int theIndex,
			final boolean theInput, final boolean theOutput) {
		super(theName, theInput, theOutput);
		setOutput(theOutput);
		this.index = theIndex;
	}

	/**
	 * @return The index of this field.
	 */
	public final int getIndex() {
		return this.index;
	}

	/**
	 * Set the index of this field.
	 * 
	 * @param theIndex
	 *            The index of this field.
	 */
	public final void setIndex(final int theIndex) {
		this.index = theIndex;
	}
}
