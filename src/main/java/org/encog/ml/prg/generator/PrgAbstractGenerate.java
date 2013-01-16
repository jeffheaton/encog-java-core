package org.encog.ml.prg.generator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.encog.ml.CalculateScore;
import org.encog.ml.ea.genome.Genome;
import org.encog.ml.ea.population.Population;
import org.encog.ml.genetic.GeneticError;
import org.encog.ml.prg.EncogProgram;
import org.encog.ml.prg.EncogProgramContext;
import org.encog.ml.prg.EncogProgramVariables;
import org.encog.ml.prg.epl.EPLHolder;
import org.encog.ml.prg.extension.ProgramExtensionTemplate;
import org.encog.ml.prg.train.PrgPopulation;

public abstract class PrgAbstractGenerate implements PrgPopulationGenerator {
	private final EncogProgramContext context;
	private final CalculateScore scoreFunction;
	private final int maxDepth;
	private final List<ProgramExtensionTemplate> leafNodes = new ArrayList<ProgramExtensionTemplate>();
	private final List<ProgramExtensionTemplate> branchNodes = new ArrayList<ProgramExtensionTemplate>();
	private final List<ProgramExtensionTemplate> allNodes = new ArrayList<ProgramExtensionTemplate>();
	private boolean requireVariable = true;
	private boolean requireUnique = true;
	
	public PrgAbstractGenerate(EncogProgramContext theContext, CalculateScore theScoreFunction, int theMaxDepth) {
		this.context = theContext;
		this.maxDepth = theMaxDepth;
		this.scoreFunction = theScoreFunction;
		
		for(ProgramExtensionTemplate temp : this.context.getFunctions().getOpCodes() ) {
			this.allNodes.add(temp);
			if( temp.getChildNodeCount()==0 ) {
				this.leafNodes.add(temp);
			} else {
				this.branchNodes.add(temp);
			}
		}
	}
	
	public void generate(Random random, Genome genome) {
		EncogProgram program = (EncogProgram)genome;
		program.clear();
		createNode(random, program,0,getMaxDepth());
	}
	
	protected void createLeafNode(Random random, EncogProgram program) {
		int opCode = random.nextInt(this.leafNodes.size());
		ProgramExtensionTemplate temp = this.leafNodes.get(opCode);
		temp.randomize(random, program, 1.0);
	}
	
	private void generateGenome(Random rnd, Population pop, EncogProgram prg, Set<String> populationContents) {
		boolean done;
		String key = "";
		int maxTries = 100000;
		
		do {
			maxTries--;
			if( maxTries<0 ) {
				throw new GeneticError("Endless loop generating population.  You are likely creating a population size too small and requring uniqueness or variables.");
			}
			prg.clear();
			this.createNode(rnd, prg, 0, getMaxDepth());
			pop.rewrite(prg);
			
			done = true;
			
			// is the program unique?
			if( this.requireUnique ) {
				key = prg.toBase64();
				if( populationContents.contains(key)) {
					done = false;
				}
			}
			
			// does the program contain a variable?
			if( done && !prg.hasVariable() ) {
				done = false;
			}
			
			// does program produce a valid score?
			if ( done && getScoreFunction() != null) {
				double score = getScoreFunction().calculateScore(prg);
				
				if ( Double.isInfinite(score) || Double.isNaN(score)) {
					done = false;
				} else {
					prg.setScore(score);
				}
			}
			
		} while (!done);
		
		if( this.requireUnique ) {
			populationContents.add(key);
		}
	}
		
	@Override
	public void generate(Random rnd, Population pop) {
		Set<String> populationContents = new HashSet<String>();
		EPLHolder holder = ((PrgPopulation) pop).getHolder();

		pop.getGenomes().clear();
		for (int i = 0; i < pop.getPopulationSize(); i++) {
			EncogProgram prg = new EncogProgram(getContext(),
					new EncogProgramVariables(), holder, i);
			pop.getGenomes().add(prg);
			generateGenome(rnd,pop,prg,populationContents);
		}
	}

	/**
	 * @return the context
	 */
	public EncogProgramContext getContext() {
		return context;
	}

	/**
	 * @return the maxDepth
	 */
	@Override
	public int getMaxDepth() {
		return maxDepth;
	}

	/**
	 * @return the leafNodes
	 */
	public List<ProgramExtensionTemplate> getLeafNodes() {
		return leafNodes;
	}

	/**
	 * @return the branchNodes
	 */
	public List<ProgramExtensionTemplate> getBranchNodes() {
		return branchNodes;
	}

	/**
	 * @return the allNodes
	 */
	public List<ProgramExtensionTemplate> getAllNodes() {
		return allNodes;
	}

	/**
	 * @return the scoreFunction
	 */
	public CalculateScore getScoreFunction() {
		return scoreFunction;
	}

	/**
	 * @return the requireVariable
	 */
	public boolean isRequireVariable() {
		return requireVariable;
	}

	/**
	 * @param requireVariable the requireVariable to set
	 */
	public void setRequireVariable(boolean requireVariable) {
		this.requireVariable = requireVariable;
	}

	/**
	 * @return the requireUnique
	 */
	public boolean isRequireUnique() {
		return requireUnique;
	}

	/**
	 * @param requireUnique the requireUnique to set
	 */
	public void setRequireUnique(boolean requireUnique) {
		this.requireUnique = requireUnique;
	}
	
	
	
}
