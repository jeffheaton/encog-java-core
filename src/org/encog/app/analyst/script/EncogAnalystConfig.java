package org.encog.app.analyst.script;

public class EncogAnalystConfig {
	private int maxClassSize = 50;
	private boolean allowIntClasses = true;
	private boolean allowRealClasses = false;
	private boolean allowStringClasses = true;
	/**
	 * @return the maxClassSize
	 */
	public int getMaxClassSize() {
		return maxClassSize;
	}
	/**
	 * @param maxClassSize the maxClassSize to set
	 */
	public void setMaxClassSize(int maxClassSize) {
		this.maxClassSize = maxClassSize;
	}
	/**
	 * @return the allowIntClasses
	 */
	public boolean isAllowIntClasses() {
		return allowIntClasses;
	}
	/**
	 * @param allowIntClasses the allowIntClasses to set
	 */
	public void setAllowIntClasses(boolean allowIntClasses) {
		this.allowIntClasses = allowIntClasses;
	}
	/**
	 * @return the allowRealClasses
	 */
	public boolean isAllowRealClasses() {
		return allowRealClasses;
	}
	/**
	 * @param allowRealClasses the allowRealClasses to set
	 */
	public void setAllowRealClasses(boolean allowRealClasses) {
		this.allowRealClasses = allowRealClasses;
	}
	/**
	 * @return the allowStringClasses
	 */
	public boolean isAllowStringClasses() {
		return allowStringClasses;
	}
	/**
	 * @param allowStringClasses the allowStringClasses to set
	 */
	public void setAllowStringClasses(boolean allowStringClasses) {
		this.allowStringClasses = allowStringClasses;
	}
	
	
}
