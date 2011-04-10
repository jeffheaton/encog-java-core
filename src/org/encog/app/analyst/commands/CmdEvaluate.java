package org.encog.app.analyst.commands;

import java.io.File;

import org.encog.app.analyst.EncogAnalyst;
import org.encog.app.analyst.evaluate.AnalystEvaluateCSV;
import org.encog.app.analyst.script.prop.ScriptProperties;
import org.encog.app.analyst.util.AnalystReportBridge;
import org.encog.app.quant.evaluate.EvaluateCSV;
import org.encog.ml.MLMethod;
import org.encog.ml.MLRegression;
import org.encog.persist.EncogDirectoryPersistence;

/**
 * This class is used to evaluate a machine learning method. Evaluation data is
 * provided and the ideal and actual responses from the machine learning method
 * are written to a file.
 * 
 */
public class CmdEvaluate extends Cmd {

	public final static String COMMAND_NAME = "EVALUATE";

	public CmdEvaluate(EncogAnalyst analyst) {
		super(analyst);
	}

	@Override
	public boolean executeCommand(String args) {
		// get filenames
		String evalID = getProp().getPropertyString(
				ScriptProperties.ML_CONFIG_evalFile);
		String resourceID = getProp().getPropertyString(
				ScriptProperties.ML_CONFIG_machineLearningFile);

		String outputID = getProp().getPropertyString(
				ScriptProperties.ML_CONFIG_outputFile);

		File evalFile = getScript().resolveFilename(evalID);
		File resourceFile = getScript().resolveFilename(resourceID);

		File outputFile = getAnalyst().getScript().resolveFilename(outputID);

		MLMethod method = (MLMethod) EncogDirectoryPersistence
				.loadObject(resourceFile);

		boolean headers = getScript().expectInputHeaders(evalID);

		AnalystEvaluateCSV eval = new AnalystEvaluateCSV();
		getAnalyst().setCurrentQuantTask(eval);
		eval.setReport(new AnalystReportBridge(this.getAnalyst()));
		eval.analyze(
				this.getAnalyst(),
				evalFile,
				headers,
				getProp().getPropertyCSVFormat(
						ScriptProperties.SETUP_CONFIG_csvFormat));
		eval.process(outputFile, getAnalyst(), method);
		getAnalyst().setCurrentQuantTask(null);
		return eval.shouldStop();
	}

	public void evaluateRaw() {

		// get filenames
		String evalID = getProp().getPropertyString(
				ScriptProperties.ML_CONFIG_evalFile);
		String resourceID = getProp().getPropertyString(
				ScriptProperties.ML_CONFIG_machineLearningFile);

		File evalFile = getScript().resolveFilename(evalID);
		File resourceFile = getScript().resolveFilename(resourceID);

		File outputFile = getScript().resolveFilename(
				getProp().getPropertyString(
						ScriptProperties.ML_CONFIG_outputFile));

		MLRegression method = (MLRegression) EncogDirectoryPersistence
				.loadObject(resourceFile);

		boolean headers = getScript().expectInputHeaders(evalID);

		EvaluateCSV eval = new EvaluateCSV();
		getAnalyst().setCurrentQuantTask(eval);
		eval.setReport(new AnalystReportBridge(getAnalyst()));
		eval.analyze(
				evalFile,
				headers,
				getProp().getPropertyCSVFormat(
						ScriptProperties.SETUP_CONFIG_csvFormat));
		eval.process(outputFile, method);
		getAnalyst().setCurrentQuantTask(null);
	}

	@Override
	public String getName() {
		return COMMAND_NAME;
	}

}
