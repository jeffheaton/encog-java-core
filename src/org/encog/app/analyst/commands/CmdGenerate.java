package org.encog.app.analyst.commands;

import org.encog.app.analyst.EncogAnalyst;
import org.encog.app.analyst.script.prop.ScriptProperties;
import org.encog.util.simple.EncogUtility;

public class CmdGenerate extends Cmd {

	public final static String COMMAND_NAME = "GENERATE";
	
	public CmdGenerate(EncogAnalyst analyst) {
		super(analyst);
	}

	@Override
	public boolean executeCommand() {
		// get filenames
		String sourceID = getProp().getPropertyString(ScriptProperties.GENERATE_CONFIG_sourceFile);
		String targetID = getProp().getPropertyString(ScriptProperties.GENERATE_CONFIG_targetFile);
		
		String sourceFile = getProp().getFilename(sourceID);
		String targetFile = getProp().getFilename(targetID);
		
		// mark generated
		getScript().markGenerated(targetID);
		
		int input = getProp().getPropertyInt(ScriptProperties.GENERATE_CONFIG_input);
		int ideal = getProp().getPropertyInt(ScriptProperties.GENERATE_CONFIG_ideal);

		boolean headers = getScript().expectInputHeaders(sourceID);
		EncogUtility.convertCSV2Binary(sourceFile, targetFile, input, ideal,
				headers);
		return false;
	}

	@Override
	public String getName() {
		return COMMAND_NAME;
	}

}
