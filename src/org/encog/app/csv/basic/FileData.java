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
	 * @param name
	 *            The name of the object.
	 * @param index
	 *            The index of the field.
	 * @param input
	 *            Is this field for input?
	 * @param output
	 *            Is this field for output?
	 */
	public FileData(String name, int index, boolean input, boolean output) {
		super(name, input, output);
		this.setOutput(output);
		this.index = index;
	}

	/**
	 * @return The index of this field.
	 */
	public int getIndex() {
		return index;
	}

	/**
	 * Set the index of this field.
	 * 
	 * @param index
	 *            The index of this field.
	 */
	public void setIndex(int index) {
		this.index = index;
	}
}
