package org.encog.neural.pnn;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import org.encog.neural.thermal.HopfieldNetwork;
import org.encog.persist.EncogFileSection;
import org.encog.persist.EncogPersistor;
import org.encog.persist.EncogReadHelper;
import org.encog.persist.EncogWriteHelper;
import org.encog.persist.PersistConst;
import org.encog.util.csv.CSVFormat;
import org.encog.util.csv.NumberList;

public class PersistBasicPNN implements EncogPersistor {

	@Override
	public String getPersistClassString() {
		return "BasicPNN";
	}

	@Override
	public Object read(InputStream is) {
		
		EncogReadHelper in = new EncogReadHelper(is);
		EncogFileSection section;
		
		while( (section = in.readNextSection()) != null ) {
			if( section.getSectionName().equals("PNN") && section.getSubSectionName().equals("PARAMS") ) {
				Map<String,String> params = section.parseParams();
				//result.getProperties().putAll(params);
			} if( section.getSectionName().equals("PNN") && section.getSubSectionName().equals("NETWORK") ) {
				Map<String,String> params = section.parseParams();
				//result.setWeights(NumberList.fromList(CSVFormat.EG_FORMAT, params.get(PersistConst.WEIGHTS)));
				//result.setCurrentState(NumberList.fromList(CSVFormat.EG_FORMAT, params.get(PersistConst.OUTPUT)));
				//result.setNeuronCount(EncogFileSection.parseInt(params, PersistConst.NEURON_COUNT));
			}
		}
		
		BasicPNN result = null;// new BasicPNN();
		
		return result;
	}

	@Override
	public void save(OutputStream os, Object obj) {
		EncogWriteHelper out = new EncogWriteHelper(os);
		BasicPNN pnn = (BasicPNN)obj;
		out.addSection("PNN");
		out.addSubSection("PARAMS");
		out.addProperties(pnn.getProperties());
		out.addSubSection("NETWORK");
		//out.writeProperty(PersistConst.WEIGHTS, hopfield.getWeights());
		//out.writeProperty(PersistConst.OUTPUT, hopfield.getCurrentState().getData());
		//out.writeProperty(PersistConst.NEURON_COUNT, hopfield.getNeuronCount());
		out.flush();
	}


	@Override
	public int getFileVersion() {
		return 1;
	}

}
