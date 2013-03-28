package org.encog.ml.ea.rules;

import org.encog.ml.ea.genome.Genome;

public interface RuleHolder {
	/**
	 * Add a rewrite rule. Rewrite rules can be used to simplify genomes.
	 * 
	 * @param rule
	 *            The rule to add.
	 */
	void addRewriteRule(RewriteRule rule);

	/**
	 * Rewrite the specified genome. The genome will still perform the same
	 * function, but it may be shorter.
	 * 
	 * @param prg
	 *            The genome to rewrite.
	 */
	void rewrite(Genome prg);
}
