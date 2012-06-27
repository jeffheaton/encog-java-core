package org.encog.ml.factory.train;

import java.util.Map;

import org.encog.ml.MLMethod;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.factory.parse.ArchitectureParse;
import org.encog.ml.train.MLTrain;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.training.nm.NelderMeadTraining;
import org.encog.util.ParamsHolder;

public class NelderMeadFactory {
	/**
	 * Create a Nelder Mead trainer.
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

		//final double learningRate = holder.getDouble(
		//		MLTrainFactory.PROPERTY_LEARNING_RATE, false, 0.1);

		return new NelderMeadTraining((BasicNetwork) method, training);
	}
}
