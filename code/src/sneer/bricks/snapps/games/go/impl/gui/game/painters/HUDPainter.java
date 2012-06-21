package sneer.bricks.snapps.games.go.impl.gui.game.painters;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Toolkit;

import sneer.bricks.snapps.games.go.impl.gui.game.GoBoardPanel;
import sneer.bricks.snapps.games.go.impl.logging.GoLogger;

public class HUDPainter {

	public static int NOONE_WIN = 0;
	public static int PLAYER_WIN = 1;
	public static int PLAYER_LOSES = 2;
	
	private Image winImg, loseImg;
	private int _winState = NOONE_WIN;
	
	public HUDPainter() {
		winImg=Toolkit.getDefaultToolkit().getImage(GoBoardPanel.class.getResource("images/winImg.png"));
	    loseImg=Toolkit.getDefaultToolkit().getImage(GoBoardPanel.class.getResource("images/loseImg.png"));
	}
	
	public void draw(final Graphics graphics){
		if(_winState == NOONE_WIN){
			return;
		}
		if(_winState == PLAYER_WIN){
			graphics.drawImage(winImg,175, 185, null);
		}
		if(_winState == PLAYER_LOSES){
			final Rectangle clipBounds = graphics.getClipBounds();
			GoLogger.log(clipBounds);
			graphics.drawImage(loseImg,175, 185, null);
		}
	}

	public void setWinState(int winState) {
		_winState = winState;
	}
	
}
