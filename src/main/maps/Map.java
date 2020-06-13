package dev.kk.proz.maps;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import dev.kk.proz.Handler;
import dev.kk.proz.entities.Entity;
import dev.kk.proz.entities.EntityManager;
import dev.kk.proz.entities.towers.BasicTower;
import dev.kk.proz.entities.towers.Castle;
import dev.kk.proz.tiles.Tile;
import dev.kk.proz.utilities.Utilities;
import dev.kk.proz.utilities.Utilities.Teams; 

public class Map {

	private int width, height;
	private int[][] tiles;
	private Handler handler;
	private long respawnTimer;
	
	private EntityManager entityManager;
	
	public Map(Handler handler, String path) {
		this.handler = handler;
		entityManager = new EntityManager(handler);
		loadMap(path);
		
		for(int y = 0; y < height; y++) {//add initial towers
			for(int x = 0; x < width; x++) {
				if(getTile(x,y) == Tile.redTower) {
					BasicTower tower = new BasicTower(handler, (int) x * Tile.TILEWIDTH - 16, (int)y * Tile.TILEHEIGHT - 16, Teams.RED);
					entityManager.addEntity(tower);
				}
				if(getTile(x,y) == Tile.blueTower) {
					BasicTower tower = new BasicTower(handler, (int) x * Tile.TILEWIDTH - 16, (int)y * Tile.TILEHEIGHT - 16, Teams.BLUE);
					entityManager.addEntity(tower);
				}
			}	
		}
		Castle redCastle = new Castle(handler, 2 * Tile.TILEWIDTH - 16, 20 * Tile.TILEHEIGHT - 16, Teams.RED);
		entityManager.addEntity(redCastle);
		Castle blueCastle = new Castle(handler, 76 * Tile.TILEWIDTH - 16, 20 * Tile.TILEHEIGHT - 16, Teams.BLUE);
		entityManager.addEntity(blueCastle);
	}
	
	public void tick() {
		entityManager.tick();
		
		if(respawnTimer >= handler.getGame().gameState.getRespawnCooldown()) {
			System.out.println("check");
			respawnTowers();
		}
	}
	
	private void respawnTowers() {
		boolean alive = false;
		for(int y = 0; y < height; y++) {
			for(int x = 0; x < width; x++) {
				if(getTile(x,y) == Tile.redTower) {
					alive = false;
					for(Entity e : entityManager.getEntities()) {
						if(e instanceof BasicTower && e.getX() == ((int) x * Tile.TILEWIDTH - 16) && e.getY() == (int)y * Tile.TILEHEIGHT - 16) {
							alive = true;
						}
					}
					if(alive == false) {
						BasicTower tower = new BasicTower(handler, (int) x * Tile.TILEWIDTH - 16, (int)y * Tile.TILEHEIGHT - 16, Teams.RED);
						entityManager.addEntity(tower);
					}
				}
				if(getTile(x,y) == Tile.blueTower) {
					alive = false;
					for(Entity e : entityManager.getEntities()) {
						if(e instanceof BasicTower && e.getX() == ((int) x * Tile.TILEWIDTH - 16) && e.getY() == (int)y * Tile.TILEHEIGHT - 16) {
							alive = true;
						}
					}
					if(alive == false) {
						BasicTower tower = new BasicTower(handler, (int) x * Tile.TILEWIDTH - 16, (int)y * Tile.TILEHEIGHT - 16, Teams.BLUE);
						entityManager.addEntity(tower);
					}
				}
			}
		}
	}
	
	public void render(Graphics g) {
		for(int y = 0; y < height; y++) {
			for(int x = 0; x < width; x++) {
				getTile(x,y).render(g, x * Tile.TILEWIDTH, y * Tile.TILEHEIGHT);
			}	
		}
		respawnDisplay(g);
		entityManager.render(g);
	}

	public Tile getTile(int x, int y) {
		if(x < 0 || y < 0 || x >= width || y >= height)
			return Tile.wallTile;
		
		Tile t = Tile.tiles[tiles[x][y]];
		if(t == null)
			return Tile.grassTile;
		return t;
	}
	
	
	private void loadMap(String path) {
		String file = Utilities.loadFileAsString(path);
		String[] tokens = file.split("\\s+");
		width = Utilities.parseInt(tokens[0]);
		height = Utilities.parseInt(tokens[1]);
		
		tiles = new int[width][height];
		
		for(int y = 0; y < height; y++) {
			for(int x = 0; x < width; x++) {
				tiles[x][y] = Utilities.parseInt(tokens[x + y * width + 2]);
			}
		}
	}
	
	private void respawnDisplay(Graphics g) {
		g.setFont(new Font("Arial", Font.BOLD, 16));
		g.setColor(Color.WHITE);
		g.drawString("Towers respawn in:"+(int) (10 - (respawnTimer / 1000)) + "s", 547, 717);
		
	}

	public EntityManager getEntityManager() {
		return entityManager;
	}

	public long getRespawnTimer() {
		return respawnTimer;
	}

	public void setRespawnTimer(long respawnTimer) {
		this.respawnTimer = respawnTimer;
	}
	
	
}