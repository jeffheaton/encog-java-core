package org.encog.neural.bam;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import org.encog.neural.art.ART1;
import org.encog.persist.EncogFileSection;
import org.encog.persist.EncogPersistor;
import org.encog.persist.EncogReadHelper;
import org.encog.persist.EncogWriteHelper;
import org.encog.persist.map.PersistConst;

public class PersistBAM implements EncogPersistor {

	@Override
	public String getPersistClassString() {
		return "BAM";
	}

	@Override
	public Object read(InputStream is) {
		BAM result = new BAM();
		EncogReadHelper in = new EncogReadHelper(is);
		EncogFileSection section;
		
		while( (section = in.readNextSection()) != null ) {
			if( section.getSectionName().equals("BAM") && section.getSubSectionName().equals("PARAMS") ) {
				Map<String,String> params = section.parseParams();
				result.getProperties().putAll(params);
			} if( section.getSectionName().equals("BAM") && section.getSubSectionName().equals("NETWORK") ) {
				Map<String,String> params = section.parseParams();
				
				result.setF1Count(EncogFileSection.parseInt(params, PersistConst.PROPERTY_F1_COUNT));
				result.setF2Count(EncogFileSection.parseInt(params, PersistConst.PROPERTY_F2_COUNT));
				result.setWeightsF1toF2(EncogFileSection.parseMatrix(params, PersistConst.PROPERTY_WEIGHTS_F1_F2));
				result.setWeightsF2toF1(EncogFileSection.parseMatrix(params, PersistConst.PROPERTY_WEIGHTS_F2_F1));			
			}
		}
		 
		return result;

	}

	@Override
	public void save(OutputStream os, Object obj) {
		EncogWriteHelper out = new EncogWriteHelper(os);
		BAM bam = (BAM)obj;
		out.addSection("BAM");
		out.addSubSection("PARAMS");
		out.addProperties(bam.getProperties());
		out.addSubSection("NETWORK");
				
		out.writeProperty(PersistConst.PROPERTY_F1_COUNT, bam.getF1Count());
		out.writeProperty(PersistConst.PROPERTY_F2_COUNT, bam.getF2Count());
		out.writeProperty(PersistConst.PROPERTY_WEIGHTS_F1_F2, bam.getWeightsF1toF2());
		out.writeProperty(PersistConst.PROPERTY_WEIGHTS_F2_F1, bam.getWeightsF2toF1());

		out.flush();
		
	}

	@Override
	public int getFileVersion() {
		return 1;
	}

}
