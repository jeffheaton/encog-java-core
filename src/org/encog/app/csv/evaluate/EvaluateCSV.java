package org.encog.app.csv.evaluate;

import java.io.File;
import java.io.PrintWriter;

import org.encog.app.csv.basic.BasicFile;
import org.encog.app.csv.basic.LoadedRow;
import org.encog.app.quant.QuantError;
import org.encog.ml.MLRegression;
import org.encog.neural.data.NeuralData;
import org.encog.neural.data.basic.BasicNeuralData;
import org.encog.util.csv.CSVFormat;
import org.encog.util.csv.ReadCSV;

public class EvaluateCSV extends BasicFile {

    /**
     * Analyze the data.  This counts the records and prepares the data to be
     * processed.
     * @param inputFile The input file to process.
     * @param headers True, if headers are present.
     * @param format The format of the CSV file.
     */
    public void analyze(File inputFile, boolean headers, CSVFormat format)
    {
        this.inputFilename = inputFile;
        this.setExpectInputHeaders( headers );
        this.setInputFormat( format );

        this.setAnalyzed( true );

        performBasicCounts();
    }
    
    public void process(File outputFile, MLRegression method) {

        ReadCSV csv = new ReadCSV(this.getInputFilename().toString(), this.isExpectInputHeaders(), this.getInputFormat() );

        PrintWriter tw = this.prepareOutputFile(outputFile);
        NeuralData input = new BasicNeuralData(method.getInputCount());
        
        int methodCount = method.getInputCount() + method.getOutputCount();
        if( methodCount!=this.getColumnCount()) {
        	throw new QuantError("ML Method expects " + methodCount + ", however " + this.getColumnCount() + " columes are in the file.");
        }

        resetStatus();
        while (csv.next() && !shouldStop() )
        {        	
            updateStatus(false);
            LoadedRow row = new LoadedRow(csv, method.getOutputCount());
            for(int i=0;i<method.getInputCount();i++) {
            	double d = this.getInputFormat().parse(row.getData()[i]);
            	input.setData(i, d);
            }
            NeuralData output = method.compute(input);
            
            for(int i=0;i<output.size();i++)
            {
            	row.getData()[i+method.getInputCount()] = this.inputFormat.format(output.getData(i), this.getPrecision());
            }
            
            writeRow(tw, row);
        }
        reportDone(false);
        tw.close();
        csv.close();
    	
    }
    
}
