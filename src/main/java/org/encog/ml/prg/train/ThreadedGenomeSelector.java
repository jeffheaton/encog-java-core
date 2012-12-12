package org.encog.ml.prg.train;

import java.util.HashSet;
import java.util.Set;

import org.encog.ml.prg.EncogProgram;

public class ThreadedGenomeSelector {
	private PrgGenetic owner;
	private Set<EncogProgram> used = new HashSet<EncogProgram>();

	public ThreadedGenomeSelector(PrgGenetic theOwner) {
		this.owner = theOwner;
	}

	public EncogProgram selectGenome() {
		EncogProgram result = null;

		synchronized (this) {
			while (result == null) {
				int selectedID = owner.getSelection().performSelection();
				EncogProgram potentialSelection = owner.getPopulation()
						.getMembers()[selectedID];
				if (!used.contains(potentialSelection)) {
					used.add(potentialSelection);
					result = potentialSelection;
				}
			}
			return result;
		}
	}
	
	public EncogProgram antiSelectGenome() {
		EncogProgram result = null;

		synchronized (this) {
			while (result == null) {
				int selectedID = owner.getSelection().performAntiSelection();
				EncogProgram potentialSelection = owner.getPopulation()
						.getMembers()[selectedID];
				if (!used.contains(potentialSelection)) {
					used.add(potentialSelection);
					result = potentialSelection;
				}
			}
			return result;
		}
	}
	
	public void releaseGenome(EncogProgram genome) {
		synchronized(this) {
			this.used.remove(genome);
		}
	}
	
	public void clear() {
		synchronized(this) {
			this.used.clear();
		}
	}
}
