package org.encog.app.analyst.commands;

import java.io.File;

import org.encog.app.analyst.EncogAnalyst;
import org.encog.app.analyst.script.prop.ScriptProperties;
import org.encog.ml.MLMethod;
import org.encog.ml.factory.MLMethodFactory;
import org.encog.neural.data.buffer.EncogEGBFile;
import org.encog.persist.EncogMemoryCollection;
import org.encog.persist.EncogPersistedObject;

public class CmdCreate extends Cmd {

	public final static String COMMAND_NAME = "CREATE";
	
	public CmdCreate(EncogAnalyst analyst) {
		super(analyst);
	}
		
	@Override
	public boolean executeCommand() {
		// get filenames
		String trainingID = getProp().getPropertyString(ScriptProperties.ML_CONFIG_trainingFile);
		String resourceID = getProp().getPropertyString(ScriptProperties.ML_CONFIG_resourceFile);
		
		String trainingFile = getProp().getFilename(trainingID);
		String resourceFile = getProp().getFilename(resourceID);
		
		String resource = getProp().getPropertyString(ScriptProperties.ML_CONFIG_resourceName);
		String type = getProp().getPropertyString(ScriptProperties.ML_CONFIG_type);
		String arch = getProp().getPropertyString(ScriptProperties.ML_CONFIG_architecture);

		EncogEGBFile egb = new EncogEGBFile(new File(trainingFile));
		egb.open();
		int input = egb.getInputCount();
		int ideal = egb.getIdealCount();
		egb.close();

		EncogMemoryCollection encog = new EncogMemoryCollection();
		if (new File(resourceFile).exists()) {
			encog.load(resourceFile);
		}

		MLMethodFactory factory = new MLMethodFactory();
		MLMethod obj = factory.create(type, arch, input, ideal);

		encog.add(resource, (EncogPersistedObject) obj);
		encog.save(resourceFile);
		return false;
	}

	@Override
	public String getName() {
		return COMMAND_NAME;
	}

}
