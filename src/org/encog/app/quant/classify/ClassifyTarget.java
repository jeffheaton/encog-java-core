package org.encog.app.quant.classify;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.encog.Encog;
import org.encog.engine.util.EngineArray;
import org.encog.mathutil.Equilateral;
import org.encog.util.csv.NumberList;

public class ClassifyTarget {
    /**
     * @return The classes that this field can hold.
     */
    public List<ClassItem> getClasses() { return this.classes; } 

    /**
     * The index of this field.
     */
    private int targetIndex;

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
    
    private int insertAt;
    private String originalName;
    private boolean inserted;

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
    
    private final ClassifyStats owner;
    
    public ClassifyTarget(ClassifyStats owner) {
    	this.owner = owner;
    }

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

	public void add(ClassItem item) {
		this.classes.add(item);
		
	}

	/**
	 * @return the targetIndex
	 */
	public int getTargetIndex() {
		return targetIndex;
	}

	/**
	 * @param targetIndex the targetIndex to set
	 */
	public void setTargetIndex(int targetIndex) {
		this.targetIndex = targetIndex;
	}
	
	/**
	 * Perform the encoding for "one of".
	 * @param classNumber The class number.
	 * @return The encoded columns.
	 */
	public String encodeOneOf(int classNumber) {
		StringBuilder result = new StringBuilder();
		for (int i = 0; i < this.classes.size(); i++) {
			if (i > 0) {
				result.append(this.owner.getFormat().getSeparator());
			}

			if (i == classNumber) {
				result.append(this.high);
			} else {
				result.append(this.low);
			}
		}
		return result.toString();
	}

	/**
	 * Perform an equilateral encode.
	 * @param classNumber The class number.
	 * @return The class to encode.
	 */
	public String encodeEquilateral(int classNumber) {
		StringBuilder result = new StringBuilder();
		double[] d = this.eq.encode(classNumber);
		NumberList
				.toList(this.getOwner().getFormat(), this.owner.getPrecision(), result, d);
		return result.toString();
	}
	
	/**
	 * Encode a single field.
	 * @param classNumber The class number to encode.
	 * @return The encoded columns.
	 */
	public String encodeSingleField(int classNumber) {
		StringBuilder result = new StringBuilder();
		result.append(classNumber);
		return result.toString();
	}

	/**
	 * Encode the class.
	 * @param method The encoding method.
	 * @param classNumber The class number.
	 * @return The encoded class.
	 */
	public String encode(int classNumber) {
		switch (this.method) {
		case OneOf:
			return encodeOneOf(classNumber);
		case Equilateral:
			return encodeEquilateral(classNumber);
		case SingleField:
			return encodeSingleField(classNumber);
		default:
			return null;
		}
	}

	/**
	 * @return the owner
	 */
	public ClassifyStats getOwner() {
		return owner;
	}

	/**
	 * @return the insertAt
	 */
	public int getInsertAt() {
		return insertAt;
	}

	/**
	 * @param insertAt the insertAt to set
	 */
	public void setInsertAt(int insertAt) {
		this.insertAt = insertAt;
	}

	/**
	 * @return the originalName
	 */
	public String getOriginalName() {
		return originalName;
	}

	/**
	 * @param originalName the originalName to set
	 */
	public void setOriginalName(String originalName) {
		this.originalName = originalName;
	}

	/**
	 * @return the inserted
	 */
	public boolean isInserted() {
		return inserted;
	}

	/**
	 * @param inserted the inserted to set
	 */
	public void setInserted(boolean inserted) {
		this.inserted = inserted;
	}

	

}
