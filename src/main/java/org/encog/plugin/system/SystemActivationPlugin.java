package org.encog.plugin.system;

import org.encog.EncogError;
import org.encog.engine.network.activation.ActivationBiPolar;
import org.encog.engine.network.activation.ActivationCompetitive;
import org.encog.engine.network.activation.ActivationFunction;
import org.encog.engine.network.activation.ActivationGaussian;
import org.encog.engine.network.activation.ActivationLOG;
import org.encog.engine.network.activation.ActivationLinear;
import org.encog.engine.network.activation.ActivationRamp;
import org.encog.engine.network.activation.ActivationSIN;
import org.encog.engine.network.activation.ActivationSigmoid;
import org.encog.engine.network.activation.ActivationSoftMax;
import org.encog.engine.network.activation.ActivationStep;
import org.encog.engine.network.activation.ActivationTANH;
import org.encog.ml.MLMethod;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.factory.MLActivationFactory;
import org.encog.ml.train.MLTrain;
import org.encog.plugin.EncogPluginBase;
import org.encog.plugin.EncogPluginService1;
import org.encog.util.csv.CSVFormat;
import org.encog.util.csv.NumberList;

public class SystemActivationPlugin implements EncogPluginService1 {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final String getPluginDescription() {
		return "This plugin provides the built in machine " +
				"learning methods for Encog.";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final String getPluginName() {
		return "HRI-System-Methods";
	}

	/**
	 * @return This is a type-1 plugin.
	 */
	@Override
	public final int getPluginType() {
		return 1;
	}
	
	private ActivationFunction allocateAF(String name) {
		if (name.equalsIgnoreCase(MLActivationFactory.AF_BIPOLAR)) {
			return new ActivationBiPolar();
		}

		if (name.equalsIgnoreCase(MLActivationFactory.AF_COMPETITIVE)) {
			return new ActivationCompetitive();
		}

		if (name.equalsIgnoreCase(MLActivationFactory.AF_GAUSSIAN)) {
			return new ActivationGaussian();
		}

		if (name.equalsIgnoreCase(MLActivationFactory.AF_LINEAR)) {
			return new ActivationLinear();
		}

		if (name.equalsIgnoreCase(MLActivationFactory.AF_LOG)) {
			return new ActivationLOG();
		}

		if (name.equalsIgnoreCase(MLActivationFactory.AF_RAMP)) {
			return new ActivationRamp();
		}

		if (name.equalsIgnoreCase(MLActivationFactory.AF_SIGMOID)) {
			return new ActivationSigmoid();
		}

		if (name.equalsIgnoreCase(MLActivationFactory.AF_SIN)) {
			return new ActivationSIN();
		}

		if (name.equalsIgnoreCase(MLActivationFactory.AF_SOFTMAX)) {
			return new ActivationSoftMax();
		}

		if (name.equalsIgnoreCase(MLActivationFactory.AF_STEP)) {
			return new ActivationStep();
		}

		if (name.equalsIgnoreCase(MLActivationFactory.AF_TANH)) {
			return new ActivationTANH();
		}

		return null;
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public ActivationFunction createActivationFunction(String fn) {
		String name;
		double[] params;

		int index = fn.indexOf('[');
		if (index != -1) {
			name = fn.substring(0, index).toLowerCase();
			int index2 = fn.indexOf(']');
			if (index2 == -1) {
				throw new EncogError(
						"Unbounded [ while parsing activation function.");
			}
			String a = fn.substring(index + 1, index2);
			params = NumberList.fromList(CSVFormat.EG_FORMAT, a);

		} else {
			name = fn.toLowerCase();
			params = new double[0];
		}

		ActivationFunction af = allocateAF(name);
		
		if( af==null ) {
			return null;
		}

		if (af.getParamNames().length != params.length) {
			throw new EncogError(name + " expected "
					+ af.getParamNames().length + ", but " + params.length
					+ " were provided.");
		}

		for (int i = 0; i < af.getParamNames().length; i++) {
			af.setParam(i, params[i]);
		}

		return af;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public MLMethod createMethod(String methodType, String architecture,
			int input, int output) {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public MLTrain createTraining(MLMethod method, MLDataSet training,
			String type, String args) {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getPluginServiceType() {
		return EncogPluginBase.TYPE_SERVICE;
	}
}
