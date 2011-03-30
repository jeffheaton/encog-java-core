package org.encog.app.quant.temporal;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import org.encog.EncogError;
import org.encog.app.quant.QuantError;
import org.encog.app.quant.basic.BasicFile;
import org.encog.app.quant.util.BarBuffer;
import org.encog.util.csv.CSVFormat;
import org.encog.util.csv.ReadCSV;

/**
 * This class is used to break a CSV file into temporal windows.  This is used for 
 * predictive neural networks.
 */
public class TemporalWindowCSV extends BasicFile {

    /**
     * The size of the input window.
     */
    private int inputWindow;

    /**
     * The size of the prediction window.
     */
    private int predictWindow;

    /**
     * @return The fields that are to be processed.
     */
    public TemporalWindowField[] getFields()
    {
            return this.fields;     
    }

    /**
     * The fields that are to be processed.
     */
    private TemporalWindowField[] fields;

    /**
     * A buffer to hold the data.
     */
    private BarBuffer buffer;


    /**
     * Construct the object and set the defaults.
     */
    public TemporalWindowCSV()
    {
    	super();
        inputWindow = 10;
        predictWindow = 1;
    }
    
    /**
     * Format the headings to a string.
     * @return The a string holding the headers, ready to be written.
     */
    private String writeHeaders()
    {
        StringBuilder line = new StringBuilder();

        // write any passthrough fields
        for(TemporalWindowField field : this.fields)
        {
            if (field.getAction() == TemporalType.PassThrough)
            {
                if (line.length() > 0)
                {
                    line.append(this.getInputFormat().getSeparator());
                }

                line.append(field.getName());
            }
        }

        // write any input fields
        for (int i = 0; i < this.inputWindow; i++)
        {
            for(TemporalWindowField field : this.fields)
            {
                if (field.getInput())
                {
                    if (line.length() > 0)
                    {
                        line.append(",");
                    }

                    line.append("Input:");
                    line.append(field.getName());

                    if (i > 0)
                    {
                        line.append("(t-");
                        line.append(i);
                        line.append(")");
                    }
                    else
                    {
                        line.append("(t)");
                    }
                }
            }
        }

        // write any output fields
        for (int i = 0; i < this.predictWindow; i++)
        {
            for(TemporalWindowField field : this.fields)
            {
                if (field.getPredict() )
                {
                    if (line.length() > 0)
                    {
                        line.append(",");
                    }

                    line.append("Predict:");
                    line.append(field.getName());

                    line.append("(t+");
                    line.append(i + 1);
                    line.append(")");
                }

            }
        }

        return line.toString();
    }

    /**
     * Process the input file, and write to the output file.
     * @param outputFile The output file.
     */
    public void process(File outputFile)
    {
        if (inputWindow < 1)
        {
            throw new EncogError("Input window must be greater than one.");
        }

        if (predictWindow < 1)
        {
            throw new EncogError("Predict window must be greater than one.");
        }

        int inputFieldCount = countInputFields();
        int predictFieldCount = countPredictFields();

        if (inputFieldCount < 1)
        {
            throw new EncogError("There must be at least 1 input field.");
        }

        if (predictFieldCount < 1)
        {
            throw new EncogError("There must be at least 1 input field.");
        }

        int barSize = inputFieldCount + predictFieldCount;

        buffer = new BarBuffer(inputWindow + predictWindow);

        ReadCSV csv = null;
        PrintWriter tw = null;

        try
        {
            csv = new ReadCSV(this.getInputFilename().toString(), this.isExpectInputHeaders(), this.getInputFormat());

            tw = new PrintWriter(new FileWriter(outputFile));

            // write headers, if needed
            if (this.isExpectInputHeaders() )
            {
                tw.println(writeHeaders());
            }

            resetStatus();
            while (csv.next()&& !this.shouldStop())
            {
                updateStatus(false);
                // begin to populate the bar
                double[] bar = new double[barSize];

                int fieldIndex = 0;
                int barIndex = 0;
                for(TemporalWindowField field : this.fields)
                {
                    String str = csv.get(fieldIndex++);

                    if (field.getAction() != TemporalType.Ignore && field.getAction()!=TemporalType.PassThrough)
                    {
                        bar[barIndex++] = this.getInputFormat().parse(str);
                    }
                    field.setLastValue(str);
                }
                buffer.add(bar);

                // if the buffer is full, begin writing out temporal windows
                if (buffer.getFull())
                {
                    StringBuilder line = new StringBuilder();
                    
                    // write passthroughs
                    for(TemporalWindowField field : this.fields)
                    {
                        if (field.getAction() == TemporalType.PassThrough)
                        {
                            if (line.length() > 0)
                            {
                                line.append(",");
                            }

                            line.append("\"");
                            line.append(field.getLastValue());
                            line.append("\"");
                        }
                    }

                    // write input
                    for (int i = 0; i < this.inputWindow; i++)
                    {
                        bar = buffer.getData().get(buffer.getData().size() - 1 - i);

                        int index = 0;
                        for(TemporalWindowField field : this.fields)
                        {
                            if (field.getInput() )
                            {
                                if (line.length() > 0)
                                    line.append(',');
                                line.append(this.getInputFormat().format(bar[index], precision));
                                index++;
                            }
                        }
                    }

                    // write prediction
                    for (int i = 0; i < this.predictWindow; i++)
                    {
                        bar = buffer.getData().get(predictWindow - 1 - i);

                        int index = 0;
                        for(TemporalWindowField field : this.fields)
                        {
                            if (field.getPredict())
                            {
                                if (line.length() > 0)
                                    line.append(getInputFormat().getSeparator());
                                line.append(this.getInputFormat().format(bar[index], precision));
                                index++;
                            }
                        }
                    }

                    // write the line
                    tw.println(line.toString());
                }
            }
        } catch (IOException e) {
			throw new QuantError(e);
		}
        finally
        {
            reportDone(false);
            if (csv != null)
            {
                try
                {
                    csv.close();
                }
                catch (Exception ex)
                {
                }
            }

            if (tw != null)
            {
                try
                {
                    tw.close();
                }
                catch (Exception ex)
                {
                }
            }
        }


    }

    /**
     * Count the number of fields that are that are in the prediction.
     * @return The number of fields predicted.
     */
    public int countPredictFields()
    {
        int result = 0;

        for(TemporalWindowField field : this.fields)
        {
            if (field.getPredict())
                result++;
        }

        return result;
    }

    /**
     * Count the number of input fields, or fields used to predict.
     * @return The number of input fields.
     */
    public int countInputFields()
    {
        int result = 0;

        for (TemporalWindowField field : this.fields)
        {
            if (field.getInput())
                result++;
        }

        return result;
    }

    /**
     * Analyze the input file, prior to processing.
     * @param filename The filename to process.
     * @param headers True, if the input file has headers.
     * @param format The format of the input file.
     */
    public void analyze(File filename, boolean headers, CSVFormat format)
    {
        ReadCSV csv = null;

        try
        {
            csv = new ReadCSV(filename.toString(), headers, format);
            if (!csv.next()&& !this.shouldStop())
            {
                throw new EncogError("Empty file");
            }

            this.setInputFilename( filename);
            this.setExpectInputHeaders( headers);
            this.setInputFormat(format);

            this.fields = new TemporalWindowField[csv.getColumnCount()];
            double d;

            for (int i = 0; i < csv.getColumnCount(); i++)
            {
                String str = csv.get(i);
                String fieldname;

                if (this.isExpectInputHeaders())
                {
                    fieldname = csv.getColumnNames().get(i);
                }
                else
                {
                    fieldname = "Column-" + i;
                }

                this.fields[i] = new TemporalWindowField(fieldname);
                try
                {
                	Double.parseDouble(str);
                	this.fields[i].setAction( TemporalType.Input);
                }
                catch(NumberFormatException ex)
                {
                	
                }
            }
        }
        finally
        {
            if (csv != null)
            {
                csv.close();
            }
        }
    }

	/**
	 * @return the inputWindow
	 */
	public int getInputWindow() {
		return inputWindow;
	}

	/**
	 * @param inputWindow the inputWindow to set
	 */
	public void setInputWindow(int inputWindow) {
		this.inputWindow = inputWindow;
	}

	/**
	 * @return the predictWindow
	 */
	public int getPredictWindow() {
		return predictWindow;
	}

	/**
	 * @param predictWindow the predictWindow to set
	 */
	public void setPredictWindow(int predictWindow) {
		this.predictWindow = predictWindow;
	}

	/**
	 * @return the buffer
	 */
	public BarBuffer getBuffer() {
		return buffer;
	}

    
	
}
