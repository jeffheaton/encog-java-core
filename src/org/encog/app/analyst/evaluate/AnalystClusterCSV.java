package org.encog.app.analyst.evaluate;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import org.encog.app.analyst.EncogAnalyst;
import org.encog.app.analyst.util.CSVHeaders;
import org.encog.app.csv.basic.BasicFile;
import org.encog.app.csv.basic.LoadedRow;
import org.encog.app.quant.QuantError;
import org.encog.engine.data.EngineData;
import org.encog.ml.kmeans.KMeansCluster;
import org.encog.ml.kmeans.KMeansClustering;
import org.encog.neural.data.NeuralData;
import org.encog.neural.data.basic.BasicNeuralData;
import org.encog.neural.data.basic.BasicNeuralDataSet;
import org.encog.util.csv.CSVFormat;
import org.encog.util.csv.ReadCSV;

/**
 * Used by the analyst to evaluate a CSV file.
 * 
 */
public class AnalystClusterCSV extends BasicFile {

	private EncogAnalyst analyst;
	private int fileColumns;
	private CSVHeaders analystHeaders;
	private BasicNeuralDataSet data;
	private Map<NeuralData,LoadedRow> rowMap = new HashMap<NeuralData,LoadedRow>();

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
	public void analyze(EncogAnalyst analyst, File inputFile, boolean headers,
			CSVFormat format) {
		this.inputFilename = inputFile;
		this.setExpectInputHeaders(headers);
		this.setInputFormat(format);

		this.setAnalyzed(true);
		this.analyst = analyst;

		this.fileColumns = this.inputHeadings.length;
		this.analystHeaders = new CSVHeaders(this.inputHeadings);
		
		if( this.getOutputFormat()==null) {
			this.setOutputFormat(this.inputFormat);
		}
		
		data = new BasicNeuralDataSet();
		resetStatus();
		int recordCount = 0;
		
		int outputLength = this.analyst.determineUniqueColumns();
		ReadCSV csv = new ReadCSV(this.inputFilename.toString(), this.expectInputHeaders,
				this.inputFormat);
		while (csv.next() && !this.shouldStop() ) {
			updateStatus(true);
			
			LoadedRow row = new LoadedRow(csv, 1);

			double[] inputArray = AnalystNormalizeCSV.extractFields(analyst, this.analystHeaders, csv, outputLength);
			NeuralData input = new BasicNeuralData(inputArray);
			this.data.add(input);
			this.rowMap.put(input, row);
			
			recordCount++;
		}
		this.setRecordCount( recordCount );
		this.columnCount = csv.getColumnCount();

		readHeaders(csv);
		csv.close();
		reportDone(true);
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

				// handle provided fields, not all may be used, but all should be displayed
				for (String heading : this.inputHeadings) {
					BasicFile.appendSeparator(line, this.getOutputFormat());
					line.append("\"");
					line.append(heading);
					line.append("\"");
				}

				// now the output fields that will be generated
				line.append("\"cluster\"");

				tw.println(line.toString());
			}

			return tw;

		} catch (IOException e) {
			throw new QuantError(e);
		}
	}

	public void process(File outputFile, EncogAnalyst analyst) {

		PrintWriter tw = this.prepareOutputFile(outputFile, analyst.getScript()
				.getNormalize().countActiveFields() - 1, 1);

		resetStatus();
		
		KMeansClustering cluster = new KMeansClustering(5,this.data);
		cluster.iteration(100);
		
		int clusterNum = 0;
		for(KMeansCluster cl : cluster.getClusters() )
		{
			for( EngineData data : cl.getData() ) {
				LoadedRow row = this.rowMap.get(data);
				int clsIndex = row.getData().length-1;
				row.getData()[clsIndex] = ""+clusterNum;
				writeRow(tw, row);
			}
			clusterNum++;
		}

		reportDone(false);
		tw.close();
	}
}
