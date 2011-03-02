package org.encog.app.analyst.commands;

import java.util.Map;

import org.encog.app.analyst.AnalystError;
import org.encog.app.analyst.EncogAnalyst;
import org.encog.app.analyst.script.prop.ScriptProperties;
import org.encog.ml.MLMethod;
import org.encog.ml.factory.parse.ArchitectureParse;
import org.encog.ml.svm.SVM;
import org.encog.ml.svm.training.SVMTrain;
import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.training.Train;
import org.encog.neural.networks.training.lma.LevenbergMarquardtTraining;
import org.encog.neural.networks.training.propagation.back.Backpropagation;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;
import org.encog.neural.networks.training.propagation.scg.ScaledConjugateGradient;
import org.encog.neural.rbf.RBFNetwork;
import org.encog.neural.rbf.training.SVDTraining;
import org.encog.persist.EncogMemoryCollection;
import org.encog.persist.EncogPersistedObject;
import org.encog.util.simple.EncogUtility;

public class CmdTrain extends Cmd {

	public final static String COMMAND_NAME = "TRAIN";

	private boolean singleIteration = false;
	private EncogMemoryCollection encog;

	public CmdTrain(EncogAnalyst analyst) {
		super(analyst);
	}

	private MLMethod obtainMethod() {
		String resourceID = getProp().getPropertyString(
				ScriptProperties.ML_CONFIG_resourceFile);
		String resourceFile = getProp().getFilename(resourceID);

		this.encog = new EncogMemoryCollection();
		encog.load(resourceFile);

		String resource = getProp().getPropertyString(
				ScriptProperties.ML_CONFIG_resourceName);

		EncogPersistedObject method = encog.find(resource);

		if (!(method instanceof MLMethod)) {
			throw new AnalystError(
					"The object to be trained must be an instance of MLMethod. "
							+ method.getClass().getSimpleName());
		}

		return (MLMethod) method;
	}

	private NeuralDataSet obtainTrainingSet() {
		String trainingID = getProp().getPropertyString(
				ScriptProperties.ML_CONFIG_trainingFile);

		String trainingFile = getProp().getFilename(trainingID);

		NeuralDataSet trainingSet = EncogUtility.loadEGB2Memory(trainingFile);

		return trainingSet;
	}

	private Train createTrainerBasicNetwork(MLMethod method,
			NeuralDataSet trainingSet) {
		String trainingType = this.getProp().getPropertyString(
				ScriptProperties.ML_TRAIN_type);
		String argsStr = this.getProp().getPropertyString(
				ScriptProperties.ML_TRAIN_arguments);
		
		Map<String, String> args = ArchitectureParse.parseParams(argsStr);

		Train train;
		
		if (trainingType==null || trainingType.equals("") || trainingType.equalsIgnoreCase("rprop")) {
			train = new ResilientPropagation((BasicNetwork) method,
					trainingSet);
			singleIteration = false;
		} else if ( trainingType.equalsIgnoreCase("lma")) {
			train = new LevenbergMarquardtTraining((BasicNetwork)method, trainingSet);
			singleIteration = false;
		} else if ( trainingType.equalsIgnoreCase("scg")) {
			train = new ScaledConjugateGradient((BasicNetwork)method, trainingSet);
			singleIteration = false;
		} else if ( trainingType.equalsIgnoreCase("backprop")) {
			double learningRate = 0.7; 
			double momentum = 0.7;
			try {
				learningRate = Double.parseDouble(args.get("LEARNINGRATE"));
				momentum = Double.parseDouble(args.get("MOMENTUM"));
			} catch(NumberFormatException ex) {
				throw new AnalystError("Invalid momentum or learning rate.");
			}
			
			train = new Backpropagation((BasicNetwork)method, trainingSet, learningRate, momentum);
			singleIteration = false;
		} else {
			throw new AnalystError("Invalid (for feedforward) or unkown training method: " + trainingType);
		}
		return train;
	}

	private Train createTrainerSVM(MLMethod method, NeuralDataSet trainingSet) {
		Train train = new SVMTrain((SVM) method, trainingSet);
		singleIteration = true;
		return train;
	}

	private Train createTrainerRBF(MLMethod method, NeuralDataSet trainingSet) {
		Train train = new SVDTraining((RBFNetwork) method, trainingSet);
		singleIteration = true;
		return train;
	}

	private Train createTrainer(MLMethod method, NeuralDataSet trainingSet) {
		Train train = null;

		if (method instanceof BasicNetwork) {
			return createTrainerBasicNetwork(method, trainingSet);
		} else if (method instanceof SVM) {
			return createTrainerSVM(method, trainingSet);
		} else if (method instanceof RBFNetwork) {
			return createTrainerRBF(method, trainingSet);
		}

		return train;
	}

	private void performTraining(Train train, MLMethod method,
			NeuralDataSet trainingSet) {
		this.getAnalyst().reportTrainingBegin();

		if (!singleIteration) {
			do {
				train.iteration();
				this.getAnalyst().reportTraining(train);
			} while (train.getError() > 0.01
					&& !this.getAnalyst().shouldStopCommand());
		} else {
			if (method instanceof SVM) {
				((SVMTrain) train).train();
				double error = EncogUtility.calculateRegressionError(
						(SVM) method, trainingSet);
				train.setError(error);
				train.setIteration(1);
				this.getAnalyst().reportTraining(train);
			} else {
				train.iteration();
				this.getAnalyst().reportTraining(train);
			}
		}

		this.getAnalyst().reportTrainingEnd();
	}

	private void saveMethod() {
		String resourceID = getProp().getPropertyString(
				ScriptProperties.ML_CONFIG_resourceFile);
		String resourceFile = getProp().getFilename(resourceID);

		encog.save(resourceFile);
	}

	public boolean executeCommand() {

		MLMethod method = obtainMethod();
		NeuralDataSet trainingSet = obtainTrainingSet();
		Train trainer = createTrainer(method, trainingSet);

		performTraining(trainer, method, trainingSet);
		saveMethod();

		return this.getAnalyst().shouldStopCommand();
	}

	@Override
	public String getName() {
		return COMMAND_NAME;
	}

}
