package org.encog.app.analyst.evaluate;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import org.encog.app.analyst.EncogAnalyst;
import org.encog.app.quant.basic.BasicFile;
import org.encog.app.quant.basic.LoadedRow;
import org.encog.app.quant.normalize.ClassItem;
import org.encog.app.quant.normalize.NormalizedField;
import org.encog.ml.MLRegression;
import org.encog.neural.data.NeuralData;
import org.encog.neural.data.basic.BasicNeuralData;
import org.encog.util.csv.CSVFormat;
import org.encog.util.csv.ReadCSV;

public class AnalystEvaluateCSV extends BasicFile {

	private final Map<String,Integer> classCorrect = new HashMap<String,Integer>();
	private final Map<String,Integer> classCount = new HashMap<String,Integer>();
	
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
	public void analyze(String inputFile, boolean headers, CSVFormat format) {
		this.inputFilename = inputFile;
		this.setExpectInputHeaders(headers);
		this.setInputFormat(format);

		this.setAnalyzed(true);

		performBasicCounts();
	}

	public void process(String outputFile, EncogAnalyst analyst,
			MLRegression method) {

		ReadCSV csv = new ReadCSV(this.getInputFilename(),
				this.isExpectInputHeaders(), this.getInputFormat());

		NeuralData output = null;
		PrintWriter tw = this.prepareOutputFile(outputFile);
		NeuralData input = new BasicNeuralData(method.getInputCount());
		NormalizedField[] fields = analyst.getScript().getNormalize()
				.getNormalizedFields();
		int finalCol = getColumnCount();

		resetStatus();
		while (csv.next()) {
			updateStatus(false);
			LoadedRow row = new LoadedRow(csv, 1);

			int inputIndex = 0;
			int outputIndex = 0;
			int computeIndex = method.getInputCount();

			for (int normFieldNumber = 0; normFieldNumber < fields.length; normFieldNumber++) {
				NormalizedField field = fields[normFieldNumber];

				int columnsNeeded = field.getColumnsNeeded();

				if (normFieldNumber < computeIndex) {
					double d = this.getInputFormat().parse(
							row.getData()[inputIndex++]);
					
					if (columnsNeeded == 1) {						
						input.setData(outputIndex++, field.normalize(d));
					} else if (columnsNeeded > 1) {
						double[] e = field.getEq().encode((int)d);
						for(int i=0;i<e.length;i++) {
							input.setData(outputIndex++, d);	
						}
					}
				} else if (normFieldNumber == computeIndex) {
					output = method.compute(input);				
					if( field.getColumnsNeeded()>1 ) {
						// classification
						ClassItem cls = field.determineClass(output.getData());
						row.getData()[finalCol] = cls.getName();
						
						String a = row.getData()[finalCol-1];
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
						row.getData()[finalCol] = this.getInputFormat().format(output.getData(0), this.getPrecision());
					}
				}
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
