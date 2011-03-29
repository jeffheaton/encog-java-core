package org.encog.ml.svm;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.util.Map;

import org.encog.mathutil.libsvm.svm;
import org.encog.mathutil.libsvm.svm_model;
import org.encog.persist.EncogFileSection;
import org.encog.persist.EncogPersistor;
import org.encog.persist.EncogReadHelper;
import org.encog.persist.EncogWriteHelper;
import org.encog.persist.PersistError;

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

		while ((section = in.readNextSection()) != null) {
			if (section.getSectionName().equals("SVM")
					&& section.getSubSectionName().equals("PARAMS")) {
				Map<String, String> params = section.parseParams();
				result.getProperties().putAll(params);
			}
			if (section.getSectionName().equals("SVM")
					&& section.getSubSectionName().equals("SVM-PARAM")) {
				Map<String, String> params = section.parseParams();
				result.getParams().C = EncogFileSection.parseDouble(params,
						PersistSVM.PARAM_C);
				result.getParams().cache_size = EncogFileSection.parseDouble(
						params, PersistSVM.PARAM_cacheSize);
				result.getParams().coef0 = EncogFileSection.parseDouble(params,
						PersistSVM.PARAM_coef0);
				result.getParams().degree = EncogFileSection.parseInt(params,
						PersistSVM.PARAM_degree);
				result.getParams().eps = EncogFileSection.parseDouble(params,
						PersistSVM.PARAM_eps);
				result.getParams().gamma = EncogFileSection.parseDouble(params,
						PersistSVM.PARAM_gamma);
				result.getParams().kernel_type = EncogFileSection.parseInt(
						params, PersistSVM.PARAM_kernelType);
				result.getParams().nr_weight = EncogFileSection.parseInt(
						params, PersistSVM.PARAM_nrWeight);
				result.getParams().nu = EncogFileSection.parseDouble(params,
						PersistSVM.PARAM_nu);
				result.getParams().p = EncogFileSection.parseDouble(params,
						PersistSVM.PARAM_p);
				result.getParams().probability = EncogFileSection.parseInt(
						params, PersistSVM.PARAM_probability);
				result.getParams().shrinking = EncogFileSection.parseInt(
						params, PersistSVM.PARAM_shrinking);
				result.getParams().statIterations = EncogFileSection.parseInt(
						params, PersistSVM.PARAM_statIterations);
				result.getParams().svm_type = EncogFileSection.parseInt(params,
						PersistSVM.PARAM_svmType);
				result.getParams().weight = EncogFileSection.parseDoubleArray(
						params, PersistSVM.PARAM_weight);
				result.getParams().weight_label = EncogFileSection
						.parseIntArray(params, PersistSVM.PARAM_weightLabel);
			} else if (section.getSectionName().equals("SVM")
					&& section.getSubSectionName().equals("SVM-MODEL")) {
				try {
					StringReader rdr = new StringReader(
							section.getLinesAsString());
					BufferedReader br = new BufferedReader(rdr);
					svm_model model = svm.svm_load_model(br);
					result.setModel(model);
					br.close();
					rdr.close();
				} catch (IOException ex) {
					throw new PersistError(ex);
				}
			}
		}

		return result;
	}

	@Override
	public void save(OutputStream os, Object obj) {
		EncogWriteHelper out = new EncogWriteHelper(os);
		SVM svm2 = (SVM) obj;
		out.addSection("SVM");
		out.addSubSection("PARAMS");
		out.addProperties(svm2.getProperties());
		out.addSubSection("SVM-PARAM");
		out.writeProperty(PersistSVM.PARAM_C, svm2.getParams().C);
		out.writeProperty(PersistSVM.PARAM_cacheSize,
				svm2.getParams().cache_size);
		out.writeProperty(PersistSVM.PARAM_coef0, svm2.getParams().coef0);
		out.writeProperty(PersistSVM.PARAM_degree, svm2.getParams().degree);
		out.writeProperty(PersistSVM.PARAM_eps, svm2.getParams().eps);
		out.writeProperty(PersistSVM.PARAM_gamma, svm2.getParams().gamma);
		out.writeProperty(PersistSVM.PARAM_kernelType,
				svm2.getParams().kernel_type);
		out.writeProperty(PersistSVM.PARAM_nrWeight, svm2.getParams().nr_weight);
		out.writeProperty(PersistSVM.PARAM_nu, svm2.getParams().nu);
		out.writeProperty(PersistSVM.PARAM_p, svm2.getParams().p);
		out.writeProperty(PersistSVM.PARAM_probability,
				svm2.getParams().probability);
		out.writeProperty(PersistSVM.PARAM_shrinking, svm2.getParams().shrinking);
		out.writeProperty(PersistSVM.PARAM_statIterations,
				svm2.getParams().statIterations);
		out.writeProperty(PersistSVM.PARAM_svmType, svm2.getParams().svm_type);
		out.writeProperty(PersistSVM.PARAM_weight, svm2.getParams().weight);
		out.writeProperty(PersistSVM.PARAM_weightLabel,
				svm2.getParams().weight_label);
		if (svm2.getModel() != null) {
			out.addSubSection("SVM-MODEL");
			try {
				ByteArrayOutputStream ba = new ByteArrayOutputStream();
				DataOutputStream das = new DataOutputStream(ba);
				svm.svm_save_model(das, svm2.getModel());
				out.write(ba.toString("UTF-8"));				
				das.close();
				ba.close();
				
			} catch (IOException ex) {
				throw new PersistError(ex);
			}
		}

		out.flush();
	}

	@Override
	public String getPersistClassString() {
		return SVM.class.getSimpleName();
	}
}
