package dev.kk.proz;

import dev.kk.proz.display.Display;

public class Launcher {
	
	public static void main(String[] args) {
		Game game = new Game("Game!", 1280, 720);
		game.start();
		
	}
}
