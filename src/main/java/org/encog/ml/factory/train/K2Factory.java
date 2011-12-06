package org.encog.ml.factory.train;

import java.util.Map;

import org.encog.ml.MLMethod;
import org.encog.ml.bayesian.BayesianNetwork;
import org.encog.ml.bayesian.training.k2.TrainK2;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.factory.MLTrainFactory;
import org.encog.ml.factory.parse.ArchitectureParse;
import org.encog.ml.train.MLTrain;
import org.encog.util.ParamsHolder;

public class K2Factory {
	/**
	 * Create a K2 trainer.
	 * 
	 * @param method
	 *            The method to use.
	 * @param training
	 *            The training data to use.
	 * @param argsStr
	 *            The arguments to use.
	 * @return The newly created trainer.
	 */
	public final MLTrain create(final MLMethod method,
			final MLDataSet training, final String argsStr) {
		final Map<String, String> args = ArchitectureParse.parseParams(argsStr);
		final ParamsHolder holder = new ParamsHolder(args);

		final int maxParents = holder.getInt(
				MLTrainFactory.PROPERTY_MAX_PARENTS, false, 5);
		return new TrainK2((BayesianNetwork) method, training, maxParents);
	}
}
