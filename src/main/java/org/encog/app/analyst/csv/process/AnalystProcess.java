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
package org.encog.app.analyst.csv.process;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.encog.app.analyst.EncogAnalyst;
import org.encog.app.analyst.csv.basic.BasicFile;
import org.encog.app.analyst.csv.basic.LoadedRow;
import org.encog.app.analyst.script.process.ProcessField;
import org.encog.app.quant.QuantError;
import org.encog.ml.prg.EncogProgram;
import org.encog.ml.prg.EncogProgramContext;
import org.encog.ml.prg.EncogProgramVariables;
import org.encog.ml.prg.expvalue.ExpressionValue;
import org.encog.ml.prg.extension.StandardExtensions;
import org.encog.util.csv.CSVFormat;
import org.encog.util.csv.ReadCSV;

/**
 * Perform many different types of transformations on a CSV.
 */
public class AnalystProcess extends BasicFile {

	private final EncogProgramContext programContext = new EncogProgramContext();
	private final EncogProgramVariables programVariables = new EncogProgramVariables();
	private final List<EncogProgram> expressionFields = new ArrayList<EncogProgram>();
	private ProcessExtension extension;
	private final EncogAnalyst analyst;
	private final int forwardWindowSize;
	private final int backwardWindowSize;
	
	
	/**
	 * Construct the object.
	 */
	public AnalystProcess(EncogAnalyst theAnalyst, int theBackwardWindowSize, int theForwardWindowSize) {
		this.analyst = theAnalyst;
		
		this.backwardWindowSize = theBackwardWindowSize;
		this.forwardWindowSize = theForwardWindowSize;
		StandardExtensions.createAll(this.programContext);
	}

	/**
	 * Analyze the neural network.
	 * 
	 * @param inputFile
	 *            The input file.
	 * @param headers
	 *            True, if there are headers.
	 * @param format
	 *            The format of the CSV file.
	 */
	public void analyze(final File inputFile, final boolean headers,
			final CSVFormat format) {
		setInputFilename(inputFile);
		setExpectInputHeaders(headers);
		setInputFormat(format);

		setAnalyzed(true);

		performBasicCounts();
		
		this.expressionFields.clear();
		this.extension = new ProcessExtension(this.getFormat());
		this.extension.register(this.programContext.getFunctions());
		
		for(ProcessField field : this.analyst.getScript().getProcess().getFields() ) {
			EncogProgram prg = new EncogProgram(this.programContext,this.programVariables);
			prg.setExtraData(ProcessExtension.EXTENSION_DATA_NAME, this.extension);
			prg.compileExpression(field.getCommand());
			this.expressionFields.add(prg);
		}
	}

	/**
	 * Get the next row from the underlying CSV file.
	 * 
	 * @param csv
	 *            The underlying CSV file.
	 * @return The loaded row.
	 */
	private LoadedRow getNextRow(final ReadCSV csv) {		
		if( csv.next() ) {
			return new LoadedRow(csv);
		} else {
			return null;
		}		
	}
	
	/**
	 * Prepare the output file, write headers if needed.
	 * 
	 * @param outputFile
	 *            The name of the output file.
	 * @return The output stream for the text file.
	 */
	public PrintWriter prepareOutputFile(final File outputFile) {
		try {
			final PrintWriter tw = new PrintWriter(new FileWriter(outputFile));

			// write headers, if needed
			if (this.isProduceOutputHeaders()) {
				int index = 0;
				final StringBuilder line = new StringBuilder();

				for( ProcessField field : this.analyst.getScript().getProcess().getFields() ) {
					if (line.length() > 0) {
						line.append(this.getFormat().getSeparator());
					}
					line.append("\"");
					line.append(field.getName());
					line.append("\"");
					index++;
				}
				
				tw.println(line.toString());
			}

			return tw;

		} catch (final IOException e) {
			throw new QuantError(e);
		}
	}
	
	private void processRow(PrintWriter tw) {
		StringBuilder line = new StringBuilder();
		
		for(EncogProgram prg: this.expressionFields) {
			ExpressionValue result = prg.evaluate();
			
			BasicFile.appendSeparator(line, this.getFormat());
			
			if( result.isString() ) {
				line.append("\"");
			}
			
			line.append(result.toStringValue());
			
			if( result.isString() ) {
				line.append("\"");
			}
		}
		tw.println(line.toString());
	}


	/**
	 * Process, and generate the output file.
	 * 
	 * @param outputFile
	 *            The output file.
	 */
	public void process(final File outputFile) {
		validateAnalyzed();

		final ReadCSV csv = new ReadCSV(getInputFilename().toString(),
				isExpectInputHeaders(), getFormat());
		LoadedRow row;

		final PrintWriter tw = prepareOutputFile(outputFile);
		this.extension.init(csv, 
				this.forwardWindowSize,
				this.backwardWindowSize);

		resetStatus();
		while ((row = getNextRow(csv)) != null) {	
			this.extension.loadRow(row);
		
			if(this.extension.isDataReady() ) {
				processRow(tw);
			}
			updateStatus(false);
		}
		reportDone(false);
		tw.close();
		csv.close();
	}
}
