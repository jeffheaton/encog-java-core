package org.encog.ml.model.config;

import org.encog.ml.data.versatile.BasicNormalizationStrategy;
import org.encog.ml.data.versatile.ColumnType;
import org.encog.ml.data.versatile.NormalizationStrategy;
import org.encog.ml.data.versatile.VersatileMLDataSet;
import org.encog.ml.data.versatile.normalizers.OneOfNNormalizer;
import org.encog.ml.data.versatile.normalizers.RangeNormalizer;
import org.encog.ml.factory.MLMethodFactory;

public class RBFNetworkConfig implements MethodConfig {
	
	@Override
	public String getMethodName() {
		return MLMethodFactory.TYPE_RBFNETWORK;
	}
	
	@Override
	public String suggestModelArchitecture(VersatileMLDataSet dataset) {
		int inputColumns = dataset.getNormHelper().getInputColumns().size();
		int outputColumns = dataset.getNormHelper().getOutputColumns().size();
		int hiddenCount = (int) ((double)(inputColumns+outputColumns) * 1.5);
		StringBuilder result = new StringBuilder();
		
		result.append("?->gaussian(c=");
		result.append(hiddenCount);
		result.append(")->?");
		return result.toString();
	}
	
	@Override
	public NormalizationStrategy suggestNormalizationStrategy(VersatileMLDataSet dataset, String architecture) {
		int outputColumns = dataset.getNormHelper().getOutputColumns().size();

		ColumnType ct = dataset.getNormHelper().getOutputColumns().get(0).getDataType();
		
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
		return "rprop";
	}


	@Override
	public String suggestTrainingArgs(String trainingType) {
		return "";
	}
}
