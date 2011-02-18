package org.encog.app.analyst.script;

import java.io.OutputStream;

import org.encog.app.analyst.script.segregate.AnalystSegregateTarget;
import org.encog.app.quant.normalize.NormalizedField;

public class ScriptSave {

	private AnalystScript script;

	public ScriptSave(AnalystScript script) {
		this.script = script;
	}

	private void saveConfig(WriteScriptFile out) {
		EncogAnalystConfig config = this.script.getConfig();

		out.addSection("SETUP");
		out.addSubSection("CONFIG");
		out.writeProperty("maxClassCount", config.getMaxClassSize());
		out.writeProperty("allowedClasses", config.getAllowedClasses());
		out.writeProperty("outputHeaders", config.isOutputHeaders());
		out.writeProperty("inputHeaders", config.isInputHeaders());
		out.writeProperty("csvFormat", config.getCSVFormat());
		out.addSubSection("FILENAMES");
		for (String key : config.getFilenames().keySet()) {
			String value = config.getFilenames().get(key);
			out.writeProperty(key, value);
		}
	}
	
	
	private void saveRandomize(WriteScriptFile out)
	{
		out.addSection("RANDOMIZE");
		out.addSubSection("CONFIG");
		out.writeProperty("sourceFile", this.script.getRandomize()
				.getSourceFile());
		out.writeProperty("targetFile", this.script.getRandomize()
				.getTargetFile());		
	}
	
	private void saveInformation(WriteScriptFile out)
	{
		out.addSection("HEADER");
		out.addSubSection("DATASOURCE");
		out.writeProperty("sourceFile", this.script.getInformation().getDataSource());
		out.writeProperty("sourceFormat", this.script.getInformation().getDataSourceFormat());
		out.writeProperty("sourceHeaders", this.script.getInformation().isDataSourceHeaders());
	}
	
	private void saveGenerate(WriteScriptFile out)
	{
		out.addSection("GENERATE");
		out.addSubSection("CONFIG");
		out.writeProperty("sourceFile", this.script.getGenerate()
				.getSourceFile());
		out.writeProperty("targetFile", this.script.getGenerate()
				.getTargetFile());
		out.writeProperty("input", this.script.getGenerate()
				.getInput());
		out.writeProperty("ideal", this.script.getGenerate()
				.getIdeal());
	}
	
	private void saveSegregate(WriteScriptFile out)
	{
		out.addSection("SEGREGATE");
		out.addSubSection("CONFIG");
		out.writeProperty("sourceFile", this.script.getSegregate()
				.getSourceFile());
		out.addSubSection("FILES");
		out.addColumn("file");
		out.addColumn("percent");
		out.writeLine();
		
		for(AnalystSegregateTarget target: this.script.getSegregate().getSegregateTargets() ) {
			out.addColumn(target.getFile());
			out.addColumn(target.getPercent());
			out.writeLine();
		}
	}
	
	private void saveMachineLearning(WriteScriptFile out)
	{
		out.addSection("ML");
		out.addSubSection("CONFIG");
		out.writeProperty("trainingFile", this.script.getMachineLearning()
				.getTrainingFile());
		out.writeProperty("evalFile", this.script.getMachineLearning()
				.getEvalFile());
		out.writeProperty("resourceFile", this.script.getMachineLearning()
				.getResourceFile());	
		out.writeProperty("outputFile", this.script.getMachineLearning()
				.getOutputFile());


		out.writeProperty("type",this.script.getMachineLearning().getMLType());		
		out.writeProperty("architecture",this.script.getMachineLearning().getMLArchitecture());
		out.writeProperty("resourceName",this.script.getMachineLearning().getResourceName());
		
	}

	private void saveData(WriteScriptFile out) {
		out.addSection("DATA");
		out.addSubSection("STATS");
		out.addColumn("name");
		out.addColumn("isclass");
		out.addColumn("iscomplete");
		out.addColumn("isint");
		out.addColumn("isreal");
		out.addColumn("amax");
		out.addColumn("amin");
		out.addColumn("mean");
		out.addColumn("sdev");
		out.writeLine();

		for (DataField field : this.script.getFields()) {
			out.addColumn(field.getName());
			out.addColumn(field.isClass());
			out.addColumn(field.isComplete());
			out.addColumn(field.isInteger());
			out.addColumn(field.isReal());
			out.addColumn(field.getMax());
			out.addColumn(field.getMin());
			out.addColumn(field.getMean());
			out.addColumn(field.getStandardDeviation());
			out.writeLine();
		}
		out.flush();

		out.addSubSection("CLASSES");
		out.addColumn("field");
		out.addColumn("code");
		out.addColumn("name");
		out.writeLine();
		
		for (DataField field : this.script.getFields()) {
			if (field.isClass()) {
				for(AnalystClassItem col: field.getClassMembers() ) {
					out.addColumn(field.getName());
					out.addColumn(col.getCode());
					out.addColumn(col.getName());
					out.writeLine();
				}
			}
		}

	}

	private void saveNormalize(WriteScriptFile out) {
		out.addSection("NORMALIZE");
		out.addSubSection("CONFIG");
		out.writeProperty("sourceFile", this.script.getNormalize()
				.getSourceFile());
		out.writeProperty("targetFile", this.script.getNormalize()
				.getTargetFile());
		out.addSubSection("RANGE");
		out.addColumn("name");
		out.addColumn("action");
		out.addColumn("high");
		out.addColumn("low");
		out.writeLine();
		for (NormalizedField field : this.script.getNormalize()
				.getNormalizedFields()) {
			out.addColumn(field.getName());
			switch (field.getAction()) {
			case Ignore:
				out.addColumn("ignore");
				break;
			case Normalize:
				out.addColumn("range");
				break;
			case PassThrough:
				out.addColumn("pass");
				break;
			case OneOf:
				out.addColumn("oneof");
				break;
			case Equilateral:
				out.addColumn("equilateral");
				break;
			case SingleField:
				out.addColumn("single");
				break;			
			}

			out.addColumn(field.getNormalizedHigh());
			out.addColumn(field.getNormalizedLow());
			out.writeLine();
		}
	}

	public void save(OutputStream stream) {
		WriteScriptFile out = new WriteScriptFile(stream);
		saveInformation(out);
		saveConfig(out);
		saveData(out);
		saveNormalize(out);
		saveRandomize(out);
		saveSegregate(out);
		saveGenerate(out);
		saveMachineLearning(out);
		out.flush();
	}
}
