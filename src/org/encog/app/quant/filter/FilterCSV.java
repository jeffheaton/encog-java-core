package org.encog.app.quant.filter;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.encog.app.quant.basic.BasicFile;
import org.encog.app.quant.basic.LoadedRow;
import org.encog.util.csv.CSVFormat;
import org.encog.util.csv.ReadCSV;

/**
 * This class can be used to remove certain rows from a CSV.  You can remove rows
 * where a specific field has a specific value
 *
 */
public class FilterCSV extends BasicFile {

    /**
     * @return A list of the fields and their values, that should be excluded.
     */
    public List<ExcludedField> getExcluded()
    {
    	return this.excludedFields;     
    }

    /**
     * @return A count of the filtered rows.  This is the resulting line count for the output CSV.
     */
    public int getFilteredRowCount()
    {
    	return this.filteredCount;
    }

    /**
     * The excluded fields.
     */
    private List<ExcludedField> excludedFields = new ArrayList<ExcludedField>();

    /**
     * A count of the filtered rows.
     */
    private int filteredCount;

    /**
     * Exclude rows where the specified field has the specified value.
     * @param fieldNumber The field number.
     * @param fieldValue The field value.
     */
    public void exclude(int fieldNumber, String fieldValue)
    {
        this.excludedFields.add(new ExcludedField(fieldNumber, fieldValue));
    }

    /**
     * Analyze the file.
     * @param inputFile The name of the input file.
     * @param headers True, if headers are expected.
     * @param format The format.
     */
    public void analyze(String inputFile, boolean headers, CSVFormat format)
    {
        this.setInputFilename( inputFile );
        this.setExpectInputHeaders( headers );
        this.setInputFormat( format );

        this.setAnalyzed( true );

        performBasicCounts();
    }

    /**
     * Determine if the specified row should be processed, or not.
     * @param row The row.
     * @return True, if the row should be processed.
     */
    private boolean shouldProcess(LoadedRow row)
    {
        for(ExcludedField field : this.excludedFields)
        {
            if( row.getData()[field.getFieldNumber()].trim().equals(field.getFieldValue().trim()) )
            {
                return false;
            }
        }

        return true;
    }

    /**
     * Process the input file.
     * @param outputFile The output file to write to.
     */
    public void process(String outputFile)
    {
        ReadCSV csv = new ReadCSV(this.getInputFilename(), this.isExpectInputHeaders(), this.getInputFormat() );

        PrintWriter tw = this.prepareOutputFile(outputFile);
        this.filteredCount = 0;

        resetStatus();
        while (csv.next())
        {
            updateStatus(false);
            LoadedRow row = new LoadedRow(csv);
            if (shouldProcess(row))
            {
                WriteRow(tw, row);
                this.filteredCount++;
            }
        }
        reportDone(false);
        tw.close();
        csv.close();
    }
}
