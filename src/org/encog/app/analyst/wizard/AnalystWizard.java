package org.encog.app.analyst.wizard;

import java.io.File;
import java.net.URL;

import org.encog.app.analyst.AnalystError;
import org.encog.app.analyst.EncogAnalyst;
import org.encog.app.analyst.script.AnalystScript;
import org.encog.app.analyst.script.DataField;
import org.encog.app.analyst.script.prop.ScriptProperties;
import org.encog.app.analyst.script.segregate.AnalystSegregateTarget;
import org.encog.app.analyst.script.task.AnalystTask;
import org.encog.app.quant.normalize.NormalizationAction;
import org.encog.app.quant.normalize.NormalizedField;
import org.encog.ml.factory.MLMethodFactory;
import org.encog.util.csv.CSVFormat;
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
	
	private AnalystScript script;
	private EncogAnalyst analyst;
	private WizardMethodType methodType;
	private boolean directClassification = false;
	
	public AnalystWizard(EncogAnalyst analyst)
	{
		this.analyst = analyst;
		this.script = analyst.getScript();
		this.methodType = WizardMethodType.FeedForward;
	}
	
	private void generateSettings(File file)
	{
		String train;
		this.script.getProperties().setFilename(AnalystWizard.FILE_RAW, file.toString());
		this.script.getProperties().setFilename(AnalystWizard.FILE_NORMALIZE,
				FileUtil.addFilenameBase(file, "_norm").toString());
		this.script.getProperties().setFilename(AnalystWizard.FILE_RANDOM,
				FileUtil.addFilenameBase(file, "_random").toString());
		this.script.getProperties().setFilename(AnalystWizard.FILE_OUTPUT,
				FileUtil.addFilenameBase(file, "_output").toString());
		this.script.getProperties().setFilename(AnalystWizard.FILE_TRAIN,
				train = FileUtil.addFilenameBase(file, "_train").toString());
		this.script.getProperties().setFilename(AnalystWizard.FILE_EVAL,
				FileUtil.addFilenameBase(file, "_eval").toString());
		this.script.getProperties().setFilename(AnalystWizard.FILE_TRAINSET,
				FileUtil.forceExtension(train, "egb"));
		this.script.getProperties().setFilename(AnalystWizard.FILE_EG,
				FileUtil.forceExtension(file.toString(), "eg"));
		
		this.script.getProperties().setProperty(ScriptProperties.HEADER_DATASOURCE_rawFile, AnalystWizard.FILE_RAW);
		this.script.getRandomize().setSourceFile(AnalystWizard.FILE_RAW);
		this.script.getRandomize().setTargetFile(AnalystWizard.FILE_RANDOM);
		this.script.getSegregate().setSourceFile(AnalystWizard.FILE_RANDOM);		
		this.script.getNormalize().setSourceFile(AnalystWizard.FILE_TRAIN);
		this.script.getNormalize().setTargetFile(AnalystWizard.FILE_NORMALIZE);
		
		this.script.getProperties().setProperty(ScriptProperties.GENERATE_CONFIG_sourceFile, AnalystWizard.FILE_NORMALIZE);
		this.script.getProperties().setProperty(ScriptProperties.GENERATE_CONFIG_targetFile, AnalystWizard.FILE_TRAINSET);
		this.script.getMachineLearning().setTrainingFile(AnalystWizard.FILE_TRAINSET);
		this.script.getMachineLearning().setResourceFile(AnalystWizard.FILE_EG);
		this.script.getMachineLearning().setOutputFile(AnalystWizard.FILE_OUTPUT);
		this.script.getMachineLearning().setEvalFile(AnalystWizard.FILE_EVAL);
	}
	
	private void generateNormalizedFields(File file) {
		NormalizedField[] norm = new NormalizedField[this.script.getFields().length];
		DataField[] dataFields = script.getFields();

		for (int i = 0; i < this.script.getFields().length; i++) {
			DataField f = dataFields[i];
			NormalizationAction action;
			boolean isLast = i==this.script.getFields().length-1;

			if ( (f.isInteger() || f.isReal()) && !f.isClass()) {
				action = NormalizationAction.Normalize;
				norm[i] = new NormalizedField(f.getName(), action, 1, -1);
				norm[i].setActualHigh(f.getMax());
				norm[i].setActualLow(f.getMin());
			} else if( f.isClass() ) { 
				if( isLast && this.directClassification ) {
					action = NormalizationAction.SingleField;
				}
				else if( f.getClassMembers().size()>2)
					action = NormalizationAction.Equilateral;
				else
					action = NormalizationAction.OneOf;
				norm[i] = new NormalizedField(f.getName(), action, 1, -1);
			} else {
				action = NormalizationAction.PassThrough;
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
	
	private NormalizedField getTargetField() {
		NormalizedField[] fields = this.script.getNormalize().getNormalizedFields();
		
		
		// first try to find a classify field
		for(int i=0;i<fields.length;i++) {
			DataField df = this.script.findDataField(fields[i].getName());
			if( fields[i].getAction().isClassify() && df.isClass() ) {
				return fields[i];
			}
		}
		
		// otherwise, just return the first regression field
		for(int i=0;i<fields.length;i++) {
			DataField df = this.script.findDataField(fields[i].getName());
			if( !df.isClass() && (df.isReal() || df.isInteger()) ) {
				return fields[i];
			}
		}
		
		// can't find anything useful!
		return null;
	}

	private void generateGenerate(File file) {
		NormalizedField targetField = getTargetField();
		
		if( targetField==null ) {
			throw new AnalystError("Failed to find a target field to analyze.  Please specify the target field.");
		}

		int inputColumns = this.script.getNormalize().calculateInputColumns(targetField);
		int idealColumns = this.script.getNormalize().calculateOutputColumns(targetField);

		this.script.getProperties().setProperty(ScriptProperties.GENERATE_CONFIG_input,inputColumns);
		this.script.getProperties().setProperty(ScriptProperties.GENERATE_CONFIG_ideal,idealColumns);

		switch(this.methodType) {
			case FeedForward:
				generateFeedForward(inputColumns);
				break;
			case SVM:
				generateSVM(inputColumns);
				break;
		}
	}
	
	private void generateFeedForward(int inputColumns) {
		int hidden = (int)(((double)inputColumns)*1.5);
		this.script.getMachineLearning().setMLType(MLMethodFactory.TYPE_FEEDFORWARD);
		this.script.getMachineLearning().setMLArchitecture("?B->TANH->"+hidden+"B->TANH->?");
		this.script.getMachineLearning().setResourceName("ml");
	}
	
	private void generateSVM(int inputColumns) {
		this.script.getMachineLearning().setMLType(MLMethodFactory.TYPE_SVM);
		this.script.getMachineLearning().setMLArchitecture("?->C(type=new,kernel=gaussian)->?");
		this.script.getMachineLearning().setResourceName("ml");
	}
	
	public void generateTasks()
	{
		AnalystTask task1 = new AnalystTask(EncogAnalyst.TASK_FULL);
		task1.getLines().add("randomize");
		task1.getLines().add("segregate");
		task1.getLines().add("normalize");
		task1.getLines().add("generate");
		task1.getLines().add("create");
		task1.getLines().add("train");
		task1.getLines().add("evaluate");
		
		AnalystTask task2 = new AnalystTask("task-generate");
		task2.getLines().add("randomize");
		task2.getLines().add("segregate");
		task2.getLines().add("normalize");
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

	public void wizard(File analyzeFile, boolean b,
			CSVFormat format) {

		
		this.script.getProperties().setProperty(ScriptProperties.HEADER_DATASOURCE_sourceFormat, format);
		this.script.getProperties().setProperty(ScriptProperties.HEADER_DATASOURCE_sourceHeaders, b);
		this.script.getProperties().setProperty(ScriptProperties.HEADER_DATASOURCE_rawFile, analyzeFile);
		
		determineClassification();
		generateSettings(analyzeFile);
		//this.analyst.getReport().reportPhase(1, 1, "Wizard analyzing data");
		this.analyst.analyze(analyzeFile, b, format);
		generateNormalizedFields(analyzeFile);
		generateRandomize(analyzeFile);
		generateSegregate(analyzeFile);
		generateGenerate(analyzeFile);
		generateTasks();
	}

	public void wizard(URL url, File saveFile, File analyzeFile, boolean b,
			CSVFormat format) {
		
		this.script.getProperties().setProperty(ScriptProperties.HEADER_DATASOURCE_sourceFile, url);
		this.script.getProperties().setProperty(ScriptProperties.HEADER_DATASOURCE_sourceFormat, format);
		this.script.getProperties().setProperty(ScriptProperties.HEADER_DATASOURCE_sourceHeaders, b);
		this.script.getProperties().setProperty(ScriptProperties.HEADER_DATASOURCE_rawFile, analyzeFile);
				
		this.generateSettings(analyzeFile);
		//this.analyst.getReport().reportPhase(2, 1, "Downloading data");
		analyst.download();
		//this.analyst.getReport().reportPhase(2, 2, "Wizard analyzing data");
		this.analyst.analyze(analyzeFile, b, format);
		generateNormalizedFields(analyzeFile);
		generateRandomize(analyzeFile);
		generateSegregate(analyzeFile);
		generateGenerate(analyzeFile);
		generateTasks();
	}

	public void reanalyze() {
		String rawID = this.script.getProperties().getPropertyFile(ScriptProperties.HEADER_DATASOURCE_rawFile);
		
		String rawFilename = this.script.getProperties().getFilename(rawID);
		
		this.analyst.analyze(new File(rawFilename),
				this.script.getProperties().getPropertyBoolean(ScriptProperties.SETUP_CONFIG_inputHeaders),
				this.script.getProperties().getPropertyFormat(ScriptProperties.SETUP_CONFIG_csvFormat));

	}

	/**
	 * @return the methodType
	 */
	public WizardMethodType getMethodType() {
		return methodType;
	}

	/**
	 * @param methodType the methodType to set
	 */
	public void setMethodType(WizardMethodType methodType) {
		this.methodType = methodType;
	}
	
	private void determineClassification()
	{
		this.directClassification = false;
		
		if( this.methodType==WizardMethodType.SVM ) {
			this.directClassification = true;
		}
	}
}
