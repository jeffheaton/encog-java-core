package org.encog.app.csv.basic;

import org.encog.util.csv.ReadCSV;

/**
 * A row of a CSV file loaded to memory.  This class is used internally by
 * many of the Encog quant classes.
 */
public class LoadedRow {

    /**
     * The row data.
     */
    private String[] data;

    /**
     * @return The row data.
     */
    public String[] getData() 
    { 
    	return this.data; 
    } 

    /**
     * Load a row from the specified CSV file.
     * @param csv The CSV file to use.
     */
    public LoadedRow(ReadCSV csv)
    {
        this(csv,0);
    }
    
    public LoadedRow(ReadCSV csv, int extra)
    {
        int count = csv.getColumnCount();
        this.data = new String[count+extra];
        for (int i = 0; i < count; i++)
        {
            this.data[i] = csv.get(i); 
        }
    }
}
