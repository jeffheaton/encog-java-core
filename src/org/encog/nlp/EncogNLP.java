/*
 * Encog Artificial Intelligence Framework v2.x
 * Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 * 
 * Copyright 2008-2009, Heaton Research Inc., and individual contributors.
 * See the copyright.txt in the distribution for a full listing of 
 * individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */package org.encog.nlp;

import org.encog.nlp.lexicon.EncogLexicon;
import org.encog.nlp.memory.Concept;
import org.encog.nlp.memory.ConstConcept;
import org.encog.nlp.memory.LongTermMemory;
import org.encog.nlp.memory.RomMemory;
import org.encog.nlp.memory.VarConcept;
import org.encog.nlp.reason.EvaluateText;
import org.encog.parse.Parse;
import org.encog.parse.units.UnitManager;
import org.encog.util.orm.ORMSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * 
 * Note: This class is part of the Encog Natural Language Processing(NLP)
 * package.  This package is still under heavy construction, and will not 
 * be considered stable until Encog 3.0.
 * 
 * @author jheaton
 *
 */
public class EncogNLP {
	/**
	 * The logging object.
	 */
	@SuppressWarnings("unused")
	final private Logger logger = LoggerFactory.getLogger(this.getClass());

	private LongTermMemory memory;
	private EvaluateText evaluate;
	private Parse parse;
	private UnitManager units;
	private EncogLexicon lexicon;
	private ORMSession session;
	
	public EncogNLP(ORMSession session)
	{
		this.session = session;		
		this.lexicon = new EncogLexicon(this.session);		
    	this.lexicon.loadCache();
		initParse();
		initNLP();		
	}
	
	private void initParse() 
	{
	    UnitManager units = new UnitManager();
	    //units.load(basePath+"units.xml");

	    parse = new Parse();
	    
	    Parse.setUnitMananger(units);
	    parse.load();
	    units.createRecognizers(parse);	    		
	}
	
	private void initNLP()
	{
		memory = new LongTermMemory();
		evaluate = new EvaluateText(this);
		RomMemory.load(memory);
	}
	
	public EvaluateText getEvaluate() {
		return evaluate;
	}

	public void setEvaluate(EvaluateText evaluate) {
		this.evaluate = evaluate;
	}

	public LongTermMemory getMemory() {
		return memory;
	}

	public void setMemory(LongTermMemory memory) {
		this.memory = memory;
	}

	public Parse getParse() {
		return parse;
	}

	public void setParse(Parse parse) {
		this.parse = parse;
	}

	public UnitManager getUnits() {
		return units;
	}

	public void setUnits(UnitManager units) {
		this.units = units;
	}
	
    public EncogLexicon getLexicon() {
		return lexicon;
	}

	public Concept getTypeConcept(String word)
    {
        if( word.equalsIgnoreCase("has") || word.equalsIgnoreCase("have") )
            return ConstConcept.CONCEPT_HAS;
        else if( word.equalsIgnoreCase("produces") || word.equalsIgnoreCase("produce") || word.equalsIgnoreCase("bears") )
            return ConstConcept.CONCEPT_CREATE;        
        else if( word.equalsIgnoreCase("is") )
            return ConstConcept.CONCEPT_SUBTYPE;
        else if( word.equalsIgnoreCase("of") )
            return ConstConcept.CONCEPT_OF;
        else
        {
            VarConcept concept = memory.getConcepts().create(word);
            return concept;
        }
    }
	
}
