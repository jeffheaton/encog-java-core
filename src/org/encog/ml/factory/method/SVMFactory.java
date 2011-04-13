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
		ArchitectureLayer paramsLayer = ArchitectureParse.parseLayer(layers.get(1),input);	
		ArchitectureLayer outputLayer = ArchitectureParse.parseLayer(layers.get(2),output);
		
		String name=paramsLayer.getName();
		String kernelStr = paramsLayer.getParams().get("KERNEL");
		String svmTypeStr = paramsLayer.getParams().get("TYPE");
		
		SVMType svmType = SVMType.NewSupportVectorClassification;
		KernelType kernelType = KernelType.RadialBasisFunction;
		
		boolean useNew = true;
		
		if( svmTypeStr==null ) {
			useNew = true;
		} else if( svmTypeStr.equalsIgnoreCase("NEW")) {
			useNew = true;
		} else if( svmTypeStr.equalsIgnoreCase("OLD")) {
			useNew = false;
		}
		else {
			throw new EncogError("Unsupported type: " + svmTypeStr + ", must be NEW or OLD.");
		}
		
		if( name.equalsIgnoreCase("C")) {
			if( useNew ) {
				svmType = SVMType.NewSupportVectorClassification;
			} else {
				svmType = SVMType.SupportVectorClassification;
			}
		} else if( name.equalsIgnoreCase("R")) {
			if( useNew ) {
				svmType = SVMType.NewSupportVectorRegression;
			} else {
				svmType = SVMType.EpsilonSupportVectorRegression;
			}
		} else {
			throw new EncogError("Unsupported mode: " + name + ", must be C for classify or R for regression.");
		}
		
		if( kernelStr.equalsIgnoreCase("linear")) {
			kernelType = KernelType.Linear;
		} else if( kernelStr.equalsIgnoreCase("poly")) {
			kernelType = KernelType.Poly;
		} else if( kernelStr.equalsIgnoreCase("precomputed")) {
			kernelType = KernelType.Precomputed;
		} else if( kernelStr.equalsIgnoreCase("rbf")) {
			kernelType = KernelType.RadialBasisFunction;
		} else if( kernelStr.equalsIgnoreCase("sigmoid")) {
			kernelType = KernelType.Sigmoid;
		} else {
			throw new EncogError("Unsupported kernel: " + kernelStr + ", must be linear,poly,precomputed,rbf or sigmoid.");
		}
		
		int inputCount = inputLayer.getCount();
		int outputCount = outputLayer.getCount();
		
		if( outputCount!=1 ) {
			throw new EncogError("SVM can only have an output size of 1.");
		}
		
		
		
		SVM result = new SVM(inputCount,svmType,kernelType);
		
		return result;
	}
}
