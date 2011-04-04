package org.encog.app.analyst.commands;

import java.io.File;

import org.encog.app.analyst.AnalystError;
import org.encog.app.analyst.EncogAnalyst;
import org.encog.app.analyst.script.prop.ScriptProperties;
import org.encog.ml.MLMethod;
import org.encog.ml.MLTrain;
import org.encog.ml.factory.MLTrainFactory;
import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.data.folded.FoldedDataSet;
import org.encog.neural.networks.training.Train;
import org.encog.neural.networks.training.cross.CrossValidationKFold;
import org.encog.persist.EncogDirectoryPersistence;
import org.encog.util.simple.EncogUtility;

/**
 * This command is used to perform training on a machine learning method and
 * dataset.
 * 
 */
public class CmdTrain extends Cmd {

	public final static String COMMAND_NAME = "TRAIN";
	private int kfold;

	public CmdTrain(EncogAnalyst analyst) {
		super(analyst);
	}
	
	private int obtainCross() {
		String cross = getProp().getPropertyString(
				ScriptProperties.ML_TRAIN_cross);
		if( cross==null || cross.length()==0 ) {
			return 0;
		} else if( cross.toLowerCase().startsWith("kfold:") ) {
			String str = cross.substring(6);
			try {
				return Integer.parseInt(str);
			} catch(NumberFormatException ex) {
				throw new AnalystError("Invalid kfold :" + str);
			}
		} else {
			throw new AnalystError("Unknown cross validation: " + cross);
		}
	}

	private MLMethod obtainMethod() {
		String resourceID = getProp().getPropertyString(
				ScriptProperties.ML_CONFIG_machineLearningFile);
		File resourceFile = getScript().resolveFilename(resourceID);

		MLMethod method = (MLMethod) EncogDirectoryPersistence
				.loadObject(resourceFile);

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

		File trainingFile = getScript().resolveFilename(trainingID);

		NeuralDataSet trainingSet = EncogUtility.loadEGB2Memory(trainingFile);
		
		if( this.kfold>0 ) {
			trainingSet = new FoldedDataSet(trainingSet);
		}

		return trainingSet;
	}

	private MLTrain createTrainer(MLMethod method, NeuralDataSet trainingSet) {

		MLTrainFactory factory = new MLTrainFactory();

		String type = this.getProp().getPropertyString(
				ScriptProperties.ML_TRAIN_type);
		String args = this.getProp().getPropertyString(
				ScriptProperties.ML_TRAIN_arguments);

		MLTrain train = factory.create(method, trainingSet, type, args);
		
		if( kfold>0 ) {
			train = new CrossValidationKFold((Train)train,kfold);
		}

		return train;
	}

	private void performTraining(MLTrain train, MLMethod method,
			NeuralDataSet trainingSet) {

		double targetError = this.getProp().getPropertyDouble(
				ScriptProperties.ML_TRAIN_targetError);
		this.getAnalyst().reportTrainingBegin();
		int maxIteration = this.getAnalyst().getMaxIteration();

		do {
			train.iteration();
			this.getAnalyst().reportTraining(train);
		} while (train.getError() > targetError
				&& !this.getAnalyst().shouldStopCommand()
				&& !train.isTrainingDone()
				&& (maxIteration==-1 || train.getIteration()<maxIteration));
		train.finishTraining();

		this.getAnalyst().reportTrainingEnd();
	}

	public boolean executeCommand(String args) {

		this.kfold = obtainCross();
		NeuralDataSet trainingSet = obtainTrainingSet();
		MLMethod method = obtainMethod();		
		MLTrain trainer = createTrainer(method, trainingSet);

		performTraining(trainer, method, trainingSet);

		String resourceID = getProp().getPropertyString(
				ScriptProperties.ML_CONFIG_machineLearningFile);
		File resourceFile = getAnalyst().getScript().resolveFilename(resourceID);
		EncogDirectoryPersistence.saveObject(resourceFile, method);

		return this.getAnalyst().shouldStopCommand();
	}

	@Override
	public String getName() {
		return COMMAND_NAME;
	}

}
