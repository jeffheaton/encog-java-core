package org.encog.app.quant.classify;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.encog.app.quant.QuantError;
import org.encog.engine.util.EngineArray;
import org.encog.mathutil.Equilateral;
import org.encog.util.csv.CSVFormat;
import org.encog.util.csv.ReadCSV;

/**
 * Holds stats about a field that has been classified.
 */
public class ClassifyStats {

    /**
     * @return The classes that this field can hold.
     */
    public List<ClassItem> getClasses() { return this.classes; } 

    /**
     * The index of this field.
     */
    private int classField;

    /**
     * The high-value that this field is normalized into.
     */
    private double high;

    /**
     * The low value that this field is normalized into.
     */
    private double low;

    /**
     * True, if this field is numeric.
     */
    private boolean numeric;

    /**
     * @return If equilateral classification is used, this is the Equilateral object.
     */
    public Equilateral getEquilateralEncode() { return this.eq; }

    /**
     * The classification method to use.
     */
    private ClassifyMethod method;

    /**
     * The list of classes.
     */
    private List<ClassItem> classes = new ArrayList<ClassItem>();

    /**
     * If equilateral classification is used, this is the Equilateral object.
     */
    private Equilateral eq;

    /**
     * Allows the index of a field to be looked up.
     */
    private Map<String, Integer> lookup = new HashMap<String, Integer>();

    /** 
     * @return Returns the number of columns needed for this classification.  The number
     * of columns needed will vary, depending on the classification method used.
     */
    public int getColumnsNeeded()
    {
            switch (this.method)
            {
                case Equilateral:
                    return this.classes.size() - 1;
                case OneOf:
                    return this.classes.size();
                case SingleField:
                    return 1;
                default:
                    return -1;
            }

    }

    /**
     * Init any internal structures.
     */
    public void init()
    {
        this.eq = new Equilateral(this.classes.size(), high, low);

        // build lookup map
        for (int i = 0; i < this.classes.size(); i++)
        {
            this.lookup.put(classes.get(i).getName(), classes.get(i).getIndex()) ;
        }
    }
    
    /**
     * Lookup the specified field.
     * @param str The name of the field to lookup.
     * @return The index of the field, or -1 if not found.
     */
    public int lookup(String str)
    {
        if (!this.lookup.containsKey(str))
            return -1;
        return this.lookup.get(str);
    }

    /**
     * Read the stats file from a CSV.
     * @param filename The filename to read.
     */
    public void readStatsFile(String filename)
    {
        List<ClassItem> list = new ArrayList<ClassItem>();

        ReadCSV csv = null;

        try
        {
            csv = new ReadCSV(filename, true, CSVFormat.EG_FORMAT);
            while (csv.next())
            {
                String name = csv.get(0);
                int index = Integer.parseInt(csv.get(1));
                int field = Integer.parseInt(csv.get(2));
                String method = csv.get(3);
                double high = csv.getDouble(4);
                double low = csv.getDouble(5);

                ClassItem item = new ClassItem(name, index);
                list.add(item);
                this.high = high;
                this.low = low;
                if (method.equals("o"))
                {
                    this.method = ClassifyMethod.OneOf;
                }
                else if (method.equals("e"))
                {
                    this.method = ClassifyMethod.Equilateral;
                }
                else if (method.equals("s"))
                {
                    this.method = ClassifyMethod.SingleField;
                }
            }
            csv.close();
            this.classes = list;
            init();

        }
        finally
        {
            if (csv != null)
                csv.close();
        }
    }

    /**
     * Write the stats file.
     * @param filename The filename to write.
     */
    public void writeStatsFile(String filename)
    {
    	PrintWriter tw = null;

        try
        {
        	   tw = new PrintWriter(new FileWriter(filename));


            tw.println("name,index,field,method,high,low");

            for(ClassItem item : this.classes)
            {
                StringBuilder line = new StringBuilder();

                line.append("\"");
                line.append(item.getName());
                line.append("\",");
                line.append(item.getIndex());
                line.append(",");
                line.append(this.classField);
                line.append(",");

                switch (this.method)
                {
                    case Equilateral:
                        line.append("e");
                        break;
                    case OneOf:
                        line.append("o");
                        break;
                    case SingleField:
                        line.append("s");
                        break;
                }

                line.append(',');
                line.append(this.high);
                line.append(',');
                line.append(this.low);

                tw.println(line.toString());
            }
        } catch (IOException e) {
			throw new QuantError(e);
		}
        finally
        {
            // close the stream
            if (tw != null)
                tw.close();
        }
    }

    /**
     * Determine what class the specified data belongs to.
     * @param data The data to analyze.
     * @return The class the data belongs to.
     */
    public ClassItem determineClass(double[] data)
    {
        int resultIndex = 0;

        switch (this.method)
        {
            case Equilateral:
                resultIndex = this.eq.decode(data);
                break;
            case OneOf:
                resultIndex = EngineArray.indexOfLargest(data);
                break;
            case SingleField:
                resultIndex = (int)data[0];
                break;
        }

        return this.classes.get(resultIndex);
    }

    /**
     * @return The class field index.
     */
	public int getClassField() {
		return classField;
	}

	/**
	 * Set the class field index.
	 * @param classField The class field index.
	 */
	public void setClassField(int classField) {
		this.classField = classField;
	}

	/**
	 * @return The high-range of the normalization target.
	 */
	public double getHigh() {
		return high;
	}

	/**
	 * Set the high range of the normalization target.
	 * @param high The high range of the nromalization target.
	 */
	public void setHigh(double high) {
		this.high = high;
	}

	/**
	 * @return The low range of the normalization target.
	 */
	public double getLow() {
		return low;
	}

	/**
	 * Set the low range of the normalization target.
	 * @param low
	 */
	public void setLow(double low) {
		this.low = low;
	}

	/**
	 * @return Is the underlying field numeric?
	 */
	public boolean isNumeric() {
		return numeric;
	}

	/**
	 * Set if the underlying field is numeric.
	 * @param numeric
	 */
	public void setNumeric(boolean numeric) {
		this.numeric = numeric;
	}

	/**
	 * @return The classification method.
	 */
	public ClassifyMethod getMethod() {
		return method;
	}

	/**
	 * Set the classification method.
	 * @param method The classification method.
	 */
	public void setMethod(ClassifyMethod method) {
		this.method = method;
	}

	/**
	 * @return The equalateral object.
	 */
	public Equilateral getEq() {
		return eq;
	}

	
}
