package org.encog.ml.data.versatile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.encog.EncogError;
import org.encog.ml.data.MLData;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.ml.data.versatile.missing.MissingHandler;
import org.encog.util.csv.CSVFormat;

public class NormalizationHelper {
	private List<ColumnDefinition> sourceColumns = new ArrayList<ColumnDefinition>();
	private List<ColumnDefinition> inputColumns = new ArrayList<ColumnDefinition>();
	private List<ColumnDefinition> outputColumns = new ArrayList<ColumnDefinition>();
	private NormalizationStrategy normStrategy;
	private CSVFormat format = CSVFormat.ENGLISH;
	private List<String> unknownValues = new ArrayList<String>();
	private Map<ColumnDefinition,MissingHandler> missingHandlers = new HashMap<ColumnDefinition,MissingHandler>();
	
	/**
	 * @return the sourceColumns
	 */
	public List<ColumnDefinition> getSourceColumns() {
		return sourceColumns;
	}
	/**
	 * @param sourceColumns the sourceColumns to set
	 */
	public void setSourceColumns(List<ColumnDefinition> sourceColumns) {
		this.sourceColumns = sourceColumns;
	}
	/**
	 * @return the inputColumns
	 */
	public List<ColumnDefinition> getInputColumns() {
		return inputColumns;
	}
	/**
	 * @param inputColumns the inputColumns to set
	 */
	public void setInputColumns(List<ColumnDefinition> inputColumns) {
		this.inputColumns = inputColumns;
	}
	/**
	 * @return the outputColumns
	 */
	public List<ColumnDefinition> getOutputColumns() {
		return outputColumns;
	}
	/**
	 * @param outputColumns the outputColumns to set
	 */
	public void setOutputColumns(List<ColumnDefinition> outputColumns) {
		this.outputColumns = outputColumns;
	}
	/**
	 * @return the normStrategy
	 */
	public NormalizationStrategy getNormStrategy() {
		return normStrategy;
	}
	/**
	 * @param normStrategy the normStrategy to set
	 */
	public void setNormStrategy(NormalizationStrategy normStrategy) {
		this.normStrategy = normStrategy;
	}
	
	public void addSourceColumn(ColumnDefinition def) {
		this.sourceColumns.add(def);
		def.setOwner(this);
	}
	
	public ColumnDefinition defineSourceColumn(String name, ColumnType colType) {
		ColumnDefinition result = new ColumnDefinition(name,colType);
		addSourceColumn(result);
		return result;
	}
	
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append("[NormalizationHelper:\n");
		for(ColumnDefinition colDef:this.sourceColumns) {
			result.append(colDef.toString());
			result.append("\n");
		}
		result.append("]");
		return result.toString();
	}
	public void clearInputOutput() {
		this.inputColumns.clear();
		this.outputColumns.clear();
		
	}
	public double[] normalizeInputColumn(int i, String value) {
		ColumnDefinition colDef = this.inputColumns.get(i);
		double[] result = new double[this.normStrategy.normalizedSize(colDef, true)];
		this.normStrategy.normalizeColumn(colDef, true, value, result, 0);
		return result;
	}
	
	public double[] normalizeOutputColumn(int i, String value) {
		ColumnDefinition colDef = this.outputColumns.get(i);
		double[] result = new double[this.normStrategy.normalizedSize(colDef, false)];
		this.normStrategy.normalizeColumn(colDef, false, value, result, 0);
		return result;
	}
	
	public int calculateNormalizedInputCount() {
		int normalizedInputColumns = 0;
		
		for(ColumnDefinition colDef: this.inputColumns) {
			normalizedInputColumns += this.normStrategy.normalizedSize(colDef,true);
		}
		
		return normalizedInputColumns;
	}
	
	public int calculateNormalizedOutputCount() {
		int normalizedOutputColumns = 0;
		
		for(ColumnDefinition colDef: this.outputColumns) {
			normalizedOutputColumns += this.normStrategy.normalizedSize(colDef,false);
		}
		
		return normalizedOutputColumns;
	}
	
	public MLData allocateInputVector() {
		return new BasicMLData(calculateNormalizedInputCount());
	}
	public void normalizeInputVector(String[] line, MLData inputVector, boolean originalOrder) {
		int outputIndex=0;
		int i = 0;
		for(ColumnDefinition colDef: this.inputColumns) {
			int idx;
			
			if( originalOrder ) {
				idx = this.sourceColumns.indexOf(colDef);
			} else {
				idx = i;
			}
			outputIndex = normalizeToVector(colDef, outputIndex, inputVector.getData(), false, line[idx]);
			i++;
		}
		
	}
	public String[] denormalizeOutputVectorToString(MLData output) {
		String[] result = new String[this.outputColumns.size()];
		
		int idx = 0;
		for(int i=0;i<this.outputColumns.size();i++) {
			ColumnDefinition colDef = this.outputColumns.get(i);
			result[i] = this.normStrategy.denormalizeColumn(colDef,false,output,idx);
			idx+=this.normStrategy.normalizedSize(colDef, false);
		}
		
		return result;
		
	}
	public void setStrategy(NormalizationStrategy strat) {
		this.normStrategy = strat;
	}
	/**
	 * @return the format
	 */
	public CSVFormat getFormat() {
		return format;
	}
	/**
	 * @param format the format to set
	 */
	public void setFormat(CSVFormat format) {
		this.format = format;
	}
	/**
	 * @return the unknownValues
	 */
	public List<String> getUnknownValues() {
		return unknownValues;
	}
	
	public void defineUnknownValue(String str) {
		this.unknownValues.add(str);
	}
	
	public int normalizeToVector(ColumnDefinition colDef, int outputColumn, double[] output, boolean isInput, String value) {
		MissingHandler handler = null;
		
		if( this.unknownValues.contains(value)) {
			if( !this.missingHandlers.containsKey(colDef)) {
				throw new EncogError("Do not know how to process missing value \""+value+"\" in field: " + colDef.getName() );
			}
			handler = this.missingHandlers.get(colDef);
		}
		
		if( colDef.getDataType()==ColumnType.continuous) {
			double d = parseDouble(value);
			if( handler!=null ) {
				d = handler.process(colDef,d);
			}
			return this.normStrategy.normalizeColumn(colDef, isInput, d,
					output, outputColumn);
		} else {
			if( handler!=null ) {
				value = handler.process(colDef,value);
			}
			return this.normStrategy.normalizeColumn(colDef, isInput, value,
					output, outputColumn);	
		}
	}
	
	public double parseDouble(String str) {
		return this.format.parse(str);
	}
	
	public void defineMissingHandler(ColumnDefinition colDef, MissingHandler handler) {
		this.missingHandlers.put(colDef, handler);
		handler.init(this);
	}
	
	
}
