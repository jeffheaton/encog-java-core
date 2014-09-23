package org.encog.ml.model.config;

import org.encog.ml.data.versatile.VersatileMLDataSet;
import org.encog.ml.data.versatile.columns.ColumnType;
import org.encog.ml.data.versatile.normalizers.OneOfNNormalizer;
import org.encog.ml.data.versatile.normalizers.RangeNormalizer;
import org.encog.ml.data.versatile.normalizers.strategies.BasicNormalizationStrategy;
import org.encog.ml.data.versatile.normalizers.strategies.NormalizationStrategy;
import org.encog.ml.factory.MLMethodFactory;

/**
 * Config class for EncogModel to use a NEAT neural network.
 */
public class NEATConfig implements MethodConfig {
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getMethodName() {
		return MLMethodFactory.TYPE_NEAT;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String suggestModelArchitecture(VersatileMLDataSet dataset) {
		return("cycles=4");
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public NormalizationStrategy suggestNormalizationStrategy(VersatileMLDataSet dataset, String architecture) {
		BasicNormalizationStrategy result = new BasicNormalizationStrategy();
		result.assignInputNormalizer(ColumnType.continuous,new RangeNormalizer(0,1));
		result.assignInputNormalizer(ColumnType.nominal,new OneOfNNormalizer(0,1));
		result.assignInputNormalizer(ColumnType.ordinal,new OneOfNNormalizer(0,1));
		
		result.assignOutputNormalizer(ColumnType.continuous,new RangeNormalizer(0,1));
		result.assignOutputNormalizer(ColumnType.nominal,new OneOfNNormalizer(0,1));
		result.assignOutputNormalizer(ColumnType.ordinal,new OneOfNNormalizer(0,1));
		return result;
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public String suggestTrainingType() {
		return "neat-ga";
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public String suggestTrainingArgs(String trainingType) {
		return "";
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int determineOutputCount(VersatileMLDataSet dataset) {
		return dataset.getNormHelper().calculateNormalizedOutputCount();
	}
}
