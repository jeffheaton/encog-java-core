package org.encog.neural.thermal;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import org.encog.persist.EncogFileSection;
import org.encog.persist.EncogPersistor;
import org.encog.persist.EncogReadHelper;
import org.encog.persist.EncogWriteHelper;
import org.encog.persist.map.PersistConst;
import org.encog.util.csv.CSVFormat;
import org.encog.util.csv.NumberList;

public class PersistBoltzmann implements EncogPersistor {

	@Override
	public String getPersistClassString() {
		return "BoltzmannMachine";
	}

	@Override
	public Object read(InputStream is) {
		BoltzmannMachine result = new BoltzmannMachine();
		EncogReadHelper in = new EncogReadHelper(is);
		EncogFileSection section;
		
		while( (section = in.readNextSection()) != null ) {
			if( section.getSectionName().equals("BOLTZMANN") && section.getSubSectionName().equals("PARAMS") ) {
				Map<String,String> params = section.parseParams();
				result.getProperties().putAll(params);
			} if( section.getSectionName().equals("BOLTZMANN") && section.getSubSectionName().equals("NETWORK") ) {
				Map<String,String> params = section.parseParams();
				result.setWeights(NumberList.fromList(CSVFormat.EG_FORMAT, params.get(PersistConst.WEIGHTS)));
				result.setCurrentState(NumberList.fromList(CSVFormat.EG_FORMAT, params.get(PersistConst.OUTPUT)));
				result.setNeuronCount(EncogFileSection.parseInt(params, PersistConst.NEURON_COUNT));
				
				result.setThreshold(NumberList.fromList(CSVFormat.EG_FORMAT, params.get(PersistConst.THRESHOLDS)));
				result.setAnnealCycles(EncogFileSection.parseInt(params, BoltzmannMachine.ANNEAL_CYCLES));
				result.setRunCycles(EncogFileSection.parseInt(params, BoltzmannMachine.RUN_CYCLES) );
				result.setTemperature(EncogFileSection.parseDouble(params, PersistConst.TEMPERATURE));				
			}
		}
		 
		return result;
	}

	@Override
	public void save(OutputStream os, Object obj) {
		EncogWriteHelper out = new EncogWriteHelper(os);
		BoltzmannMachine boltz = (BoltzmannMachine)obj;
		out.addSection("BOLTZMANN");
		out.addSubSection("PARAMS");
		out.addProperties(boltz.getProperties());
		out.addSubSection("NETWORK");
		out.writeProperty(PersistConst.WEIGHTS, boltz.getWeights());
		out.writeProperty(PersistConst.OUTPUT, boltz.getCurrentState().getData());
		out.writeProperty(PersistConst.NEURON_COUNT, boltz.getNeuronCount());
		
		out.writeProperty(PersistConst.THRESHOLDS,boltz.getThreshold()); 
		out.writeProperty(BoltzmannMachine.ANNEAL_CYCLES,boltz.getAnnealCycles());
		out.writeProperty(BoltzmannMachine.RUN_CYCLES,boltz.getRunCycles()); 
		out.writeProperty(PersistConst.TEMPERATURE,boltz.getTemperature()); 
		
		
		out.flush();
		
	}

	@Override
	public int getFileVersion() {
		return 1;
	}

}
