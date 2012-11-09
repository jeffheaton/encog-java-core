package org.encog.ml.prg.train;

import java.util.ArrayList;
import java.util.List;

import org.encog.ml.prg.EncogProgram;
import org.encog.ml.prg.EncogProgramContext;
import org.encog.parse.expression.common.RenderCommonExpression;

public class PrgPopulation {

	private final EncogProgramContext context;
	private final List<EncogProgram> members = new ArrayList<EncogProgram>();
	private int maxDepth = 5;
	private int maxPopulation = 1000;
	
	public PrgPopulation(EncogProgramContext theContext) {
		this.context = theContext;
	}
	
	public void createRandomPopulation(int maxDepth) {
		CreateRandom rnd = new CreateRandom(context,this.maxDepth);
		this.members.clear();
		for(int i=0;i<this.maxPopulation;i++) {
			this.members.add(rnd.generate());
		}
	}
	
	public void dumpMembers() {
		
		RenderCommonExpression render = new RenderCommonExpression();
		
		int index = 0;
		for(EncogProgram prg: this.members) {
			System.out.println(index + " : " + render.render(prg));
			index++;
		}
	}

	public int getMaxDepth() {
		return maxDepth;
	}

	public void setMaxDepth(int maxDepth) {
		this.maxDepth = maxDepth;
	}

	public int getMaxPopulation() {
		return maxPopulation;
	}

	public void setMaxPopulation(int maxPopulation) {
		this.maxPopulation = maxPopulation;
	}

	public EncogProgramContext getContext() {
		return context;
	}

	public List<EncogProgram> getMembers() {
		return members;
	}
	
	
	
	
}
