package org.encog.app.analyst.commands;

import java.io.File;

import org.encog.app.analyst.EncogAnalyst;
import org.encog.app.analyst.script.prop.ScriptProperties;
import org.encog.app.analyst.util.AnalystReportBridge;
import org.encog.app.quant.shuffle.ShuffleCSV;
import org.encog.ml.kmeans.KMeansClustering;
import org.encog.util.csv.CSVFormat;

/**
 * This command is used to randomize the lines in a CSV file.
 *
 */
public class CmdCluster extends Cmd {

	public final static String COMMAND_NAME = "CLUSTER";	
	
	public CmdCluster(EncogAnalyst analyst) {
		super(analyst);
	}

	@Override
	public boolean executeCommand(String args) {
		// get filenames
		String sourceID = getProp().getPropertyString(ScriptProperties.CLUSTER_CONFIG_sourceFile);
		String targetID = getProp().getPropertyString(ScriptProperties.CLUSTER_CONFIG_targetFile);
		String type = getProp().getPropertyString(ScriptProperties.CLUSTER_CONFIG_type);
		
		File sourceFile = this.getScript().resolveFilename(sourceID);
		File targetFile = this.getScript().resolveFilename(targetID);

		// get formats
		CSVFormat inputFormat = this.getScript().determineInputFormat(sourceID);
		CSVFormat outputFormat = this.getScript().determineOutputFormat(); 
			
		
		// mark generated
		getScript().markGenerated(targetID);

		//KMeansClustering kmeans = new KMeansClustering(2,set);
		
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
