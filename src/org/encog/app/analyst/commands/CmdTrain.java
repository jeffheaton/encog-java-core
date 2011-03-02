package org.encog.app.analyst.commands;

import org.encog.app.analyst.EncogAnalyst;
import org.encog.app.analyst.script.prop.ScriptProperties;
import org.encog.ml.svm.SVM;
import org.encog.ml.svm.training.SVMTrain;
import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.training.Train;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;
import org.encog.neural.rbf.RBFNetwork;
import org.encog.neural.rbf.training.SVDTraining;
import org.encog.persist.EncogMemoryCollection;
import org.encog.persist.EncogPersistedObject;
import org.encog.util.simple.EncogUtility;

public class CmdTrain extends Cmd {

	public final static String COMMAND_NAME = "TRAIN";
	
	public CmdTrain(EncogAnalyst analyst) {
		super(analyst);		
	}

	public boolean executeCommand() {
	
		// get filenames
		String trainingID = getProp().getPropertyString(ScriptProperties.ML_CONFIG_trainingFile);
		String resourceID = getProp().getPropertyString(ScriptProperties.ML_CONFIG_resourceFile);
		
		String trainingFile = getProp().getFilename(
				trainingID);
		String resourceFile = getProp().getFilename(
				resourceID);
		String resource = getProp().getPropertyString(ScriptProperties.ML_CONFIG_resourceName);
	
		NeuralDataSet trainingSet = EncogUtility.loadEGB2Memory(trainingFile);
	
		EncogMemoryCollection encog = new EncogMemoryCollection();
		encog.load(resourceFile);
	
		EncogPersistedObject method = encog.find(resource);
	
		Train train = null;
		boolean singleIteration = false;
	
		if (method instanceof BasicNetwork) {
			train = new ResilientPropagation((BasicNetwork) method, trainingSet);
			singleIteration = false;
		} else if (method instanceof SVM) {
			train = new SVMTrain((SVM) method, trainingSet);
			singleIteration = true;
		} else if (method instanceof RBFNetwork){
			train = new SVDTraining((RBFNetwork)method,trainingSet);
			singleIteration = true;
		}
	
		this.getAnalyst().reportTrainingBegin();
	
		if (!singleIteration) {
			do {
				train.iteration();
				this.getAnalyst().reportTraining(train);
			} while (train.getError() > 0.01 && !this.getAnalyst().shouldStopCommand());
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
	
		encog.save(resourceFile);
		return this.getAnalyst().shouldStopCommand();
	}

	@Override
	public String getName() {
		return COMMAND_NAME;
	}
	
	
	

}
