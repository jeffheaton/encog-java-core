package org.encog.app.analyst.commands;

import java.io.File;

import org.encog.app.analyst.AnalystError;
import org.encog.app.analyst.EncogAnalyst;
import org.encog.app.analyst.script.DataField;
import org.encog.app.analyst.script.prop.ScriptProperties;
import org.encog.app.analyst.util.AnalystReportBridge;
import org.encog.app.quant.balance.BalanceCSV;
import org.encog.util.csv.CSVFormat;

public class CmdBalance extends Cmd {

	public final static String COMMAND_NAME = "BALANCE";

	public CmdBalance(EncogAnalyst analyst) {
		super(analyst);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean executeCommand() {
		// get filenames
		String sourceID = getProp().getPropertyString(
				ScriptProperties.BALANCE_CONFIG_sourceFile);
		String targetID = getProp().getPropertyString(
				ScriptProperties.BALANCE_CONFIG_targetFile);

		File sourceFile = getScript().resolveFilename(sourceID);
		File targetFile = getScript().resolveFilename(targetID);

		// get other config data
		int countPer = getProp().getPropertyInt(
				ScriptProperties.BALANCE_CONFIG_countPer);
		String targetFieldStr = getProp().getPropertyString(
				ScriptProperties.BALANCE_CONFIG_balanceField);
		DataField targetFieldDF = getAnalyst().getScript().findDataField(
				targetFieldStr);
		if (targetFieldDF == null) {
			throw new AnalystError("Can't find balance target field: "
					+ targetFieldStr);
		}
		if (!targetFieldDF.isClass()) {
			throw new AnalystError("Can't balance on non-class field: "
					+ targetFieldStr);
		}

		int targetFieldIndex = getAnalyst().getScript().findDataFieldIndex(
				targetFieldDF);

		// mark generated
		getScript().markGenerated(targetID);

		// get formats
		CSVFormat inputFormat = this.getScript().determineInputFormat(sourceID);
		CSVFormat outputFormat = this.getScript().determineOutputFormat();

		// prepare to normalize
		BalanceCSV balance = new BalanceCSV();
		getAnalyst().setCurrentQuantTask(balance);
		balance.setReport(new AnalystReportBridge(getAnalyst()));

		boolean headers = getScript().expectInputHeaders(sourceID);
		balance.Analyze(sourceFile, headers, inputFormat);
		balance.setOutputFormat(outputFormat);
		balance.setProduceOutputHeaders(getScript()
				.getProperties()
				.getPropertyBoolean(ScriptProperties.SETUP_CONFIG_outputHeaders));
		balance.Process(targetFile, targetFieldIndex, countPer);
		getAnalyst().setCurrentQuantTask(null);
		return balance.shouldStop();
	}

	@Override
	public String getName() {
		return COMMAND_NAME;
	}

}
