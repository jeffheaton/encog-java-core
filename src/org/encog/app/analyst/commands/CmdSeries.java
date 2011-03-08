package org.encog.app.analyst.commands;

import org.encog.app.analyst.EncogAnalyst;
import org.encog.app.analyst.script.prop.ScriptProperties;
import org.encog.app.analyst.util.AnalystReportBridge;
import org.encog.app.quant.shuffle.ShuffleCSV;
import org.encog.app.quant.temporal.TemporalWindowCSV;
import org.encog.util.csv.CSVFormat;

public class CmdSeries extends Cmd {

	public final static String COMMAND_NAME = "SERIES";
	
	public CmdSeries(EncogAnalyst analyst) {
		super(analyst);
	}

	@Override
	public boolean executeCommand() {
		// get filenames
		String sourceID = getProp().getPropertyString(ScriptProperties.SERIES_CONFIG_sourceFile);
		String targetID = getProp().getPropertyString(ScriptProperties.SERIES_CONFIG_targetFile);
		
		String sourceFile = getProp().getFilename(sourceID);
		String targetFile = getProp().getFilename(targetID);
		
		// get window sizes
		int inputWindowSize = this.getProp().getPropertyInt(ScriptProperties.SERIES_CONFIG_lag);
		int outputWindowSize =  this.getProp().getPropertyInt(ScriptProperties.SERIES_CONFIG_lead);
		
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
		series.process(targetFile);
		getAnalyst().setCurrentQuantTask(null);
		return series.shouldStop();
	}

	@Override
	public String getName() {
		return COMMAND_NAME;
	}

}
