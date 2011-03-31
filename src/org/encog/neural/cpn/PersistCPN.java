package org.encog.neural.cpn;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import org.encog.mathutil.matrices.Matrix;
import org.encog.persist.EncogFileSection;
import org.encog.persist.EncogPersistor;
import org.encog.persist.EncogReadHelper;
import org.encog.persist.EncogWriteHelper;
import org.encog.persist.PersistConst;

public class PersistCPN implements EncogPersistor {
	
	final static String PROPERTY_inputToInstar = "inputToInstar";
	final static String PROPERTY_instarToInput = "instarToInput";
	final static String PROPERTY_winnerCount = "winnerCount";

	@Override
	public String getPersistClassString() {
		return "CPN";
	}

	@Override
	public Object read(InputStream is) {
		Map<String,String> networkParams = null;
		EncogReadHelper in = new EncogReadHelper(is);
		EncogFileSection section;
		int inputCount = 0;
		int instarCount = 0;
		int outputCount = 0;
		int winnerCount = 0;
		Matrix m1 = null;
		Matrix m2 = null;
		
		while( (section = in.readNextSection()) != null ) {
			if( section.getSectionName().equals("CPN") && section.getSubSectionName().equals("PARAMS") ) {
				networkParams = section.parseParams();
			} if( section.getSectionName().equals("CPN") && section.getSubSectionName().equals("NETWORK") ) {
				Map<String,String> params = section.parseParams();
		
				inputCount = EncogFileSection.parseInt(params,PersistConst.INPUT_COUNT);
				instarCount = EncogFileSection.parseInt(params,PersistConst.INSTAR);
				outputCount = EncogFileSection.parseInt(params,PersistConst.OUTPUT_COUNT);
				winnerCount = EncogFileSection.parseInt(params,PROPERTY_winnerCount);
				m1 = EncogFileSection.parseMatrix(params,PROPERTY_inputToInstar);
				m2 = EncogFileSection.parseMatrix(params,PROPERTY_instarToInput);				
			}
		}
		
		CPN result = new CPN(inputCount,instarCount,outputCount,winnerCount);
		result.getProperties().putAll(networkParams);
		result.getWeightsInputToInstar().set(m1);
		result.getWeightsInstarToOutstar().set(m2);		 
		return result;
	}

	@Override
	public void save(OutputStream os, Object obj) {
		EncogWriteHelper out = new EncogWriteHelper(os);
		CPN cpn = (CPN)obj;
		out.addSection("CPN");
		out.addSubSection("PARAMS");
		out.addProperties(cpn.getProperties());
		out.addSubSection("NETWORK");
		
		out.writeProperty(PersistConst.INPUT_COUNT, cpn.getInputCount() );
		out.writeProperty(PersistConst.INSTAR, cpn.getInstarCount() );
		out.writeProperty(PersistConst.OUTPUT_COUNT, cpn.getOutputCount() );
		out.writeProperty(PROPERTY_inputToInstar, cpn.getWeightsInputToInstar() );
		out.writeProperty(PROPERTY_instarToInput, cpn.getWeightsInstarToOutstar() );
		out.writeProperty(PROPERTY_winnerCount, cpn.getWinnerCount() );

		out.flush();		
	}

	@Override
	public int getFileVersion() {
		return 1;
	}

}
