package main.states;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.swing.JOptionPane;

import main.Handler;
import main.entities.creatures.Player;
import main.entities.creatures.PlayerMP;
import main.entities.towers.Castle;
import main.gfx.Assets;
import main.input.KeyManager;
import main.input.WindowManager;
import main.maps.Map;
import main.net.GameClient;
import main.net.GameServer;
import main.net.packets.Packet00Login;
import main.net.packets.Packet01Disconnect;
import main.net.packets.Packet04Start;
import main.ui.ClickListener;
import main.ui.HealthBar;
import main.ui.UIButton;
import main.ui.UIManager;
import main.utilities.Utilities.Teams;

// main state in which gameplay is performed

public class GameState extends State {

	private UIManager uiManager;
	private Map basicMap;

	// multiplayer
	private GameClient socketClient;
	private GameServer socketServer;
	public Player player;
	private float spawnX = 50, spawnY = 50;
	private KeyManager keyManager;
	@SuppressWarnings("unused")
	private WindowManager windowManager;
	private HealthBar playerHPBar;
	private boolean waiting = true;

	// variables for towers respawning
	private long lastRespawnTimer, respawnCooldown = 10000, respawnTimer = respawnCooldown;

	public GameState(Handler handler) {
		super(handler);

		basicMap = new Map(handler, "/map/basicMap.txt");
		handler.setMap(basicMap);
		handler.getMouseManager().setUIManager(uiManager);

		gui();
		multiplayer();
	}

	@Override
	public void tick() {
		if (socketServer != null) {
			respawnTimer += System.currentTimeMillis() - lastRespawnTimer;
			lastRespawnTimer = System.currentTimeMillis();
			if (respawnTimer >= respawnCooldown) {
				Packet04Start packet = new Packet04Start(waiting, respawnTimer);
				packet.writeData(handler.getSocketClient());
				respawnTimer = 0;
			} else {
				waiting = checkForOponents();
				Packet04Start packet = new Packet04Start(waiting, respawnTimer);
				packet.writeData(handler.getSocketClient());
			}
		}
		handler.getMouseManager().setUIManager(uiManager);
		if (!waiting) {
			basicMap.tick();
			playerHPBar.setValue(player.getHealth());
		}
		uiManager.tick();
	}

	@Override
	public void render(Graphics g) {
		basicMap.render(g);
		uiManager.render(g);

		if (waiting) {
			g.setFont(new Font("Arial", Font.PLAIN, 100));
			g.drawString("WAIT FOR OPONENTS", 75, 360);
		}
		if (!player.isLiving()) {
			g.setFont(new Font("Arial", Font.PLAIN, 240));
			g.drawString("YOU DIED", 75, 360);
			g.setFont(new Font("Arial", Font.BOLD, 28));
			g.setColor(Color.BLACK);
			g.drawString("Respawn in:" + (int) (10 - (player.getDeathTimer() / 1000)) + "s", 540, 400);
		}
	}

	public Player getPlayer() {
		return player;
	}

	public void gui() {
		uiManager = new UIManager(handler);

		if (State.getSide() == Teams.RED) // player health bar
			playerHPBar = new HealthBar(16, 675, 136, 32, 0, Player.MAX_HEALTH, Color.RED, true);
		else if (State.getSide() == Teams.BLUE)
			playerHPBar = new HealthBar(1128, 675, 136, 32, 0, Player.MAX_HEALTH, Color.BLUE, false);
		uiManager.addObject(playerHPBar);

		UIButton exitButton = new UIButton(1225, 690, 50, 25, Assets.exitButton, new ClickListener() {
			@Override
			public void onClick() {
				handler.getMouseManager().setUIManager(null);
				State.setState(handler.getGame().menuState);

				Packet01Disconnect packet = new Packet01Disconnect(player.getUsername());
				packet.writeData(socketClient);
			}
		});
		uiManager.addObject(exitButton);
	}

	public void multiplayer() {
		String[] options = { "run", "join" };
		int choose = JOptionPane.showOptionDialog(null, "Do you want to run the server or join exisiting?",
				"Server creation", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options,
				options[0]);
		if (choose == 0) { // create server thread if player wants to run the server
			socketServer = new GameServer(handler);
			socketServer.start();
			handler.setSocketServer(socketServer);
		}
		String ipAddress;
		if (choose == 1) { // if player wants to join the server he needs to input its ip
			ipAddress = JOptionPane.showInputDialog("Enter a server ip");
		} else {
			InetAddress inetAddress;
			try {
				inetAddress = InetAddress.getLocalHost();
			} catch (UnknownHostException e) {
				e.printStackTrace();
				inetAddress = null;
			} // give ip address for host player
			ipAddress = inetAddress.getHostAddress();
		}
		// create client thread
		socketClient = new GameClient(handler, ipAddress);
		socketClient.start();
		handler.setSocketClient(socketClient);

		if (State.getSide() == Teams.RED) { // set spawn
			spawnX = 256;
			spawnY = 344;
		} else if (State.getSide() == Teams.BLUE) {
			spawnX = 1024;
			spawnY = 344;
		}

		keyManager = new KeyManager(handler); // input for player, have to be set individually
		player = new PlayerMP(handler, keyManager, spawnX, spawnY, JOptionPane.showInputDialog("Enter a username"),
				State.getSide(), null, -1);
		basicMap.getEntityManager().addEntity(player);
		// send information to the server that new player joins the game
		Packet00Login loginPacket = new Packet00Login(player.getUsername(), player.getX(), player.getY(),
				player.getTeam(), Castle.MAX_HEALTH, Castle.MAX_HEALTH);
		if (socketServer != null) { // if this is host, add player to the connection list
			socketServer.addConnection((PlayerMP) player, loginPacket);
		}
		// send packet
		loginPacket.writeData(socketClient);
		// create window listener for proper disconnect
		windowManager = new WindowManager(handler, player.getUsername());
	}

	public boolean checkForOponents() { // check if player have to wait for oponents
		int redCount = basicMap.getEntityManager().countRedTeammates();
		int blueCount = basicMap.getEntityManager().countBlueTeammates();
		return !(redCount > 0 && blueCount > 0);
	}

	// getters and setters

	public boolean isWaiting() {
		return waiting;
	}

	public void setWaiting(boolean waiting) {
		this.waiting = waiting;
	}

	public long getRespawnCooldown() {
		return respawnCooldown;
	}

	public void setRespawnCooldown(long respawnCooldown) {
		this.respawnCooldown = respawnCooldown;
	}
}
