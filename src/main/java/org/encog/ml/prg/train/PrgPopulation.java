package org.encog.ml.prg.train;

import org.encog.ml.ea.genome.Genome;
import org.encog.ml.ea.population.BasicPopulation;
import org.encog.ml.prg.EncogProgram;
import org.encog.ml.prg.EncogProgramContext;
import org.encog.ml.prg.epl.EPLHolder;
import org.encog.parse.expression.common.RenderCommonExpression;

public class PrgPopulation extends BasicPopulation {

	private final EncogProgramContext context;
	private EPLHolder holder;
	
	public PrgPopulation(EncogProgramContext theContext) {
		super(theContext.getParams().getPopulationSize(), new PrgGenomeFactory(theContext));
		GeneticTrainingParams params = theContext.getParams();
		this.holder = theContext.getHolderFactory().factor(params.getPopulationSize(), params.getMaxIndividualSize());
		this.context = theContext;
	}
		
	public void dumpMembers(int i) {
		
		RenderCommonExpression render = new RenderCommonExpression();
		
		int index = 0;
		for(Genome obj: getGenomes()) {
			EncogProgram prg = (EncogProgram)obj;
			System.out.println(index + ": Score " + prg.getScore() + " : " + render.render(prg));
			index++;
			if( index>i) {
				break;
			}
		}
	}

	public EncogProgramContext getContext() {
		return context;
	}

	/**
	 * @return the holder
	 */
	public EPLHolder getHolder() {
		return holder;
	}
	
	@Override
	public int getMaxIndividualSize() {
		return getHolder().getMaxIndividualSize();
	}
}
