/*
 * Encog(tm) Core v3.3 - Java Version
 * http://www.heatonresearch.com/encog/
 * https://github.com/encog/encog-java-core
 
 * Copyright 2008-2014 Heaton Research, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *   
 * For more information on Heaton Research copyrights, licenses 
 * and trademarks visit:
 * http://www.heatonresearch.com/copyright
 */
package org.encog.ml.data.versatile.normalizers.strategies;

import java.util.HashMap;
import java.util.Map;

import org.encog.EncogError;
import org.encog.ml.data.MLData;
import org.encog.ml.data.versatile.columns.ColumnDefinition;
import org.encog.ml.data.versatile.columns.ColumnType;
import org.encog.ml.data.versatile.normalizers.Normalizer;
import org.encog.ml.data.versatile.normalizers.OneOfNNormalizer;
import org.encog.ml.data.versatile.normalizers.RangeNormalizer;
import org.encog.ml.data.versatile.normalizers.RangeOrdinal;

/**
 * Provides a basic normalization strategy that will work with most models built into Encog.
 * This is often used as a starting point for building more customized models, as this 
 * normalizer works mainly by using maps to define which normalizer to use for what 
 * data type.
 */
public class BasicNormalizationStrategy implements NormalizationStrategy {
	private static final long serialVersionUID = 1L;

	/**
	 * Mapping to all of the input normalizers.
	 */
	private final Map<ColumnType,Normalizer> inputNormalizers = new HashMap<ColumnType,Normalizer>();
	
	/**
	 * Mapping to all of the output normalizers.
	 */
	private final Map<ColumnType,Normalizer> outputNormalizers = new HashMap<ColumnType,Normalizer>();
	
	/**
	 * Construct the basic normalization strategy.
	 * @param inputLow The desired low to normalize input into.
	 * @param inputHigh The desired high to normalize input into.
	 * @param outputLow The desired low to normalize output into.
	 * @param outputHigh The desired high to normalize output into.
	 */
	public BasicNormalizationStrategy(double inputLow, double inputHigh, double outputLow, double outputHigh) {
		assignInputNormalizer(ColumnType.continuous,new RangeNormalizer(inputLow,inputHigh));
		assignInputNormalizer(ColumnType.nominal,new OneOfNNormalizer(inputLow,inputHigh));
		assignInputNormalizer(ColumnType.ordinal,new RangeOrdinal(inputLow,inputHigh));
		
		assignOutputNormalizer(ColumnType.continuous,new RangeNormalizer(outputLow,outputHigh));
		assignOutputNormalizer(ColumnType.nominal,new OneOfNNormalizer(outputLow,outputHigh));
		assignOutputNormalizer(ColumnType.ordinal,new RangeOrdinal(outputLow,outputHigh));
	}
	
	/**
	 * Default constructor.
	 */
	public BasicNormalizationStrategy() {
	}

	@Override
	public boolean equals(Object obj) { 
		boolean result;
		
		if ( obj instanceof BasicNormalizationStrategy ) {
			BasicNormalizationStrategy that = (BasicNormalizationStrategy) obj;
			
			result = this.inputNormalizers.equals( that.inputNormalizers )
					&& this.outputNormalizers.equals( that.outputNormalizers );
		
		} else {
			result = false;
		}
		
		return result;
	}
	
	/**
	 * Assign a normalizer to the specified column type for output.
	 * @param colType The column type.
	 * @param norm The normalizer.
	 */
	public void assignInputNormalizer(ColumnType colType, Normalizer norm) {
		this.inputNormalizers.put(colType, norm);
	}
	
	/**
	 * Assign a normalizer to the specified column type for output.
	 * @param colType The column type.
	 * @param norm The normalizer.
	 */
	public void assignOutputNormalizer(ColumnType colType, Normalizer norm) {
		this.outputNormalizers.put(colType, norm);
	}
	
	/**
	 * Find a normalizer for the specified column definition, and if it is input or output.
	 * @param colDef The column definition.
	 * @param isInput True if the column is input.
	 * @return The normalizer to use.
	 */
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

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int normalizedSize(ColumnDefinition colDef, boolean isInput) {
		Normalizer norm = findNormalizer(colDef,isInput);
		return norm.outputSize(colDef);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int normalizeColumn(ColumnDefinition colDef, boolean isInput,
			String value, double[] outputData, int outputColumn) {
		Normalizer norm = findNormalizer(colDef,isInput);
		return norm.normalizeColumn(colDef,value,outputData,outputColumn);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int normalizeColumn(ColumnDefinition colDef, boolean isInput,
			double value, double[] outputData, int outputColumn) {
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

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String denormalizeColumn(ColumnDefinition colDef, boolean isInput,
			MLData data, int dataColumn) {
		Normalizer norm = findNormalizer(colDef,isInput);
		return norm.denormalizeColumn(colDef,data,dataColumn);
	}
}
