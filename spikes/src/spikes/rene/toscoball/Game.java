package spikes.rene.toscoball;
//this is finally becoming a nice 8-bit billiards game
//Main class

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JFrame;
import javax.swing.JOptionPane;


public class Game {
	
	private Mesa mesa;
	private JFrame window;
	private Thread ctrl;
	private int spacing=0, turning=0;

	public static void main(String args[]) {new Game();}

	private Game() {
		mesa=new Mesa(this);

		window=new JFrame("toscoBall");
		window.addKeyListener(new TeclasListener());
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setResizable(false);
		window.setContentPane(mesa);
		window.pack();
		window.setLocationRelativeTo(null);
		window.setVisible(true);
		
		startGame();
	}
	
	private void startGame() {
		ctrl=new Thread() {@Override public void run() {
			while(true) {
				try {Thread.sleep(16);}
				catch (InterruptedException e) {}
				finally {updateGame();}
		}}};
		ctrl.start();
	}
	
	private void updateGame() {
		mesa.step(spacing, turning);
	}
	
	private void endGame() {
		//something
		System.exit(0);
	}
	
	void winGame() {
		JOptionPane.showMessageDialog(null, "You won the game!", "You Win", JOptionPane.INFORMATION_MESSAGE);
		mesa.stopGame();
	}
	
	void loseGame() {
		JOptionPane.showMessageDialog(null, "You lost the game.", "Game Over", JOptionPane.INFORMATION_MESSAGE);
		mesa.stopGame();
	}
	
	void shineAt(int x, int y) {
		mesa.shineAt(x,y);
	}

	
	private class TeclasListener implements KeyListener {
		@Override public void keyPressed(KeyEvent e) {
			switch (e.getKeyCode()) {
				case KeyEvent.VK_ESCAPE: {endGame(); break;}
				case KeyEvent.VK_ENTER: {mesa.beginGame(); break;}
				case KeyEvent.VK_LEFT: {if (turning==0) {mesa.moveCursor(0,2); turning=1;} else turning=2; break;}
				case KeyEvent.VK_RIGHT: {if (turning==0) {mesa.moveCursor(0,-2); turning=-1;} else turning=-2; break;}
				case KeyEvent.VK_UP: {spacing=2; break;}
				case KeyEvent.VK_DOWN: {spacing=-2; break;}
				case KeyEvent.VK_SPACE: {mesa.shoot(); break;}
			}
		}
		@Override public void keyReleased(KeyEvent e) {
			switch (e.getKeyCode()) {
			case KeyEvent.VK_UP:
			case KeyEvent.VK_DOWN: {spacing=0; break;}
			case KeyEvent.VK_LEFT:
			case KeyEvent.VK_RIGHT: {turning=0; break;}
			}
		}
		@Override public void keyTyped(KeyEvent e) {} 
	}
}
