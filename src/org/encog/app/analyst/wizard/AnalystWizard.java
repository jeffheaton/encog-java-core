package org.encog.app.analyst.wizard;

import java.io.File;
import java.net.URL;

import org.encog.app.analyst.AnalystError;
import org.encog.app.analyst.AnalystFileFormat;
import org.encog.app.analyst.AnalystGoal;
import org.encog.app.analyst.EncogAnalyst;
import org.encog.app.analyst.script.AnalystScript;
import org.encog.app.analyst.script.DataField;
import org.encog.app.analyst.script.prop.ScriptProperties;
import org.encog.app.analyst.script.segregate.AnalystSegregateTarget;
import org.encog.app.analyst.script.task.AnalystTask;
import org.encog.app.quant.normalize.NormalizationAction;
import org.encog.app.quant.normalize.NormalizedField;
import org.encog.ml.factory.MLMethodFactory;
import org.encog.util.file.FileUtil;

public class AnalystWizard {

	public final static String FILE_RAW = "FILE_RAW";
	public static final String FILE_NORMALIZE = "FILE_NORMALIZE";
	public static final String FILE_RANDOM = "FILE_RANDOMIZE";
	public static final String FILE_TRAIN = "FILE_TRAIN";
	public static final String FILE_EVAL = "FILE_EVAL";
	public static final String FILE_TRAINSET = "FILE_TRAINSET";
	public static final String FILE_EG = "FILE_EG";
	public static final String FILE_OUTPUT = "FILE_OUTPUT";
	public static final String FILE_SERIES = "FILE_SERIES";

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

	private void generateSettings(File file) {
		String train;
		this.script.getProperties().setFilename(AnalystWizard.FILE_RAW,
				file.toString());
		this.script.getProperties().setFilename(AnalystWizard.FILE_NORMALIZE,
				FileUtil.addFilenameBase(file, "_norm").toString());

		if (!this.timeSeries) {
			this.script.getProperties().setFilename(AnalystWizard.FILE_RANDOM,
					FileUtil.addFilenameBase(file, "_random").toString());
		} else {
			this.script.getProperties().setFilename(AnalystWizard.FILE_SERIES,
					FileUtil.addFilenameBase(file, "_series").toString());
		}

		this.script.getProperties().setFilename(AnalystWizard.FILE_OUTPUT,
				FileUtil.addFilenameBase(file, "_output").toString());
		this.script.getProperties().setFilename(AnalystWizard.FILE_TRAIN,
				train = FileUtil.addFilenameBase(file, "_train").toString());
		this.script.getProperties().setFilename(AnalystWizard.FILE_EVAL,
				FileUtil.addFilenameBase(file, "_eval").toString());
		this.script.getProperties().setFilename(AnalystWizard.FILE_TRAINSET,
				FileUtil.forceExtension(train, "egb"));
		
		if( this.egName==null ) {
			egName = new File( FileUtil.forceExtension(file.toString(), "eg") );			
		} 
		
		this.script.getProperties().setFilename(AnalystWizard.FILE_EG,
				egName.toString());
		
		String target;

		// starting point
		this.script.getProperties().setProperty(
				ScriptProperties.HEADER_DATASOURCE_rawFile,
				target = AnalystWizard.FILE_RAW);

		// randomize
		if (!this.timeSeries) {
			this.script.getProperties().setProperty(
					ScriptProperties.RANDOMIZE_CONFIG_sourceFile,
					AnalystWizard.FILE_RAW);
			this.script.getProperties().setProperty(
					ScriptProperties.RANDOMIZE_CONFIG_targetFile,
					target = AnalystWizard.FILE_RANDOM);
		}

		// segregate
		this.script.getProperties().setProperty(
				ScriptProperties.SEGREGATE_CONFIG_sourceFile, target);

		// normalize
		this.script.getProperties().setProperty(
				ScriptProperties.NORMALIZE_CONFIG_sourceFile,
				AnalystWizard.FILE_TRAIN);
		this.script.getProperties().setProperty(
				ScriptProperties.NORMALIZE_CONFIG_targetFile,
				target = AnalystWizard.FILE_NORMALIZE);

		// series
		if (this.timeSeries) {
			this.script.getProperties().setProperty(
					ScriptProperties.SERIES_CONFIG_sourceFile,
					target);
			this.script.getProperties().setProperty(
					ScriptProperties.SERIES_CONFIG_targetFile,
					target = AnalystWizard.FILE_SERIES);
		}

		// generate
		this.script.getProperties().setProperty(
				ScriptProperties.GENERATE_CONFIG_sourceFile,
				target);
		this.script.getProperties().setProperty(
				ScriptProperties.GENERATE_CONFIG_targetFile,
				AnalystWizard.FILE_TRAINSET);

		// ML
		this.script.getProperties().setProperty(
				ScriptProperties.ML_CONFIG_trainingFile,
				AnalystWizard.FILE_TRAINSET);
		this.script.getProperties().setProperty(
				ScriptProperties.ML_CONFIG_resourceFile, AnalystWizard.FILE_EG);
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
		NormalizedField[] norm = new NormalizedField[this.script.getFields().length];
		DataField[] dataFields = script.getFields();

		for (int i = 0; i < this.script.getFields().length; i++) {
			DataField f = dataFields[i];
			NormalizationAction action;
			boolean isLast = i == this.script.getFields().length - 1;

			if ((f.isInteger() || f.isReal()) && !f.isClass()) {
				action = NormalizationAction.Normalize;
				norm[i] = new NormalizedField(f.getName(), action, 1, -1);
				norm[i].setActualHigh(f.getMax());
				norm[i].setActualLow(f.getMin());
			} else if (f.isClass()) {
				if (isLast && this.directClassification) {
					action = NormalizationAction.SingleField;
				} else if (f.getClassMembers().size() > 2)
					action = NormalizationAction.Equilateral;
				else
					action = NormalizationAction.OneOf;
				norm[i] = new NormalizedField(f.getName(), action, 1, -1);
			} else {
				action = NormalizationAction.Ignore;
				norm[i] = new NormalizedField(action, f.getName());
			}
		}
		this.script.getNormalize().setNormalizedFields(norm);
		this.script.getNormalize().init(this.script);
	}

	private void generateRandomize(File file) {

	}

	private void generateSegregate(File file) {
		AnalystSegregateTarget[] array = new AnalystSegregateTarget[2];
		array[0] = new AnalystSegregateTarget(AnalystWizard.FILE_TRAIN, 75);
		array[1] = new AnalystSegregateTarget(AnalystWizard.FILE_EVAL, 25);
		this.script.getSegregate().setSegregateTargets(array);
	}

	private void determineTargetField() {
		NormalizedField[] fields = this.script.getNormalize()
				.getNormalizedFields();

		if (this.targetField.trim().length() == 0) {
			boolean success = false;

			if (this.goal == AnalystGoal.Classification) {
				// first try to the last classify field
				for (int i = 0; i < fields.length; i++) {
					DataField df = this.script.findDataField(fields[i]
							.getName());
					if (fields[i].getAction().isClassify() && df.isClass()) {
						this.targetField = fields[i].getName();
						success = true;
					}
				}
			} else {

				// otherwise, just return the last regression field
				for (int i = 0; i < fields.length; i++) {
					DataField df = this.script.findDataField(fields[i]
							.getName());
					if (!df.isClass() && (df.isReal() || df.isInteger())) {
						this.targetField = fields[i].getName();
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
				ScriptProperties.DATA_CONFIG_targetField, this.targetField);
		this.script.getProperties().setProperty(
				ScriptProperties.DATA_CONFIG_goal, this.goal);

	}

	private void generateGenerate(File file) {
		determineTargetField();
		NormalizedField targetField = this.script
				.findNormalizedField(this.targetField);

		if (targetField == null) {
			throw new AnalystError(
					"Failed to find normalized version of target field: "
							+ this.targetField);
		}

		int inputColumns = this.script.getNormalize().calculateInputColumns(
				targetField);
		int idealColumns = this.script.getNormalize().calculateOutputColumns(
				targetField);

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
		this.script.getProperties().setProperty(
				ScriptProperties.ML_CONFIG_architecture,
				"?B->TANH->" + hidden + "B->TANH->?");
		this.script.getProperties().setProperty(
				ScriptProperties.ML_CONFIG_resourceName, "ml");

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
		this.script.getProperties().setProperty(
				ScriptProperties.ML_CONFIG_resourceName, "ml");

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
		this.script.getProperties().setProperty(
				ScriptProperties.ML_CONFIG_resourceName, "ml");

		if (outputColumns > 1)
			this.script.getProperties().setProperty(
					ScriptProperties.ML_TRAIN_type, "rprop");
		else
			this.script.getProperties().setProperty(
					ScriptProperties.ML_TRAIN_type, "svd");

		this.script.getProperties().setProperty(ScriptProperties.ML_TRAIN_type,
				0.01);
	}

	public void generateTasks() {
		AnalystTask task1 = new AnalystTask(EncogAnalyst.TASK_FULL);
		if (!this.timeSeries) {
			task1.getLines().add("randomize");
		}
		task1.getLines().add("segregate");
		task1.getLines().add("normalize");
		if (this.timeSeries) {
			task1.getLines().add("series");
		}
		task1.getLines().add("generate");
		task1.getLines().add("create");
		task1.getLines().add("train");
		task1.getLines().add("evaluate");

		AnalystTask task2 = new AnalystTask("task-generate");
		if (!this.timeSeries) {
			task2.getLines().add("randomize");
		}
		task2.getLines().add("segregate");
		task2.getLines().add("normalize");
		if (this.timeSeries) {
			task1.getLines().add("series");
		}
		task2.getLines().add("generate");

		AnalystTask task3 = new AnalystTask("task-create");
		task3.getLines().add("create");

		AnalystTask task4 = new AnalystTask("task-train");
		task4.getLines().add("train");

		AnalystTask task5 = new AnalystTask("task-evaluate");
		task5.getLines().add("evaluate");

		this.script.addTask(task1);
		this.script.addTask(task2);
		this.script.addTask(task3);
		this.script.addTask(task4);
		this.script.addTask(task5);
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

		this.generateSettings(analyzeFile);
		analyst.download();

		wizard(analyzeFile, b, format);
	}

	public void reanalyze() {
		String rawID = this.script.getProperties().getPropertyFile(
				ScriptProperties.HEADER_DATASOURCE_rawFile);

		String rawFilename = this.script.getProperties().getFilename(rawID);

		this.analyst.analyze(
				new File(rawFilename),
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
	 * @param lagWindowSize the lagWindowSize to set
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
	 * @param leadWindowSize the leadWindowSize to set
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
	 * @param includeTargetField the includeTargetField to set
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

		generateTasks();
		determineClassification();
		generateSettings(analyzeFile);
		// this.analyst.getReport().reportPhase(1, 1, "Wizard analyzing data");
		this.analyst.analyze(analyzeFile, b, format);
		generateNormalizedFields(analyzeFile);
		generateRandomize(analyzeFile);
		generateSegregate(analyzeFile);
		generateGenerate(analyzeFile);
		generateTime(analyzeFile);

	}

	/**
	 * @return the egName
	 */
	public File getEGName() {
		return egName;
	}

	/**
	 * @param projectFile the egName to set
	 */
	public void setEGName(File projectFile) {
		this.egName = projectFile;
	}
}
