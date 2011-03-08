package org.encog.app.analyst.commands;

import org.encog.app.analyst.AnalystFileFormat;
import org.encog.app.analyst.EncogAnalyst;
import org.encog.app.analyst.script.prop.ScriptProperties;
import org.encog.app.analyst.util.AnalystReportBridge;
import org.encog.app.quant.shuffle.ShuffleCSV;
import org.encog.util.csv.CSVFormat;

public class CmdRandomize extends Cmd {

	public final static String COMMAND_NAME = "RANDOMIZE";	
	
	public CmdRandomize(EncogAnalyst analyst) {
		super(analyst);
	}

	@Override
	public boolean executeCommand() {
		// get filenames
		String sourceID = getProp().getPropertyString(ScriptProperties.RANDOMIZE_CONFIG_sourceFile);
		String targetID = getProp().getPropertyString(ScriptProperties.RANDOMIZE_CONFIG_targetFile);
		
		String sourceFile = getProp().getFilename(sourceID);
		String targetFile = getProp().getFilename(targetID);

		// get formats
		CSVFormat inputFormat = this.getScript().determineInputFormat(sourceID);
		CSVFormat outputFormat = this.getScript().determineOutputFormat(); 
			
		
		// mark generated
		getScript().markGenerated(targetID);

		// prepare to normalize
		ShuffleCSV norm = new ShuffleCSV();
		getAnalyst().setCurrentQuantTask(norm);
		norm.setReport(new AnalystReportBridge(getAnalyst()));
		boolean headers = this.getScript().expectInputHeaders(sourceID);
		norm.analyze(sourceFile, headers, inputFormat);
		norm.setOutputFormat(outputFormat);
		norm.process(targetFile);
		getAnalyst().setCurrentQuantTask(null);
		return norm.shouldStop();
	}

	@Override
	public String getName() {
		return COMMAND_NAME;
	}

}
