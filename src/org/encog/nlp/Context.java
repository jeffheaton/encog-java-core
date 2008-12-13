/*
 * Encog Neural Network and Bot Library for Java v1.x
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 * 
 * Copyright 2008, Heaton Research Inc., and individual contributors.
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
 */
package org.encog.nlp;

import java.io.IOException;

import org.encog.nlp.memory.Concept;
import org.encog.nlp.memory.ConstConcept;
import org.encog.nlp.memory.LongTermMemory;
import org.encog.nlp.memory.RomMemory;
import org.encog.nlp.memory.VarConcept;
import org.encog.nlp.reason.EvaluateText;
import org.encog.parse.Parse;
import org.encog.parse.units.UnitManager;


public class Context {

	private LongTermMemory memory;
	private EvaluateText evaluate;
	private Parse parse;
	private UnitManager units;
	
	private void initParse() 
	{
		try
		{
	    UnitManager units = new UnitManager();
	    //units.load(basePath+"units.xml");

	    parse = new Parse();
	    
	    Parse.setUnitMananger(units);
	    parse.load();
	    units.createRecognizers(parse);
		}
		catch(IOException e)
		{
			throw(new NLPError(e));
		}
	    		
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

	public void init() throws IOException
	{
		initParse();
		initNLP();
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
