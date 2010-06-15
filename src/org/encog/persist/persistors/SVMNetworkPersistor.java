package org.encog.persist.persistors;

import java.io.DataOutputStream;
import java.io.IOException;

import org.encog.mathutil.libsvm.svm;
import org.encog.mathutil.libsvm.svm_model;
import org.encog.mathutil.libsvm.svm_node;
import org.encog.mathutil.libsvm.svm_parameter;
import org.encog.mathutil.rbf.RadialBasisFunction;
import org.encog.neural.networks.layers.RadialBasisFunctionLayer;
import org.encog.neural.networks.svm.SVMNetwork;
import org.encog.parse.tags.read.ReadXML;
import org.encog.parse.tags.write.WriteXML;
import org.encog.persist.EncogPersistedCollection;
import org.encog.persist.EncogPersistedObject;
import org.encog.persist.Persistor;

public class SVMNetworkPersistor implements Persistor {

	public static final String svm_type_table[] =
	{
		"c_svc","nu_svc","one_class","epsilon_svr","nu_svr",
	};

	public static final String kernel_type_table[]=
	{
		"linear","polynomial","rbf","sigmoid","precomputed"
	};
	
	/**
	 * The input tag.
	 */
	public static final String TAG_INPUT = "input";

	/**
	 * The output tag.
	 */
	public static final String TAG_OUTPUT = "output";
	
	/**
	 * The models tag.
	 */
	public static final String TAG_MODELS = "models";

	
	@Override
	public EncogPersistedObject load(ReadXML in) {
		SVMNetwork result = null;
		int input=-1,output=-1;
		
		while (in.readToTag()) {
			if (in.is(SVMNetworkPersistor.TAG_INPUT,true)) {
				input = Integer.parseInt(in.readTextToTag());
			}
			else if (in.is(SVMNetworkPersistor.TAG_OUTPUT,true)) {
				output = Integer.parseInt(in.readTextToTag());
			}
			else if (in.is(SVMNetworkPersistor.TAG_MODELS,true)) {
				result = new SVMNetwork(input,output,false);
				handleModels(in,result);
			}
			else if (in.is(EncogPersistedCollection.TYPE_SVM, false)) {
				break;
			}
		}
		
		return result;
	}
	
	private void handleModels(ReadXML in, SVMNetwork network)
	{
		for(int i=0;i<network.getModels().length;i++)
		{
			svm_parameter param = new svm_parameter();
			svm_model model = new svm_model();
			model.param = param;
			network.getModels()[i] = model;
		}
		
		while (in.readToTag()) {
			if (in.is(SVMNetworkPersistor.TAG_INPUT,true)) {
				//input = Integer.parseInt(in.readTextToTag());
			}
			else if (in.is(SVMNetworkPersistor.TAG_OUTPUT,true)) {
				//output = Integer.parseInt(in.readTextToTag());
			}
			else if (in.is(SVMNetworkPersistor.TAG_MODELS,true)) {
				//result = new SVMNetwork(input,output,false);
				//handleModels(in,result);
			}
			else if (in.is(SVMNetworkPersistor.TAG_MODELS, false)) {
				break;
			}
		}

	}
	
	public static void saveModel(WriteXML out, svm_model model)
	{
		out.beginTag("Model");
		svm_parameter param = model.param;

		out.addProperty("typeSVM", svm_type_table[param.svm_type]);
		out.addProperty("typeKernel", kernel_type_table[param.kernel_type]);
		
		if(param.kernel_type == svm_parameter.POLY)
		{
			out.addProperty("degree", param.degree);
		}

		if(param.kernel_type == svm_parameter.POLY ||
		   param.kernel_type == svm_parameter.RBF ||
		   param.kernel_type == svm_parameter.SIGMOID)
		{
			out.addProperty("gamma", param.gamma);
		}
			
		if(param.kernel_type == svm_parameter.POLY ||
		   param.kernel_type == svm_parameter.SIGMOID)
		{
			out.addProperty("coef0", param.coef0);
		}
			
		int nr_class = model.nr_class;
		int l = model.l;
		
		out.addProperty("numClass", nr_class);
		out.addProperty("totalSV", l);

		out.addProperty("rho", model.rho, nr_class*(nr_class-1)/2);
		out.addProperty("label", model.label, nr_class);
		out.addProperty("probA", model.probA, nr_class*(nr_class-1)/2);
		out.addProperty("probB", model.probB, nr_class*(nr_class-1)/2);
		out.addProperty("nSV", model.nSV, nr_class);

		out.beginTag("Data");

		double[][] sv_coef = model.sv_coef;
		svm_node[][] SV = model.SV;

		StringBuilder line = new StringBuilder();
		for(int i=0;i<l;i++)
		{
			out.beginTag("row");
			line.setLength(0);
			for(int j=0;j<nr_class-1;j++)
				line.append(sv_coef[j][i]+" ");

			svm_node[] p = SV[i];
			if(param.kernel_type == svm_parameter.PRECOMPUTED)
				line.append("0:"+(int)(p[0].value));
			else	
				for(int j=0;j<p.length;j++)
					line.append(p[j].index+":"+p[j].value+" ");
			out.addProperty("row",line.toString() );
		}
		
		out.endTag();
		out.endTag();
	}

	@Override
	public void save(EncogPersistedObject obj, WriteXML out) {
		PersistorUtil.beginEncogObject(
				EncogPersistedCollection.TYPE_SVM, out, obj,
				false);
		final SVMNetwork net = (SVMNetwork) obj;
		
		out.addProperty(SVMNetworkPersistor.TAG_INPUT, net.getInputCount());
		out.addProperty(SVMNetworkPersistor.TAG_OUTPUT, net.getOutputCount());
		out.beginTag(SVMNetworkPersistor.TAG_MODELS);
		for(int i=0;i<net.getModels().length;i++)
		{
			saveModel(out,net.getModels()[i]);
		}
		out.endTag();
		out.endTag();
	}
	
}
