package dev.kk.proz.entities.towers;

import java.awt.Graphics;

import dev.kk.proz.Handler;
import dev.kk.proz.gfx.Assets;
import dev.kk.proz.tiles.Tile;
import dev.kk.proz.utilities.Utilities.Teams;

public class BasicTower extends Tower {
	
	

	public BasicTower(Handler handler, float x, float y, Teams team) {
		super(handler, x, y, 3 * Tile.TILEWIDTH, 3 * Tile.TILEHEIGHT, team);
		
		bounds.x = 2;
		bounds.y  = 2;
		bounds.width = 44;
		bounds.height = 44; 
		health = 100;
	}

	@Override
	public void tick() {
		
	}

	@Override
	public void render(Graphics g) {
		if(team == Teams.RED)
			g.drawImage(Assets.redBasicTower, (int) x, (int) y, 3 * Tile.TILEWIDTH, 3 * Tile.TILEHEIGHT, null);
		else
			g.drawImage(Assets.blueBasicTower, (int) x, (int) y, 3 * Tile.TILEWIDTH, 3 * Tile.TILEHEIGHT, null);
			
	}

	@Override
	public void die() {
		 
	}

	public Teams getTeam() {
		return team;
	}

	public void setTeam(Teams team) {
		this.team = team;
	}
	
}