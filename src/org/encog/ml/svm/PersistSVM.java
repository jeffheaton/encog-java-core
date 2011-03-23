package org.encog.ml.svm;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import org.encog.mathutil.libsvm.svm;
import org.encog.mathutil.libsvm.svm_model;
import org.encog.persist.EncogPersistor;
import org.encog.persist.PersistError;

public class PersistSVM implements EncogPersistor {
	
	public int getFileVersion() {
		return 1;
	}
	
	public Object read(InputStream is) {
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		try {
			svm_model model = svm.svm_load_model(br);
			
			return new SVM(model);
		} catch (IOException e) {
			throw new PersistError(e);
		}
	}
	
	public void save(OutputStream is, Object obj) {
		SVM obj2 = (SVM)obj;
		DataOutputStream das = new DataOutputStream(is);
		try {
			svm.svm_save_model(das, obj2.getModel());
		} catch (IOException e) {
			throw new PersistError(e);
		}
	
	}

	@Override
	public String getPersistClassString() {
		return SVM.class.getSimpleName();
	}
}
