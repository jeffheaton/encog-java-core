package org.encog.neural.art;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import org.encog.neural.thermal.HopfieldNetwork;
import org.encog.persist.EncogFileSection;
import org.encog.persist.EncogPersistor;
import org.encog.persist.EncogReadHelper;
import org.encog.persist.EncogWriteHelper;
import org.encog.persist.map.PersistConst;
import org.encog.util.csv.CSVFormat;
import org.encog.util.csv.NumberList;

public class PersistART1 implements EncogPersistor {

	@Override
	public String getPersistClassString() {
		return "ART1";
	}

	@Override
	public Object read(InputStream is) {
		ART1 result = new ART1();
		EncogReadHelper in = new EncogReadHelper(is);
		EncogFileSection section;
		
		while( (section = in.readNextSection()) != null ) {
			if( section.getSectionName().equals("ART1") && section.getSubSectionName().equals("PARAMS") ) {
				Map<String,String> params = section.parseParams();
				result.getProperties().putAll(params);
			} if( section.getSectionName().equals("ART1") && section.getSubSectionName().equals("NETWORK") ) {
				Map<String,String> params = section.parseParams();
				
				result.setA1(EncogFileSection.parseDouble(params, ART1.PROPERTY_A1) );
				result.setB1(EncogFileSection.parseDouble(params, ART1.PROPERTY_B1) );
				result.setC1(EncogFileSection.parseDouble(params, ART1.PROPERTY_C1) );
				result.setD1(EncogFileSection.parseDouble(params, ART1.PROPERTY_D1) );
				result.setF1Count(EncogFileSection.parseInt(params, PersistConst.PROPERTY_F1_COUNT));
				result.setF2Count(EncogFileSection.parseInt(params, PersistConst.PROPERTY_F2_COUNT));
				result.setNoWinner(EncogFileSection.parseInt(params, ART1.PROPERTY_NO_WINNER));
				result.setL(EncogFileSection.parseDouble(params, ART1.PROPERTY_L));
				result.setVigilance(EncogFileSection.parseDouble(params, ART1.PROPERTY_VIGILANCE) );
				result.setWeightsF1toF2(EncogFileSection.parseMatrix(params, PersistConst.PROPERTY_WEIGHTS_F1_F2));
				result.setWeightsF2toF1(EncogFileSection.parseMatrix(params, PersistConst.PROPERTY_WEIGHTS_F2_F1));			
			}
		}
		 
		return result;
	}

	@Override
	public void save(OutputStream os, Object obj) {
		EncogWriteHelper out = new EncogWriteHelper(os);
		ART1 art1 = (ART1)obj;
		out.addSection("ART1");
		out.addSubSection("PARAMS");
		out.addProperties(art1.getProperties());
		out.addSubSection("NETWORK");
				
		out.writeProperty(ART1.PROPERTY_A1, art1.getA1());
		out.writeProperty(ART1.PROPERTY_B1, art1.getB1());
		out.writeProperty(ART1.PROPERTY_C1, art1.getC1());
		out.writeProperty(ART1.PROPERTY_D1, art1.getD1());
		out.writeProperty(PersistConst.PROPERTY_F1_COUNT, art1.getF1Count());
		out.writeProperty(PersistConst.PROPERTY_F2_COUNT, art1.getF2Count());
		out.writeProperty(ART1.PROPERTY_NO_WINNER, art1.getNoWinner());
		out.writeProperty(ART1.PROPERTY_L, art1.getL());
		out.writeProperty(ART1.PROPERTY_VIGILANCE, art1.getVigilance());
		out.writeProperty(PersistConst.PROPERTY_WEIGHTS_F1_F2, art1.getWeightsF1toF2());
		out.writeProperty(PersistConst.PROPERTY_WEIGHTS_F2_F1, art1.getWeightsF2toF1());

		out.flush();
		
	}

	@Override
	public int getFileVersion() {
		return 1;
	}

}
