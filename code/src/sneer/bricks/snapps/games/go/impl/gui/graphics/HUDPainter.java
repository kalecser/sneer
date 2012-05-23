package sneer.bricks.snapps.games.go.impl.gui.graphics;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;

import sneer.bricks.snapps.games.go.impl.gui.GoBoardPanel;

public class HUDPainter {

	public static int NOONE_WIN = 0;
	public static int PLAYER_WIN = 1;
	public static int PLAYER_LOSES = 2;
	
	private Image winImg, loseImg;
	
	public HUDPainter() {
		winImg=Toolkit.getDefaultToolkit().getImage(GoBoardPanel.class.getResource("images/winImg.png"));
	    loseImg=Toolkit.getDefaultToolkit().getImage(GoBoardPanel.class.getResource("images/loseImg.png"));
	}
	
	public void draw(final Graphics graphics, final int winState){
		if(winState == NOONE_WIN){
			return;
		}
		if(winState == PLAYER_WIN){
			graphics.drawImage(winImg,175, 185, null);
		}
		if(winState == PLAYER_LOSES){
			graphics.drawImage(loseImg,175, 185, null);
		}
	}
	
}
