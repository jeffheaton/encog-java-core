package org.encog.app.generate.generators.js;

import java.io.File;

import org.encog.Encog;
import org.encog.EncogError;
import org.encog.app.generate.AnalystCodeGenerationError;
import org.encog.app.generate.generators.AbstractGenerator;
import org.encog.app.generate.program.EncogProgram;
import org.encog.app.generate.program.EncogProgramNode;
import org.encog.app.generate.program.EncogTreeNode;
import org.encog.engine.network.activation.ActivationElliott;
import org.encog.engine.network.activation.ActivationFunction;
import org.encog.engine.network.activation.ActivationLinear;
import org.encog.engine.network.activation.ActivationSigmoid;
import org.encog.engine.network.activation.ActivationTANH;
import org.encog.ml.MLFactory;
import org.encog.ml.MLMethod;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.neural.flat.FlatNetwork;
import org.encog.neural.networks.ContainsFlat;
import org.encog.persist.EncogDirectoryPersistence;
import org.encog.util.csv.CSVFormat;
import org.encog.util.csv.NumberList;
import org.encog.util.simple.EncogUtility;

public class GenerateEncogJavaScript extends AbstractGenerator {
	
	private boolean embed;
	
	private void generateComment(EncogProgramNode commentNode) {
		this.addLine("// " + commentNode.getName());
	}
	
	private void generateClass(EncogProgramNode node) {
		addBreak();
		
		addLine("<!DOCTYPE html>");
		addLine("<html>");
		addLine("<head>");
		addLine("<title>Encog Generated Javascript</title>");
		addLine("</head>");
		addLine("<body>");
		addLine("<script src=\"../encog.js\"></script>");
		addLine("<script src=\"../encog-widget.js\"></script>");
		addLine("<pre>");
		addLine("<script type=\"text/javascript\">");
		
		generateForChildren(node);
		
		addLine("</script>");
		addLine("<noscript>Your browser does not support JavaScript! Note: if you are trying to view this in Encog Workbench, right-click file and choose \"Open as Text\".</noscript>");
		addLine("</pre>");
		addLine("</body>");
		addLine("</html>");
	}
	
	private void generateMainFunction(EncogProgramNode node) {
		addBreak();
		generateForChildren(node);
	}
	
	private void generateFunctionCall(EncogProgramNode node) {
		addBreak();
		StringBuilder line = new StringBuilder();
		if( node.getArgs().get(0).getValue().toString().length()>0 ) {
			line.append("var ");
			line.append(node.getArgs().get(1).getValue().toString());
			line.append(" = ");			
		}
		
		line.append(node.getName());
		line.append("();");
		addLine(line.toString());		
	}
	
	private void generateFunction(EncogProgramNode node) {
		addBreak();
		
		StringBuilder line = new StringBuilder();
		line.append("function ");
		line.append(node.getName());
		line.append("() {");
		indentLine(line.toString());
		
		generateForChildren(node);
		unIndentLine("}");
	}
	
	private void embedNetwork(EncogProgramNode node) {
		addBreak();
		
		File methodFile = (File)node.getArgs().get(0).getValue();
		
		MLMethod method = (MLMethod)EncogDirectoryPersistence.loadObject(methodFile);
		
		if( !(method instanceof MLFactory) ) {
			throw new EncogError("Code generation not yet supported for: " + method.getClass().getName());
		}
		
		FlatNetwork flat = ((ContainsFlat)method).getFlat();
		
		// header		
		StringBuilder line = new StringBuilder();
		line.append("public static MLMethod ");
		line.append(node.getName());
		line.append("() {");
		indentLine(line.toString());
		
		// create factory
		line.setLength(0);
		
		addLine("var network = ENCOG.BasicNetwork.create( null );");
        addLine("network.inputCount = " + flat.getInputCount()+";");
        addLine("network.outputCount = " + flat.getOutputCount()+";");
        addLine("network.layerCounts = "+toSingleLineArray(flat.getLayerCounts())+";");
        addLine("network.layerContextCount = "+toSingleLineArray(flat.getLayerContextCount())+";");
        addLine("network.weightIndex = "+toSingleLineArray(flat.getWeightIndex())+";");
        addLine("network.layerIndex = "+toSingleLineArray(flat.getLayerIndex())+";");
        addLine("network.activationFunctions = " + toSingleLineArray(flat.getActivationFunctions()) +";");
        addLine("network.layerFeedCounts = "+toSingleLineArray(flat.getLayerFeedCounts())+";");
        addLine("network.contextTargetOffset = "+toSingleLineArray(flat.getContextTargetOffset())+";");
        addLine("network.contextTargetSize = "+toSingleLineArray(flat.getContextTargetSize())+";");
        addLine("network.biasActivation = "+toSingleLineArray(flat.getBiasActivation())+";");
        addLine("network.beginTraining = "+flat.getBeginTraining()+";");
        addLine("network.endTraining="+flat.getEndTraining()+";");
        addLine("network.weights = WEIGHTS;");
        addLine("network.layerOutput = "+toSingleLineArray(flat.getLayerOutput())+";");
        addLine("network.layerSums = "+toSingleLineArray(flat.getLayerSums())+";");
		
		// return
		addLine("return network;");
		
		unIndentLine("}");		
	}
	
	private String toSingleLineArray(ActivationFunction[] activationFunctions) {
		StringBuilder result = new StringBuilder();
		result.append('[');
		for(int i=0;i<activationFunctions.length;i++) {
			if( i>0 ) {
				result.append(',');
			}
			
			ActivationFunction af = activationFunctions[i];
			if( af instanceof ActivationSigmoid ) {
				result.append("ENCOG.ActivationSigmoid.create()");
			} else if( af instanceof ActivationTANH ) {
				result.append("ENCOG.ActivationTANH.create()");
			} else if( af instanceof ActivationLinear ) {
				result.append("ENCOG.ActivationLinear.create()");
			} else if( af instanceof ActivationElliott ) {
				result.append("ENCOG.ActivationElliott.create()");
			} else if( af instanceof ActivationElliott ) {
				result.append("ENCOG.ActivationElliott.create()");
			} else {
				throw new AnalystCodeGenerationError("Unsupported activatoin function for code generation: " + af.getClass().getSimpleName());
			}
			
		}
		result.append(']');
		return result.toString();
	}
	
	private void generateConst(EncogProgramNode node) {
		StringBuilder line = new StringBuilder();
		line.append("var ");
		line.append(node.getName());
		line.append(" = \"");
		line.append(node.getArgs().get(0).getValue());
		line.append("\";");
		
		addLine(line.toString());		
	}
	
	private String toSingleLineArray(double[] d) {
		StringBuilder line = new StringBuilder();
		line.append("[");
		for(int i=0;i<d.length;i++) {
			line.append(CSVFormat.EG_FORMAT.format(d[i],Encog.DEFAULT_PRECISION) );
			if( i<(d.length-1) ) {
				line.append(",");
			}	
		}
		line.append("]");
		return line.toString();
	}
	
	private String toSingleLineArray(int[] d) {
		StringBuilder line = new StringBuilder();
		line.append("[");
		for(int i=0;i<d.length;i++) {
			line.append(d[i] );
			if( i<(d.length-1) ) {
				line.append(",");
			}	
		}
		line.append("]");
		return line.toString();
	}
	
	private void generateArrayInit(EncogProgramNode node) {
		StringBuilder line = new StringBuilder();
		line.append("var ");
		line.append(node.getName());
		line.append(" = [");
		indentLine(line.toString());
		
		double[] a = (double[])node.getArgs().get(0).getValue();
		
		line.setLength(0);
		
		int lineCount = 0;
		for(int i=0;i<a.length;i++) {
			line.append(CSVFormat.EG_FORMAT.format(a[i],Encog.DEFAULT_PRECISION) );
			if( i<(a.length-1) ) {
				line.append(",");
			}
			
			lineCount++;
			if( lineCount>=10 ) {
				addLine(line.toString());
				line.setLength(0);
				lineCount = 0;
			}
		}
		
		if( line.length()>0 ) {
			addLine(line.toString());
			line.setLength(0);
		}
		
		
		
		unIndentLine("];");
	}

	
	private void generateNode(EncogProgramNode node) {
		switch(node.getType()) {
			case Comment:
				generateComment(node);
				break;
			case Class:
				generateClass(node);
				break;
			case MainFunction:
				generateMainFunction(node);
				break;
			case Const:
				generateConst(node);
				break;
			case StaticFunction:
				generateFunction(node);
				break;
			case FunctionCall:
				generateFunctionCall(node);
				break;
			case CreateNetwork:
				embedNetwork(node);
				break;
			case InitArray:
				generateArrayInit(node);
				break;
			case EmbedTraining:
				embedTraining(node);
				break;
		}
	}
	
	private void embedTraining(EncogProgramNode node) {
		
		File dataFile = (File)node.getArgs().get(0).getValue();
		MLDataSet data = EncogUtility.loadEGB2Memory(dataFile);
		
		// generate the input data
		
		this.indentLine("var INPUT_DATA = [");
		for(MLDataPair pair: data) {
			MLData item = pair.getInput();
						
			StringBuilder line = new StringBuilder();
			
			NumberList.toList(CSVFormat.EG_FORMAT, line, item.getData());
			line.insert(0, "[ ");
			line.append(" ],");
			this.addLine(line.toString());
		}
		this.unIndentLine("];");
		
		this.addBreak();
		
		// generate the ideal data
		
		this.indentLine("var IDEAL_DATA = [");
		for(MLDataPair pair: data) {
			MLData item = pair.getIdeal();
						
			StringBuilder line = new StringBuilder();
			
			NumberList.toList(CSVFormat.EG_FORMAT, line, item.getData());
			line.insert(0, "[ ");
			line.append(" ],");
			this.addLine(line.toString());
		}
		this.unIndentLine("];");		
	}

	private void generateForChildren(EncogTreeNode parent) {
		for(EncogProgramNode node : parent.getChildren()) {
			generateNode(node);
		}
	}
	
	public void generate(EncogProgram program, boolean shouldEmbed) {
		if( !shouldEmbed ) {
			throw new AnalystCodeGenerationError("Must embed when generating Javascript");
		}
		this.embed = shouldEmbed;
		generateForChildren(program);		
	}
}
