package org.encog.neural.networks;

import java.io.InputStream;
import java.io.OutputStream;

import org.encog.engine.network.activation.ActivationFunction;
import org.encog.engine.network.flat.FlatNetwork;
import org.encog.neural.bam.BAM;
import org.encog.persist.EncogPersistor;
import org.encog.persist.EncogWriteHelper;
import org.encog.persist.map.PersistConst;

public class PersistBasicNetwork implements EncogPersistor {

	@Override
	public String getPersistClassString() {
		return "BasicNetwork";
	}

	@Override
	public Object read(InputStream is) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void save(OutputStream os, Object obj) {
		EncogWriteHelper out = new EncogWriteHelper(os);
		BasicNetwork net = (BasicNetwork)obj;
		FlatNetwork flat = net.getStructure().getFlat();
		out.addSection("BASIC");
		out.addSubSection("PARAMS");
		out.addProperties(net.getProperties());
		out.addSubSection("NETWORK");
		//out.writeProperty(PersistConst.ACTIVATION_FUNCTION, activationFunctions);
		out.writeProperty(BasicNetwork.TAG_BEGIN_TRAINING, flat.getBeginTraining());
		out.writeProperty(BasicNetwork.TAG_CONNECTION_LIMIT, flat.getConnectionLimit());		
		out.writeProperty(BasicNetwork.TAG_CONTEXT_TARGET_OFFSET, flat.getContextTargetOffset() );
		out.writeProperty(BasicNetwork.TAG_CONTEXT_TARGET_SIZE, flat.getContextTargetSize() );
		out.writeProperty(BasicNetwork.TAG_END_TRAINING, flat.getEndTraining() );
		out.writeProperty(BasicNetwork.TAG_HAS_CONTEXT, flat.getHasContext() );
		out.writeProperty(PersistConst.INPUT_COUNT, flat.getInputCount() );
		out.writeProperty(BasicNetwork.TAG_LAYER_COUNTS, flat.getLayerCounts() );
		out.writeProperty(BasicNetwork.TAG_LAYER_FEED_COUNTS, flat.getLayerFeedCounts() );
		out.writeProperty(BasicNetwork.TAG_LAYER_CONTEXT_COUNT, flat.getLayerContextCount() );

		out.writeProperty(BasicNetwork.TAG_LAYER_INDEX, flat.getLayerIndex() );
		out.writeProperty(PersistConst.OUTPUT, flat.getLayerOutput() );
		out.writeProperty(PersistConst.OUTPUT_COUNT, flat.getOutputCount() );
		out.writeProperty(PersistConst.NEURONS, flat.getNeuronCount() );
		out.writeProperty(BasicNetwork.TAG_WEIGHT_INDEX, flat.getWeightIndex() );
		out.writeProperty(PersistConst.WEIGHTS, flat.getWeights());
		out.writeProperty(BasicNetwork.TAG_BIAS_ACTIVATION, flat.getBiasActivation());
		out.addSubSection("ACTIVATION");
		for(ActivationFunction af : flat.getActivationFunctions()) {
			out.addColumn(af.getClass().getSimpleName());
			for(int i=0;i<af.getParams().length;i++) {
				out.addColumn(af.getParams()[i]);
			}
			out.writeLine();
		}

		out.flush();		
	}

	@Override
	public int getFileVersion() {
		return 1;
	}
	
}
