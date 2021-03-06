package main.entities.towers;

import main.Handler;
import main.entities.Entity;
import main.utilities.Utilities.Teams;

/* class for every towerlike entities 
 */

public abstract class Tower extends Entity{

	public static final float DEFUALT_ATTACK_SPEED = 3.0f;
	public static final int DEFUALT_TOWER_WIDTH = 32, DEFUALT_TOWER_HEIGHT = 32;
	protected Teams team = Teams.NONE;
	
	
	public Tower(Handler handler, float x, float y, int width, int height, Teams team) {
		super(handler, x, y, width, height);
		this.team = team;
	}

	//getters and setters

	public Teams getTeam() {
		return team;
	}
	public void setTeam(Teams team) {
		this.team = team;
	}	
	
	
}
