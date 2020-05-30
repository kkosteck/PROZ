package dev.kk.proz;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;

import dev.kk.proz.display.Display;
import dev.kk.proz.gfx.Assets;
import dev.kk.proz.gfx.ImageLoader;
import dev.kk.proz.gfx.SpriteSheet;
import dev.kk.proz.input.KeyManager;
import dev.kk.proz.states.GameState;
import dev.kk.proz.states.MenuState;
import dev.kk.proz.states.State;

public class Game implements Runnable {
	
	private Display display;
	public int width, height; //widht and height for our game window
	public String title; //title for game window
	
	private boolean running = false;
	private Thread thread;
	
	private BufferStrategy bs;
	private Graphics g;
	
	//states
	private State gameState;
	private State menuState;
	
	//input
	private KeyManager keyManager;
	
	
	public Game(String title, int width, int height) {
		this.width = width;
		this.height = height;
		this.title = title;
		keyManager = new KeyManager();
	}
	
	private void init() { //initilize our game window
		display = new Display(title, width, height);
		display.getFrame().addKeyListener(keyManager);
		Assets.init();
		
		gameState = new GameState(this);
		menuState = new MenuState(this);
		State.setState(gameState);;
	}
	
	private void tick() {
		keyManager.tick();
		
		if(State.getState() != null)
			State.getState().tick();
	}
	
	private void render() {
		bs = display.getCanvas().getBufferStrategy();
		if(bs == null) {
			display.getCanvas().createBufferStrategy(3);
			return;
		}
		g = bs.getDrawGraphics();
		g.clearRect(0, 0, width, height); //clear screen

		if(State.getState() != null)
			State.getState().render(g);
		
		
		bs.show();
		g.dispose();
	}
	
	public void run() {
		
		init();
		
		int fps = 60; //frames per second
		double timePerTick = 1e9 / fps; //time between every frame
		double delta = 0; //time counter for ticking
		long now; //first timestamp
		long lastTime = System.nanoTime(); //second timestamp
		long timer = 0; //timer for fps counter
		int ticks = 0; //fps counter
		
		//game loop
		while(running) {
			now = System.nanoTime(); //get current time
			delta += (now - lastTime) / timePerTick; //add time passed every loop
			timer += now - lastTime; //time counter for fps counter
			lastTime = now; //move timestamps
			
			if(delta >= 1){ //check if we need to do tick
				tick();
				render();
				ticks++;
				delta--;
			}
			
			if(timer >= 1e9) { //fps counter
				System.out.println("FPS: " + ticks);
				ticks = 0;
				timer = 0;
			}
				
			
		}
		stop();
	}
	
	public KeyManager getKeyManager() {
		return keyManager;
	}
	
	
	public synchronized void start() {//starting game loop
		if(running)
			return;
		running = true;
		thread = new Thread(this);
		thread.start();
	}
	
	public synchronized void stop() { //stoping game loop
		if(!running)
			return;
		running = false;
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
