package spikes.rene.toscoball;
//this is supposed to evolve into a billiards game.
//main class

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JFrame;



public class Game {
	
	private Mesa mesa;
	private JFrame window;
	private Thread ctrl;
	private int space=0, k=0;

	public static void main(String args[]) {new Game();}

	private Game() {
		window=new JFrame("tosco Ball");
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setBounds(0,0,512,480);
		window.addKeyListener(new TeclasListener(this));

		mesa=new Mesa(6, window, this);
		mesa.setLayout(null);
		
		window.setContentPane(mesa);
		window.setVisible(true);
		window.pack();
		window.setLocationRelativeTo(null);
		startGame();
	}
	
	private void startGame() {
		ctrl=new Thread() {@Override
		public void run() {while(true) {
			try {Thread.sleep(16);}
			catch (InterruptedException e) {}
			finally {updateGame();}
		}}};
		ctrl.start();
	}
	
	private void updateGame() {
		mesa.space(space);
		if (k==2) mesa.turn(1);
		if (k==-2) mesa.turn(-1);
		mesa.stepBalls();
		mesa.repaint();
	}
	
	private void endGame() {
		System.exit(0);
	}
	
	void loseGame() {
		System.err.println("LOSER!");
		endGame();
	}
	
	private void exitMenu() {
		mesa.isRunning=true;
	}

	
	private class TeclasListener implements KeyListener {
	Game game;
	TeclasListener(Game g) {game=g;}
		@Override public void keyPressed(KeyEvent e) {
			if (e.getKeyCode()==KeyEvent.VK_ESCAPE) game.endGame();
			if (e.getKeyCode()==KeyEvent.VK_ENTER) game.exitMenu();
			if (e.getKeyCode()==KeyEvent.VK_LEFT) {if (k==0) {mesa.turn(1); k=1;} else k=2;};
			if (e.getKeyCode()==KeyEvent.VK_RIGHT) {if (k==0) {mesa.turn(-1); k=-1;} else k=-2;};
			if (e.getKeyCode()==KeyEvent.VK_UP) space=2;
			if (e.getKeyCode()==KeyEvent.VK_DOWN) space=-2;
			if (e.getKeyCode()==KeyEvent.VK_SPACE) mesa.shoot();
		}
		@Override public void keyReleased(KeyEvent e) {
			if (e.getKeyCode()==KeyEvent.VK_UP | e.getKeyCode()==KeyEvent.VK_DOWN) space=0;
			if (e.getKeyCode()==KeyEvent.VK_LEFT | e.getKeyCode()==KeyEvent.VK_RIGHT) k=0;
		}
		@Override public void keyTyped(KeyEvent e) {} 
	}
}
