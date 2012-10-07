package org.encog.app.generate.generators.ninja;

import java.io.File;

import org.encog.app.analyst.EncogAnalyst;
import org.encog.app.analyst.script.DataField;
import org.encog.app.analyst.script.prop.ScriptProperties;
import org.encog.app.generate.generators.AbstractTemplateGenerator;
import org.encog.ml.MLMethod;
import org.encog.persist.EncogDirectoryPersistence;

public class GenerateNinjaScript extends AbstractTemplateGenerator {

	@Override
	public String getTemplatePath() {
		return "org/encog/data/ninja.cs";
	}
	
	private void addNameValue(String name, String value) {
		StringBuilder line = new StringBuilder();
		line.append(name);
		line.append(" = ");
		line.append(value);
		line.append(";");
		addLine(line.toString());
	}
	
	private void addCols() {
		StringBuilder line = new StringBuilder();
		line.append("public readonly string[] ENCOG_COLS = {");
		
		boolean first = true;
		
		for(DataField df: this.getAnalyst().getScript().getFields()) {
			
			if( !first ) {
				line.append(",");
			}
			
			line.append("\"");
			line.append(df.getName());
			line.append("\"");
			first = false;
		}
		
		line.append("};");
		addLine(line.toString());
	}
	
	private void processMainBlock() {
		EncogAnalyst analyst = getAnalyst();
		
		final String processID = analyst
				.getScript()
				.getProperties()
				.getPropertyString(
						ScriptProperties.PROCESS_CONFIG_TARGET_FILE);
		
		final String methodID = analyst
				.getScript()
				.getProperties()
				.getPropertyString(
						ScriptProperties.ML_CONFIG_MACHINE_LEARNING_FILE);

		final File methodFile = analyst.getScript().resolveFilename(methodID);
		
		final File processFile = analyst.getScript().resolveFilename(processID);
		
		MLMethod method = null;
		
		if( methodFile.exists() ) {
			method = (MLMethod)EncogDirectoryPersistence.loadObject(methodFile);
		}
		
		addLine("\t\t#region Encog Data");
		addNameValue("public const string EXPORT_FILENAM",processFile.toString());
		addCols();
		
		addNameValue("private readonly int[] _contextTargetOffset","{0}");
		addNameValue("private readonly int[] _contextTargetSize","{0};");
		addNameValue("private const bool _hasContext","false;");
		addNameValue("private const int _inputCount","0;");
		addNameValue("private readonly int[] _layerContextCount","{0};");
		addNameValue("private readonly int[] _layerCounts","{0};");
		addNameValue("private readonly int[] _layerFeedCounts","{0};");
		addNameValue("private readonly int[] _layerIndex","{0};");
		addNameValue("private readonly double[] _layerOutput","{0};");
		addNameValue("private readonly double[] _layerSums","{0};");
		addNameValue("private const int _outputCount","0;");
		addNameValue("private readonly int[] _weightIndex","{0};");
		addNameValue("private readonly double[] _weights","{0};");
		addNameValue("private readonly int[] _activation","{0};");
		addNameValue("private readonly double[] _p","{0};");
		
		if( methodFile==null ) {
			
		}
		
		addLine("#endregion");
	}

	@Override
	public void processToken(String command) {
		if(command.equalsIgnoreCase("MAIN-BLOCK")) {
			processMainBlock();
		}
		
	}


}
