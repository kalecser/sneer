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
		window=new JFrame("toscoBall");
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setBounds(0,0,512,480);
		window.setResizable(false);

		mesa=new Mesa(6, this);
		
		window.setContentPane(mesa);
		window.pack();
		window.addKeyListener(new TeclasListener(this));
		window.setLocationRelativeTo(null);
		window.setVisible(true);
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
		mesa.restartGame();
		//endGame();
	}
	
	void tellMesaToShine(int x, int y) {
		mesa.shine(x,y);
	}
	
	private void exitMenu() {
		mesa.isRunning=true;
	}

	
	private class TeclasListener implements KeyListener {
	Game game;
	TeclasListener(Game g) {game=g;}
		@Override public void keyPressed(KeyEvent e) {
			switch (e.getKeyCode()) {
				case KeyEvent.VK_ESCAPE: {game.endGame(); break;}
				case KeyEvent.VK_ENTER: {game.exitMenu(); break;}
				case KeyEvent.VK_LEFT: {if (k==0) {mesa.turn(1); k=1;} else k=2; break;}
				case KeyEvent.VK_RIGHT: {if (k==0) {mesa.turn(-1); k=-1;} else k=-2; break;}
				case KeyEvent.VK_UP: {space=2; break;}
				case KeyEvent.VK_DOWN: {space=-2; break;}
				case KeyEvent.VK_SPACE: {mesa.shoot(); break;}
			}
		}
		@Override public void keyReleased(KeyEvent e) {
			switch (e.getKeyCode()) {
			case KeyEvent.VK_UP:
			case KeyEvent.VK_DOWN: {space=0; break;}
			case KeyEvent.VK_LEFT:
			case KeyEvent.VK_RIGHT: {k=0; break;}
			}
		}
		@Override public void keyTyped(KeyEvent e) {} 
	}
}
