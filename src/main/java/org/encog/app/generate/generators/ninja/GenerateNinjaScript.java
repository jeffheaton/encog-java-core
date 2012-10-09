package org.encog.app.generate.generators.ninja;

import java.io.File;

import org.encog.app.analyst.EncogAnalyst;
import org.encog.app.analyst.script.DataField;
import org.encog.app.analyst.script.normalize.AnalystField;
import org.encog.app.analyst.script.prop.ScriptProperties;
import org.encog.app.generate.AnalystCodeGenerationError;
import org.encog.app.generate.generators.AbstractTemplateGenerator;
import org.encog.engine.network.activation.ActivationElliott;
import org.encog.engine.network.activation.ActivationElliottSymmetric;
import org.encog.engine.network.activation.ActivationFunction;
import org.encog.engine.network.activation.ActivationLinear;
import org.encog.engine.network.activation.ActivationSigmoid;
import org.encog.engine.network.activation.ActivationTANH;
import org.encog.ml.MLMethod;
import org.encog.neural.flat.FlatNetwork;
import org.encog.neural.networks.BasicNetwork;
import org.encog.persist.EncogDirectoryPersistence;
import org.encog.util.EngineArray;
import org.encog.util.csv.CSVFormat;
import org.encog.util.csv.NumberList;

public class GenerateNinjaScript extends AbstractTemplateGenerator {

	@Override
	public String getTemplatePath() {
		return "org/encog/data/ninja.cs";
	}

	private void addNameValue(String name, int value) {
		addNameValue(name, "" + value);
	}

	private void addNameValue(String name, String value) {
		StringBuilder line = new StringBuilder();
		line.append(name);
		line.append(" = ");
		line.append(value);
		line.append(";");
		addLine(line.toString());
	}

	private void addNameValue(String name, int[] data) {
		StringBuilder value = new StringBuilder();
		NumberList.toBrokenList(CSVFormat.EG_FORMAT, value, data);
		addNameValue(name, "{" + value.toString() + "}");
	}

	private void addNameValue(String name, double[] data) {
		StringBuilder value = new StringBuilder();
		NumberList.toBrokenList(CSVFormat.EG_FORMAT, value, data);
		addNameValue(name, "{" + value.toString() + "}");
	}

	private void addCols() {
		StringBuilder line = new StringBuilder();
		line.append("public readonly string[] ENCOG_COLS = {");

		boolean first = true;

		for (DataField df : this.getAnalyst().getScript().getFields()) {

			if (!df.getName().equalsIgnoreCase("time") && !df.getName().equalsIgnoreCase("prediction")) {
				if (!first) {
					line.append(",");
				}

				line.append("\"");
				line.append(df.getName());
				line.append("\"");
				first = false;
			}
		}

		line.append("};");
		addLine(line.toString());
	}

	private void processMainBlock() {
		EncogAnalyst analyst = getAnalyst();

		final String processID = analyst.getScript().getProperties()
				.getPropertyString(ScriptProperties.PROCESS_CONFIG_TARGET_FILE);

		final String methodID = analyst
				.getScript()
				.getProperties()
				.getPropertyString(
						ScriptProperties.ML_CONFIG_MACHINE_LEARNING_FILE);

		final File methodFile = analyst.getScript().resolveFilename(methodID);

		final File processFile = analyst.getScript().resolveFilename(processID);

		MLMethod method = null;
		int[] contextTargetOffset = null;
		int[] contextTargetSize = null;
		boolean hasContext = false;
		int inputCount = 0;
		int[] layerContextCount = null;
		int[] layerCounts = null;
		int[] layerFeedCounts = null;
		int[] layerIndex = null;
		double[] layerOutput = null;
		double[] layerSums = null;
		int outputCount = 0;
		int[] weightIndex = null;
		double[] weights = null;
		;
		int[] activation = null;
		double[] p = null;

		if (methodFile.exists()) {
			method = (MLMethod) EncogDirectoryPersistence
					.loadObject(methodFile);
			FlatNetwork flat = ((BasicNetwork) method).getFlat();

			contextTargetOffset = flat.getContextTargetOffset();
			contextTargetSize = flat.getContextTargetSize();
			hasContext = flat.getHasContext();
			inputCount = flat.getInputCount();
			layerContextCount = flat.getLayerContextCount();
			layerCounts = flat.getLayerCounts();
			layerFeedCounts = flat.getLayerFeedCounts();
			layerIndex = flat.getLayerIndex();
			layerOutput = flat.getLayerOutput();
			layerSums = flat.getLayerSums();
			outputCount = flat.getOutputCount();
			weightIndex = flat.getWeightIndex();
			weights = flat.getWeights();
			activation = createActivations(flat);
			p = createParams(flat);
		}

		setIndentLevel(2);
		addLine("#region Encog Data");
		indentIn();
		addNameValue("public const string EXPORT_FILENAME", "\""
				+ processFile.toString() + "\"");
		addCols();

		addNameValue("private readonly int[] _contextTargetOffset",
				contextTargetOffset);
		addNameValue("private readonly int[] _contextTargetSize",
				contextTargetSize);
		addNameValue("private const bool _hasContext", hasContext ? "true"
				: "false");
		addNameValue("private const int _inputCount", inputCount);
		addNameValue("private readonly int[] _layerContextCount",
				layerContextCount);
		addNameValue("private readonly int[] _layerCounts", layerCounts);
		addNameValue("private readonly int[] _layerFeedCounts",
				layerFeedCounts);
		addNameValue("private readonly int[] _layerIndex", layerIndex);
		addNameValue("private readonly double[] _layerOutput", layerOutput);
		addNameValue("private readonly double[] _layerSums", layerSums);
		addNameValue("private const int _outputCount", outputCount);
		addNameValue("private readonly int[] _weightIndex", weightIndex);
		addNameValue("private readonly double[] _weights", weights);
		addNameValue("private readonly int[] _activation", activation);
		addNameValue("private readonly double[] _p", p);
		indentOut();
		addLine("#endregion");
		setIndentLevel(0);
	}

	private double[] createParams(FlatNetwork flat) {
		double[] result = new double[flat.getActivationFunctions().length];
		EngineArray.fill(result, 1);
		return result;
	}

	private int[] createActivations(FlatNetwork flat) {
		int[] result = new int[flat.getActivationFunctions().length];
		for (int i = 0; i < flat.getActivationFunctions().length; i++) {
			ActivationFunction af = flat.getActivationFunctions()[i];

			if (af instanceof ActivationLinear) {
				result[i] = 0;
			} else if (af instanceof ActivationTANH) {
				result[i] = 1;
			}
			if (af instanceof ActivationSigmoid) {
				result[i] = 2;
			}
			if (af instanceof ActivationElliottSymmetric) {
				result[i] = 3;
			}
			if (af instanceof ActivationElliott) {
				result[i] = 4;
			}
		}

		return result;
	}

	private void processCalc() {
		AnalystField firstOutputField = null;
		int barsNeeded = this.getAnalyst().determineMaxTimeSlice();

		setIndentLevel(2);
		addLine("if( _inputCount>0 && CurrentBar>=" + barsNeeded + " )");
		addLine("{");
		indentIn();
		addLine("double[] input = new double[_inputCount];");
		addLine("double[] output = new double[_outputCount];");

		int idx = 0;
		for (AnalystField field : this.getAnalyst().getScript().getNormalize()
				.getNormalizedFields()) {
			if (field.isInput()) {

				DataField df = this.getAnalyst().getScript()
						.findDataField(field.getName());

				switch (field.getAction()) {
				case PassThrough:
					addLine("input[" + idx + "]=" + df.getSource() + "["
							+ (-field.getTimeSlice()) + "];");
					idx++;
					break;
				case Normalize:
					addLine("input[" + idx + "]=Norm(" + df.getSource() + "["
							+ (-field.getTimeSlice()) + "],"
							+ field.getNormalizedHigh() + ","
							+ field.getNormalizedLow() + ","
							+ field.getActualHigh() + ","
							+ field.getActualLow() + ");");
					idx++;
					break;
				case Ignore:
					break;
				default:
					throw new AnalystCodeGenerationError(
							"Can't generate Ninjascript code, unsupported normalizatoin action: "
									+ field.getAction().toString());
				}
			}
			if (field.isOutput()) {
				if (firstOutputField == null) {
					firstOutputField = field;
				}
			}
		}

		if (firstOutputField == null) {
			throw new AnalystCodeGenerationError(
					"Could not find an output field.");
		}

		addLine("Compute(input,output);");
		addLine("Output.Set(DeNorm(output[0]" + ","
				+ firstOutputField.getNormalizedHigh() + ","
				+ firstOutputField.getNormalizedLow() + ","
				+ firstOutputField.getActualHigh() + ","
				+ firstOutputField.getActualLow() + "));");
		indentOut();
		addLine("}");
		setIndentLevel(2);
	}

	private void processObtain() {
		setIndentLevel(3);
		addLine("double[] result = new double[ENCOG_COLS.Length];");

		int idx = 0;
		for (DataField df : this.getAnalyst().getScript().getFields()) {
			if (!df.getName().equalsIgnoreCase("time") && !df.getName().equalsIgnoreCase("prediction")) {
				addLine("result[" + idx + "]=" + df.getSource() + "[0];");
				idx++;
			}
		}
		addLine("return result;");
		setIndentLevel(0);
	}

	@Override
	public void processToken(String command) {
		if (command.equalsIgnoreCase("MAIN-BLOCK")) {
			processMainBlock();
		} else if (command.equals("CALC")) {
			processCalc();
		} else if (command.equals("OBTAIN")) {
			processObtain();
		}
		setIndentLevel(0);

	}

}
