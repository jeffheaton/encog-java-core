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
package org.encog.nlp.memory;

import java.util.*;
import java.io.*;

/**
 *
 * @author  jheaton
 */
public class LongTermMemory {
    private RelationHolder relations;
    private ConceptHolder concepts;
    
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
    throws FileNotFoundException
    {
        FileOutputStream f = new FileOutputStream(path);
        save(f);
    }
    
    private void loadConcept(String line)
    throws FormatException
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
            throw new FormatException("Illegal concept number:"+strNum);
        }
        VarConcept lconcept = new VarConcept(serialNumber,s);     
        concepts.load(lconcept);
    }
    
    private void loadRelation(String line)
    throws FormatException,ConceptNotFoundException
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
            throw new FormatException("Not enough arguments on line: " + line );
        }
        
        long sourceNum =0,typeNum =0,targetNum =0;
        
        try
        {
            sourceNum = Long.parseLong(strSource);
        }
        catch(NumberFormatException e)
        {
            throw new FormatException("Illegal source on line:" + line);
        }
        
        try
        {
            typeNum = Long.parseLong(strType);
        }
        catch(NumberFormatException e)
        {
            throw new FormatException("Illegal type on line:" + line);
        }        
        
        try
        {
            targetNum = Long.parseLong(strTarget);
        }
        catch(NumberFormatException e)
        {
            throw new FormatException("Illegal target on line:" + line);
        }                
        
        Concept source = concepts.find(sourceNum);
        Concept type = concepts.find(typeNum);
        Concept target = concepts.find(targetNum);
        
        Relation relation = new Relation(source,type,target);
        relations.add(relation);
    }
    
    public void load(InputStream is)
    throws IOException,FormatException,ConceptNotFoundException
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

    public void load(String path)
    throws FileNotFoundException,IOException,FormatException,ConceptNotFoundException
    {
        FileInputStream f = new FileInputStream(path);
        load(f);
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
