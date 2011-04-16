package org.encog.neural.thermal;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import org.encog.persist.EncogFileSection;
import org.encog.persist.EncogPersistor;
import org.encog.persist.EncogReadHelper;
import org.encog.persist.EncogWriteHelper;
import org.encog.persist.PersistConst;
import org.encog.util.csv.CSVFormat;
import org.encog.util.csv.NumberList;

public class PersistHopfield implements EncogPersistor {

	@Override
	public String getPersistClassString() {
		return HopfieldNetwork.class.getSimpleName();
	}

	@Override
	public Object read(InputStream is) {
		HopfieldNetwork result = new HopfieldNetwork();
		EncogReadHelper in = new EncogReadHelper(is);
		EncogFileSection section;
		
		while( (section = in.readNextSection()) != null ) {
			if( section.getSectionName().equals("HOPFIELD") && section.getSubSectionName().equals("PARAMS") ) {
				Map<String,String> params = section.parseParams();
				result.getProperties().putAll(params);
			} if( section.getSectionName().equals("HOPFIELD") && section.getSubSectionName().equals("NETWORK") ) {
				Map<String,String> params = section.parseParams();
				result.setWeights(NumberList.fromList(CSVFormat.EG_FORMAT, params.get(PersistConst.WEIGHTS)));
				result.setCurrentState(NumberList.fromList(CSVFormat.EG_FORMAT, params.get(PersistConst.OUTPUT)));
				result.setNeuronCount(EncogFileSection.parseInt(params, PersistConst.NEURON_COUNT));
			}
		}
		 
		return result;
	}

	@Override
	public void save(OutputStream os, Object obj) {
		EncogWriteHelper out = new EncogWriteHelper(os);
		HopfieldNetwork hopfield = (HopfieldNetwork)obj;
		out.addSection("HOPFIELD");
		out.addSubSection("PARAMS");
		out.addProperties(hopfield.getProperties());
		out.addSubSection("NETWORK");
		out.writeProperty(PersistConst.WEIGHTS, hopfield.getWeights());
		out.writeProperty(PersistConst.OUTPUT, hopfield.getCurrentState().getData());
		out.writeProperty(PersistConst.NEURON_COUNT, hopfield.getNeuronCount());
		out.flush();
	}

	@Override
	public int getFileVersion() {
		return 1;
	}

}
