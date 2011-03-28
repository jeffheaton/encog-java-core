package org.encog.ml.svm;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Map;

import org.encog.mathutil.libsvm.svm;
import org.encog.mathutil.libsvm.svm_model;
import org.encog.neural.thermal.HopfieldNetwork;
import org.encog.persist.EncogFileSection;
import org.encog.persist.EncogPersistor;
import org.encog.persist.EncogReadHelper;
import org.encog.persist.EncogWriteHelper;
import org.encog.persist.PersistConst;
import org.encog.persist.PersistError;
import org.encog.util.csv.CSVFormat;
import org.encog.util.csv.NumberList;

public class PersistSVM implements EncogPersistor {
	
	public static final String PARAM_C = "C";
	public static final String PARAM_cacheSize = "cacheSize";
	public static final String PARAM_coef0 = "coef0";
	public static final String PARAM_degree = "degree";
	public static final String PARAM_eps = "eps";
	public static final String PARAM_gamma = "gamma";
	public static final String PARAM_kernelType = "kernelType";
	public static final String PARAM_nrWeight = "nrWeight";
	public static final String PARAM_nu = "nu";
	public static final String PARAM_p = "p";
	public static final String PARAM_probability = "probability";
	public static final String PARAM_shrinking = "shrinking";
	public static final String PARAM_statIterations = "statIterations";
	public static final String PARAM_svmType = "svmType";
	public static final String PARAM_weight = "weight";
	public static final String PARAM_weightLabel = "weightLabel";
	
	public int getFileVersion() {
		return 1;
	}
	
	@Override
	public Object read(InputStream is) {
		SVM result = new SVM();
		EncogReadHelper in = new EncogReadHelper(is);
		EncogFileSection section;
		
		while( (section = in.readNextSection()) != null ) {
			if( section.getSectionName().equals("SVM") && section.getSubSectionName().equals("PARAMS") ) {
				Map<String,String> params = section.parseParams();
				result.getProperties().putAll(params);
			} if( section.getSectionName().equals("SVM") && section.getSubSectionName().equals("SVM-PARAMS") ) {
				Map<String,String> params = section.parseParams();
				result.getParams().C = EncogFileSection.parseDouble(params, PersistSVM.PARAM_C);
				result.getParams().cache_size = EncogFileSection.parseDouble(params, PersistSVM.PARAM_cacheSize);
				result.getParams().coef0 = EncogFileSection.parseDouble(params, PersistSVM.PARAM_coef0);
				result.getParams().degree = EncogFileSection.parseInt(params, PersistSVM.PARAM_degree);
				result.getParams().eps = EncogFileSection.parseDouble(params, PersistSVM.PARAM_eps);
				result.getParams().gamma = EncogFileSection.parseDouble(params, PersistSVM.PARAM_gamma);
				result.getParams().kernel_type = EncogFileSection.parseInt(params, PersistSVM.PARAM_kernelType);
				result.getParams().nr_weight = EncogFileSection.parseInt(params, PersistSVM.PARAM_nrWeight);
				result.getParams().nu = EncogFileSection.parseDouble(params, PersistSVM.PARAM_nu);
				result.getParams().p = EncogFileSection.parseDouble(params, PersistSVM.PARAM_p);
				result.getParams().probability = EncogFileSection.parseInt(params, PersistSVM.PARAM_probability);
				result.getParams().shrinking = EncogFileSection.parseInt(params, PersistSVM.PARAM_shrinking);
				result.getParams().statIterations = EncogFileSection.parseInt(params, PersistSVM.PARAM_statIterations);
				result.getParams().svm_type = EncogFileSection.parseInt(params, PersistSVM.PARAM_svmType);
				result.getParams().weight = EncogFileSection.parseDoubleArray(params, PersistSVM.PARAM_weight);
				result.getParams().weight_label = EncogFileSection.parseIntArray(params, PersistSVM.PARAM_weightLabel);
			}
		}
		 
		return result;
	}

	@Override
	public void save(OutputStream os, Object obj) {
		EncogWriteHelper out = new EncogWriteHelper(os);
		SVM svm = (SVM)obj;
		out.addSection("HOPFIELD");
		out.addSubSection("PARAMS");
		out.addProperties(svm.getProperties());
		out.addSubSection("SVM-PARAM");
		out.writeProperty(PersistSVM.PARAM_C,svm.getParams().C);
		out.writeProperty(PersistSVM.PARAM_cacheSize,svm.getParams().cache_size);
		out.writeProperty(PersistSVM.PARAM_coef0,svm.getParams().coef0);
		out.writeProperty(PersistSVM.PARAM_degree,svm.getParams().degree);
		out.writeProperty(PersistSVM.PARAM_eps,svm.getParams().eps);
		out.writeProperty(PersistSVM.PARAM_gamma,svm.getParams().gamma);
		out.writeProperty(PersistSVM.PARAM_kernelType,svm.getParams().kernel_type);
		out.writeProperty(PersistSVM.PARAM_nrWeight,svm.getParams().nr_weight);
		out.writeProperty(PersistSVM.PARAM_nu,svm.getParams().nu);
		out.writeProperty(PersistSVM.PARAM_p,svm.getParams().p);
		out.writeProperty(PersistSVM.PARAM_probability,svm.getParams().probability);
		out.writeProperty(PersistSVM.PARAM_shrinking,svm.getParams().shrinking);
		out.writeProperty(PersistSVM.PARAM_statIterations,svm.getParams().statIterations);
		out.writeProperty(PersistSVM.PARAM_svmType,svm.getParams().svm_type);
		out.writeProperty(PersistSVM.PARAM_weight,svm.getParams().weight);
		out.writeProperty(PersistSVM.PARAM_weightLabel,svm.getParams().weight_label);
		if( svm.getModel()!=null ) {
			out.addSubSection("SVM-MODEL");
			
		}
		
		out.flush();
	}

	@Override
	public String getPersistClassString() {
		return SVM.class.getSimpleName();
	}
}
