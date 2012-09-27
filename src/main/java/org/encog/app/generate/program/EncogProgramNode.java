package org.encog.app.generate.program;

import java.util.ArrayList;
import java.util.List;

import org.encog.ml.MLMethod;

public class EncogProgramNode extends EncogTreeNode {	
	private final List<EncogProgramArg> args = new ArrayList<EncogProgramArg>();	
	private final NodeType type;
	private final String name;
	
	public EncogProgramNode(EncogProgram theProgram, EncogTreeNode theParent, NodeType theNodeType, String theName) {
		super( theProgram, theParent);
		this.type = theNodeType;
		this.name = theName;
	}

	public List<EncogProgramArg> getArgs() {
		return args;
	}
	
	public void addArg(int argValue) {
		EncogProgramArg arg = new EncogProgramArg(argValue);
		this.args.add(arg);
	}
		
	public NodeType getType() {
		return type;
	}
	
	

	public String getName() {
		return name;
	}

	public void addArg(double argValue) {
		EncogProgramArg arg = new EncogProgramArg(argValue);
		this.args.add(arg);
	}
	
	public void addArg(String argValue) {
		EncogProgramArg arg = new EncogProgramArg(argValue);
		this.args.add(arg);
	}
	
	public void addArg(Object argValue) {
		EncogProgramArg arg = new EncogProgramArg(argValue);
		this.args.add(arg);
	}
	
	public EncogProgramNode createFunction(String theName) {
		EncogProgramNode node = new EncogProgramNode(getProgram(), this,
				NodeType.StaticFunction, theName);
		this.getChildren().add(node);
		return node;
	}

	public EncogProgramNode createMainFunction() {
		EncogProgramNode node = new EncogProgramNode(getProgram(), this,
				NodeType.MainFunction, null);
		this.getChildren().add(node);
		return node;
	}

	public void defineConst(EncogArgType type, String name, String value) {
		EncogProgramNode node = new EncogProgramNode(getProgram(), this,
				NodeType.Const, name);
		node.addArg(value);
		node.addArg(type.toString());
		this.getChildren().add(node);		
	}

	public EncogProgramNode createFunctionCall(EncogProgramNode funct, String returnType, String returnVariable) {
		EncogProgramNode node = new EncogProgramNode(getProgram(), this,
				NodeType.FunctionCall, funct.getName());
		node.addArg(returnType);
		node.addArg(returnVariable);
		this.getChildren().add(node);
		return node;
		
	}

	public EncogProgramNode createNetworkFunction(String name, MLMethod method) {
		EncogProgramNode node = new EncogProgramNode(getProgram(), this,
				NodeType.CreateNetwork, name);
		node.addArg(method);
		this.getChildren().add(node);
		return node;
	}

	public EncogProgramNode createArray(String name, double[] a) {
		EncogProgramNode node = new EncogProgramNode(getProgram(), this,
				NodeType.InitArray, name);
		node.addArg(a);
		this.getChildren().add(node);
		return node;
	}	
}
