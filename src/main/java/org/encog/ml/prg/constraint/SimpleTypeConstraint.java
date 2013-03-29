package org.encog.ml.prg.constraint;

import org.encog.ml.ea.genome.Genome;
import org.encog.ml.prg.EncogProgram;
import org.encog.ml.prg.ProgramNode;
import org.encog.ml.prg.expvalue.ValueType;

public class SimpleTypeConstraint implements PrgConstraintRule {

	@Override
	public boolean isValid(Genome genome) {
		EncogProgram prg = (EncogProgram)genome;
		ValueType ret = prg.getReturnType();
		ProgramNode root = prg.getRootNode();
		return root.getTemplate().returnsType(root, ret);
	}

}
