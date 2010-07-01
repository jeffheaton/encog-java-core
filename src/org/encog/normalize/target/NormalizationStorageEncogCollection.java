package org.encog.normalize.target;

import org.encog.normalize.DataNormalization;
import org.encog.persist.annotations.EGAttribute;

public class NormalizationStorageEncogCollection implements
		NormalizationStorage {

	@EGAttribute
	private String resourceName;
	
	@Override
	public void close() {
		// TODO Auto-generated method stub

	}

	@Override
	public void open(DataNormalization norm) {
		// TODO Auto-generated method stub

	}

	@Override
	public void write(double[] data, int inputCount) {
		// TODO Auto-generated method stub

	}

	/**
	 * @return the resourceName
	 */
	public String getResourceName() {
		return resourceName;
	}

	/**
	 * @param resourceName the resourceName to set
	 */
	public void setResourceName(String resourceName) {
		this.resourceName = resourceName;
	}
	
	

}