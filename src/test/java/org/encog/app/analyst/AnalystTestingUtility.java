/*
 * Encog(tm) Core v3.3 - Java Version
 * http://www.heatonresearch.com/encog/
 * https://github.com/encog/encog-java-core
 
 * Copyright 2008-2014 Heaton Research, Inc.
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
package org.encog.app.analyst;

import java.io.File;

import junit.framework.Assert;

import org.encog.app.analyst.report.AnalystReport;
import org.encog.app.analyst.script.DataField;
import org.encog.app.analyst.script.normalize.AnalystField;
import org.encog.app.analyst.script.prop.ScriptProperties;
import org.encog.app.analyst.wizard.AnalystWizard;
import org.encog.app.analyst.wizard.WizardMethodType;
import org.encog.ml.MLError;
import org.encog.ml.MLMethod;
import org.encog.ml.data.MLDataSet;
import org.encog.persist.EncogDirectoryPersistence;
import org.encog.util.Format;
import org.encog.util.TempDir;
import org.encog.util.file.FileUtil;
import org.encog.util.simple.EncogUtility;

public class AnalystTestingUtility {
	public static final boolean CONSOLE_OUTPUT = false;
	public static final int MAX_ITERATIONS = 100000;
	public static final int MAX_CYCLES = 10;
	private String baseDataFile;
	private final TempDir tempDir = new TempDir();
	private File rawFile;
	private File analystFile;
	private EncogAnalyst encogAnalyst;
	private AnalystFileFormat format = AnalystFileFormat.DECPNT_COMMA;
	
	
	public AnalystTestingUtility(String theBaseDataFile) 
	{
		System.gc();
		tempDir.clearContents();
		this.baseDataFile = theBaseDataFile;
		this.rawFile = tempDir.createFile("test.csv");
		FileUtil.copyResource(theBaseDataFile, rawFile);
		this.analystFile = tempDir.createFile("test.ega");
		this.encogAnalyst = new EncogAnalyst();
	}
	
	public void wizard(AnalystGoal goal, WizardMethodType methodType, boolean headers) {
		this.encogAnalyst.setMaxIteration(AnalystTestingUtility.MAX_ITERATIONS);
		AnalystWizard wiz = new AnalystWizard(this.encogAnalyst);		
		wiz.setGoal(goal);
		wiz.setMethodType(methodType);
		wiz.setEvidenceSegements(3);
		wiz.wizard(rawFile, headers, format);
		
		this.encogAnalyst.save(analystFile);
		this.encogAnalyst.load(analystFile);
	}
	
	public void process(double maxError) {
		int cycles = 0;
		double e;
		
		if( AnalystTestingUtility.CONSOLE_OUTPUT ) {
			this.encogAnalyst.addAnalystListener(new ConsoleAnalystListener());	
		}
		
		do {		
			this.encogAnalyst.executeTask("task-full");
			e = calculateError();
			cycles++;
		} while( cycles<=MAX_CYCLES && e>maxError );
		
		Assert.assertTrue("Too many cycles to perform successful train.", cycles<=MAX_CYCLES);
		
	}
	
	public void report() {
		AnalystReport report = new AnalystReport(this.encogAnalyst);
		report.produceReport(tempDir.createFile("report.html"));
	}

	public void validateDataField(
			int i, 
			double max, 
			double mean, 
			double min, 
			double sd, 
			String name, 
			boolean isClass, 
			boolean isComplete, 
			boolean isInteger, 
			boolean isReal) {
		DataField df = this.encogAnalyst.getScript().getFields()[i];
		Assert.assertEquals(max, df.getMax(),0.001);
		Assert.assertEquals(mean, df.getMean(),0.001);
		Assert.assertEquals(min, df.getMin(),0.001);
		Assert.assertEquals(sd, df.getStandardDeviation(),0.001);
		Assert.assertEquals(name, df.getName() );
		Assert.assertEquals(isClass, df.isClass() );
		Assert.assertEquals(isComplete, df.isComplete() );
		Assert.assertEquals(isInteger, df.isInteger() );
		Assert.assertEquals(isReal, df.isReal() );		
	}
	
	public void dumpDataField(int i) {
		DataField df = this.encogAnalyst.getScript().getFields()[i];
		System.out.print(Format.formatDouble(df.getMax(),6));
		System.out.print(";");
		System.out.print(Format.formatDouble(df.getMean(),6));
		System.out.print(";");
		System.out.print(Format.formatDouble(df.getMin(),6));
		System.out.print(";");
		System.out.print(Format.formatDouble(df.getStandardDeviation(),6));
		System.out.print(";");
		System.out.print(df.getName());
		System.out.print(";");
		System.out.print(df.isClass()?'1':'0');
		System.out.print(";");
		System.out.print(df.isComplete()?'1':'0');
		System.out.print(";");
		System.out.print(df.isInteger()?'1':'0');
		System.out.print(";");
		System.out.println(df.isReal()?'1':'0');
	}

	public void validateDataClass(int i, String... args) {
		DataField df = this.encogAnalyst.getScript().getFields()[i];
		Assert.assertEquals(args.length, df.getClassMembers().size());
		for(int j=0;j<df.getClassMembers().size();j++) {
			Assert.assertEquals(args[j], df.getClassMembers().get(j).getName());
		}		
	}
	
	/**
	 * Obtain the ML method.
	 * @return The method.
	 */
	public MLMethod obtainMethod() {
		final ScriptProperties prop = this.encogAnalyst.getScript().getProperties(); 
		final String resourceID = prop.getPropertyString(
				ScriptProperties.ML_CONFIG_MACHINE_LEARNING_FILE);
		final File resourceFile = this.encogAnalyst.getScript().resolveFilename(resourceID);

		final MLMethod method = (MLMethod) EncogDirectoryPersistence
				.loadObject(resourceFile);

		if (!(method instanceof MLMethod)) {
			throw new AnalystError(
					"The object to be trained must be an instance of MLMethod. "
							+ method.getClass().getSimpleName());
		}

		return method;
	}
	
	/**
	 * Obtain the training set.
	 * @return The training set.
	 */
	private MLDataSet obtainTrainingSet() {
		final ScriptProperties prop = this.encogAnalyst.getScript().getProperties();
		final String trainingID = prop.getPropertyString(
				ScriptProperties.ML_CONFIG_TRAINING_FILE);

		final File trainingFile = this.encogAnalyst.getScript().resolveFilename(trainingID);

		MLDataSet trainingSet = EncogUtility.loadEGB2Memory(trainingFile);
		return trainingSet;
	}

	public void validateMethodError(double targetError) {
		double e = calculateError();
		Assert.assertTrue(e<targetError);
	}
	
	public double calculateError() {
		MLMethod method = obtainMethod();
		MLDataSet data = obtainTrainingSet();
		return ((MLError)method).calculateError(data);
	}

	public void dumpAnalystField(int i) {
		AnalystField af = this.encogAnalyst.getScript().getNormalize().getNormalizedFields().get(i);
		
		System.out.print(Format.formatDouble(af.getActualHigh(),6));
		System.out.print(";");
		System.out.print(Format.formatDouble(af.getActualLow(),6));
		System.out.print(";");
		System.out.print(Format.formatDouble(af.getNormalizedHigh(),6));
		System.out.print(";");
		System.out.print(Format.formatDouble(af.getNormalizedLow(),6));
		System.out.print(";");
		System.out.print(af.getName());
		System.out.print(";");
		System.out.print(af.getTimeSlice());
		System.out.print(";");
		System.out.println(af.getAction().toString());
		
	}

	public void validateAnalystField(int i, double high, double low, double normHigh, double normLow,
			String name, int timeSlice, String action) {
		AnalystField af = this.encogAnalyst.getScript().getNormalize().getNormalizedFields().get(i);
		Assert.assertEquals(high, af.getActualHigh(),0.001);
		Assert.assertEquals(low, af.getActualLow(),0.001);
		Assert.assertEquals(normHigh, af.getNormalizedHigh(),0.001);
		Assert.assertEquals(normLow, af.getNormalizedLow(),0.001);
		Assert.assertEquals(name, af.getName() );
		Assert.assertEquals(timeSlice, af.getTimeSlice() );
		Assert.assertEquals(action, af.getAction().toString() );
	}

	/**
	 * @return the format
	 */
	public AnalystFileFormat getFormat() {
		return format;
	}

	/**
	 * @param format the format to set
	 */
	public void setFormat(AnalystFileFormat format) {
		this.format = format;
	}
	
	
	
}
