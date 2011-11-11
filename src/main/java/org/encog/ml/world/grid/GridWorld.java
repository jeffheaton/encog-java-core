package org.encog.ml.world.grid;

import java.util.ArrayList;
import java.util.List;

import org.encog.ml.world.Action;
import org.encog.ml.world.WorldAgent;
import org.encog.ml.world.basic.BasicAction;
import org.encog.ml.world.basic.BasicWorld;

public class GridWorld extends BasicWorld {
	
	public static final Action ACTION_NORTH = new BasicAction("NORTH");
	public static final Action ACTION_SOUTH = new BasicAction("SOUTH");
	public static final Action ACTION_EAST = new BasicAction("EAST");
	public static final Action ACTION_WEST = new BasicAction("WEST");
	
	private GridState[][] state;
	private List<WorldAgent> agents = new ArrayList<WorldAgent>();
	
	public void GridWorld(int rows, int columns) {
		addAction(ACTION_NORTH);
		addAction(ACTION_SOUTH);
		addAction(ACTION_EAST);
		addAction(ACTION_WEST);
		this.state = new GridState[rows][columns];
		
		for(int row = 0; row<rows; row++) {
			for(int col = 0; col<columns; col++) {
				this.state[row][col] = new GridState(this, row, col, false);
			}
		}
		
	}

	@Override
	public List<WorldAgent> getAgents() {
		return this.agents;
	}
	
	public GridState getState(int row, int column) {
		return this.state[row][column];
	}
	
	public void addAgent(WorldAgent agent, int row, int column) {
		this.agents.add(agent);
		agent.setCurrentState(getState(row,column));
	}
}
