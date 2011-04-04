package org.encog.app.analyst.commands;

import java.io.File;

import org.encog.app.analyst.EncogAnalyst;
import org.encog.app.analyst.script.prop.ScriptProperties;
import org.encog.app.analyst.script.segregate.AnalystSegregateTarget;
import org.encog.app.analyst.util.AnalystReportBridge;
import org.encog.app.quant.segregate.SegregateCSV;
import org.encog.app.quant.segregate.SegregateTargetPercent;
import org.encog.util.csv.CSVFormat;

/**
 * This command is used to segregate one CSV file into several. This can be
 * useful for creating training and evaluation sets.
 * 
 */
public class CmdSegregate extends Cmd {

	public final static String COMMAND_NAME = "SEGREGATE";

	public CmdSegregate(EncogAnalyst analyst) {
		super(analyst);
	}

	@Override
	public boolean executeCommand(String args) {
		// get filenames
		String sourceID = getProp().getPropertyString(
				ScriptProperties.SEGREGATE_CONFIG_sourceFile);

		File sourceFile = getScript().resolveFilename(sourceID);

		// get formats
		CSVFormat inputFormat = this.getScript().determineInputFormat(sourceID);
		CSVFormat outputFormat = this.getScript().determineOutputFormat();

		// prepare to segregate
		boolean headers = getScript().expectInputHeaders(sourceID);
		SegregateCSV seg = new SegregateCSV();
		getAnalyst().setCurrentQuantTask(seg);
		for (AnalystSegregateTarget target : getScript().getSegregate()
				.getSegregateTargets()) {
			File filename = getScript().resolveFilename(target.getFile());
			seg.getTargets().add(
					new SegregateTargetPercent(filename, target.getPercent()));
			// mark generated
			this.getScript().markGenerated(target.getFile());
		}

		seg.setReport(new AnalystReportBridge(getAnalyst()));
		seg.analyze(sourceFile, headers, inputFormat);
		seg.setOutputFormat(outputFormat);

		seg.process();
		getAnalyst().setCurrentQuantTask(null);
		return seg.shouldStop();
	}

	@Override
	public String getName() {
		return COMMAND_NAME;
	}

}
