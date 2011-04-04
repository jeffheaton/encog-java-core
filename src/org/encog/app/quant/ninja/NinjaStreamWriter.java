package org.encog.app.quant.ninja;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.encog.app.quant.QuantError;
import org.encog.app.quant.basic.FileData;
import org.encog.util.csv.CSVFormat;
import org.encog.util.time.NumericDateUtil;

/**
 * Can be used from within NinjaTrader to export data.  This class is usually placed 
 * inside of a NinjaTrader indicator to export NinjaTrader indicators and data.
 * 
 * Ninja Trader, at this point, only directly supports C#.  So this class will be
 * of limited use on the Java platform.
 */
public class NinjaStreamWriter {

    /**
     * The percision to use.
     */
    private int percision;

    /**
     * The columns to use.
     */
    private List<String> columns = new ArrayList<String>();

    /// <summary>
    /// The output file.
    /// </summary>
    private PrintWriter tw;

    /**
     * True, if headers are present.
     */
    private boolean headers;

    /**
     * The format of the CSV file.
     */
    private CSVFormat format;

    /**
     * The output line, as it is built.
     */
    private StringBuilder line;

    /**
     * True, if columns were defined.
     */
    private boolean columnsDefined;

    /**
     * Construct the object, and set the defaults.
     */
    public NinjaStreamWriter()
    {
        this.percision = 10;
        columnsDefined = false;
    }

    /**
     * Open the file for output.
     * @param filename The filename.
     * @param headers True, if headers are present.
     * @param format The CSV format.
     */
    public void open(String filename, boolean headers, CSVFormat format)
    {
    	try {
			tw = new PrintWriter(new FileWriter(filename));
	        this.format = format;
	        this.headers = headers;
		} catch (IOException e) {
			throw new QuantError(e);
		}
    }

    /**
     * Write the headers.
     */
    private void writeHeaders()
    {
        if (tw == null)
            throw new QuantError("Must open file first.");

        StringBuilder line = new StringBuilder();

        line.append(FileData.DATE);
        line.append(this.format.getSeparator());
        line.append(FileData.TIME);

        for (String str :this.columns)
        {
            if (line.length() > 0)
                line.append(this.format.getSeparator() );

            line.append("\"");
            line.append(str);
            line.append("\"");
        }
        this.tw.println(line.toString());
    }

    /**
     * Close the file.
     */
    public void close()
    {
        if (tw == null)
            throw new QuantError("Must open file first.");
        tw.close();
    }

    /**
     * Begin a bar, for the specified date/time.
     * @param dt The date/time where the bar begins.
     */
    public void beginBar(Date dt)
    {
        if (tw == null)
        {
            throw new QuantError("Must open file first.");
        }

        if (line != null)
        {
            throw new QuantError("Must call end bar");
        }

        line = new StringBuilder();
        line.append(NumericDateUtil.date2Long(dt));
        line.append(this.format.getSeparator());
        line.append(NumericDateUtil.time2Int(dt));
    }

    /**
     * End the current bar.
     */
    public void endBar()
    {
        if (tw == null)
        {
            throw new QuantError("Must open file first.");
        }

        if (line == null)
        {
            throw new QuantError("Must call BeginBar first.");
        }

        if (headers && !columnsDefined)
        {
            writeHeaders();                
        }

        tw.println(line.toString());
        line = null;
        columnsDefined = true;
    }
    
    /**
     * Store a column.
     * @param name The name of the column.
     * @param d The value to store.
     */
    public void storeColumn(String name, double d)
    {
        if (line == null)
        {
            throw new QuantError("Must call BeginBar first.");
        }

        if (line.length() > 0)
        {
            line.append(this.format.getSeparator());
        }

        line.append(this.format.format(d, percision));

        if (!columnsDefined)
        {
            this.columns.add(name);
        }
    }

    /**
     * @return The percision to use.
     */
	public int getPercision() {
		return percision;
	}

	/**
	 * Set the percision to use.
	 * @param percision The percision to use.
	 */
	public void setPercision(int percision) {
		this.percision = percision;
	}

    
	
}
