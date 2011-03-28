package org.encog.neural.som;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import org.encog.persist.EncogFileSection;
import org.encog.persist.EncogPersistor;
import org.encog.persist.EncogReadHelper;
import org.encog.persist.EncogWriteHelper;
import org.encog.persist.PersistConst;

public class PersistSOM implements EncogPersistor {

	@Override
	public String getPersistClassString() {
		return "SOM";
	}

	@Override
	public Object read(InputStream is) {
		SOM result = new SOM();
		EncogReadHelper in = new EncogReadHelper(is);
		EncogFileSection section;
		
		while( (section = in.readNextSection()) != null ) {
			if( section.getSectionName().equals("SOM") && section.getSubSectionName().equals("PARAMS") ) {
				Map<String,String> params = section.parseParams();
				result.getProperties().putAll(params);
			} if( section.getSectionName().equals("SOM") && section.getSubSectionName().equals("NETWORK") ) {
				Map<String,String> params = section.parseParams();
				result.setWeights(EncogFileSection.parseMatrix(params,PersistConst.WEIGHTS));
				result.setOutputNeuronCount(EncogFileSection.parseInt(params,PersistConst.OUTPUT_COUNT));
				result.setInputCount(EncogFileSection.parseInt(params,PersistConst.INPUT_COUNT));
			}
		}
		 
		return result;
	}

	@Override
	public void save(OutputStream os, Object obj) {
		EncogWriteHelper out = new EncogWriteHelper(os);
		SOM som = (SOM)obj;
		out.addSection("SOM");
		out.addSubSection("PARAMS");
		out.addProperties(som.getProperties());
		out.addSubSection("NETWORK");
		out.writeProperty(PersistConst.WEIGHTS, som.getWeights());
		out.writeProperty(PersistConst.INPUT_COUNT, som.getInputCount());
		out.writeProperty(PersistConst.OUTPUT_COUNT, som.getOutputNeuronCount());
		out.flush();
	}

	@Override
	public int getFileVersion() {
		return 1;
	}
	
}
