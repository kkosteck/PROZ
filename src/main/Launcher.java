package main;

//launch the game from this class

public class Launcher {

	public static void main(String[] args) {
		Game game = new Game("Game!", 1280, 720);
		game.start();

	}
}
