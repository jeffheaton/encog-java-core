package org.encog.app.quant.basic;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.encog.app.quant.QuantError;
import org.encog.util.csv.CSVFormat;
import org.encog.util.csv.ReadCSV;

/**
 * Forms the foundation of all of the cached files in Encog Quant.
 */
public class BasicCachedFile extends BasicFile {

	/**
	 * @return The column mappings.
	 */
    public Map<String, BaseCachedColumn> getColumnMapping() 
    {
    	return columnMapping; 
    }

    /**
     * @return The columns.
     */
    public List<BaseCachedColumn> getColumns() 
    { 
    	return columns; 
    } 

    /**
     * The column mapping.
     */
    private Map<String, BaseCachedColumn> columnMapping = new HashMap<String, BaseCachedColumn>();

    /**
     * The columns.
     */
    private List<BaseCachedColumn> columns = new ArrayList<BaseCachedColumn>();

    /**
     * Analyze the input file.
     * @param input The input file.
     * @param headers True, if there are headers.
     * @param format The format of the CSV data.
     */
    public void analyze(String input, boolean headers, CSVFormat format)
    {
        resetStatus();
        setInputFilename(input);
        setExpectInputHeaders(headers);
        setInputFormat(format);
        this.columnMapping.clear();
        this.columns.clear();

        // first count the rows
        BufferedReader reader = null;
        try
        {
            int recordCount = 0;
            reader = new BufferedReader(new FileReader(this.inputFilename));
            while (reader.readLine() != null)
            {
                updateStatus(true);
                recordCount++;
            }

            if (headers)
                recordCount--;
            this.setRecordCount( recordCount );
        }
        catch(IOException ex) {
        	throw new QuantError(ex);
        }
        finally
        {
            reportDone(true);
            if (reader != null) {
                try {
					reader.close();
				} catch (IOException e) {
					throw new QuantError(e);
				}
            }
            this.setInputFilename( input );
            this.setExpectInputHeaders( headers );
            this.inputFormat = format;
        }

        // now analyze columns
        ReadCSV csv = null;
        try
        {
            csv = new ReadCSV(input, headers, format);
            if (!csv.next())
            {
                throw new QuantError("File is empty");
            }

            for (int i = 0; i < csv.getColumnCount(); i++)
            {
                String name;

                if (headers)
                    name = attemptResolveName(csv.getColumnNames().get(i));
                else
                    name = "Column-" + (i + 1);

                // determine if it should be an input/output field                    

                String str = csv.get(i);
                
                boolean io = false;
                
                try {
                	Double.parseDouble(str);
                	io = true;
                }
                catch(NumberFormatException ex) {
                	
                }

                addColumn(new FileData(name, i, io, io));
            }
        }
        finally
        {
            csv.close();
            this.setAnalyzed( true );
        }
    }

    /**
     * Attempt to resolve a column name.
     * @param name The unknown column name.
     * @return The known column name.
     */
    private String attemptResolveName(String name)
    {
        name = name.toLowerCase();

        if (name.indexOf("open") != -1)
        {
            return FileData.OPEN;
        }
        else if (name.indexOf("close") != -1)
        {
            return FileData.CLOSE;
        }
        else if (name.indexOf("low") != -1)
        {
            return FileData.LOW;
        }
        else if (name.indexOf("hi") != -1)
        {
            return FileData.HIGH;
        }
        else if (name.indexOf("vol") != -1)
        {
            return FileData.VOLUME;
        }
        else if (name.indexOf("date") != -1 || name.indexOf("yyyy")!=-1)
        {
            return FileData.DATE;
        }
        else if (name.indexOf("time") != -1)
        {
            return FileData.TIME;
        }

        return name;
    }

    /**
     * Add a new column.
     * @param column The column to add.
     */
    public void addColumn(BaseCachedColumn column)
    {
        this.columns.add(column);
        this.columnMapping.put(column.getName(), column);
    }

    /**
     * Get the data for a specific column.
     * @param name The column to read.
     * @param csv The CSV file to read from.
     * @return The column data.
     */
    public String getColumnData(String name, ReadCSV csv)
    {
        if (!this.columnMapping.containsKey(name))
            return null;

        BaseCachedColumn column = this.columnMapping.get(name);

        if (!(column instanceof FileData))
            return null;

        FileData fd = (FileData)column;
        return csv.get(fd.getIndex());
    }

}
