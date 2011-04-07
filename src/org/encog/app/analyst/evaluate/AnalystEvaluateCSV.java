package org.encog.app.analyst.evaluate;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import org.encog.app.analyst.AnalystError;
import org.encog.app.analyst.EncogAnalyst;
import org.encog.app.analyst.script.normalize.AnalystField;
import org.encog.app.quant.QuantError;
import org.encog.app.quant.basic.BasicFile;
import org.encog.app.quant.basic.LoadedRow;
import org.encog.app.quant.normalize.ClassItem;
import org.encog.ml.MLRegression;
import org.encog.neural.data.NeuralData;
import org.encog.neural.data.basic.BasicNeuralData;
import org.encog.util.csv.CSVFormat;
import org.encog.util.csv.ReadCSV;

/**
 * Used by the analyst to evaluate a CSV file.
 *
 */
public class AnalystEvaluateCSV extends BasicFile {

	private final Map<String,Integer> classCorrect = new HashMap<String,Integer>();
	private final Map<String,Integer> classCount = new HashMap<String,Integer>();
	private final Map<AnalystField,Integer> columnMapping = new HashMap<AnalystField,Integer>();
	private EncogAnalyst analyst;
	private int inputFieldCount;
	private int outputFieldCount;
	private int idealFieldCount;

	
	/**
	 * Analyze the data. This counts the records and prepares the data to be
	 * processed.
	 * 
	 * @param inputFile
	 *            The input file to process.
	 * @param headers
	 *            True, if headers are present.
	 * @param format
	 *            The format of the CSV file.
	 */
	public void analyze(EncogAnalyst analyst, File inputFile, boolean headers, CSVFormat format) {
		this.inputFilename = inputFile;
		this.setExpectInputHeaders(headers);
		this.setInputFormat(format);

		this.setAnalyzed(true);
		this.analyst = analyst;

		performBasicCounts();
		
		this.inputFieldCount = this.analyst.determineInputFieldCount();
		this.outputFieldCount = this.analyst.determineOutputFieldCount();
		this.idealFieldCount = this.getInputHeadings().length - inputFieldCount;

		if (this.getInputHeadings().length != inputFieldCount
				&& this.getInputHeadings().length != (inputFieldCount + outputFieldCount)) {
			throw new AnalystError(
					"Invalid number of columns("+this.getInputHeadings().length+"), must match input(" + inputFieldCount
							+ ") count or input+output("
							+ (inputFieldCount + outputFieldCount) + ") count.");
		}

		
		// perform mapping
		for(int i=0;i<this.inputHeadings.length;i++) {
			String heading = this.inputHeadings[i];
			AnalystField field = this.analyst.getScript().findNormalizedField(heading);
			if( field!=null ) {
				this.columnMapping.put(field, i);
			}
		}
	}
	
	/**
	 * Prepare the output file, write headers if needed.
	 * 
	 * @param outputFile
	 *            The name of the output file.
	 * @param method 
	 * @return The output stream for the text file.
	 */
	public PrintWriter prepareOutputFile(File outputFile, int input, int output) {
		try {
			PrintWriter tw = new PrintWriter(new FileWriter(outputFile));

			// write headers, if needed
			if (this.isProduceOutputHeaders()) {
				StringBuilder line = new StringBuilder();

				// first handle the input fields
				for (AnalystField field : this.analyst.getScript()
						.getNormalize().getNormalizedFields()) {
					if (field.isInput()) {
						field.addRawHeadings(line, null, this.getOutputFormat());
					}
				}

				// now, handle any ideal fields
				if (this.idealFieldCount > 0) {
					for (AnalystField field : this.analyst.getScript()
							.getNormalize().getNormalizedFields()) {
						if (field.isOutput()) {
							field.addRawHeadings(line, "ideal:",
									this.getOutputFormat());
						}
					}
				}

				// now, handle the output fields
				for (AnalystField field : this.analyst.getScript()
						.getNormalize().getNormalizedFields()) {
					if (field.isOutput()) {
						field.addRawHeadings(line, "output:",
								this.getOutputFormat());
					}
				}

				tw.println(line.toString());
			}

			return tw;

		} catch (IOException e) {
			throw new QuantError(e);
		}
	}


	public void process(File outputFile, EncogAnalyst analyst,
			MLRegression method, String targetFieldName) {

		ReadCSV csv = new ReadCSV(this.getInputFilename().toString(),
				this.isExpectInputHeaders(), this.getInputFormat());

		NeuralData output = null;
		
		NeuralData input = new BasicNeuralData(method.getInputCount());
		
		AnalystField targetField = analyst.getScript().findNormalizedField(targetFieldName);

		PrintWriter tw = this.prepareOutputFile(outputFile, analyst.getScript().getNormalize().countActiveFields()-1, 1);
		
		resetStatus();
		while (csv.next()) {
			updateStatus(false);
			LoadedRow row = new LoadedRow(csv, 1);
			int finalCol = row.getData().length-1;

			int inputIndex = 0;
			int outputIndex = 0;
			String idealClass = "";

			// build the input
			for(AnalystField field : analyst.getScript().getNormalize().getNormalizedFields()) {
				int columnsNeeded = field.getColumnsNeeded();
				String str = row.getData()[inputIndex];

				if (field!=targetField ) {
					// input data
					 					
					if (columnsNeeded == 1) {	
						double d = this.getInputFormat().parse(str);
						input.setData(outputIndex++, field.normalize(d));
					} else if (columnsNeeded > 1) {
						int idx = field.lookup(str);
						double[] e = field.getEq().encode(idx);
						for(int i=0;i<e.length;i++) {
							input.setData(outputIndex++, e[i]);	
						}
					}
				} else {
					idealClass = str;
				}
				
				inputIndex++;
			}
			
			// evaluate
			

			// evaluation data
			output = method.compute(input);	

			if( targetField.isClassify() ) {
				// classification
				ClassItem cls = targetField.determineClass(output.getData());
				row.getData()[finalCol] = cls.getName();
				
				String a = idealClass;
				String b = row.getData()[finalCol];

				// update count
				if( !classCount.containsKey(a) ) {
					classCount.put(a, 1);
				} else {
					int i = classCount.get(a);
					classCount.put(a, i+1);
				}
				
				// update correct
				if( a.equals(b) ) {
					if( !classCorrect.containsKey(a) ) {
						classCorrect.put(a, 1);
					} else {
						int i = classCorrect.get(a);
						classCorrect.put(a, i+1);
					}	
				}						
			} else {
				// regression
				 double n = output.getData(0);
				 n = targetField.deNormalize(n);				 
				 row.getData()[finalCol] = this.getInputFormat().format(n, this.getPrecision());
			}
		

			
			writeRow(tw, row);
		}
		reportDone(false);
		tw.close();
		csv.close();
	}

	public Map<String, Integer> getClassCorrect() {
		return classCorrect;
	}

	public Map<String, Integer> getClassCount() {
		return classCount;
	}
	
	

}
