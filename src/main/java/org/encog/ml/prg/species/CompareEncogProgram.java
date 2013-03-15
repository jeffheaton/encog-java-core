package org.encog.ml.prg.species;

import org.encog.ml.prg.EncogProgram;
import org.encog.ml.prg.ProgramNode;

public class CompareEncogProgram {
	
	public double compare(EncogProgram prg1, EncogProgram prg2) {
		return compareNode(0, prg1.getRootNode(),prg2.getRootNode());
	}

	private double compareNode(double result, ProgramNode node1, ProgramNode node2) {
		double newResult = result;
		
		if( node1.getTemplate()!=node2.getTemplate() ) {
			newResult++;
		}
		
		int node1Size = node1.getChildNodes().size();
		int node2Size = node2.getChildNodes().size();
		int childNodeCount = Math.max(node1Size, node2Size);
		
		
		for(int i=0;i<childNodeCount;i++) {
			if( i<node1Size && i<node2Size) {
				ProgramNode childNode1 = node1.getChildNode(i);
				ProgramNode childNode2 = node2.getChildNode(i);
				newResult=compareNode(newResult,childNode1,childNode2);
			} else {
				newResult++;
			}
		}
		
		return newResult;
	}
}
