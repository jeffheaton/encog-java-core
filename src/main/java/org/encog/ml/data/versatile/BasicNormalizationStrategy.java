package org.encog.ml.data.versatile;

import java.util.HashMap;
import java.util.Map;

import org.encog.EncogError;
import org.encog.ml.data.MLData;
import org.encog.ml.data.versatile.normalizers.OneOfNNormalizer;
import org.encog.ml.data.versatile.normalizers.RangeNormalizer;

public class BasicNormalizationStrategy implements NormalizationStrategy {

	private final Map<ColumnType,Normalizer> inputNormalizers = new HashMap<ColumnType,Normalizer>();
	private final Map<ColumnType,Normalizer> outputNormalizers = new HashMap<ColumnType,Normalizer>();

	public BasicNormalizationStrategy() {
		
	}
	
	public BasicNormalizationStrategy(double inputLow, double inputHigh, double outputLow, double outputHigh) {
		assignInputNormalizer(ColumnType.continuous,new RangeNormalizer(inputLow,inputHigh));
		assignInputNormalizer(ColumnType.nominal,new OneOfNNormalizer(inputLow,inputHigh));
		assignInputNormalizer(ColumnType.ordinal,new OneOfNNormalizer(inputLow,inputHigh));
		
		assignOutputNormalizer(ColumnType.continuous,new RangeNormalizer(outputLow,outputHigh));
		assignOutputNormalizer(ColumnType.nominal,new OneOfNNormalizer(outputLow,outputHigh));
		assignOutputNormalizer(ColumnType.ordinal,new OneOfNNormalizer(outputLow,outputHigh));
	}
	
	public void assignInputNormalizer(ColumnType colType, Normalizer norm) {
		this.inputNormalizers.put(colType, norm);
	}
	
	public void assignOutputNormalizer(ColumnType colType, Normalizer norm) {
		this.outputNormalizers.put(colType, norm);
	}
	
	@Override
	public int calculateTotalRows(int analyzedRows) {
		return analyzedRows;
	}
	
	private Normalizer findNormalizer(ColumnDefinition colDef, boolean isInput) {
		Normalizer norm = null;
		
		if(isInput) {
			if( this.inputNormalizers.containsKey(colDef.getDataType())) {
				norm = this.inputNormalizers.get(colDef.getDataType());
			}
		} else {
			if( this.outputNormalizers.containsKey(colDef.getDataType())) {
				norm = this.outputNormalizers.get(colDef.getDataType());
			}
		}
		
		if( norm==null ) {
			throw new EncogError("No normalizer defined for input="+isInput+", type=" + colDef.getDataType());
		}
		return norm;
	}

	@Override
	public int normalizedSize(ColumnDefinition colDef, boolean isInput) {
		Normalizer norm = findNormalizer(colDef,isInput);
		return norm.outputSize(colDef);
	}

	@Override
	public int normalizeColumn(ColumnDefinition colDef, boolean isInput,
			String value, double[] outputData, int outputColumn) {
		Normalizer norm = findNormalizer(colDef,isInput);
		return norm.normalizeColumn(colDef,value,outputData,outputColumn);
	}

	/**
	 * @return the inputNormalizers
	 */
	public Map<ColumnType, Normalizer> getInputNormalizers() {
		return inputNormalizers;
	}

	/**
	 * @return the outputNormalizers
	 */
	public Map<ColumnType, Normalizer> getOutputNormalizers() {
		return outputNormalizers;
	}

	@Override
	public String denormalizeColumn(ColumnDefinition colDef, boolean isInput,
			MLData data, int dataColumn) {
		Normalizer norm = findNormalizer(colDef,isInput);
		return norm.denormalizeColumn(colDef,data,dataColumn);
	}
	
	

}
