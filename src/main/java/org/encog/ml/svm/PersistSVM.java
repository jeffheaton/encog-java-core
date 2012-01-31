/*
 * Encog(tm) Core v3.1 - Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 
 * Copyright 2008-2012 Heaton Research, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *   
 * For more information on Heaton Research copyrights, licenses 
 * and trademarks visit:
 * http://www.heatonresearch.com/copyright
 */
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
import org.encog.persist.PersistConst;
import org.encog.persist.PersistError;

/**
 * Persist a SVM.
 */
public class PersistSVM implements EncogPersistor {

	/**
	 * The parameter to hold the const C.
	 */
	public static final String PARAM_C = "C";
	
	/**
	 * The parameter to hold the cache size.
	 */
	public static final String PARAM_CACHE_SIZE = "cacheSize";
	
	/**
	 * The parameter to hold the coef0.
	 */
	public static final String PARAM_COEF0 = "coef0";
	
	/**
	 * The parameter to hold the degree.
	 */
	public static final String PARAM_DEGREE = "degree";
	
	/**
	 * The parameter to hold the eps.
	 */
	public static final String PARAM_EPS = "eps";
	
	/**
	 * The parameter to hold the gamma.
	 */
	public static final String PARAM_GAMMA = "gamma";
	
	/**
	 * The parameter to hold the kernel type.
	 */
	public static final String PARAM_KERNEL_TYPE = "kernelType";
	
	/**
	 * The parameter to hold the number of weights.
	 */
	public static final String PARAM_NUM_WEIGHT = "nrWeight";
	
	/**
	 * The parameter to hold the nu.
	 */
	public static final String PARAM_NU = "nu";
	
	/**
	 * The parameter to hold the p.
	 */
	public static final String PARAM_P = "p";
	
	/**
	 * The parameter to hold the probability.
	 */
	public static final String PARAM_PROBABILITY = "probability";
	
	/**
	 * The parameter to hold the shrinking.
	 */
	public static final String PARAM_SHRINKING = "shrinking";
	
	
	/**
	 * The parameter to hold the SVM type.
	 */
	public static final String PARAM_SVM_TYPE = "svmType";
	
	/**
	 * The paramater to hold the weight.
	 */
	public static final String PARAM_WEIGHT = "weight";
	
	/**
	 * The parameter to hold the weight label.
	 */
	public static final String PARAM_WEIGHT_LABEL = "weightLabel";

	/**
	 * @return The file version.
	 */
	@Override
	public final int getFileVersion() {
		return 1;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final String getPersistClassString() {
		return SVM.class.getSimpleName();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final Object read(final InputStream is) {
		final SVM result = new SVM();
		final EncogReadHelper in = new EncogReadHelper(is);
		EncogFileSection section;

		while ((section = in.readNextSection()) != null) {
			if (section.getSectionName().equals("SVM")
					&& section.getSubSectionName().equals("PARAMS")) {
				final Map<String, String> params = section.parseParams();
				result.getProperties().putAll(params);
			}
			if (section.getSectionName().equals("SVM")
					&& section.getSubSectionName().equals("SVM-PARAM")) {
				final Map<String, String> params = section.parseParams();
				result.setInputCount(EncogFileSection.parseInt(params,
						PersistConst.INPUT_COUNT));
				result.getParams().C = EncogFileSection.parseDouble(params,
						PersistSVM.PARAM_C);
				result.getParams().cache_size = EncogFileSection.parseDouble(
						params, PersistSVM.PARAM_CACHE_SIZE);
				result.getParams().coef0 = EncogFileSection.parseDouble(params,
						PersistSVM.PARAM_COEF0);
				result.getParams().degree = EncogFileSection.parseInt(params,
						PersistSVM.PARAM_DEGREE);
				result.getParams().eps = EncogFileSection.parseDouble(params,
						PersistSVM.PARAM_EPS);
				result.getParams().gamma = EncogFileSection.parseDouble(params,
						PersistSVM.PARAM_GAMMA);
				result.getParams().kernel_type = EncogFileSection.parseInt(
						params, PersistSVM.PARAM_KERNEL_TYPE);
				result.getParams().nr_weight = EncogFileSection.parseInt(
						params, PersistSVM.PARAM_NUM_WEIGHT);
				result.getParams().nu = EncogFileSection.parseDouble(params,
						PersistSVM.PARAM_NU);
				result.getParams().p = EncogFileSection.parseDouble(params,
						PersistSVM.PARAM_P);
				result.getParams().probability = EncogFileSection.parseInt(
						params, PersistSVM.PARAM_PROBABILITY);
				result.getParams().shrinking = EncogFileSection.parseInt(
						params, PersistSVM.PARAM_SHRINKING);
				result.getParams().svm_type = EncogFileSection.parseInt(params,
						PersistSVM.PARAM_SVM_TYPE);
				result.getParams().weight = section.parseDoubleArray(
						params, PersistSVM.PARAM_WEIGHT);
				result.getParams().weight_label = EncogFileSection
						.parseIntArray(params, PersistSVM.PARAM_WEIGHT_LABEL);
			} else if (section.getSectionName().equals("SVM")
					&& section.getSubSectionName().equals("SVM-MODEL")) {
				try {
					final StringReader rdr = new StringReader(
							section.getLinesAsString());
					final BufferedReader br = new BufferedReader(rdr);
					final svm_model model = svm.svm_load_model(br);
					result.setModel(model);
					br.close();
					rdr.close();
				} catch (final IOException ex) {
					throw new PersistError(ex);
				}
			}
		}

		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void save(final OutputStream os, final Object obj) {
		final EncogWriteHelper out = new EncogWriteHelper(os);
		final SVM svm2 = (SVM) obj;
		out.addSection("SVM");
		out.addSubSection("PARAMS");
		out.addProperties(svm2.getProperties());
		out.addSubSection("SVM-PARAM");
		out.writeProperty(PersistConst.INPUT_COUNT, svm2.getInputCount());
		out.writeProperty(PersistSVM.PARAM_C, svm2.getParams().C);
		out.writeProperty(PersistSVM.PARAM_CACHE_SIZE,
				svm2.getParams().cache_size);
		out.writeProperty(PersistSVM.PARAM_COEF0, svm2.getParams().coef0);
		out.writeProperty(PersistSVM.PARAM_DEGREE, svm2.getParams().degree);
		out.writeProperty(PersistSVM.PARAM_EPS, svm2.getParams().eps);
		out.writeProperty(PersistSVM.PARAM_GAMMA, svm2.getParams().gamma);
		out.writeProperty(PersistSVM.PARAM_KERNEL_TYPE,
				svm2.getParams().kernel_type);
		out.writeProperty(
				PersistSVM.PARAM_NUM_WEIGHT, svm2.getParams().nr_weight);
		out.writeProperty(PersistSVM.PARAM_NU, svm2.getParams().nu);
		out.writeProperty(PersistSVM.PARAM_P, svm2.getParams().p);
		out.writeProperty(PersistSVM.PARAM_PROBABILITY,
				svm2.getParams().probability);
		out.writeProperty(PersistSVM.PARAM_SHRINKING,
				svm2.getParams().shrinking);

		out.writeProperty(PersistSVM.PARAM_SVM_TYPE, svm2.getParams().svm_type);
		out.writeProperty(PersistSVM.PARAM_WEIGHT, svm2.getParams().weight);
		out.writeProperty(PersistSVM.PARAM_WEIGHT_LABEL,
				svm2.getParams().weight_label);
		if (svm2.getModel() != null) {
			out.addSubSection("SVM-MODEL");
			try {
				final ByteArrayOutputStream ba = new ByteArrayOutputStream();
				final DataOutputStream das = new DataOutputStream(ba);
				svm.svm_save_model(das, svm2.getModel());
				out.write(ba.toString("UTF-8"));
				das.close();
				ba.close();

			} catch (final IOException ex) {
				throw new PersistError(ex);
			}
		}

		out.flush();
	}
}
