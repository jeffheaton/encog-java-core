/*
 * Encog(tm) Core v3.3 - Java Version
 * http://www.heatonresearch.com/encog/
 * https://github.com/encog/encog-java-core
 
 * Copyright 2008-2014 Heaton Research, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *   
 * For more information on Heaton Research copyrights, licenses 
 * and trademarks visit:
 * http://www.heatonresearch.com/copyright
 */
package org.encog.ml.ea.rules;

import java.util.List;

import org.encog.ml.ea.genome.Genome;

/**
 * Holds a set of rules for an EA.
 */
public interface RuleHolder {
	/**
	 * Add a rewrite rule. Rewrite rules can be used to simplify genomes.
	 * 
	 * @param rule
	 *            The rule to add.
	 */
	void addRewriteRule(RewriteRule rule);
	
	/**
	 * Add a constraint rule.
	 * @param rule The rule to add.
	 */
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
