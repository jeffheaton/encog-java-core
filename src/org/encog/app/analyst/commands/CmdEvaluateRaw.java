package org.encog.app.analyst.commands;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.encog.app.analyst.EncogAnalyst;
import org.encog.app.analyst.evaluate.AnalystEvaluateCSV;
import org.encog.app.analyst.script.prop.ScriptProperties;
import org.encog.app.analyst.util.AnalystReportBridge;
import org.encog.app.quant.evaluate.EvaluateCSV;
import org.encog.engine.util.Format;
import org.encog.ml.MLRegression;
import org.encog.persist.EncogDirectoryPersistence;

/**
 * This class is used to evaluate a machine learning method. Evaluation data is
 * provided and the ideal and actual responses from the machine learning method
 * are written to a file.
 * 
 */
public class CmdEvaluateRaw extends Cmd {

	public final static String COMMAND_NAME = "EVALUATE-RAW";
	private Map<String, Integer> classCorrect = new HashMap<String, Integer>();
	private Map<String, Integer> classCount = new HashMap<String, Integer>();

	public CmdEvaluateRaw(EncogAnalyst analyst) {
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

		String targetField = getProp().getPropertyString(
				ScriptProperties.DATA_CONFIG_targetField);

		MLRegression method = (MLRegression) EncogDirectoryPersistence
				.loadObject(resourceFile);

		boolean headers = getScript().expectInputHeaders(evalID);

		AnalystEvaluateCSV eval = new AnalystEvaluateCSV();
		getAnalyst().setCurrentQuantTask(eval);
		eval.setReport(new AnalystReportBridge(this.getAnalyst()));
		eval.analyze(
				evalFile,
				headers,
				getProp().getPropertyCSVFormat(
						ScriptProperties.SETUP_CONFIG_csvFormat));
		eval.process(outputFile, getAnalyst(), method, targetField);
		getAnalyst().setCurrentQuantTask(null);
		this.classCorrect = eval.getClassCorrect();
		this.classCount = eval.getClassCount();
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

	public Map<String, Integer> getClassCorrect() {
		return classCorrect;
	}

	public void setClassCorrect(Map<String, Integer> classCorrect) {
		this.classCorrect = classCorrect;
	}

	public Map<String, Integer> getClassCount() {
		return classCount;
	}

	public void setClassCount(Map<String, Integer> classCount) {
		this.classCount = classCount;
	}

	public String evalToString() {
		List<String> list = new ArrayList<String>();
		list.addAll(this.classCount.keySet());
		Collections.sort(list);

		StringBuilder result = new StringBuilder();
		for (String key : list) {
			result.append(key);
			result.append(" ");
			double correct = classCorrect.get(key);
			double count = classCount.get(key);

			result.append(Format.formatInteger((int) correct));
			result.append('/');
			result.append(Format.formatInteger((int) count));
			result.append('(');
			result.append(Format.formatPercent(correct / count));
			result.append(")\n");
		}

		return result.toString();
	}

	@Override
	public String getName() {
		return COMMAND_NAME;
	}

}
