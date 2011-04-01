package org.encog.app.analyst.script.segregate;


/**
 * This class specifies a target for the segregation process.
 *
 */
public class AnalystSegregateTarget {
	private String file;
	private int percent;
	
	public AnalystSegregateTarget(String file, int percent) {
		super();
		this.file = file;
		this.percent = percent;
	}
	/**
	 * @return the file
	 */
	public String getFile() {
		return file;
	}
	/**
	 * @param file the file to set
	 */
	public void setFile(String file) {
		this.file = file;
	}
	/**
	 * @return the percent
	 */
	public int getPercent() {
		return percent;
	}
	/**
	 * @param percent the percent to set
	 */
	public void setPercent(int percent) {
		this.percent = percent;
	}
	
	/** {@inheritDoc} */
	public String toString() {
		StringBuilder result = new StringBuilder("[");
		result.append(getClass().getSimpleName());
		result.append(" file=");
		result.append(this.file.toString());
		result.append(", percent=");
		result.append(this.file);
		result.append("]");
		return result.toString();
	}
	
}
