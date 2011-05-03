package org.encog.neural.networks;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import org.encog.engine.network.activation.ActivationFunction;
import org.encog.neural.flat.FlatNetwork;
import org.encog.persist.EncogFileSection;
import org.encog.persist.EncogPersistor;
import org.encog.persist.EncogReadHelper;
import org.encog.persist.EncogWriteHelper;
import org.encog.persist.PersistConst;
import org.encog.persist.PersistError;
import org.encog.util.csv.CSVFormat;

public class PersistBasicNetwork implements EncogPersistor {

	@Override
	public String getPersistClassString() {
		return "BasicNetwork";
	}

	@Override
	public Object read(InputStream is) {
		BasicNetwork result = new BasicNetwork();
		FlatNetwork flat = new FlatNetwork();
		EncogReadHelper in = new EncogReadHelper(is);
		EncogFileSection section;
		
		while( (section = in.readNextSection()) != null ) {
			if( section.getSectionName().equals("BASIC") && section.getSubSectionName().equals("PARAMS") ) {
				Map<String,String> params = section.parseParams();
				result.getProperties().putAll(params);
			} if( section.getSectionName().equals("BASIC") && section.getSubSectionName().equals("NETWORK") ) {
				Map<String,String> params = section.parseParams();
				
				flat.setBeginTraining(EncogFileSection.parseInt(params, BasicNetwork.TAG_BEGIN_TRAINING));					
				flat.setConnectionLimit( EncogFileSection.parseDouble(params, BasicNetwork.TAG_CONNECTION_LIMIT));		
				flat.setContextTargetOffset( EncogFileSection.parseIntArray(params, BasicNetwork.TAG_CONTEXT_TARGET_OFFSET));
				flat.setContextTargetSize(  EncogFileSection.parseIntArray(params, BasicNetwork.TAG_CONTEXT_TARGET_SIZE));
				flat.setEndTraining( EncogFileSection.parseInt(params, BasicNetwork.TAG_END_TRAINING));
				flat.setHasContext( EncogFileSection.parseBoolean(params, BasicNetwork.TAG_HAS_CONTEXT));
				flat.setInputCount( EncogFileSection.parseInt(params, PersistConst.INPUT_COUNT));
				flat.setLayerCounts( EncogFileSection.parseIntArray(params, BasicNetwork.TAG_LAYER_COUNTS));
				flat.setLayerFeedCounts( EncogFileSection.parseIntArray(params, BasicNetwork.TAG_LAYER_FEED_COUNTS));
				flat.setLayerContextCount( EncogFileSection.parseIntArray(params, BasicNetwork.TAG_LAYER_CONTEXT_COUNT));
				flat.setLayerIndex( EncogFileSection.parseIntArray(params, BasicNetwork.TAG_LAYER_INDEX));
				flat.setLayerOutput( EncogFileSection.parseDoubleArray(params, PersistConst.OUTPUT));
				flat.setOutputCount( EncogFileSection.parseInt(params, PersistConst.OUTPUT_COUNT));
				flat.setWeightIndex( EncogFileSection.parseIntArray(params, BasicNetwork.TAG_WEIGHT_INDEX));
				flat.setWeights( EncogFileSection.parseDoubleArray(params,  PersistConst.WEIGHTS));
				flat.setBiasActivation( EncogFileSection.parseDoubleArray(params,  BasicNetwork.TAG_BIAS_ACTIVATION));
			} 
			else if( section.getSectionName().equals("BASIC") && section.getSubSectionName().equals("ACTIVATION") ) {
				int index = 0;
				
				flat.setActivationFunctions(new ActivationFunction[flat.getLayerCounts().length]);
				 
				for(String line : section.getLines()) {
					ActivationFunction af =  null;
					List<String> cols = EncogFileSection.splitColumns(line);
					String name = "org.encog.neural.activation." + cols.get(0);
					try {
						Class<?> clazz = Class.forName(name);
						af = (ActivationFunction) clazz.newInstance();
					} catch (ClassNotFoundException e) {
						throw new PersistError(e);
					} catch (InstantiationException e) {
						throw new PersistError(e);
					} catch (IllegalAccessException e) {
						throw new PersistError(e);
					}
					
					for(int i=0;i<af.getParamNames().length;i++) {
						af.setParam(i, CSVFormat.EG_FORMAT.parse(cols.get(i+1)));
					}
					
					flat.getActivationFunctions()[index++] = af;
				}
			}
		} 
		
		result.getStructure().setFlat(flat);
		 
		return result;
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
