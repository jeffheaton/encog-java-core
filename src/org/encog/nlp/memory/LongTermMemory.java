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
 */
package org.encog.nlp.memory;

import java.util.*;
import java.io.*;

import org.encog.EncogError;
import org.encog.nlp.NLPError;
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
public class LongTermMemory {
    private RelationHolder relations;
    private ConceptHolder concepts;
    
    /**
	 * The logging object.
	 */
	@SuppressWarnings("unused")
	final private Logger logger = LoggerFactory.getLogger(this.getClass());
    
    public LongTermMemory() {
        relations = new RelationHolder();
        concepts = new ConceptHolder();  
    }
    
    public RelationHolder getRelations()
    {
        return relations;
    }
    
    public ConceptHolder getConcepts()
    {
        return concepts;
    }
    
    public void save(OutputStream os)
    {
        PrintStream ps = new PrintStream(os);
        
        for(VarConcept concept:concepts.getConcepts())
        {        
            ps.println( concept.save() );
        }       
        
        for(Relation relation:relations.getRelations())
        {
            ps.println( relation.save() );
        }
        ps.close();
    }
    
    public void save(String path)
    {
        FileOutputStream f;
		try {
			f = new FileOutputStream(path);
	        save(f);
		} catch (FileNotFoundException e) {
			if( logger.isErrorEnabled() )
        	{        		
        		logger.error("Exception",e);
        	}
			throw new NLPError(e);
		}

    }
    
    private void loadConcept(String line)
    {
        StringTokenizer tok = new StringTokenizer(line,"|");
        tok.nextElement();
        String strNum = (String)tok.nextElement ();
        
        String s = (String)tok.nextElement ();
        
        long serialNumber = 0;
        try
        {
            serialNumber = Long.parseLong(strNum);
        }
        catch(NumberFormatException e)
        {
        	String str = "Illegal concept number:"+strNum;
        	if( logger.isErrorEnabled() )
        	{        		
        		logger.error(str);
        	}
            throw new FormatError(str);
        }
        VarConcept lconcept = new VarConcept(serialNumber,s);     
        concepts.load(lconcept);
    }
    
    private void loadRelation(String line)
    {
        StringTokenizer tok = new StringTokenizer(line,"|");
        String strSource,strType,strTarget;
        
        try
        {
            tok.nextElement();
            strSource = tok.nextToken();
            strType = tok.nextToken();
            strTarget = tok.nextToken();
        }
        catch(NoSuchElementException e)
        {
        	String str = "Not enough arguments on line: " + line;
        	if( logger.isErrorEnabled() )
        	{        		
        		logger.error(str);
        	}
            throw new FormatError(str );
        }
        
        long sourceNum =0,typeNum =0,targetNum =0;
        
        try
        {        	
            sourceNum = Long.parseLong(strSource);
        }
        catch(NumberFormatException e)
        {
        	String str = "Illegal source on line:" + line;
        	if( logger.isErrorEnabled() )
        	{        		
        		logger.error(str);
        	}
            throw new FormatError(str);
        }
        
        try
        {
            typeNum = Long.parseLong(strType);
        }
        catch(NumberFormatException e)
        {
        	String str = "Illegal type on line:" + line;
        	if( logger.isErrorEnabled() )
        	{        		
        		logger.error(str);
        	}
            throw new FormatError(str);
        }        
        
        try
        {
            targetNum = Long.parseLong(strTarget);
        }
        catch(NumberFormatException e)
        {
        	String str = "Illegal target on line:" + line;
        	if( logger.isErrorEnabled() )
        	{        		
        		logger.error(str);
        	}
            throw new FormatError(str);
        }                
        
        Concept source = concepts.find(sourceNum);
        Concept type = concepts.find(typeNum);
        Concept target = concepts.find(targetNum);
        
        Relation relation = new Relation(source,type,target);
        relations.add(relation);
    }
    
    public void load(InputStream is)
    {
    	try
    	{
        String line;
        BufferedReader br = new BufferedReader(new InputStreamReader(is));        
        
        while( (line=br.readLine())!=null )
        {
            line = line.trim();
            switch(line.charAt(0))
            {
                case 'C':
                    loadConcept(line);
                    break;
                case 'R':
                    loadRelation(line);
                    break;
            }
        }
    	}
    	catch(IOException e)
    	{
    		if( logger.isErrorEnabled() )
    		{
    			logger.error("Exception",e);
    		}
    		throw new EncogError(e);
    	}
    }

    public void load(String path)
    {
    	try
    	{
        FileInputStream f = new FileInputStream(path);
        load(f);
    	}
    	catch(IOException e)
    	{
    		if( logger.isErrorEnabled() )
    		{
    			logger.error("Exception",e);
    		}
    		throw new NLPError(e);
    	}
    }
        
    public void addRelation(Concept source,Concept type,Concept target)
    {
        // now add the relation
        Relation relation = new Relation();
        relation.setSource(source);
        relation.setType(type);
        relation.setTarget(target);
        relations.add(relation);
    }
    
    public void addRelation(String source,Concept type,Concept target)
    {
        addRelation(concepts.create(source) , type,target);
    }
    
    public void addRelation(RelationHolder holder)
    {
        relations.add(holder);
    }
}
