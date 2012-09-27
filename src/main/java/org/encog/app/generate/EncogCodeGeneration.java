package org.encog.app.generate;

import java.io.File;

import org.encog.app.analyst.EncogAnalyst;
import org.encog.app.analyst.script.prop.ScriptProperties;
import org.encog.app.generate.generators.LanguageSpecificGenerator;
import org.encog.app.generate.generators.java.GenerateEncogJava;
import org.encog.app.generate.program.EncogProgram;
import org.encog.app.generate.program.EncogProgramNode;
import org.encog.ml.MLEncodable;
import org.encog.ml.MLMethod;
import org.encog.persist.EncogDirectoryPersistence;

public class EncogCodeGeneration {
	
	private final TargetLanguage targetLanguage; 
	private final File targetFile;
	private boolean embedData;
	private MLMethod method;
	private LanguageSpecificGenerator generator;
	private final EncogProgram program = new EncogProgram();
	
	public EncogCodeGeneration(TargetLanguage theTargetLanguage, File theTargetFile) {
		this.targetLanguage = theTargetLanguage;
		this.targetFile = theTargetFile;
		
		switch( theTargetLanguage ) {
			case Java:
				this.generator = new GenerateEncogJava();
				break;
		}
	}
	
	/*private void generateFileNames(EncogAnalyst analyst, EncogProgramNode mainClass) {
		for(String str: analyst.getScript().getProperties().getFilenames() ) {
			String value = analyst.getScript().getProperties().getFilename(str);
			mainClass.defineConst(EncogArgType.String,str,value);
		}
	}*/
		
	/**
	 * @return the targetLanguage
	 */
	public TargetLanguage getTargetLanguage() {
		return targetLanguage;
	}

	/**
	 * @return the targetFile
	 */
	public File getTargetFile() {
		return targetFile;
	}
	
	public void generate(MLMethod method) {
		this.program.addComment("Hello World");
		EncogProgramNode mainClass = this.program.createClass("EncogExample");
		
		MLEncodable encodable = (MLEncodable)method;
		double[] weights = new double[encodable.encodedArrayLength()];
		encodable.encodeToArray(weights);
		
		mainClass.createArray("WEIGHTS",weights);
		
		EncogProgramNode createNetworkFunction = mainClass.createNetworkFunction("createNetwork", method);
				
		EncogProgramNode mainFunction = mainClass.createMainFunction();
		mainFunction.createFunctionCall(createNetworkFunction, "MLMethod", "method");
		
		this.generator.generate(this.program);
		this.generator.writeContents(this.targetFile);
	}


	public void generate(EncogAnalyst analyst) {
		
		final String resourceID = analyst.getScript().getProperties().getPropertyString(
				ScriptProperties.ML_CONFIG_MACHINE_LEARNING_FILE);

		final File resourceFile = analyst.getScript().resolveFilename(resourceID);
		
		MLMethod method = (MLMethod)EncogDirectoryPersistence.loadObject(resourceFile);
		
		generate(method);
	}

	public boolean isEmbedData() {
		return embedData;
	}



	public void setEmbedData(boolean embedData) {
		this.embedData = embedData;
	}



	public MLMethod getMethod() {
		return method;
	}



	public void setMethod(MLMethod method) {
		this.method = method;
	}
	
	
	
}
