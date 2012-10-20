/*
 * Encog(tm) Core v3.2 - Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 
 * Copyright 2008-2012 Heaton Research, Inc.
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
package org.encog.app.generate.generators;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import org.encog.Encog;
import org.encog.app.analyst.EncogAnalyst;
import org.encog.app.generate.AnalystCodeGenerationError;
import org.encog.engine.network.activation.ActivationElliott;
import org.encog.engine.network.activation.ActivationElliottSymmetric;
import org.encog.engine.network.activation.ActivationFunction;
import org.encog.engine.network.activation.ActivationLinear;
import org.encog.engine.network.activation.ActivationSigmoid;
import org.encog.engine.network.activation.ActivationTANH;
import org.encog.neural.flat.FlatNetwork;
import org.encog.util.EngineArray;
import org.encog.util.csv.CSVFormat;
import org.encog.util.file.ResourceInputStream;
import org.encog.util.logging.EncogLogging;

public abstract class AbstractTemplateGenerator implements TemplateGenerator {

	private StringBuilder contents = new StringBuilder();
	private EncogAnalyst analyst;
	private int indentLevel = 0;
	
	public abstract String getTemplatePath();
	public abstract void processToken(String command);
	
	public void writeContents(File targetFile) {
		try {
			FileWriter outFile = new FileWriter(targetFile);
			PrintWriter out = new PrintWriter(outFile);
			out.print(this.contents.toString());
			out.close();
		} catch (IOException e){
			e.printStackTrace();
		}
	}

	@Override
	public String getContents() {
		return this.contents.toString();
	}

	@Override
	public void generate(EncogAnalyst theAnalyst) {
		InputStream is = null;
		BufferedReader br = null;
		
		this.analyst = theAnalyst;
		
		try {
			is = ResourceInputStream.openResourceInputStream(getTemplatePath());
			br = new BufferedReader(new InputStreamReader(is));
		
			String line;
			while( (line=br.readLine())!=null ) {
				if( line.startsWith("~~") ) {
					processToken(line.substring(2).trim());
				} else {
					this.contents.append(line);
					this.contents.append("\n");
				}
			}
		br.close();
		} catch(IOException ex) {
			throw new AnalystCodeGenerationError(ex);
		} finally {
			if( is!=null ) {
				try {
					is.close();
				} catch(IOException ex) {
					EncogLogging.log(EncogLogging.LEVEL_ERROR, ex);
				}
			}
			
			if( br!=null ) {
				try {
					br.close();
				} catch(IOException ex) {
					EncogLogging.log(EncogLogging.LEVEL_ERROR, ex);
				}
			}
		}
		
	}
	public EncogAnalyst getAnalyst() {
		return analyst;
	}
	
	public void addLine(String line) {
		for(int i=0;i<this.indentLevel;i++) {
			this.contents.append("\t");
		}
		this.contents.append(line);
		this.contents.append("\n");
	}
	public int getIndentLevel() {
		return indentLevel;
	}
	public void setIndentLevel(int indentLevel) {
		this.indentLevel = indentLevel;
	}
	
	public void indentIn() {
		this.indentLevel++;
	}
	
	public void indentOut() {
		this.indentLevel--;
	}
	
	public void addNameValue(String name, int value) {
		addNameValue(name, "" + value);
	}

	public void addNameValue(String name, String value) {
		StringBuilder line = new StringBuilder();
		line.append(name);
		line.append(" = ");
		
		if( value==null ) {
			line.append(getNullArray());
		} else {
			line.append(value);
		}
		
		line.append(";");
		addLine(line.toString());
	}

	public void addNameValue(String name, int[] data) {
		StringBuilder value = new StringBuilder();
		if( data==null ) {
			value.append(name);
			value.append(" = "+getNullArray()+";");
			addLine(value.toString());
		} else {			
			toBrokenList(value, data);
			addNameValue(name, "{" + value.toString() + "}");
		}
	}

	public void addNameValue(String name, double[] data) {
		StringBuilder value = new StringBuilder();
		if( data==null ) {
			value.append(name);
			value.append(" = "+getNullArray()+";");
			addLine(value.toString());
		} else {	
			toBrokenList(value, data);
			addNameValue(name, "{" + value.toString() + "}");
		}
	}
	
	public double[] createParams(FlatNetwork flat) {
		double[] result = new double[flat.getActivationFunctions().length];
		EngineArray.fill(result, 1);
		return result;
	}

	public int[] createActivations(FlatNetwork flat) {
		int[] result = new int[flat.getActivationFunctions().length];
		for (int i = 0; i < flat.getActivationFunctions().length; i++) {
			ActivationFunction af = flat.getActivationFunctions()[i];

			if (af instanceof ActivationLinear) {
				result[i] = 0;
			} else if (af instanceof ActivationTANH) {
				result[i] = 1;
			}
			if (af instanceof ActivationSigmoid) {
				result[i] = 2;
			}
			if (af instanceof ActivationElliottSymmetric) {
				result[i] = 3;
			}
			if (af instanceof ActivationElliott) {
				result[i] = 4;
			}
		}

		return result;
	}
	
	public void toBrokenList(StringBuilder result,
			int[] data) {
		int lineCount = 0;
		
		for (int i = 0; i < data.length; i++) {
			if (i != 0) {
				result.append(',');
			}
			
			lineCount++;
			if( lineCount>10 ) {
				result.append("\n");
				lineCount = 0;
			}
			result.append(""+data[i]);
		}
	}

	public void toBrokenList(StringBuilder result, double[] data) {
		int lineCount = 0;
		result.setLength(0);
		for (int i = 0; i < data.length; i++) {
			if (i != 0) {
				result.append(',');
			}
			
			lineCount++;
			if( lineCount>10 ) {
				result.append("\n");
				lineCount = 0;
			}
			result.append(CSVFormat.EG_FORMAT.format(data[i], Encog.DEFAULT_PRECISION));			
		}
		
	}
	
	public abstract String getNullArray();

}
