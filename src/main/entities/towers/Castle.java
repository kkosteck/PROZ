package main.entities.towers;

import java.awt.Color;
import java.awt.Graphics;

import main.Handler;
import main.entities.Entity;
import main.gfx.Assets;
import main.net.packets.Packet01Disconnect;
import main.states.GameEnd;
import main.states.GameState;
import main.states.State;
import main.tiles.Tile;
import main.ui.HealthBar;
import main.utilities.Utilities.Teams;

public class Castle extends Tower{

	public static final int MAX_HEALTH = 1000;
	private HealthBar hpBar;

	public Castle(Handler handler, float x, float y, Teams team) {
		super(handler, x, y, 4 * Tile.TILEWIDTH, 8 * Tile.TILEHEIGHT, team);
		health = MAX_HEALTH;
		if(team == Teams.RED)
			hpBar = new HealthBar(16, 8, 608, 32, 0, Castle.MAX_HEALTH, Color.RED, true);
		else
			hpBar = new HealthBar(656, 8, 608, 32, 0, Castle.MAX_HEALTH, Color.BLUE, false);
	}

	@Override
	public void tick() {
		if(hpBar != null)
			hpBar.setValue(health);
	}

	@Override
	public void render(Graphics g) {
		if(team == Teams.RED)
			g.drawImage(Assets.castle, (int) x, (int) y, 4 * Tile.TILEWIDTH, 8 * Tile.TILEHEIGHT, null);
		else
			g.drawImage(Assets.castle, (int) (x + 4 * Tile.TILEWIDTH), (int) y, -(4 * Tile.TILEWIDTH), 8 * Tile.TILEHEIGHT, null);
		
		hpBar.render(g);
	}

	@Override
	public void die() {
		handler.getMouseManager().setUIManager(null);
		handler.getGame().gameEnd = new GameEnd(handler, team);
		State.setState(handler.getGame().gameEnd);
		
		Packet01Disconnect packet = new Packet01Disconnect(((GameState)(handler.getGame().gameState)).getPlayer().getUsername());
        packet.writeData(handler.getSocketClient());
        ((GameState)(handler.getGame().gameState)).getPlayer().setActive(false);
	}
	@Override
	public void hurt(int amount) {
		for(Entity e : handler.getEntityManager().getEntities()) {
			if(e.equals(this))
				continue;
			else if(e instanceof Tower && (((Tower)e).getTeam().equals(team))) {
				return;
			}
		}
		super.hurt(amount);
	}

}
