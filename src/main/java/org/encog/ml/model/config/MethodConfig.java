package org.encog.ml.model.config;

import org.encog.ml.data.versatile.VersatileMLDataSet;
import org.encog.ml.data.versatile.normalizers.strategies.NormalizationStrategy;

/**
 * Define normalization for a specific method.
 */
public interface MethodConfig {

	/**
	 * @return The method name.
	 */
	String getMethodName();

	/**
	 * Suggest a model architecture, based on a dataset.
	 * @param dataset The dataset.
	 * @return The model architecture.
	 */
	String suggestModelArchitecture(VersatileMLDataSet dataset);

	/**
	 * Suggest a normalization strategy based on a dataset.
	 * @param dataset The dataset.
	 * @param architecture The architecture.
	 * @return The strategy.
	 */
	NormalizationStrategy suggestNormalizationStrategy(VersatileMLDataSet dataset, String architecture);

	/**
	 * Suggest a training type.
	 * @return The training type.
	 */
	String suggestTrainingType();

	/**
	 * Suggest training arguments.
	 * @param trainingType The training type.
	 * @return The training arguments.
	 */
	String suggestTrainingArgs(String trainingType);

	/**
	 * Determine the needed output count.
	 * @param dataset The dataset.
	 * @return The needed output count.
	 */
	int determineOutputCount(VersatileMLDataSet dataset);

}
