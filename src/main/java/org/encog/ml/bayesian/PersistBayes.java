/*
 * Encog(tm) Core v3.0 - Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 
 * Copyright 2008-2011 Heaton Research, Inc.
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
package org.encog.ml.bayesian;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import org.encog.Encog;
import org.encog.ml.bayesian.query.BayesianQuery;
import org.encog.ml.bayesian.query.enumerate.EnumerationQuery;
import org.encog.ml.bayesian.query.sample.EventState;
import org.encog.ml.bayesian.query.sample.SamplingQuery;
import org.encog.ml.bayesian.table.TableLine;
import org.encog.persist.EncogFileSection;
import org.encog.persist.EncogPersistor;
import org.encog.persist.EncogReadHelper;
import org.encog.persist.EncogWriteHelper;

/**
 * Persist a SVM.
 */
public class PersistBayes implements EncogPersistor {


	/**
	 * @return The file version.
	 */
	@Override
	public final int getFileVersion() {
		return 1;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final Object read(final InputStream is) {
		final BayesianNetwork result = new BayesianNetwork();
		final EncogReadHelper in = new EncogReadHelper(is);
		EncogFileSection section;
		String queryType = "";
		String queryStr = "";

		while ((section = in.readNextSection()) != null) {
			if (section.getSectionName().equals("BAYES-NETWORK")
					&& section.getSubSectionName().equals("BAYES-PARAM")) {
				final Map<String, String> params = section.parseParams();
				queryType = params.get("queryType");
				queryStr = params.get("query");
			}
			if (section.getSectionName().equals("BAYES-NETWORK")
					&& section.getSubSectionName().equals("BAYES-EVENT")) {
				for (String line : section.getLines()) {
					List<String> cols = EncogFileSection.splitColumns(line);
					String label = cols.get(0);
					result.createEvent(label);
				}
			}
			if (section.getSectionName().equals("BAYES-NETWORK")
					&& section.getSubSectionName().equals("BAYES-TABLE")) {
				
				// first, define relationships (1st pass)
				for( String line : section.getLines() ) {
					result.defineRelationship(line);					
				}
				
				result.finalizeStructure();
				
				// now define the probabilities (2nd pass)
				for( String line : section.getLines() ) {
					result.defineProbability(line);					
				}							
			}
		}
		
		// define query, if it exists
		if( queryType.length()>0) {
			BayesianQuery query = null;
			if( queryType.equals("EnumerationQuery")) {
				query = new EnumerationQuery(result);
			} else {
				query = new SamplingQuery(result);
			}
			
			if( query!=null ) {
				result.setQuery(query);
				result.defineQuery(queryStr);
			}
		}
		
		

		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void save(final OutputStream os, final Object obj) {
		final EncogWriteHelper out = new EncogWriteHelper(os);
		final BayesianNetwork b = (BayesianNetwork) obj;
		out.addSection("BAYES-NETWORK");
		out.addSubSection("BAYES-PARAM");
		String queryType = "";
		String queryStr = "";
		if( b.getQuery()!=null ) {
			queryType = b.getQuery().getClass().getSimpleName();
			queryStr = b.getQuery().getProblem();
		}
		out.writeProperty("queryType", queryType);
		out.writeProperty("query", queryStr);
		out.addSubSection("BAYES-EVENT");
		for( BayesianEvent event: b.getEvents()) {
			out.addColumn(event.getLabel());
			for(String str: event.getChoices() ) {
				out.addColumn(str);
			}			
			out.writeLine();
		}
		out.addSubSection("BAYES-TABLE");
		for( BayesianEvent event: b.getEvents()) {
			for( TableLine line : event.getTable().getLines() ) {
				StringBuilder str = new StringBuilder();
				str.append("P(");
				
				if( event.isBoolean()) {
					if( Math.abs(line.getResult())<Encog.DEFAULT_DOUBLE_EQUAL) {
						str.append("-");
					} else {
						str.append("+");
					}
				}
				str.append(event.getLabel());
				if( !event.isBoolean()) {
						str.append("=");
						str.append(line.getResult());
				}
				
				if( event.getParents().size()>0) {
					str.append("|");
				}
				
				int index = 0;
				boolean first = true;
				for( BayesianEvent parentEvent : event.getParents()) {
					if( !first ) {
						str.append(",");						
					}
					first = false;
					double arg = line.getArguments()[index];
					if( parentEvent.isBoolean() ) {
						if( Math.abs(arg)<Encog.DEFAULT_DOUBLE_EQUAL ) {
							str.append("-");
						} else {
							str.append("+");
						}
					}
					str.append(parentEvent.getLabel());
					if( !parentEvent.isBoolean() ) {
						str.append("=");
						str.append(arg);
					}
				}
				str.append(")=");
				str.append(line.getProbability());
				str.append("\n");
				out.write(str.toString());
			}
		}
				
		out.flush();
	}

	@Override
	public String getPersistClassString() {
		return "BayesianNetwork";
	}
}
