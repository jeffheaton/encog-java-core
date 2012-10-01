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
import org.encog.util.simple.EncogUtility;

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
	
	private void generateForMethod(EncogProgramNode mainClass, File method) {
				
		if( this.embedData ) {
			MLEncodable encodable = (MLEncodable)EncogDirectoryPersistence.loadObject(method);
			double[] weights = new double[encodable.encodedArrayLength()];
			encodable.encodeToArray(weights);
			mainClass.createArray("WEIGHTS",weights);
		}
		
		EncogProgramNode createNetworkFunction = mainClass.createNetworkFunction("createNetwork", method);
				
		EncogProgramNode mainFunction = mainClass.createMainFunction();
		mainFunction.createFunctionCall(createNetworkFunction, "MLMethod", "method");
	}
	
	public void generate(File method, File data) {
		this.program.addComment("Hello World");
		EncogProgramNode mainClass = this.program.createClass("EncogExample");
		
		if( data!=null ) {
			mainClass.embedTraining(data);
		}
		
		if( method!=null ) {
			generateForMethod(mainClass, method);
		}
		

		
		
		this.generator.generate(this.program, this.embedData);		
	}


	public void generate(EncogAnalyst analyst) {
		
		final String methodID = analyst.getScript().getProperties().getPropertyString(
				ScriptProperties.ML_CONFIG_MACHINE_LEARNING_FILE);
		
		final String trainingID = analyst.getScript().getProperties().getPropertyString(
				ScriptProperties.ML_CONFIG_TRAINING_FILE);

		final File methodFile = analyst.getScript().resolveFilename(methodID);
		final File trainingFile = analyst.getScript().resolveFilename(trainingID);
				
		generate(methodFile, trainingFile);
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
