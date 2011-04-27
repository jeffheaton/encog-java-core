/*
 * Encog(tm) Core v3.0 - Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 
 * Copyright 2008-2011 Heaton Research, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *   
 * For more information on Heaton Research copyrights, licenses 
 * and trademarks visit:
 * http://www.heatonresearch.com/copyright
 */
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
import org.encog.app.csv.normalize.NormalizationAction;
import org.encog.ml.factory.MLMethodFactory;
import org.encog.ml.factory.MLTrainFactory;
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

	/**
	 * The default training percent.
	 */
	public static final int DEFAULT_TRAIN_PERCENT = 75;
	
	/**
	 * The default evaluation percent.
	 */
	public static final int DEFAULT_EVAL_PERCENT = 25;
	
	/**
	 * The default training error.
	 */
	public static final double DEFAULT_TRAIN_ERROR = 0.01;

	/**
	 * The raw file.
	 */
	public static final String FILE_RAW = "FILE_RAW";
	
	/**
	 * The normalized file.
	 */
	public static final String FILE_NORMALIZE = "FILE_NORMALIZE";
	
	/**
	 * The randomized file.
	 */
	public static final String FILE_RANDOM = "FILE_RANDOMIZE";
	
	/**
	 * The training file.
	 */
	public static final String FILE_TRAIN = "FILE_TRAIN";
	
	/**
	 * The evaluation file.
	 */
	public static final String FILE_EVAL = "FILE_EVAL";
	
	/**
	 * The eval file normalization file.
	 */
	public static final String FILE_EVAL_NORM = "FILE_EVAL_NORM";
	
	/**
	 * The training set.
	 */
	public static final String FILE_TRAINSET = "FILE_TRAINSET";
	
	/**
	 * The machine learning file.
	 */
	public static final String FILE_ML = "FILE_ML";
	
	/**
	 * The output file.
	 */
	public static final String FILE_OUTPUT = "FILE_OUTPUT";
	
	/**
	 * The balanced file.
	 */
	public static final String FILE_BALANCE = "FILE_BALANCE";
	
	/**
	 * The clustered file.
	 */
	public static final String FILE_CLUSTER = "FILE_CLUSTER";

	/**
	 * The raw filename.
	 */
	private String filenameRaw;
	
	/**
	 * The normalized filename.
	 */
	private String filenameNorm;
	
	/**
	 * The random file name.
	 */
	private String filenameRandom;
	
	/**
	 * The training filename.
	 */
	private String filenameTrain;
	
	/**
	 * The evaluation filename.
	 */
	private String filenameEval;
	
	/**
	 * The normalization eval file name.
	 */
	private String filenameEvalNorm;
	
	/**
	 * The training set filename.
	 */
	private String filenameTrainSet;
	
	/**
	 * The machine learning file name.
	 */
	private String filenameML;
	
	/**
	 * The output filename.
	 */
	private String filenameOutput;
	
	/**
	 * The balance filename.
	 */
	private String filenameBalance;
	
	/**
	 * The cluster filename.
	 */
	private String filenameCluster;

	/**
	 * The analyst script.
	 */
	private final AnalystScript script;
	
	/**
	 * The analyst.
	 */
	private final EncogAnalyst analyst;
	
	/**
	 * The machine learning method that we will be using.
	 */
	private WizardMethodType methodType;
		
	/**
	 * Are we using single-field(direct) classification.
	 */
	private boolean directClassification = false;
	
	/**
	 * The target field, or "" to detect.
	 */
	private String targetField;
	
	/**
	 * The analyst goal.
	 */
	private AnalystGoal goal;
	
	/**
	 * The size of the lag window, if we are doing time-series.
	 */
	private int lagWindowSize;
	
	/**
	 * The size of the lead window, if we are doing time-series.
	 */
	private int leadWindowSize;
	
	/**
	 * Should the target field be included int he input, if we are doing 
	 * time-series.
	 */
	private boolean includeTargetField;
	
	/**
	 * True if we are doing time-series.
	 */
	private boolean timeSeries;
	
	/**
	 * True if the segregate command should be generated.
	 */
	private boolean taskSegregate = true;
	
	/**
	 * True if the randomize command should be generated.
	 */
	private boolean taskRandomize = true;
	
	/**
	 * True if the normalize command should be generated.
	 */
	private boolean taskNormalize = true;
	
	/**
	 * True if the balance command should be generated.
	 */
	private boolean taskBalance = false;
	
	/**
	 * True if the cluster command should be generated.
	 */
	private boolean taskCluster = true;
	
	/**
	 * The normalization range.
	 */
	private NormalizeRange range = NormalizeRange.NegOne2One;

	/**
	 * Construct the analyst wizard.
	 * @param theAnalyst The analyst to use.
	 */
	public AnalystWizard(final EncogAnalyst theAnalyst) {
		this.analyst = theAnalyst;
		this.script = analyst.getScript();
		this.methodType = WizardMethodType.FeedForward;
		this.targetField = "";
		this.goal = AnalystGoal.Classification;
		this.leadWindowSize = 0;
		this.lagWindowSize = 0;
		this.includeTargetField = false;
	}

	/**
	 * Create a "set" command to add to a task.
	 * @param setTarget The target.
	 * @param setSource The source.
	 * @return The "set" command.
	 */
	private String createSet(final String setTarget, final String setSource) {
		final StringBuilder result = new StringBuilder();
		result.append("set ");
		result.append(ScriptProperties.toDots(setTarget));
		result.append("=\"");
		result.append(setSource);
		result.append("\"");
		return result.toString();
	}

	/**
	 * Determine the type of classification used.
	 */
	private void determineClassification() {
		this.directClassification = false;

		if ((this.methodType == WizardMethodType.SVM)
				|| (this.methodType == WizardMethodType.SOM)) {
			this.directClassification = true;
		}
	}

	/**
	 * Determine the target field.
	 */
	private void determineTargetField() {
		final List<AnalystField> fields = this.script.getNormalize()
				.getNormalizedFields();

		if (this.targetField.trim().length() == 0) {
			boolean success = false;

			if (this.goal == AnalystGoal.Classification) {
				// first try to the last classify field
				for (final AnalystField field : fields) {
					final DataField df = this.script.findDataField(field
							.getName());
					if (field.getAction().isClassify() && df.isClass()) {
						this.targetField = field.getName();
						success = true;
					}
				}
			} else {

				// otherwise, just return the last regression field
				for (final AnalystField field : fields) {
					final DataField df = this.script.findDataField(field
							.getName());
					if (!df.isClass() && (df.isReal() || df.isInteger())) {
						this.targetField = field.getName();
						success = true;
					}
				}
			}

			if (!success) {
				throw new AnalystError(
						"Can't determine target field automatically, " 
					+ "please specify one.\nThis can also happen if you " 
						+ "specified the wrong file format.");
			}
		} else {
			if (this.script.findDataField(this.targetField) == null) {
				throw new AnalystError("Invalid target field: "
						+ this.targetField);
			}
		}

		this.script.getProperties().setProperty(
				ScriptProperties.DATA_CONFIG_GOAL, this.goal);

		if (!this.timeSeries && this.taskBalance) {
			this.script.getProperties().setProperty(
					ScriptProperties.BALANCE_CONFIG_BALANCE_FIELD,
					this.targetField);
			final DataField field = this.analyst.getScript().findDataField(
					this.targetField);
			if ((field != null) && field.isClass()) {
				final int countPer = field.getMinClassCount();
				this.script.getProperties().setProperty(
						ScriptProperties.BALANCE_CONFIG_COUNT_PER, countPer);
			}
		}

		// now that the target field has been determined, set the analyst fields
		AnalystField af = null;
		for (final AnalystField field : this.analyst.getScript().getNormalize()
				.getNormalizedFields()) {
			if ((field.getAction() != NormalizationAction.Ignore)
					&& field.getName().equalsIgnoreCase(this.targetField)) {
				if ((af == null) || (af.getTimeSlice() < af.getTimeSlice())) {
					af = field;
				}
			}
		}

		if (af != null) {
			af.setOutput(true);
		}

		// set the clusters count
		if (this.taskCluster) {
			if ((this.targetField.length() == 0)
					|| (this.goal != AnalystGoal.Classification)) {
				this.script.getProperties().setProperty(
						ScriptProperties.CLUSTER_CONFIG_CLUSTERS, 2);
			} else {
				final DataField tf = this.script
						.findDataField(this.targetField);
				this.script.getProperties().setProperty(
						ScriptProperties.CLUSTER_CONFIG_CLUSTERS,
						tf.getClassMembers().size());
			}
		}
	}

	/**
	 * Expand the time-series fields.
	 */
	private void expandTimeSlices() {
		final List<AnalystField> oldList = this.script.getNormalize()
				.getNormalizedFields();
		final List<AnalystField> newList = new ArrayList<AnalystField>();

		// generate the inputs for the new list
		for (final AnalystField field : oldList) {
			if (!field.isIgnored()) {

				if (this.includeTargetField || field.isInput()) {
					for (int i = 0; i < this.lagWindowSize; i++) {
						final AnalystField newField = new AnalystField(field);
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
		for (final AnalystField field : oldList) {
			if (!field.isIgnored()) {
				if (field.isOutput()) {
					for (int i = 1; i <= this.leadWindowSize; i++) {
						final AnalystField newField = new AnalystField(field);
						newField.setTimeSlice(i);
						newList.add(newField);
					}
				}
			}
		}

		// generate the ignores for the new list
		for (final AnalystField field : oldList) {
			if (field.isIgnored()) {
				newList.add(field);
			}
		}

		// swap back in
		oldList.clear();
		oldList.addAll(newList);

	}

	/**
	 * Generate a feed forward machine learning method.
	 * @param inputColumns The input column count.
	 * @param outputColumns The output column count.
	 */
	private void generateFeedForward(final int inputColumns,
			final int outputColumns) {
		final int hidden = (int) ((inputColumns) * 1.5);
		this.script.getProperties().setProperty(
				ScriptProperties.ML_CONFIG_TYPE,
				MLMethodFactory.TYPE_FEEDFORWARD);

		if (this.range == NormalizeRange.NegOne2One) {
			this.script.getProperties().setProperty(
					ScriptProperties.ML_CONFIG_ARCHITECTURE,
					"?:B->TANH->" + hidden + ":B->TANH->?");
		} else {
			this.script.getProperties().setProperty(
					ScriptProperties.ML_CONFIG_ARCHITECTURE,
					"?:B->SIGMOID->" + hidden + ":B->SIGMOID->?");
		}

		this.script.getProperties().setProperty(ScriptProperties.ML_TRAIN_TYPE,
				"rprop");
		this.script.getProperties().setProperty(
				ScriptProperties.ML_TRAIN_TARGET_ERROR, DEFAULT_TRAIN_ERROR);
	}

	/**
	 * Generate filenames.
	 * @param rawFile The raw filename.
	 */
	private void generateFilenames(final File rawFile) {
		this.filenameRaw = rawFile.getName();
		this.filenameNorm = FileUtil.addFilenameBase(rawFile, "_norm")
				.getName();
		this.filenameRandom = FileUtil.addFilenameBase(rawFile, "_random")
				.getName();
		this.filenameTrain = FileUtil.addFilenameBase(rawFile, "_train")
				.getName();
		this.filenameEval = FileUtil.addFilenameBase(rawFile, "_eval")
				.getName();
		this.filenameEvalNorm = FileUtil.addFilenameBase(rawFile, "_eval_norm")
				.getName();
		this.filenameTrainSet = FileUtil.forceExtension(this.filenameTrain,
				"egb");
		this.filenameML = FileUtil.forceExtension(this.filenameTrain, "eg");
		this.filenameOutput = FileUtil.addFilenameBase(rawFile, "_output")
				.getName();
		this.filenameBalance = FileUtil.addFilenameBase(rawFile, "_balance")
				.getName();
		this.filenameCluster = FileUtil.addFilenameBase(rawFile, "_cluster")
				.getName();

		final ScriptProperties p = this.script.getProperties();

		p.setFilename(AnalystWizard.FILE_RAW, this.filenameRaw);
		p.setFilename(AnalystWizard.FILE_NORMALIZE, this.filenameNorm);
		p.setFilename(AnalystWizard.FILE_RANDOM, this.filenameRandom);
		p.setFilename(AnalystWizard.FILE_TRAIN, this.filenameTrain);
		p.setFilename(AnalystWizard.FILE_EVAL, this.filenameEval);
		p.setFilename(AnalystWizard.FILE_EVAL_NORM, this.filenameEvalNorm);
		p.setFilename(AnalystWizard.FILE_TRAINSET, this.filenameTrainSet);
		p.setFilename(AnalystWizard.FILE_ML, this.filenameML);
		p.setFilename(AnalystWizard.FILE_OUTPUT, this.filenameOutput);
		p.setFilename(AnalystWizard.FILE_BALANCE, this.filenameBalance);
		p.setFilename(AnalystWizard.FILE_CLUSTER, this.filenameCluster);
	}

	/**
	 * Generate the generate task.
	 */
	private void generateGenerate() {
		determineTargetField();

		if (this.targetField == null) {
			throw new AnalystError(
					"Failed to find normalized version of target field: "
							+ this.targetField);
		}

		final int inputColumns = this.script.getNormalize()
				.calculateInputColumns();
		final int idealColumns = this.script.getNormalize()
				.calculateOutputColumns();

		switch (this.methodType) {
		case FeedForward:
			generateFeedForward(inputColumns, idealColumns);
			break;
		case SVM:
			generateSVM(inputColumns, idealColumns);
			break;
		case RBF:
			generateRBF(inputColumns, idealColumns);
			break;
		case SOM:
			generateSOM(inputColumns);
			break;
		default:
			throw new AnalystError("Unknown method type");
		}
	}

	/**
	 * Generate the normalized fields.
	 */
	private void generateNormalizedFields() {
		final List<AnalystField> norm = this.script.getNormalize()
				.getNormalizedFields();
		norm.clear();
		final DataField[] dataFields = this.script.getFields();

		for (int i = 0; i < this.script.getFields().length; i++) {
			final DataField f = dataFields[i];

			NormalizationAction action;
			final boolean isLast = i == this.script.getFields().length - 1;

			if ((f.isInteger() || f.isReal()) && !f.isClass()) {
				action = NormalizationAction.Normalize;
				AnalystField af;
				if (this.range == NormalizeRange.NegOne2One) {
					af = new AnalystField(f.getName(), action, 1, -1);
				} else {					
					af = new AnalystField(f.getName(), action, 1, 0);
				}
				norm.add(af);
				af.setActualHigh(f.getMax());
				af.setActualLow(f.getMin());
			} else if (f.isClass()) {
				if (isLast && this.directClassification) {
					action = NormalizationAction.SingleField;
				} else if (f.getClassMembers().size() > 2) {
					action = NormalizationAction.Equilateral;
				} else {
					action = NormalizationAction.OneOf;
				}

				if (this.range == NormalizeRange.NegOne2One) {
					norm.add(new AnalystField(f.getName(), action, 1, -1));
				} else {
					norm.add(new AnalystField(f.getName(), action, 1, 0));
				}
			} else {
				action = NormalizationAction.Ignore;
				norm.add(new AnalystField(action, f.getName()));
			}
		}

		this.script.getNormalize().init(this.script);
	}

	/**
	 * Generate a RBF machine learning method.
	 * @param inputColumns The number of input columns.
	 * @param outputColumns The number of output columns.
	 */
	private void generateRBF(final int inputColumns, final int outputColumns) {
		final int hidden = (int) ((inputColumns) * 1.5);
		this.script.getProperties().setProperty(
				ScriptProperties.ML_CONFIG_TYPE,
				MLMethodFactory.TYPE_RBFNETWORK);
		this.script.getProperties().setProperty(
				ScriptProperties.ML_CONFIG_ARCHITECTURE,
				"?->GAUSSIAN(c=" + hidden + ")->?");

		if (outputColumns > 1) {
			this.script.getProperties().setProperty(
					ScriptProperties.ML_TRAIN_TYPE, "rprop");
		} else {
			this.script.getProperties().setProperty(
					ScriptProperties.ML_TRAIN_TYPE, "svd");
		}

		this.script.getProperties().setProperty(ScriptProperties.ML_TRAIN_TYPE,
				DEFAULT_TRAIN_ERROR);
	}

	/**
	 * Generate the segregate task.
	 */
	private void generateSegregate() {
		if (this.taskSegregate) {
			final AnalystSegregateTarget[] array 
				= new AnalystSegregateTarget[2];
			array[0] = new AnalystSegregateTarget(
					AnalystWizard.FILE_TRAIN, DEFAULT_TRAIN_PERCENT);
			array[1] = new AnalystSegregateTarget(
					AnalystWizard.FILE_EVAL, DEFAULT_EVAL_PERCENT);
			this.script.getSegregate().setSegregateTargets(array);
		} else {
			final AnalystSegregateTarget[] array 
				= new AnalystSegregateTarget[0];
			this.script.getSegregate().setSegregateTargets(array);
		}
	}

	/**
	 * Generate the settings.
	 */
	private void generateSettings() {

		String target;

		// starting point
		target = AnalystWizard.FILE_RAW;
		this.script.getProperties().setProperty(
				ScriptProperties.HEADER_DATASOURCE_RAW_FILE,
				target);

		// randomize
		if (!this.timeSeries && this.taskRandomize) {
			this.script.getProperties().setProperty(
					ScriptProperties.RANDOMIZE_CONFIG_SOURCE_FILE,
					AnalystWizard.FILE_RAW);
			target = AnalystWizard.FILE_RANDOM;
			this.script.getProperties().setProperty(
					ScriptProperties.RANDOMIZE_CONFIG_TARGET_FILE,
					target);
		}

		// balance
		if (!this.timeSeries && this.taskBalance) {
			this.script.getProperties().setProperty(
					ScriptProperties.BALANCE_CONFIG_SOURCE_FILE, target);
			target = AnalystWizard.FILE_BALANCE;
			this.script.getProperties().setProperty(
					ScriptProperties.BALANCE_CONFIG_TARGET_FILE,
					target);
		}

		// segregate
		if (this.taskSegregate) {
			this.script.getProperties().setProperty(
					ScriptProperties.SEGREGATE_CONFIG_SOURCE_FILE, target);
			target = AnalystWizard.FILE_TRAIN;
		}

		// normalize
		this.script.getProperties().setProperty(
				ScriptProperties.NORMALIZE_CONFIG_SOURCE_FILE, target);
		target = AnalystWizard.FILE_NORMALIZE;
		this.script.getProperties().setProperty(
				ScriptProperties.NORMALIZE_CONFIG_TARGET_FILE,
				target);

		// cluster
		if (this.taskCluster) {
			this.script.getProperties().setProperty(
					ScriptProperties.CLUSTER_CONFIG_SOURCE_FILE,
					AnalystWizard.FILE_EVAL);
			this.script.getProperties().setProperty(
					ScriptProperties.CLUSTER_CONFIG_TARGET_FILE,
					AnalystWizard.FILE_CLUSTER);
			this.script.getProperties().setProperty(
					ScriptProperties.CLUSTER_CONFIG_TYPE, "kmeans");
		}

		// generate
		this.script.getProperties().setProperty(
				ScriptProperties.GENERATE_CONFIG_SOURCE_FILE, target);
		this.script.getProperties().setProperty(
				ScriptProperties.GENERATE_CONFIG_TARGET_FILE,
				AnalystWizard.FILE_TRAINSET);

		// ML
		this.script.getProperties().setProperty(
				ScriptProperties.ML_CONFIG_TRAINING_FILE,
				AnalystWizard.FILE_TRAINSET);
		this.script.getProperties().setProperty(
				ScriptProperties.ML_CONFIG_MACHINE_LEARNING_FILE,
				AnalystWizard.FILE_ML);
		this.script.getProperties().setProperty(
				ScriptProperties.ML_CONFIG_OUTPUT_FILE,
				AnalystWizard.FILE_OUTPUT);

		this.script.getProperties().setProperty(
				ScriptProperties.ML_CONFIG_EVAL_FILE, AnalystWizard.FILE_EVAL);

		// other
		this.script.getProperties().setProperty(
				ScriptProperties.SETUP_CONFIG_CSV_FORMAT,
				AnalystFileFormat.DECPNT_COMMA);
	}

	/**
	 * Generate a SOM machine learning method.
	 * @param inputColumns The number of input columns.
	 */
	private void generateSOM(final int inputColumns) {
		this.script.getProperties().setProperty(
				ScriptProperties.ML_CONFIG_TYPE, MLMethodFactory.TYPE_SOM);
		this.script.getProperties().setProperty(
				ScriptProperties.ML_CONFIG_ARCHITECTURE, "?->?");

		this.script.getProperties().setProperty(ScriptProperties.ML_TRAIN_TYPE,
				MLTrainFactory.TYPE_SOM_NEIGHBORHOOD);
		this.script.getProperties().setProperty(
				ScriptProperties.ML_TRAIN_ARGUMENTS, 
				"ITERATIONS=1000,NEIGHBORHOOD=rbf1d,RBF_TYPE=gaussian");

		// ScriptProperties.ML_TRAIN_arguments
		this.script.getProperties().setProperty(
				ScriptProperties.ML_TRAIN_TARGET_ERROR, DEFAULT_TRAIN_ERROR);
	}

	/**
	 * Generate a SVM machine learning method.
	 * @param inputColumns The number of input columns.
	 * @param outputColumns The number of ideal columns.
	 */
	private void generateSVM(final int inputColumns, final int outputColumns) {
		
		StringBuilder arch = new StringBuilder();
		arch.append("?->");
		if (this.goal == AnalystGoal.Classification) {
			arch.append("C");
		} else {
			arch.append("R");
		}
		arch.append("(type=new,kernel=rbf)->?");
		
		
		this.script.getProperties().setProperty(
				ScriptProperties.ML_CONFIG_TYPE, MLMethodFactory.TYPE_SVM);
		this.script.getProperties().setProperty(
				ScriptProperties.ML_CONFIG_ARCHITECTURE,
				arch.toString());

		this.script.getProperties().setProperty(ScriptProperties.ML_TRAIN_TYPE,
				"svm-train");
		this.script.getProperties().setProperty(
				ScriptProperties.ML_TRAIN_TARGET_ERROR, DEFAULT_TRAIN_ERROR);
	}

	/**
	 * Generate the tasks.
	 */
	private void generateTasks() {
		final AnalystTask task1 = new AnalystTask(EncogAnalyst.TASK_FULL);
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

		final AnalystTask task2 = new AnalystTask("task-generate");
		if (!this.timeSeries && this.taskRandomize) {
			task2.getLines().add("randomize");
		}

		if (this.taskSegregate) {
			task2.getLines().add("segregate");
		}
		if (this.taskNormalize) {
			task2.getLines().add("normalize");
		}
		task2.getLines().add("generate");

		final AnalystTask task3 = new AnalystTask("task-evaluate-raw");
		task3.getLines().add(
				createSet(ScriptProperties.ML_CONFIG_EVAL_FILE,
						AnalystWizard.FILE_EVAL_NORM));
		task3.getLines().add(
				createSet(ScriptProperties.NORMALIZE_CONFIG_SOURCE_FILE,
						AnalystWizard.FILE_EVAL));
		task3.getLines().add(
				createSet(ScriptProperties.NORMALIZE_CONFIG_TARGET_FILE,
						AnalystWizard.FILE_EVAL_NORM));
		task3.getLines().add("normalize");
		task3.getLines().add("evaluate-raw");

		final AnalystTask task4 = new AnalystTask("task-create");
		task4.getLines().add("create");

		final AnalystTask task5 = new AnalystTask("task-train");
		task5.getLines().add("train");

		final AnalystTask task6 = new AnalystTask("task-evaluate");
		task6.getLines().add("evaluate");

		final AnalystTask task7 = new AnalystTask("task-cluster");
		task7.getLines().add("cluster");

		this.script.addTask(task1);
		this.script.addTask(task2);
		this.script.addTask(task3);
		this.script.addTask(task4);
		this.script.addTask(task5);
		this.script.addTask(task6);
		this.script.addTask(task7);
	}

	/**
	 * @return The analyst goal.
	 */
	public final AnalystGoal getGoal() {
		return this.goal;
	}

	/**
	 * @return the lagWindowSize
	 */
	public final int getLagWindowSize() {
		return this.lagWindowSize;
	}

	/**
	 * @return the leadWindowSize
	 */
	public final int getLeadWindowSize() {
		return this.leadWindowSize;
	}

	/**
	 * @return the methodType
	 */
	public final WizardMethodType getMethodType() {
		return this.methodType;
	}

	/**
	 * @return the range
	 */
	public final NormalizeRange getRange() {
		return this.range;
	}

	/**
	 * @return Get the target field.
	 */
	public final String getTargetField() {
		return this.targetField;
	}

	/**
	 * @return the includeTargetField
	 */
	public final boolean isIncludeTargetField() {
		return this.includeTargetField;
	}

	/**
	 * @return the taskBalance
	 */
	public final boolean isTaskBalance() {
		return this.taskBalance;
	}

	/**
	 * @return the taskCluster
	 */
	public final boolean isTaskCluster() {
		return this.taskCluster;
	}

	/**
	 * @return the taskNormalize
	 */
	public final boolean isTaskNormalize() {
		return this.taskNormalize;
	}

	/**
	 * @return the taskRandomize
	 */
	public final boolean isTaskRandomize() {
		return this.taskRandomize;
	}

	/**
	 * @return the taskSegregate
	 */
	public final boolean isTaskSegregate() {
		return this.taskSegregate;
	}

	/**
	 * Reanalyze column ranges.
	 */
	public final void reanalyze() {
		final String rawID = this.script.getProperties().getPropertyFile(
				ScriptProperties.HEADER_DATASOURCE_RAW_FILE);

		final File rawFilename = this.analyst.getScript()
				.resolveFilename(rawID);

		this.analyst.analyze(
				rawFilename,
				this.script.getProperties().getPropertyBoolean(
						ScriptProperties.SETUP_CONFIG_INPUT_HEADERS),
				this.script.getProperties().getPropertyFormat(
						ScriptProperties.SETUP_CONFIG_CSV_FORMAT));

	}

	/**
	 * Set the goal.
	 * @param theGoal The goal.
	 */
	public final void setGoal(final AnalystGoal theGoal) {
		this.goal = theGoal;
	}

	/**
	 * @param theIncludeTargetField
	 *            the includeTargetField to set
	 */
	public final void setIncludeTargetField(
			final boolean theIncludeTargetField) {
		this.includeTargetField = theIncludeTargetField;
	}

	/**
	 * @param theLagWindowSize
	 *            the lagWindowSize to set
	 */
	public final void setLagWindowSize(final int theLagWindowSize) {
		this.lagWindowSize = theLagWindowSize;
	}

	/**
	 * @param theLeadWindowSize
	 *            the leadWindowSize to set
	 */
	public final void setLeadWindowSize(final int theLeadWindowSize) {
		this.leadWindowSize = theLeadWindowSize;
	}

	/**
	 * @param theMethodType
	 *            the methodType to set
	 */
	public final void setMethodType(final WizardMethodType theMethodType) {
		this.methodType = theMethodType;
	}

	/**
	 * @param theRange
	 *            the range to set
	 */
	public final void setRange(final NormalizeRange theRange) {
		this.range = theRange;
	}

	/**
	 * Set the target field.
	 * @param theTargetField The target field.
	 */
	public final void setTargetField(final String theTargetField) {
		this.targetField = theTargetField;
	}

	/**
	 * @param theTaskBalance
	 *            the taskBalance to set
	 */
	public final void setTaskBalance(final boolean theTaskBalance) {
		this.taskBalance = theTaskBalance;
	}

	/**
	 * @param theTaskCluster
	 *            the taskCluster to set
	 */
	public final void setTaskCluster(final boolean theTaskCluster) {
		this.taskCluster = theTaskCluster;
	}

	/**
	 * @param theTaskNormalize
	 *            the taskNormalize to set
	 */
	public final void setTaskNormalize(final boolean theTaskNormalize) {
		this.taskNormalize = theTaskNormalize;
	}

	/**
	 * @param theTaskRandomize
	 *            the taskRandomize to set
	 */
	public final void setTaskRandomize(final boolean theTaskRandomize) {
		this.taskRandomize = theTaskRandomize;
	}

	/**
	 * @param theTaskSegregate
	 *            the taskSegregate to set
	 */
	public final void setTaskSegregate(final boolean theTaskSegregate) {
		this.taskSegregate = theTaskSegregate;
	}

	/**
	 * Analyze a file.
	 * @param analyzeFile The file to analyze.
	 * @param b True if there are headers.
	 * @param format The file format.
	 */
	public final void wizard(final File analyzeFile, final boolean b,
			final AnalystFileFormat format) {

		this.script.getProperties().setProperty(
				ScriptProperties.HEADER_DATASOURCE_SOURCE_FORMAT, format);
		this.script.getProperties().setProperty(
				ScriptProperties.HEADER_DATASOURCE_SOURCE_HEADERS, b);
		this.script.getProperties().setProperty(
				ScriptProperties.HEADER_DATASOURCE_RAW_FILE, analyzeFile);

		this.timeSeries = ((this.lagWindowSize > 0) 
				|| (this.leadWindowSize > 0));

		determineClassification();
		generateFilenames(analyzeFile);
		generateSettings();
		this.analyst.analyze(analyzeFile, b, format);
		generateNormalizedFields();
		generateSegregate();

		generateGenerate();

		generateTasks();
		if (this.timeSeries && (this.lagWindowSize > 0)
				&& (this.leadWindowSize > 0)) {
			expandTimeSlices();
		}
	}

	/**
	 * Analyze a file at the specified URL.
	 * @param url The URL to analyze.
	 * @param saveFile The save file.
	 * @param analyzeFile The Encog analyst file.
	 * @param b True if there are headers.
	 * @param format The file format.
	 */
	public final void wizard(final URL url, final File saveFile,
			final File analyzeFile, final boolean b,
			final AnalystFileFormat format) {

		this.script.setBasePath(saveFile.getParent());
		
		this.script.getProperties().setProperty(
				ScriptProperties.HEADER_DATASOURCE_SOURCE_FILE, url);
		this.script.getProperties().setProperty(
				ScriptProperties.HEADER_DATASOURCE_SOURCE_FORMAT, format);
		this.script.getProperties().setProperty(
				ScriptProperties.HEADER_DATASOURCE_SOURCE_HEADERS, b);
		this.script.getProperties().setProperty(
				ScriptProperties.HEADER_DATASOURCE_RAW_FILE, analyzeFile);

		generateFilenames(analyzeFile);
		generateSettings();
		this.analyst.download();

		wizard(analyzeFile, b, format);
	}

}
