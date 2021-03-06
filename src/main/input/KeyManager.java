package main.input;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import main.Handler;

// keyboard input
// assignment for every key in use
// keyManager is individually created with every player

public class KeyManager implements KeyListener {

	private boolean[] keys;
	public boolean up, down, left, right;
	public boolean attackTrigger;
	
	
	public KeyManager(Handler handler) {
		// we add key listener only if player is created
		handler.getGame().getDisplay().getFrame().addKeyListener(this);
		keys = new boolean[256];
	}
	
	public void tick() {
		up = keys[KeyEvent.VK_W];
		down = keys[KeyEvent.VK_S];
		left = keys[KeyEvent.VK_A];
		right = keys[KeyEvent.VK_D];
		attackTrigger = keys[KeyEvent.VK_SPACE];
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		keys[e.getKeyCode()] = true;
	}

	@Override
	public void keyReleased(KeyEvent e) {
		keys[e.getKeyCode()] = false;
	}

	@Override
	public void keyTyped(KeyEvent e) {
		
	}
	
	
	
}
