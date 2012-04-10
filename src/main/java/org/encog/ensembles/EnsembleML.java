/**
 * 
 */
package org.encog.ensembles;

import org.encog.ml.MLMethod;

/**
 * @author nitbix
 *
 */
public interface EnsembleML extends MLMethod {

	/**
	 * Set the dataset for this member
	 * @param dataSet
	 */
	public void setTrainingSet(EnsembleDataSet dataSet);

	/**
	 * Get the dataset for this member
	 * @return
	 */
	public EnsembleDataSet getTrainingSet();
}
