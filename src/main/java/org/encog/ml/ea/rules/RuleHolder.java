package org.encog.ml.ea.rules;

import java.util.List;

import org.encog.ml.ea.genome.Genome;

public interface RuleHolder {
	/**
	 * Add a rewrite rule. Rewrite rules can be used to simplify genomes.
	 * 
	 * @param rule
	 *            The rule to add.
	 */
	void addRewriteRule(RewriteRule rule);
	
	void addConstraintRule(ConstraintRule rule);

	/**
	 * Rewrite the specified genome. The genome will still perform the same
	 * function, but it may be shorter.
	 * 
	 * @param genome
	 *            The genome to rewrite.
	 */
	void rewrite(Genome genome);
	
	/**
	 * Determine if the specified genome is valid according to the constraint rules.
	 * @param genome The gnome to check.
	 * @return True, if the genome is valid.
	 */
	boolean isValid(Genome genome);
	
	/**
	 * @return The list of constraints.
	 */
	List<ConstraintRule> getConstraintRules();
	
	/**
	 * @return The rewrite rules.
	 */
	List<RewriteRule> getRewriteRules();
}
