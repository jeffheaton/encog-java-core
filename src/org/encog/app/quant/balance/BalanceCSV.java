package org.encog.app.quant.balance;

import java.io.File;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import org.encog.app.quant.basic.BasicFile;
import org.encog.app.quant.basic.LoadedRow;
import org.encog.util.csv.CSVFormat;
import org.encog.util.csv.ReadCSV;

/**
 * Balance a CSV file.  This utility is useful when you have several an unbalanced
 * training set.  You may have a large number of one particular class, and many fewer
 * elements of other classes.  This can hinder many Machine Learning methods.  This
 * class can be used to balance the data.
 * 
 * Obviously this class cannot generate data.  You must request how many items you
 * want per class.  Some classes will have lower than this number if they were already
 * below the specified amount.  Any class above this amount will be trimmed to that
 * amount.
 */
public class BalanceCSV extends BasicFile {
    /**
     * Tracks the counts of each class.
     */
    private Map<String, Integer> counts;

    /**
     * @return Tracks the counts of each class.
     */
    public Map<String, Integer> getCounts() { return this.counts; }

    /**
     * Analyze the data.  This counts the records and prepares the data to be
     * processed.
     * @param inputFile The input file to process.
     * @param headers True, if headers are present.
     * @param format The format of the CSV file.
     */
    public void Analyze(File inputFile, boolean headers, CSVFormat format)
    {
        this.inputFilename = inputFile;
        this.setExpectInputHeaders( headers );
        this.setInputFormat( format );

        this.setAnalyzed( true );

        performBasicCounts();
    }

    /**
     * Process and balance the data.
     * @param outputFile The output file to write data to.
     * @param targetField The field that is being balanced, 
     * this field determines the classes.
     * @param countPer The desired count per class.
     */
    public void Process(File outputFile, int targetField, int countPer)
    {
        validateAnalyzed();
        PrintWriter tw = this.prepareOutputFile(outputFile);
        
        counts = new HashMap<String, Integer>();

        ReadCSV csv = new ReadCSV(this.getInputFilename().toString(), this.isExpectInputHeaders(), this.getInputFormat());

        resetStatus();
        while (csv.next() && !shouldStop() )
        {
            LoadedRow row = new LoadedRow(csv);
            updateStatus(false);
            String key = row.getData()[targetField];
            int count;
            if (!counts.containsKey(key))
            {
                count = 0;
            }
            else
            {
                count = counts.get(key);
            }

            if (count < countPer)
            {
                writeRow(tw, row);
                count++;
            }

            counts.put(key, count);
        }
        reportDone(false);
        csv.close();
        tw.close();
    }


    /**
     * Return a string that lists the counts per class.
     * @return The counts per class.
     */
    public String DumpCounts()
    {
        StringBuilder result = new StringBuilder();
        for (String key : this.counts.keySet())
        {
            result.append(key);
            result.append(" : ");
            result.append(this.counts.get(key));
            result.append("\n");
        }
        return result.toString();
    }

}
