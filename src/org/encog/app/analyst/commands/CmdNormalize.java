package org.encog.app.analyst.commands;

import org.encog.app.analyst.EncogAnalyst;
import org.encog.app.analyst.script.prop.ScriptProperties;
import org.encog.app.analyst.util.AnalystReportBridge;
import org.encog.app.quant.normalize.NormalizationStats;
import org.encog.app.quant.normalize.NormalizeCSV;
import org.encog.app.quant.normalize.NormalizedField;

public class CmdNormalize extends Cmd {

	public final static String COMMAND_NAME = "NORMALIZE";
	
	public CmdNormalize(EncogAnalyst analyst) {
		super(analyst);
	}

	@Override
	public boolean executeCommand() {
		// get filenames
		String sourceID = getProp().getPropertyString(ScriptProperties.NORMALIZE_CONFIG_sourceFile);
		String targetID = getProp().getPropertyString(ScriptProperties.NORMALIZE_CONFIG_targetFile);
		
		String sourceFile = getProp().getFilename(sourceID);
		String targetFile = getProp().getFilename(targetID);

		// mark generated
		getScript().markGenerated(targetID);
		
		// prepare to normalize
		NormalizeCSV norm = new NormalizeCSV();
		getAnalyst().setCurrentQuantTask(norm);
		norm.setReport(new AnalystReportBridge(getAnalyst()));
		NormalizedField[] normFields = getScript().getNormalize()
				.getNormalizedFields();
		NormalizationStats stats = new NormalizationStats(normFields);

		boolean headers = getScript().expectInputHeaders(sourceID);
		norm.analyze(sourceFile, headers, 
				getProp().getPropertyCSVFormat(ScriptProperties.SETUP_CONFIG_csvFormat),
				stats);
		norm.setProduceOutputHeaders(getScript().getProperties().getPropertyBoolean(ScriptProperties.SETUP_CONFIG_outputHeaders));
		norm.normalize(targetFile);
		getAnalyst().setCurrentQuantTask(null);
		return norm.shouldStop();
	}

	@Override
	public String getName() {
		return COMMAND_NAME;
	}

}
