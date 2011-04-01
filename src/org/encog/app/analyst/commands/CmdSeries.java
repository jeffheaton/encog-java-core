package org.encog.app.analyst.commands;

import java.io.File;

import org.encog.app.analyst.EncogAnalyst;
import org.encog.app.analyst.script.prop.ScriptProperties;
import org.encog.app.analyst.util.AnalystReportBridge;
import org.encog.app.quant.temporal.TemporalType;
import org.encog.app.quant.temporal.TemporalWindowCSV;
import org.encog.app.quant.temporal.TemporalWindowField;
import org.encog.util.csv.CSVFormat;

/**
 * This command is used to process time-series data. The data is processed in a
 * way that a neural network can predict from.
 * 
 */
public class CmdSeries extends Cmd {

	public final static String COMMAND_NAME = "SERIES";

	public CmdSeries(EncogAnalyst analyst) {
		super(analyst);
	}

	@Override
	public boolean executeCommand() {
		// get filenames
		String sourceID = getProp().getPropertyString(
				ScriptProperties.SERIES_CONFIG_sourceFile);
		String targetID = getProp().getPropertyString(
				ScriptProperties.SERIES_CONFIG_targetFile);

		File sourceFile = this.getScript().resolveFilename(sourceID);
		File targetFile = this.getScript().resolveFilename(targetID);

		// find out about the target field
		String targetField = getProp().getPropertyString(
				ScriptProperties.DATA_CONFIG_targetField);
		boolean includeTarget = getProp().getPropertyBoolean(
				ScriptProperties.SERIES_CONFIG_includeTarget);

		// get window sizes
		int inputWindowSize = this.getProp().getPropertyInt(
				ScriptProperties.SERIES_CONFIG_lag);
		int outputWindowSize = this.getProp().getPropertyInt(
				ScriptProperties.SERIES_CONFIG_lead);

		// get formats
		CSVFormat inputFormat = this.getScript().determineInputFormat(sourceID);
		CSVFormat outputFormat = this.getScript().determineOutputFormat();

		// mark generated
		getScript().markGenerated(targetID);

		// prepare to series
		TemporalWindowCSV series = new TemporalWindowCSV();
		getAnalyst().setCurrentQuantTask(series);
		series.setReport(new AnalystReportBridge(getAnalyst()));
		boolean headers = this.getScript().expectInputHeaders(sourceID);
		series.setInputWindow(inputWindowSize);
		series.setPredictWindow(outputWindowSize);
		series.analyze(sourceFile, headers, inputFormat);
		series.setOutputFormat(outputFormat);

		// setup the fields
		String altTarget = targetField.toLowerCase() + "-";

		for (TemporalWindowField field : series.getFields()) {
			if (field.getName().toLowerCase().startsWith(altTarget)
					|| field.getName().equalsIgnoreCase(targetField)) {
				if (includeTarget)
					field.setAction(TemporalType.InputAndPredict);
				else
					field.setAction(TemporalType.Predict);
			} else {
				field.setAction(TemporalType.Input);
			}
		}

		// perform the series operation
		series.process(targetFile);
		getAnalyst().setCurrentQuantTask(null);
		return series.shouldStop();
	}

	@Override
	public String getName() {
		return COMMAND_NAME;
	}

}
