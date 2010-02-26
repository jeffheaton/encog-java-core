package org.encog.neural.networks.training.neat;

import java.util.ArrayList;
import java.util.List;

public class NEATSpecies {

	  private NEATGenome leader;	 
	  private final List<NEATGenome> members = new ArrayList<NEATGenome>();	 
	  private int speciesID;	
	  private double bestFitness;
	  private int gensNoImprovement;
	  private int age;
	  private double spawnsRequired;
	  
	  public NEATSpecies(
        NEATGenome first,
        int speciesID)
	  {
		  this.speciesID = speciesID;
		  this.bestFitness = first.getFitness();
		  this.gensNoImprovement = 0;
		  this.age = 0;
		  this.leader = first;
		  this.spawnsRequired = 0;
		  this.members.add(first);
	  }
	
	  public void adjustFitness() {
		// TODO Auto-generated method stub		
	}

	public NEATGenome getLeader() {
		return leader;
	}

	public void setLeader(NEATGenome leader) {
		this.leader = leader;
	}

	public double getBestFitness() {
		return bestFitness;
	}

	public void setBestFitness(double bestFitness) {
		this.bestFitness = bestFitness;
	}

	public int getGensNoImprovement() {
		return gensNoImprovement;
	}

	public void setGensNoImprovement(int gensNoImprovement) {
		this.gensNoImprovement = gensNoImprovement;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public double getSpawnsRequired() {
		return spawnsRequired;
	}

	public void setSpawnsRequired(double spawnsRequired) {
		this.spawnsRequired = spawnsRequired;
	}

	public List<NEATGenome> getMembers() {
		return members;
	}

	public int getSpeciesID() {
		return speciesID;
	}

	public void addMember(NEATGenome genome) {
		// TODO Auto-generated method stub
		
	}

	public void calculateSpawnAmount() {
		// TODO Auto-generated method stub
		
	}
	  
}
