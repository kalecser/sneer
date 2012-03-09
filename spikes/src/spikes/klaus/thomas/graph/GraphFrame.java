package spikes.klaus.thomas.graph;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JFrame;


public class GraphFrame extends JFrame {
	
	private static final int _WIDTH = 255;
	private static final int _HEIGHT = 255;
	private static final int MARGIN = 8;

	private static final long serialVersionUID = 1L;
	
	
	public static void main(String[] args) {
		new GraphFrame();
	}
	
	
	public GraphFrame() {
		setTitle("Graph");	  
	    setResizable(true);
	    setBounds(50, 50, _WIDTH, _HEIGHT);
	    setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		repaint();
	}


	@Override
	public void paint(Graphics graphics) {
		for (int px = 0; px < _WIDTH; px++)
			for (int py = _HEIGHT - 1; py >= 0; py--)
				paint(graphics, px, py);
	}


	private void paint(Graphics graphics, int x, int y) {
		graphics.setColor(colorFor(x, 255 - y));
		graphics.drawLine(MARGIN + x, y - MARGIN, MARGIN + x, y - MARGIN);
	}


	private Color colorFor(int x, int y) {
		int r = (x * x) + (y * y) > 100 ? 255 : 0;
		int g = 0;
		int b = 0;
		return color(r, g, b);
	}


	private Color color(int r, int g, int b) {
		return new Color(range(r), range(g), range(b));
	}


	private int range(int valor) {
		if (valor < 0) return 0;
		if (valor > 255) return 255;
		return valor;
	}

}
