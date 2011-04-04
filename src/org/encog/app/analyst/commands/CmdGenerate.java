package org.encog.app.analyst.commands;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.encog.app.analyst.EncogAnalyst;
import org.encog.app.analyst.script.prop.ScriptProperties;
import org.encog.app.analyst.util.CSVHeaders;
import org.encog.app.quant.normalize.NormalizedField;
import org.encog.util.csv.CSVFormat;
import org.encog.util.simple.EncogUtility;

/**
 * This command is used to generate the binary EGB file from a CSV file. The
 * resulting file can be used for training.
 * 
 */
public class CmdGenerate extends Cmd {

	public final static String COMMAND_NAME = "GENERATE";

	public CmdGenerate(EncogAnalyst analyst) {
		super(analyst);
	}

	public int[] determineInputFields(CSVHeaders headerList) {
		List<Integer> fields = new ArrayList<Integer>();
		String targetField = this.getScript().getProperties()
				.getPropertyString(ScriptProperties.DATA_CONFIG_targetField);

		for (int currentIndex = 0; currentIndex < headerList.size(); currentIndex++) {
			String baseName = headerList.getBaseHeader(currentIndex);
			if (headerList.isSeriesInput(currentIndex)
					|| !baseName.equalsIgnoreCase(targetField)) {
				if (headerList.isSeriesInput(currentIndex)) {
					fields.add(currentIndex++);
				}
			}
		}

		// allocate result array
		int[] result = new int[fields.size()];
		for (int i = 0; i < result.length; i++) {
			result[i] = fields.get(i);
		}

		return result;
	}

	public int[] determineIdealFields(CSVHeaders headerList) {
		List<Integer> fields = new ArrayList<Integer>();
		String targetField = this.getScript().getProperties()
				.getPropertyString(ScriptProperties.DATA_CONFIG_targetField);

		for (int currentIndex = 0; currentIndex < headerList.size(); currentIndex++) {
			String baseName = headerList.getBaseHeader(currentIndex);
			if (headerList.isSeriesPredict(currentIndex)
					|| baseName.equalsIgnoreCase(targetField)) {
				if (!headerList.isSeriesInput(currentIndex)) {
					fields.add(currentIndex++);
				}
			}
		}

		// allocate result array
		int[] result = new int[fields.size()];
		for (int i = 0; i < result.length; i++) {
			result[i] = fields.get(i);
		}

		return result;
	}

	@Override
	public boolean executeCommand() {
		// get filenames
		String sourceID = getProp().getPropertyString(
				ScriptProperties.GENERATE_CONFIG_sourceFile);
		String targetID = getProp().getPropertyString(
				ScriptProperties.GENERATE_CONFIG_targetFile);
		CSVFormat format = this.getAnalyst().getScript()
				.determineInputFormat(sourceID);

		File sourceFile = getScript().resolveFilename(sourceID);
		File targetFile = getScript().resolveFilename(targetID);

		// mark generated
		getScript().markGenerated(targetID);

		// read file
		boolean headers = getScript().expectInputHeaders(sourceID);
		CSVHeaders headerList = new CSVHeaders(sourceFile, headers, format);

		int[] input = determineInputFields(headerList);
		int[] ideal = determineIdealFields(headerList);

		EncogUtility.convertCSV2Binary(sourceFile, format, targetFile, input,
				ideal, headers);
		return false;
	}

	@Override
	public String getName() {
		return COMMAND_NAME;
	}

}
