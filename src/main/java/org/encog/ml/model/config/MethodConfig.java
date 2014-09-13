package org.encog.ml.model.config;

import org.encog.ml.data.versatile.NormalizationStrategy;
import org.encog.ml.data.versatile.VersatileMLDataSet;

public interface MethodConfig {

	String getMethodName();

	String suggestModelArchitecture(VersatileMLDataSet dataset);

	NormalizationStrategy suggestNormalizationStrategy(VersatileMLDataSet dataset, String architecture);

	String suggestTrainingType();

	String suggestTrainingArgs(String trainingType);

}
