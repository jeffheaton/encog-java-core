package org.encog.ml.ea.rules;

import java.util.ArrayList;
import java.util.List;

import org.encog.ml.ea.genome.Genome;

public class BasicRuleHolder implements RuleHolder {
	/**
	 * Rewrite rules that can simplify genomes.
	 */
	private final List<RewriteRule> rewriteRules = new ArrayList<RewriteRule>();
	
	/**
	 * Rewrite rules that can simplify genomes.
	 */
	private final List<ConstraintRule> constraintRules = new ArrayList<ConstraintRule>();

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addRewriteRule(final RewriteRule rule) {
		this.rewriteRules.add(rule);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void rewrite(final Genome prg) {

		boolean done = false;

		while (!done) {
			done = true;

			for (final RewriteRule rule : this.rewriteRules) {
				if (rule.rewrite(prg)) {
					done = false;
				}
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addConstraintRule(ConstraintRule rule) {
		this.constraintRules.add(rule);
		
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isValid(Genome genome) {
		for(ConstraintRule rule: this.constraintRules) {
			if(!rule.isValid(genome)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<ConstraintRule> getConstraintRules() {
		return this.constraintRules;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<RewriteRule> getRewriteRules() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
