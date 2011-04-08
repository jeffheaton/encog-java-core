package org.encog.app.analyst.wizard;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.encog.app.analyst.AnalystError;
import org.encog.app.analyst.AnalystFileFormat;
import org.encog.app.analyst.AnalystGoal;
import org.encog.app.analyst.EncogAnalyst;
import org.encog.app.analyst.script.AnalystScript;
import org.encog.app.analyst.script.DataField;
import org.encog.app.analyst.script.normalize.AnalystField;
import org.encog.app.analyst.script.prop.ScriptProperties;
import org.encog.app.analyst.script.segregate.AnalystSegregateTarget;
import org.encog.app.analyst.script.task.AnalystTask;
import org.encog.app.quant.normalize.NormalizationAction;
import org.encog.ml.factory.MLMethodFactory;
import org.encog.util.file.FileUtil;

/**
 * The Encog Analyst Wizard can be used to create Encog Analyst script files
 * from a CSV file. This class is typically used by the Encog Workbench, but it
 * can easily be used from any program to create a starting point for an Encog
 * Analyst Script.
 * 
 * Several items must be provided to the wizard.
 * 
 * Desired Machine Learning Method: This is the machine learning method that you
 * would like the wizard to use. This might be a neural network, SVM or other
 * supported method.
 * 
 * Normalization Range: This is the range that the data should be normalized
 * into. Some machine learning methods perform better with different ranges. The
 * two ranges supported by the wizard are -1 to 1 and 0 to 1.
 * 
 * Goal: What are we trying to accomplish. Is this a classification, regression
 * or autoassociation problem.
 * 
 */
public class AnalystWizard {

	public final static String FILE_RAW = "FILE_RAW";
	public static final String FILE_NORMALIZE = "FILE_NORMALIZE";
	public static final String FILE_RANDOM = "FILE_RANDOMIZE";
	public static final String FILE_TRAIN = "FILE_TRAIN";
	public static final String FILE_EVAL = "FILE_EVAL";
	public static final String FILE_EVAL_NORM = "FILE_EVAL_NORM";
	public static final String FILE_TRAINSET = "FILE_TRAINSET";
	public static final String FILE_ML = "FILE_ML";
	public static final String FILE_OUTPUT = "FILE_OUTPUT";
	public static final String FILE_BALANCE = "FILE_BALANCE";

	private String filenameRaw;
	private String filenameNorm;
	private String filenameRandom;
	private String filenameTrain;
	private String filenameEval;
	private String filenameEvalNorm;
	private String filenameTrainSet;
	private String filenameML;
	private String filenameOutput;
	private String filenameBalance;

	private AnalystScript script;
	private EncogAnalyst analyst;
	private WizardMethodType methodType;
	private boolean directClassification = false;
	private String targetField;
	private AnalystGoal goal;
	private int lagWindowSize;
	private int leadWindowSize;
	private boolean includeTargetField;
	private boolean timeSeries;
	private File egName;
	private boolean taskSegregate = true;
	private boolean taskRandomize = true;
	private boolean taskNormalize = true;
	private boolean taskBalance = false;
	private NormalizeRange range = NormalizeRange.NegOne2One;

	public AnalystWizard(EncogAnalyst analyst) {
		this.analyst = analyst;
		this.script = analyst.getScript();
		this.methodType = WizardMethodType.FeedForward;
		this.targetField = "";
		this.goal = AnalystGoal.Classification;
		this.leadWindowSize = 0;
		this.lagWindowSize = 0;
		this.includeTargetField = false;
	}

	private void generateFilenames(File rawFile) {
		this.filenameRaw = rawFile.getName();
		this.filenameNorm = FileUtil.addFilenameBase(rawFile, "_norm")
				.toString();
		this.filenameRandom = FileUtil.addFilenameBase(rawFile, "_random")
				.toString();
		this.filenameTrain = FileUtil.addFilenameBase(rawFile, "_train")
				.toString();
		this.filenameEval = FileUtil.addFilenameBase(rawFile, "_eval")
				.toString();
		this.filenameEvalNorm = FileUtil.addFilenameBase(rawFile, "_eval_norm")
				.toString();
		this.filenameTrainSet = FileUtil.forceExtension(this.filenameTrain,
				"egb");
		this.filenameML = FileUtil.forceExtension(this.filenameTrain, "eg");
		this.filenameOutput = FileUtil.addFilenameBase(rawFile, "_output")
				.toString();
		this.filenameBalance = FileUtil.addFilenameBase(rawFile, "_balance")
				.toString();

		ScriptProperties p = this.script.getProperties();

		p.setFilename(FILE_RAW, this.filenameRaw);
		p.setFilename(FILE_NORMALIZE, this.filenameNorm);
		p.setFilename(FILE_RANDOM, this.filenameRandom);
		p.setFilename(FILE_TRAIN, this.filenameTrain);
		p.setFilename(FILE_EVAL, this.filenameEval);
		p.setFilename(FILE_EVAL_NORM, this.filenameEvalNorm);
		p.setFilename(FILE_TRAINSET, this.filenameTrainSet);
		p.setFilename(FILE_ML, this.filenameML);
		p.setFilename(FILE_OUTPUT, this.filenameOutput);
		p.setFilename(FILE_BALANCE, this.filenameBalance);
	}

	private void generateSettings() {

		String target;

		// starting point
		this.script.getProperties().setProperty(
				ScriptProperties.HEADER_DATASOURCE_rawFile,
				target = AnalystWizard.FILE_RAW);

		// randomize
		if (!this.timeSeries && this.taskRandomize) {
			this.script.getProperties().setProperty(
					ScriptProperties.RANDOMIZE_CONFIG_sourceFile,
					AnalystWizard.FILE_RAW);
			this.script.getProperties().setProperty(
					ScriptProperties.RANDOMIZE_CONFIG_targetFile,
					target = AnalystWizard.FILE_RANDOM);
		}

		// balance
		if (!this.timeSeries && this.taskBalance) {
			this.script.getProperties().setProperty(
					ScriptProperties.BALANCE_CONFIG_sourceFile, target);
			this.script.getProperties().setProperty(
					ScriptProperties.BALANCE_CONFIG_targetFile,
					target = AnalystWizard.FILE_BALANCE);
		}

		// segregate
		if (this.taskSegregate) {
			this.script.getProperties().setProperty(
					ScriptProperties.SEGREGATE_CONFIG_sourceFile, target);
			target = AnalystWizard.FILE_TRAIN;
		}

		// normalize
		this.script.getProperties().setProperty(
				ScriptProperties.NORMALIZE_CONFIG_sourceFile, target);
		this.script.getProperties().setProperty(
				ScriptProperties.NORMALIZE_CONFIG_targetFile,
				target = AnalystWizard.FILE_NORMALIZE);

		// generate
		this.script.getProperties().setProperty(
				ScriptProperties.GENERATE_CONFIG_sourceFile, target);
		this.script.getProperties().setProperty(
				ScriptProperties.GENERATE_CONFIG_targetFile,
				AnalystWizard.FILE_TRAINSET);

		// ML
		this.script.getProperties().setProperty(
				ScriptProperties.ML_CONFIG_trainingFile,
				AnalystWizard.FILE_TRAINSET);
		this.script.getProperties().setProperty(
				ScriptProperties.ML_CONFIG_machineLearningFile,
				AnalystWizard.FILE_ML);
		this.script.getProperties().setProperty(
				ScriptProperties.ML_CONFIG_outputFile,
				AnalystWizard.FILE_OUTPUT);

		this.script.getProperties().setProperty(
				ScriptProperties.ML_CONFIG_evalFile, AnalystWizard.FILE_EVAL);

		// other
		script.getProperties().setProperty(
				ScriptProperties.SETUP_CONFIG_csvFormat,
				AnalystFileFormat.DECPNT_COMMA);
	}

	private void generateNormalizedFields(File file) {
		List<AnalystField> norm = this.script.getNormalize()
				.getNormalizedFields();
		norm.clear();
		DataField[] dataFields = script.getFields();

		for (int i = 0; i < this.script.getFields().length; i++) {
			DataField f = dataFields[i];
			NormalizationAction action;
			boolean isLast = i == this.script.getFields().length - 1;

			if ((f.isInteger() || f.isReal()) && !f.isClass()) {
				action = NormalizationAction.Normalize;
				AnalystField af;
				if (this.range == NormalizeRange.NegOne2One)
					norm.add(af = new AnalystField(f.getName(), action, 1, -1));
				else
					norm.add(af = new AnalystField(f.getName(), action, 1, 0));
				af.setActualHigh(f.getMax());
				af.setActualLow(f.getMin());
			} else if (f.isClass()) {
				if (isLast && this.directClassification) {
					action = NormalizationAction.SingleField;
				} else if (f.getClassMembers().size() > 2)
					action = NormalizationAction.Equilateral;
				else
					action = NormalizationAction.OneOf;

				if (this.range == NormalizeRange.NegOne2One)
					norm.add(new AnalystField(f.getName(), action, 1, -1));
				else
					norm.add(new AnalystField(f.getName(), action, 1, 0));
			} else {
				action = NormalizationAction.Ignore;
				norm.add(new AnalystField(action, f.getName()));
			}
		}

		this.script.getNormalize().init(this.script);
	}

	private void generateSegregate(File file) {
		if (this.taskSegregate) {
			AnalystSegregateTarget[] array = new AnalystSegregateTarget[2];
			array[0] = new AnalystSegregateTarget(AnalystWizard.FILE_TRAIN, 75);
			array[1] = new AnalystSegregateTarget(AnalystWizard.FILE_EVAL, 25);
			this.script.getSegregate().setSegregateTargets(array);
		} else {
			AnalystSegregateTarget[] array = new AnalystSegregateTarget[0];
			this.script.getSegregate().setSegregateTargets(array);
		}
	}

	private void determineTargetField() {
		List<AnalystField> fields = this.script.getNormalize()
				.getNormalizedFields();

		if (this.targetField.trim().length() == 0) {
			boolean success = false;

			if (this.goal == AnalystGoal.Classification) {
				// first try to the last classify field
				for (AnalystField field : fields) {
					DataField df = this.script.findDataField(field.getName());
					if (field.getAction().isClassify() && df.isClass()) {
						this.targetField = field.getName();
						success = true;
					}
				}
			} else {

				// otherwise, just return the last regression field
				for (AnalystField field : fields) {
					DataField df = this.script.findDataField(field.getName());
					if (!df.isClass() && (df.isReal() || df.isInteger())) {
						this.targetField = field.getName();
						success = true;
					}
				}
			}

			if (!success) {
				throw new AnalystError(
						"Can't determine target field automatically, please specify one.\nThis can also happen if you specified the wrong file format.");
			}
		} else {
			if (this.script.findDataField(this.targetField) == null) {
				throw new AnalystError("Invalid target field: "
						+ this.targetField);
			}
		}

		this.script.getProperties().setProperty(
				ScriptProperties.DATA_CONFIG_goal, this.goal);

		if (!this.timeSeries && this.taskBalance) {
			this.script.getProperties().setProperty(
					ScriptProperties.BALANCE_CONFIG_balanceField, targetField);
			DataField field = this.analyst.getScript().findDataField(
					targetField);
			if (field != null && field.isClass()) {
				int countPer = field.getMinClassCount();
				this.script.getProperties().setProperty(
						ScriptProperties.BALANCE_CONFIG_countPer, countPer);
			}
		}

		// now that the target field has been determined, set the analyst fields
		AnalystField af = null;
		for (AnalystField field : this.analyst.getScript().getNormalize()
				.getNormalizedFields()) {
			if (field.getAction() != NormalizationAction.Ignore
					&& field.getName().equalsIgnoreCase(this.targetField)) {
				if (af == null || af.getTimeSlice() < af.getTimeSlice()) {
					af = field;
				}
			}
		}

		if (af != null) {
			af.setOutput(true);
		}
	}

	private void generateGenerate(File file) {
		determineTargetField();

		if (targetField == null) {
			throw new AnalystError(
					"Failed to find normalized version of target field: "
							+ this.targetField);
		}

		int inputColumns = this.script.getNormalize().calculateInputColumns();
		int idealColumns = this.script.getNormalize().calculateOutputColumns();

		switch (this.methodType) {
		case FeedForward:
			generateFeedForward(inputColumns, idealColumns);
			break;
		case SVM:
			generateSVM(inputColumns, idealColumns);
			break;
		case RBF:
			generateRBF(inputColumns, idealColumns);
		}
	}

	private void generateFeedForward(int inputColumns, int outputColumns) {
		int hidden = (int) (((double) inputColumns) * 1.5);
		this.script.getProperties().setProperty(
				ScriptProperties.ML_CONFIG_type,
				MLMethodFactory.TYPE_FEEDFORWARD);

		if (this.range == NormalizeRange.NegOne2One) {
			this.script.getProperties().setProperty(
					ScriptProperties.ML_CONFIG_architecture,
					"?B->TANH->" + hidden + "B->TANH->?");
		} else {
			this.script.getProperties().setProperty(
					ScriptProperties.ML_CONFIG_architecture,
					"?B->SIGMOID->" + hidden + "B->SIGMOID->?");
		}

		this.script.getProperties().setProperty(ScriptProperties.ML_TRAIN_type,
				"rprop");
		this.script.getProperties().setProperty(
				ScriptProperties.ML_TRAIN_targetError, 0.01);
	}

	private void generateSVM(int inputColumns, int outputColumns) {
		this.script.getProperties().setProperty(
				ScriptProperties.ML_CONFIG_type, MLMethodFactory.TYPE_SVM);
		this.script.getProperties().setProperty(
				ScriptProperties.ML_CONFIG_architecture,
				"?->C(type=new,kernel=gaussian)->?");

		this.script.getProperties().setProperty(ScriptProperties.ML_TRAIN_type,
				"svm-train");
		this.script.getProperties().setProperty(
				ScriptProperties.ML_TRAIN_targetError, 0.01);
	}

	private void generateRBF(int inputColumns, int outputColumns) {
		int hidden = (int) (((double) inputColumns) * 1.5);
		this.script.getProperties().setProperty(
				ScriptProperties.ML_CONFIG_type,
				MLMethodFactory.TYPE_RBFNETWORK);
		this.script.getProperties().setProperty(
				ScriptProperties.ML_CONFIG_architecture,
				"?->GAUSSIAN(" + hidden + ")->?");

		if (outputColumns > 1)
			this.script.getProperties().setProperty(
					ScriptProperties.ML_TRAIN_type, "rprop");
		else
			this.script.getProperties().setProperty(
					ScriptProperties.ML_TRAIN_type, "svd");

		this.script.getProperties().setProperty(ScriptProperties.ML_TRAIN_type,
				0.01);
	}

	private String createSet(String setTarget, String setSource) {
		StringBuilder result = new StringBuilder();
		result.append("set ");
		result.append(ScriptProperties.toDots(setTarget));
		result.append("=\"");
		result.append(setSource);
		result.append("\"");
		return result.toString();
	}

	public void generateTasks() {
		AnalystTask task1 = new AnalystTask(EncogAnalyst.TASK_FULL);
		if (!this.timeSeries && this.taskRandomize) {
			task1.getLines().add("randomize");
		}

		if (!this.timeSeries && this.taskBalance) {
			task1.getLines().add("balance");
		}

		if (this.taskSegregate) {
			task1.getLines().add("segregate");
		}

		if (this.taskNormalize) {
			task1.getLines().add("normalize");
		}

		task1.getLines().add("generate");
		task1.getLines().add("create");
		task1.getLines().add("train");
		task1.getLines().add("evaluate");

		AnalystTask task2 = new AnalystTask("task-generate");
		if (!this.timeSeries && this.taskRandomize) {
			task2.getLines().add("randomize");
		}

		if (this.taskSegregate) {
			task2.getLines().add("segregate");
		}
		if (this.taskNormalize) {
			task2.getLines().add("normalize");
		}
		if (this.timeSeries) {
			task1.getLines().add("series");
		}
		task2.getLines().add("generate");

		AnalystTask task3 = new AnalystTask("task-evaluate-raw");
		task3.getLines().add(
				createSet(ScriptProperties.ML_CONFIG_evalFile,
						AnalystWizard.FILE_EVAL_NORM));
		task3.getLines().add(
				createSet(ScriptProperties.NORMALIZE_CONFIG_sourceFile,
						AnalystWizard.FILE_EVAL));
		task3.getLines().add(
				createSet(ScriptProperties.NORMALIZE_CONFIG_targetFile,
						AnalystWizard.FILE_EVAL_NORM));
		task3.getLines().add("normalize");
		task3.getLines().add("evaluate-raw");

		AnalystTask task4 = new AnalystTask("task-create");
		task4.getLines().add("create");

		AnalystTask task5 = new AnalystTask("task-train");
		task5.getLines().add("train");

		AnalystTask task6 = new AnalystTask("task-evaluate");
		task6.getLines().add("evaluate");

		this.script.addTask(task1);
		this.script.addTask(task2);
		this.script.addTask(task3);
		this.script.addTask(task4);
		this.script.addTask(task5);
		this.script.addTask(task6);
	}

	public void wizard(URL url, File saveFile, File analyzeFile, boolean b,
			AnalystFileFormat format) {

		this.script.getProperties().setProperty(
				ScriptProperties.HEADER_DATASOURCE_sourceFile, url);
		this.script.getProperties().setProperty(
				ScriptProperties.HEADER_DATASOURCE_sourceFormat, format);
		this.script.getProperties().setProperty(
				ScriptProperties.HEADER_DATASOURCE_sourceHeaders, b);
		this.script.getProperties().setProperty(
				ScriptProperties.HEADER_DATASOURCE_rawFile, analyzeFile);

		this.generateFilenames(analyzeFile);
		this.generateSettings();
		analyst.download();

		wizard(analyzeFile, b, format);
	}

	public void reanalyze() {
		String rawID = this.script.getProperties().getPropertyFile(
				ScriptProperties.HEADER_DATASOURCE_rawFile);

		File rawFilename = this.analyst.getScript().resolveFilename(rawID);

		this.analyst.analyze(
				rawFilename,
				this.script.getProperties().getPropertyBoolean(
						ScriptProperties.SETUP_CONFIG_inputHeaders),
				this.script.getProperties().getPropertyFormat(
						ScriptProperties.SETUP_CONFIG_csvFormat));

	}

	/**
	 * @return the methodType
	 */
	public WizardMethodType getMethodType() {
		return methodType;
	}

	/**
	 * @param methodType
	 *            the methodType to set
	 */
	public void setMethodType(WizardMethodType methodType) {
		this.methodType = methodType;
	}

	private void determineClassification() {
		this.directClassification = false;

		if (this.methodType == WizardMethodType.SVM) {
			this.directClassification = true;
		}
	}

	public AnalystGoal getGoal() {
		return goal;
	}

	public void setGoal(AnalystGoal goal) {
		this.goal = goal;
	}

	public void setTargetField(String targetField) {
		this.targetField = targetField;
	}

	public String getTargetField() {
		return this.targetField;
	}

	/**
	 * @return the lagWindowSize
	 */
	public int getLagWindowSize() {
		return lagWindowSize;
	}

	/**
	 * @param lagWindowSize
	 *            the lagWindowSize to set
	 */
	public void setLagWindowSize(int lagWindowSize) {
		this.lagWindowSize = lagWindowSize;
	}

	/**
	 * @return the leadWindowSize
	 */
	public int getLeadWindowSize() {
		return leadWindowSize;
	}

	/**
	 * @param leadWindowSize
	 *            the leadWindowSize to set
	 */
	public void setLeadWindowSize(int leadWindowSize) {
		this.leadWindowSize = leadWindowSize;
	}

	/**
	 * @return the includeTargetField
	 */
	public boolean isIncludeTargetField() {
		return includeTargetField;
	}

	/**
	 * @param includeTargetField
	 *            the includeTargetField to set
	 */
	public void setIncludeTargetField(boolean includeTargetField) {
		this.includeTargetField = includeTargetField;
	}

	private void generateTime(File file) {

		this.script.getProperties().setProperty(
				ScriptProperties.SERIES_CONFIG_lag, this.lagWindowSize);
		this.script.getProperties().setProperty(
				ScriptProperties.SERIES_CONFIG_lead, this.leadWindowSize);
		this.script.getProperties().setProperty(
				ScriptProperties.SERIES_CONFIG_includeTarget,
				this.includeTargetField);

	}

	public void wizard(File analyzeFile, boolean b, AnalystFileFormat format) {

		this.script.getProperties().setProperty(
				ScriptProperties.HEADER_DATASOURCE_sourceFormat, format);
		this.script.getProperties().setProperty(
				ScriptProperties.HEADER_DATASOURCE_sourceHeaders, b);
		this.script.getProperties().setProperty(
				ScriptProperties.HEADER_DATASOURCE_rawFile, analyzeFile);

		this.timeSeries = (this.lagWindowSize > 0 || this.leadWindowSize > 0);

		determineClassification();
		this.generateFilenames(analyzeFile);
		generateSettings();
		this.analyst.analyze(analyzeFile, b, format);
		generateNormalizedFields(analyzeFile);
		generateSegregate(analyzeFile);

		generateGenerate(analyzeFile);
		generateTime(analyzeFile);
		generateTasks();
		if (this.timeSeries && this.lagWindowSize > 0
				&& this.leadWindowSize > 0) {
			expandTimeSlices();
		}
	}

	/**
	 * @return the egName
	 */
	public File getEGName() {
		return egName;
	}

	/**
	 * @param projectFile
	 *            the egName to set
	 */
	public void setEGName(File projectFile) {
		this.egName = projectFile;
	}

	/**
	 * @return the taskSegregate
	 */
	public boolean isTaskSegregate() {
		return taskSegregate;
	}

	/**
	 * @param taskSegregate
	 *            the taskSegregate to set
	 */
	public void setTaskSegregate(boolean taskSegregate) {
		this.taskSegregate = taskSegregate;
	}

	/**
	 * @return the taskRandomize
	 */
	public boolean isTaskRandomize() {
		return taskRandomize;
	}

	/**
	 * @param taskRandomize
	 *            the taskRandomize to set
	 */
	public void setTaskRandomize(boolean taskRandomize) {
		this.taskRandomize = taskRandomize;
	}

	/**
	 * @return the taskNormalize
	 */
	public boolean isTaskNormalize() {
		return taskNormalize;
	}

	/**
	 * @param taskNormalize
	 *            the taskNormalize to set
	 */
	public void setTaskNormalize(boolean taskNormalize) {
		this.taskNormalize = taskNormalize;
	}

	/**
	 * @return the range
	 */
	public NormalizeRange getRange() {
		return range;
	}

	/**
	 * @param range
	 *            the range to set
	 */
	public void setRange(NormalizeRange range) {
		this.range = range;
	}

	/**
	 * @return the taskBalance
	 */
	public boolean isTaskBalance() {
		return taskBalance;
	}

	/**
	 * @param taskBalance the taskBalance to set
	 */
	public void setTaskBalance(boolean taskBalance) {
		this.taskBalance = taskBalance;
	}

	private void expandTimeSlices() {
		List<AnalystField> oldList = this.script.getNormalize()
				.getNormalizedFields();
		List<AnalystField> newList = new ArrayList<AnalystField>();

		// generate the inputs for the new list
		for (AnalystField field : oldList) {
			if (!field.isIgnored()) {

				if (this.includeTargetField || field.isInput()) {
					for (int i = 0; i < this.lagWindowSize; i++) {
						AnalystField newField = new AnalystField(field);
						newField.setTimeSlice(-i);
						newField.setOutput(false);
						newList.add(newField);
					}
				}
			} else {
				newList.add(field);
			}
		}
		
		// generate the outputs for the new list
		for (AnalystField field : oldList) {
			if (!field.isIgnored()) {
				if (field.isOutput()) {
					for (int i = 1; i <= this.leadWindowSize; i++) {
						AnalystField newField = new AnalystField(field);
						newField.setTimeSlice(i);
						newList.add(newField);
					}
				}
			}
		}
		
		// generate the ignores for the new list
		for (AnalystField field : oldList) {
			if (field.isIgnored()) {
				newList.add(field);
			} 
		}

		// swap back in
		oldList.clear();
		oldList.addAll(newList);

	}
}
