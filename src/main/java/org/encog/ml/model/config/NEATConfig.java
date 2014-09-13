package org.encog.ml.model.config;

import org.encog.ml.data.versatile.BasicNormalizationStrategy;
import org.encog.ml.data.versatile.ColumnType;
import org.encog.ml.data.versatile.NormalizationStrategy;
import org.encog.ml.data.versatile.VersatileMLDataSet;
import org.encog.ml.data.versatile.normalizers.OneOfNNormalizer;
import org.encog.ml.data.versatile.normalizers.RangeNormalizer;
import org.encog.ml.factory.MLMethodFactory;

public class NEATConfig implements MethodConfig {
	
	@Override
	public String getMethodName() {
		return MLMethodFactory.TYPE_NEAT;
	}
	
	@Override
	public String suggestModelArchitecture(VersatileMLDataSet dataset) {
		return("cycles=4");
	}
	
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


	@Override
	public String suggestTrainingType() {
		return "neat-ga";
	}


	@Override
	public String suggestTrainingArgs(String trainingType) {
		return "";
	}
}
