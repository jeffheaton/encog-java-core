package org.encog.app.quant.indicators;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import org.encog.app.quant.QuantError;
import org.encog.app.quant.basic.BaseCachedColumn;
import org.encog.app.quant.basic.BasicCachedFile;
import org.encog.app.quant.basic.FileData;
import org.encog.util.csv.ReadCSV;

/**
 * Process indicators and generate output.
 */
public class ProcessIndicators extends BasicCachedFile {

	/**
	 * Read the CSV file.
	 */
    private void readFile()
    {
        ReadCSV csv = null;

        try
        {
            csv = new ReadCSV(getInputFilename().toString(), isExpectInputHeaders(), getInputFormat());

            resetStatus();
            int row = 0;
            while (csv.next() && !shouldStop() )
            {
                updateStatus("Reading data");
                for(BaseCachedColumn column : this.getColumns())
                {
                    if (column instanceof FileData)
                    {
                        if( column.isInput() )
                        {
                            FileData fd = (FileData)column;
                            String str = csv.get(fd.getIndex());
                            double d = this.getInputFormat().parse(str);
                            fd.getData()[row] = d;
                        }
                    }
                }
                row++;
            }
        }
        finally
        {
            reportDone("Reading data");
            if (csv != null)
                csv.close();
        }
    }

    /**
     * @return Get the beginning index.
     */
    private int getBeginningIndex()
    {
        int result = 0;

        for(BaseCachedColumn column : getColumns() )
        {
            if (column instanceof Indicator)
            {
                Indicator ind = (Indicator)column;
                result = Math.max(ind.getBeginningIndex(), result);
            }
        }

        return result;
    }

    /** 
     * @return Get the ending index.
     */
    private int getEndingIndex()
    {
        int result = this.getRecordCount()-1;

        for(BaseCachedColumn column : getColumns() )
        {
            if (column instanceof Indicator)
            {
                Indicator ind = (Indicator)column;
                result = Math.min(ind.getEndingIndex(), result);
            }
        }

        return result;
    }

    /**
     * Write the CSV.
     * @param filename The target filename.
     */
    private void writeCSV(File filename)
    {
        PrintWriter tw = null;

        try
        {
            resetStatus();
            tw = new PrintWriter(new FileWriter(filename));

            // write the headers
            if (this.isExpectInputHeaders())
            {
                StringBuilder line = new StringBuilder();

                for(BaseCachedColumn column : getColumns())
                {
                    if (column.isOutput())
                    {
                        if (line.length() > 0)
                            line.append(this.getInputFormat().getSeparator());
                        line.append("\"");
                        line.append(column.getName());
                        line.append("\"");
                    }
                }

                tw.println(line.toString());
            }

            // starting and ending index
            int beginningIndex = getBeginningIndex();
            int endingIndex = getEndingIndex();

            // write the file data
            for (int row = beginningIndex; row <= endingIndex; row++)
            {
                updateStatus("Writing data");
                StringBuilder line = new StringBuilder();

                for(BaseCachedColumn column : getColumns())
                {
                    if (column.isOutput())
                    {
                        if (line.length() > 0)
                            line.append(this.getInputFormat().getSeparator());
                        double d = column.getData()[row];
                        line.append(this.getInputFormat().format(d, getPrecision()));
                    }
                }

                tw.println(line.toString());
            }
        } catch (IOException e) {
			throw( new QuantError(e));
		}
        finally
        {
            if (tw != null)
                tw.close();
        }
    }

    /**
     * Allocate storage.
     */
    private void allocateStorage()
    {
        for(BaseCachedColumn column : this.getColumns())
        {
            column.allocate(this.getRecordCount());
        }
    }

    /**
     * Calculate the indicators.
     */
    private void calculateIndicators()
    {
        for (BaseCachedColumn column : this.getColumns())
        {
            if (column.isOutput())
            {
                if (column instanceof Indicator)
                {
                    Indicator indicator = (Indicator)column;
                    indicator.calculate(this.getColumnMapping(), this.getRecordCount());
                }
            }
        }
    }

    /**
     * Rename a column.
     * @param index The column index.
     * @param newName The new name.
     */
    public void renameColumn(int index, String newName)
    {
        this.getColumnMapping().remove(getColumns().get(index).getName());
        this.getColumns().get(index).setName(newName);
        this.getColumnMapping().put(newName, this.getColumns().get(index));
    }

    /**
     * Process and write the specified output file.
     * @param output The output file.
     */
    public void process(File output)
    {
        validateAnalyzed();

        allocateStorage();
        readFile();
        calculateIndicators();
        writeCSV(output);
    }	
}
