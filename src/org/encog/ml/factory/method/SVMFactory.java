package org.encog.ml.factory.method;

import java.util.List;

import org.encog.EncogError;
import org.encog.ml.MLMethod;
import org.encog.ml.factory.parse.ArchitectureLayer;
import org.encog.ml.factory.parse.ArchitectureParse;
import org.encog.ml.svm.KernelType;
import org.encog.ml.svm.SVM;
import org.encog.ml.svm.SVMType;

public class SVMFactory {
	public MLMethod create(String architecture, int input, int output) {
		
		List<String> layers = ArchitectureParse.parseLayers(architecture);
		if( layers.size()!=3) {
			throw new EncogError("SVM's must have exactly three elements, separated by ->.");
		}
		
		ArchitectureLayer inputLayer = ArchitectureParse.parseLayer(layers.get(0),input);
		ArchitectureLayer svmLayer = ArchitectureParse.parseLayer(layers.get(1),-1);
		ArchitectureLayer outputLayer = ArchitectureParse.parseLayer(layers.get(2),output);
		
		int inputCount = inputLayer.getCount();
		int outputCount = outputLayer.getCount();
		SVMType svmType = SVMType.NewSupportVectorClassification;
		KernelType kernelType = KernelType.RadialBasisFunction;
		
		SVM result = new SVM(inputCount,outputCount,svmType,kernelType);
		
		return result;
	}
}
