package org.encog.app.quant.segregate;

import java.io.File;

/**
 * Specifies a segregation target, and what percent that target should need.
 *
 */
public class SegregateTargetPercent {
	
	/**
	 * Percent that this target should get.
	 */
    private int percent;

    /**
     * Used internally to track the number of items remaining for this target.
     */
    private int numberRemaining;

    /**
     * Used internally to hold the target filename.
     */
    private File filename;

    /**
     * Construct the object.
     * @param outputFile The output filename.
     * @param percent The target percent.
     */
    public SegregateTargetPercent(File outputFile, int percent)
    {
        this.percent = percent;
        this.filename = outputFile;
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

	/**
	 * @return the numberRemaining
	 */
	public int getNumberRemaining() {
		return numberRemaining;
	}

	/**
	 * @param numberRemaining the numberRemaining to set
	 */
	public void setNumberRemaining(int numberRemaining) {
		this.numberRemaining = numberRemaining;
	}

	/**
	 * @return the filename
	 */
	public File getFilename() {
		return filename;
	}

	/**
	 * @param filename the filename to set
	 */
	public void setFilename(File filename) {
		this.filename = filename;
	}
    
    
}
