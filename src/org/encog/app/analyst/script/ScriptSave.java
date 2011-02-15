package org.encog.app.analyst.script;

import java.io.OutputStream;

import org.encog.app.analyst.script.classify.ClassifyField;
import org.encog.app.analyst.script.normalize.NormalizedField;
import org.encog.app.quant.classify.ClassifyMethod;
import org.encog.app.quant.normalize.NormalizationDesired;

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
		out.addSubSection("FILENAMES");
		for (String key : config.getFilenames().keySet()) {
			String value = config.getFilenames().get(key);
			out.writeProperty(key, value);
		}
	}
	
	private void saveClassify(WriteScriptFile out) {
		out.addSection("CLASSIFY");
		out.addSection("CONFIG");
		out.writeProperty("sourceFile", this.script.getClassify()
				.getSourceFile());
		out.writeProperty("targetFile", this.script.getClassify()
				.getTargetFile());
		
		out.addSection("FIELDS");
		out.addColumn("name");
		out.addColumn("type");
		out.writeLine();
		for(ClassifyField field: this.script.getClassify().getClassifiedFields())
		{
			out.addColumn(field.getName());
			switch(field.getMethod()) {
				case Equilateral:
					out.addColumn("equilateral");
					break;
				case OneOf:
					out.addColumn("oneof");
					break;
			}
			out.writeLine();
		}
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
				for(ClassItem col: field.getClassMembers() ) {
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
			}

			out.addColumn(field.getNormalizedHigh());
			out.addColumn(field.getNormalizedLow());
			out.writeLine();
		}
	}

	public void save(OutputStream stream) {
		WriteScriptFile out = new WriteScriptFile(stream);
		saveConfig(out);
		saveData(out);
		saveNormalize(out);
		saveClassify(out);
		out.flush();
	}
}
