package org.encog.app.analyst.commands;

import java.io.File;

import org.encog.app.analyst.EncogAnalyst;
import org.encog.app.analyst.script.prop.ScriptProperties;
import org.encog.ml.MLMethod;
import org.encog.ml.factory.MLMethodFactory;
import org.encog.neural.data.buffer.EncogEGBFile;
import org.encog.persist.EncogDirectoryPersistence;

/**
 * The Encog Analyst create command. This command is used to create a Machine
 * Learning method.
 * 
 */
public class CmdCreate extends Cmd {

	public final static String COMMAND_NAME = "CREATE";

	public CmdCreate(EncogAnalyst analyst) {
		super(analyst);
	}

	@Override
	public boolean executeCommand(String args) {
		// get filenames
		String trainingID = getProp().getPropertyString(
				ScriptProperties.ML_CONFIG_trainingFile);
		String resourceID = getProp().getPropertyString(
				ScriptProperties.ML_CONFIG_machineLearningFile);

		File trainingFile = getScript().resolveFilename(trainingID);
		File resourceFile = getScript().resolveFilename(resourceID);

		String type = getProp().getPropertyString(
				ScriptProperties.ML_CONFIG_type);
		String arch = getProp().getPropertyString(
				ScriptProperties.ML_CONFIG_architecture);

		EncogEGBFile egb = new EncogEGBFile(trainingFile);
		egb.open();
		int input = egb.getInputCount();
		int ideal = egb.getIdealCount();
		egb.close();

		MLMethodFactory factory = new MLMethodFactory();
		MLMethod obj = factory.create(type, arch, input, ideal);

		EncogDirectoryPersistence.saveObject(resourceFile, obj);

		return false;
	}

	@Override
	public String getName() {
		return COMMAND_NAME;
	}

}
