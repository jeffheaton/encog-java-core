package org.encog.engine.network.flat;

import org.encog.engine.EngineMachineLearning;
import org.encog.engine.validate.BasicMachineLearningValidate;

public class ValidateForOpenCL extends BasicMachineLearningValidate {

	@Override
	public String isValid(EngineMachineLearning network) {
		
		if( (network instanceof FlatNetwork) )
			return "Only flat networks are valid to be used for OpenCL";
		
		FlatNetwork  flat = (FlatNetwork)network;
		
		for( int activation : flat.getActivationType() )
		{
			if( (activation!=ActivationFunctions.ACTIVATION_LINEAR)&&
				(activation!=ActivationFunctions.ACTIVATION_SIGMOID)&&
				(activation!=ActivationFunctions.ACTIVATION_TANH) )
			{
				return "Can't use OpenCL if activation function is not linear, sigmoid or tanh.";
			}
		}
		
		boolean hasContext = false;
		for(int i = 0; i<flat.getLayerCounts().length;i++)
		{
			if( flat.getContextTargetOffset()[i]!=0 )
			{
				hasContext = true;
			}
			
			if( flat.getContextTargetSize()[i]!=0 )
			{
				hasContext = true;
			}
		}
		
		if( hasContext )
			return "Can't use OpenCL if context neurons are present.";
		
		return null;
	}

}
