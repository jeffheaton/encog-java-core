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
import org.encog.ml.data.MLDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.persist.EncogDirectoryPersistence;

public class EncogCodeGeneration {
	
	private final TargetLanguage targetLanguage; 
	private boolean embedData;
	private LanguageSpecificGenerator generator;
	private final EncogProgram program = new EncogProgram();
	
	public EncogCodeGeneration(TargetLanguage theTargetLanguage) {
		this.targetLanguage = theTargetLanguage;
		
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
	
	public void save(File file) {
		this.generator.writeContents(file);
	}
	
	public String save() {
		return this.generator.getContents();
	}
	
	private void generateForMethod(EncogProgramNode mainClass, MLMethod method) {
		MLEncodable encodable = (MLEncodable)method;
		double[] weights = new double[encodable.encodedArrayLength()];
		encodable.encodeToArray(weights);
		
		mainClass.createArray("WEIGHTS",weights);
		
		EncogProgramNode createNetworkFunction = mainClass.createNetworkFunction("createNetwork", method);
				
		EncogProgramNode mainFunction = mainClass.createMainFunction();
		mainFunction.createFunctionCall(createNetworkFunction, "MLMethod", "method");
	}
	
	public void generate(MLMethod method, MLDataSet data) {
		this.program.addComment("Hello World");
		EncogProgramNode mainClass = this.program.createClass("EncogExample");
		
		if( method!=null ) {
			generateForMethod(mainClass, method);
		}
		
		if( data!=null ) {
			mainClass.embedTraining(data);
		}
		
		
		this.generator.generate(this.program);		
	}


	public void generate(EncogAnalyst analyst) {
		
		final String resourceID = analyst.getScript().getProperties().getPropertyString(
				ScriptProperties.ML_CONFIG_MACHINE_LEARNING_FILE);

		final File resourceFile = analyst.getScript().resolveFilename(resourceID);
		
		MLMethod method = (MLMethod)EncogDirectoryPersistence.loadObject(resourceFile);
		
		generate(method, null);
	}

	public boolean isEmbedData() {
		return embedData;
	}



	public void setEmbedData(boolean embedData) {
		this.embedData = embedData;
	}

	public static boolean isSupported(MLMethod method) {
		if( method instanceof BasicNetwork ) {
			return true;
		} else {
			return false;
		}
	}
	
	
}
