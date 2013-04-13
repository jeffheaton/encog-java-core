package org.encog.ml.prg.extension;

import java.io.Serializable;
import java.util.List;
import java.util.Random;

import org.encog.ml.prg.EncogProgramContext;
import org.encog.ml.prg.ProgramNode;
import org.encog.ml.prg.expvalue.ExpressionValue;
import org.encog.ml.prg.expvalue.ValueType;

/**
 * Defines an opcode.  Opcodes are used to extend Encog programs.  
 */
public interface ProgramExtensionTemplate extends Serializable {
	/**
	 * Defines a very low precidence.
	 */
	public static final int NO_PREC = 100;
	
	/**
	 * @return Get the name of this opcode.
	 */
	String getName();
	
	/**
	 * @return Get the number of child nodes that this opcode requires.
	 */
	int getChildNodeCount();
	
	/**
	 * Evaluate the specified actual program node, using this opcode template.
	 * @param actual The tree node in the actual program.
	 * @return The result of the evaluation.
	 */
	ExpressionValue evaluate(ProgramNode actual);
	
	/**
	 * @return Returns true if this node is variable.
	 */
	boolean isVariable();
	
	/**
	 * Randomize this actual tree node.
	 * @param rnd Random number generator.
	 * @param desiredType The desired type of the randomization, if allowed.
	 * @param actual The actual program node to randomize.
	 * @param minValue The minimum value to use for randomization.
	 * @param maxValue The maximum value to use for randomization.
	 */
	void randomize(Random rnd, List<ValueType> desiredType, ProgramNode actual, double minValue, double maxValue);
	
	/**
	 * @return The size of extra data that is stored by this node.
	 */
	int getDataSize();
	
	/**
	 * @return The node type.
	 */
	NodeType getNodeType();
	
	/**
	 * @return The operator precedence.
	 */
	int getPrecedence();
	
	/**
	 * @return The return value for this opcode.
	 */
	ParamTemplate getReturnValue();
	
	/**
	 * @return The parameters (child nodes) required by this node.
	 */
	List<ParamTemplate> getParams();
	
	/**
	 * Determines if the specified return type is a possible return type.
	 * @param context The program context.
	 * @param rtn The potential return type to check.
	 * @return True, if the specified type is a possible return type.
	 */
	boolean isPossibleReturnType(EncogProgramContext context, ValueType rtn);
}
