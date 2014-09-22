package org.encog.ml.model.config;

import org.encog.EncogError;
import org.encog.ml.data.versatile.VersatileMLDataSet;
import org.encog.ml.data.versatile.columns.ColumnType;
import org.encog.ml.data.versatile.normalizers.IndexedNormalizer;
import org.encog.ml.data.versatile.normalizers.OneOfNNormalizer;
import org.encog.ml.data.versatile.normalizers.RangeNormalizer;
import org.encog.ml.data.versatile.normalizers.strategies.BasicNormalizationStrategy;
import org.encog.ml.data.versatile.normalizers.strategies.NormalizationStrategy;
import org.encog.ml.factory.MLMethodFactory;
import org.encog.ml.factory.MLTrainFactory;

public class PNNConfig implements MethodConfig {
	
	@Override
	public String getMethodName() {
		return MLMethodFactory.TYPE_PNN;
	}
	
	@Override
	public String suggestModelArchitecture(VersatileMLDataSet dataset) {
		return ("?->C(kernel=gaussian)->?");
	}
	
	@Override
	public NormalizationStrategy suggestNormalizationStrategy(VersatileMLDataSet dataset, String architecture) {
		int outputColumns = dataset.getNormHelper().getOutputColumns().size();
		
		if( outputColumns>1 ) {
			throw new EncogError("PNN does not support multiple output columns.");
		}
		
		ColumnType ct = dataset.getNormHelper().getOutputColumns().get(0).getDataType();
		
		BasicNormalizationStrategy result = new BasicNormalizationStrategy();
		result.assignInputNormalizer(ColumnType.continuous,new RangeNormalizer(0,1));
		result.assignInputNormalizer(ColumnType.nominal,new OneOfNNormalizer(0,1));
		result.assignInputNormalizer(ColumnType.ordinal,new OneOfNNormalizer(0,1));
		
		result.assignOutputNormalizer(ColumnType.continuous,new RangeNormalizer(0,1));
		result.assignOutputNormalizer(ColumnType.nominal,new IndexedNormalizer());
		result.assignOutputNormalizer(ColumnType.ordinal,new OneOfNNormalizer(0,1));
		return result;
	}


	@Override
	public String suggestTrainingType() {
		return MLTrainFactory.TYPE_PNN;
	}


	@Override
	public String suggestTrainingArgs(String trainingType) {
		return "";
	}
	
	@Override
	public int determineOutputCount(VersatileMLDataSet dataset) {
		return dataset.getNormHelper().getOutputColumns().get(0).getClasses().size();
	}
}
