package spikes.rene.toscoball;
//this is supposed to evolve into a billiards game.
//main class

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JFrame;



public class Game {
	
	private Mesa mesa;
	private JFrame window;
	private Thread ctrl;

	public static void main(String args[]) {new Game();}

	private Game() {
		window=new JFrame("Billiards R");
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setBounds(0,0,640,480);
		window.addKeyListener(new TeclasListener(this));

		mesa=new Mesa(6, window, this);
		mesa.setLayout(null);
		mesa.addMouseListener(new MouseAdapter(){
			@Override public void mouseReleased(MouseEvent e) {mesa.shoot(e);}
		});
		
		window.setContentPane(mesa);
		window.setVisible(true);
		window.pack();
		window.setLocationRelativeTo(null);
		startGame();
	}
	
	private void startGame() {
		ctrl=new Thread() {@Override
		public void run() {while(true) {
			try {Thread.sleep(33);}
			catch (InterruptedException e) {}
			finally {updateGame();}
		}}};
		ctrl.start();
	}
	
	private void updateGame() {
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
		}
		@Override public void keyReleased(KeyEvent e) {}
		@Override public void keyTyped(KeyEvent e) {} 
	}
}
