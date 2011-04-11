package org.encog.app.analyst.commands;

import java.io.File;

import org.encog.app.analyst.EncogAnalyst;
import org.encog.app.analyst.evaluate.AnalystNormalizeCSV;
import org.encog.app.analyst.script.prop.ScriptProperties;
import org.encog.app.analyst.util.AnalystReportBridge;
import org.encog.util.csv.CSVFormat;

/**
 * The normalize command is used to normalize data. Data normalization generally
 * maps values from one number range to another, typically to -1 to 1.
 * 
 */
public class CmdNormalize extends Cmd {

	public final static String COMMAND_NAME = "NORMALIZE";

	public CmdNormalize(EncogAnalyst analyst) {
		super(analyst);
	}

	@Override
	public boolean executeCommand(String args) {
		// get filenames
		String sourceID = getProp().getPropertyString(
				ScriptProperties.NORMALIZE_CONFIG_sourceFile);
		String targetID = getProp().getPropertyString(
				ScriptProperties.NORMALIZE_CONFIG_targetFile);

		File sourceFile = getScript().resolveFilename(sourceID);
		File targetFile = getScript().resolveFilename(targetID);

		// mark generated
		getScript().markGenerated(targetID);

		// get formats
		CSVFormat inputFormat = this.getScript().determineInputFormat(sourceID);
		CSVFormat outputFormat = this.getScript().determineOutputFormat();

		// prepare to normalize
		AnalystNormalizeCSV norm = new AnalystNormalizeCSV();
		getAnalyst().setCurrentQuantTask(norm);
		norm.setReport(new AnalystReportBridge(getAnalyst()));

		boolean headers = getScript().expectInputHeaders(sourceID);
		norm.analyze(sourceFile, headers, inputFormat, this.getAnalyst());
		norm.setOutputFormat(outputFormat);
		norm.setProduceOutputHeaders(true);
		norm.normalize(targetFile);
		getAnalyst().setCurrentQuantTask(null);
		return norm.shouldStop();
	}

	@Override
	public String getName() {
		return COMMAND_NAME;
	}

}
