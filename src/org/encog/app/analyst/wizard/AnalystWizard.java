package org.encog.app.analyst.wizard;

import java.io.File;
import java.net.URL;

import org.encog.app.analyst.EncogAnalyst;
import org.encog.app.analyst.script.AnalystClassItem;
import org.encog.app.analyst.script.AnalystScript;
import org.encog.app.analyst.script.DataField;
import org.encog.app.analyst.script.EncogAnalystConfig;
import org.encog.app.analyst.script.segregate.AnalystSegregateTarget;
import org.encog.app.quant.normalize.ClassItem;
import org.encog.app.quant.normalize.NormalizationAction;
import org.encog.app.quant.normalize.NormalizedField;
import org.encog.bot.BotUtil;
import org.encog.util.csv.CSVFormat;
import org.encog.util.file.FileUtil;

public class AnalystWizard {
	
	private AnalystScript script;
	private EncogAnalyst analyst;
	
	public AnalystWizard(EncogAnalyst analyst)
	{
		this.analyst = analyst;
		this.script = analyst.getScript();
	}
	
	private void generateSettings(File file)
	{
		String train;
		this.script.getConfig().setFilename(EncogAnalystConfig.FILE_RAW, file.toString());
		this.script.getConfig().setFilename(EncogAnalystConfig.FILE_NORMALIZE,
				FileUtil.addFilenameBase(file, "_norm").toString());
		this.script.getConfig().setFilename(EncogAnalystConfig.FILE_RANDOM,
				FileUtil.addFilenameBase(file, "_random").toString());
		this.script.getConfig().setFilename(EncogAnalystConfig.FILE_OUTPUT,
				FileUtil.addFilenameBase(file, "_output").toString());
		this.script.getConfig().setFilename(EncogAnalystConfig.FILE_TRAIN,
				train = FileUtil.addFilenameBase(file, "_train").toString());
		this.script.getConfig().setFilename(EncogAnalystConfig.FILE_EVAL,
				FileUtil.addFilenameBase(file, "_eval").toString());
		this.script.getConfig().setFilename(EncogAnalystConfig.FILE_TRAINSET,
				FileUtil.forceExtension(train, "egb"));
		this.script.getConfig().setFilename(EncogAnalystConfig.FILE_EG,
				FileUtil.forceExtension(file.toString(), "eg"));
		
		this.script.getInformation().setRawFile(EncogAnalystConfig.FILE_RAW);
		this.script.getRandomize().setSourceFile(EncogAnalystConfig.FILE_RAW);
		this.script.getRandomize().setTargetFile(EncogAnalystConfig.FILE_RANDOM);
		this.script.getSegregate().setSourceFile(EncogAnalystConfig.FILE_RANDOM);		
		this.script.getNormalize().setSourceFile(EncogAnalystConfig.FILE_TRAIN);
		this.script.getNormalize().setTargetFile(EncogAnalystConfig.FILE_NORMALIZE);
		this.script.getGenerate().setSourceFile(EncogAnalystConfig.FILE_NORMALIZE);
		this.script.getGenerate().setTargetFile(EncogAnalystConfig.FILE_TRAINSET);
		this.script.getMachineLearning().setTrainingFile(EncogAnalystConfig.FILE_TRAINSET);
		this.script.getMachineLearning().setResourceFile(EncogAnalystConfig.FILE_EG);
		this.script.getMachineLearning().setOutputFile(EncogAnalystConfig.FILE_OUTPUT);
		this.script.getMachineLearning().setEvalFile(EncogAnalystConfig.FILE_EVAL);
		this.script.getMachineLearning().setMLType("feedforward");
		this.script.getMachineLearning().setMLArchitecture("?B->TANH->10B->TANH->?");
		this.script.getMachineLearning().setResourceName("ml");
	}
	
	private void generateNormalizedFields(File file) {
		NormalizedField[] norm = new NormalizedField[this.script.getFields().length];
		DataField[] dataFields = script.getFields();

		for (int i = 0; i < this.script.getFields().length; i++) {
			DataField f = dataFields[i];
			NormalizationAction action;

			if (f.isInteger() || f.isReal() && !f.isClass()) {
				action = NormalizationAction.Normalize;
				norm[i] = new NormalizedField(f.getName(), action, 1, -1);
				norm[i].setActualHigh(f.getMax());
				norm[i].setActualLow(f.getMin());
			} else if( f.isClass() ) { 
				if( f.getClassMembers().size()>2)
					action = NormalizationAction.Equilateral;
				else
					action = NormalizationAction.OneOf;
				norm[i] = new NormalizedField(f.getName(), action, 1, -1);
				int index = 0;
				for(AnalystClassItem item : f.getClassMembers() )
				{
					norm[i].getClasses().add(new ClassItem(item.getName(),index++));
				}
			} else {
				action = NormalizationAction.PassThrough;
				norm[i] = new NormalizedField(action, f.getName());
			}
		}
		this.script.getNormalize().setNormalizedFields(norm);
	}


	private void generateRandomize(File file) {

	}

	private void generateSegregate(File file) {
		AnalystSegregateTarget[] array = new AnalystSegregateTarget[2];
		array[0] = new AnalystSegregateTarget(EncogAnalystConfig.FILE_TRAIN, 75);
		array[1] = new AnalystSegregateTarget(EncogAnalystConfig.FILE_EVAL, 25);
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

		int inputColumns = this.script.getNormalize().calculateInputColumns(targetField);
		int idealColumns = this.script.getNormalize().calculateOutputColumns(targetField);

		this.script.getGenerate().setInput(inputColumns);
		this.script.getGenerate().setIdeal(idealColumns);

	}

	public void wizard(File saveFile, File analyzeFile, boolean b,
			CSVFormat english) {
		this.analyst.analyze(analyzeFile, b, english);
		generateNormalizedFields(analyzeFile);
		generateRandomize(analyzeFile);
		generateSegregate(analyzeFile);
		generateGenerate(analyzeFile);
	}

	public void wizard(URL url, File saveFile, File analyzeFile, boolean b,
			CSVFormat format) {
		this.script.getInformation().setDataSource(url.toExternalForm());
		this.script.getInformation().setDataSourceFormat(format);
		this.script.getInformation().setDataSourceHeaders(b);
		this.script.getInformation().setRawFile(analyzeFile.toString());
		
		this.generateSettings(analyzeFile);
		this.analyst.getReport().reportPhase(2, 1, "Downloading data");
		analyst.download();
		this.analyst.getReport().reportPhase(2, 2, "Wizard analyzing data");
		this.analyst.analyze(analyzeFile, b, format);
		generateNormalizedFields(analyzeFile);
		generateRandomize(analyzeFile);
		generateSegregate(analyzeFile);
		generateGenerate(analyzeFile);
	}

}
