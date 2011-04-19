package org.encog.neural.pnn;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.basic.BasicMLDataArray;
import org.encog.ml.data.basic.BasicMLDataPair;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.encog.persist.EncogFileSection;
import org.encog.persist.EncogPersistor;
import org.encog.persist.EncogReadHelper;
import org.encog.persist.EncogWriteHelper;
import org.encog.persist.PersistConst;
import org.encog.util.EngineArray;
import org.encog.util.csv.CSVFormat;

public class PersistBasicPNN implements EncogPersistor {
	
	public static final String PROPERTY_outputMode = "outputMode";

	@Override
	public String getPersistClassString() {
		return "BasicPNN";
	}

	@Override
	public Object read(InputStream is) {
		
		EncogReadHelper in = new EncogReadHelper(is);
		EncogFileSection section;
		BasicMLDataSet samples = new BasicMLDataSet();
		Map<String,String> networkParams = null;
		PNNKernelType kernel = null; 
		PNNOutputMode outmodel = null;
		int inputCount = 0;
		int outputCount = 0;
		double error = 0;
		double[] sigma = null;
		
		while( (section = in.readNextSection()) != null ) {
			if( section.getSectionName().equals("PNN") && section.getSubSectionName().equals("PARAMS") ) {
				networkParams = section.parseParams();
			} if( section.getSectionName().equals("PNN") && section.getSubSectionName().equals("NETWORK") ) {
				Map<String,String> params = section.parseParams();
				inputCount = EncogFileSection.parseInt(params, PersistConst.INPUT_COUNT);
				outputCount = EncogFileSection.parseInt(params, PersistConst.OUTPUT_COUNT);
				kernel = stringToKernel(params.get(PersistConst.KERNEL));
				outmodel = stringToOutputMode(params.get(PROPERTY_outputMode));
				error = EncogFileSection.parseDouble(params, PersistConst.ERROR);
				sigma = EncogFileSection.parseDoubleArray(params, PersistConst.SIGMA);				
			} if( section.getSectionName().equals("PNN") && section.getSubSectionName().equals("SAMPLES") ) {
				for(String line: section.getLines()) {
					List<String> cols = EncogFileSection.splitColumns(line);
					int index = 0;
					MLData inputData = new BasicMLDataArray(inputCount);
					for(int i=0;i<inputCount;i++) {
						inputData.setData(i, CSVFormat.EG_FORMAT.parse(cols.get(index++)));
					}
					MLData idealData = new BasicMLDataArray(inputCount);
					for(int i=0;i<outputCount;i++) {
						idealData.setData(i, CSVFormat.EG_FORMAT.parse(cols.get(index++)));
					}
					MLDataPair pair = new BasicMLDataPair(inputData,idealData);
					samples.add(pair);
				}
			}
		}
		
		BasicPNN result = new BasicPNN(kernel,outmodel,inputCount,outputCount);
		if( networkParams!=null ) {
			result.getProperties().putAll(networkParams);
		}
		result.setSamples(samples);
		result.setError(error);
		if( sigma!=null )
			EngineArray.arrayCopy(sigma,result.getSigma());
		
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
		
		out.writeProperty(PersistConst.ERROR,pnn.getError());
		out.writeProperty(PersistConst.INPUT_COUNT,pnn.getInputCount());
		out.writeProperty(PersistConst.KERNEL,kernelToString(pnn.getKernel()));
		out.writeProperty(PersistConst.OUTPUT_COUNT,pnn.getOutputCount());
		out.writeProperty(PROPERTY_outputMode,outputModeToString(pnn.getOutputMode()));
		out.writeProperty(PersistConst.SIGMA,pnn.getSigma());
		
		out.addSubSection("SAMPLES");
		for(MLDataPair pair: pnn.getSamples()) {
			for(int i=0;i<pair.getInput().size();i++) {
				out.addColumn(pair.getInput().getData(i));	
			}
			for(int i=0;i<pair.getIdeal().size();i++) {
				out.addColumn(pair.getIdeal().getData(i));	
			}
			out.writeLine();
		}
		
		out.flush();
	}


	@Override
	public int getFileVersion() {
		return 1;
	}
	
	public static String kernelToString(PNNKernelType k) {
		switch(k) {
			case Gaussian:
				return "gaussian";
			case Reciprocal:
				return "reciprocal";
			default:
				return null;
		}
	}
	
	public static PNNKernelType stringToKernel(String k) {
		if( k.equalsIgnoreCase("gaussian")) {
			return PNNKernelType.Gaussian;
		} else if( k.equalsIgnoreCase("reciprocal")) {
			return PNNKernelType.Reciprocal;
		} else {
			return null;
		}		
	}
	
	public static String outputModeToString(PNNOutputMode mode) {
		switch(mode) {
			case Regression:
				return "regression";
			case Unsupervised:
				return "unsupervised";
			case Classification:
				return "classification";
			default:
				return null;
		}
	}
	
	public static PNNOutputMode stringToOutputMode(String mode) {
		if( mode.equalsIgnoreCase("regression"))
			return PNNOutputMode.Regression;
		else if( mode.equalsIgnoreCase("unsupervised"))
			return PNNOutputMode.Unsupervised;
		else if( mode.equalsIgnoreCase("classification"))
			return PNNOutputMode.Classification;
		else
			return null;
	}
}
