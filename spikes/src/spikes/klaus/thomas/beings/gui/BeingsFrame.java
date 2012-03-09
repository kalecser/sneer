package spikes.klaus.thomas.beings.gui;

import static sneer.foundation.environments.Environments.my;

import java.awt.Color;
import java.awt.Graphics;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.swing.JFrame;

import sneer.bricks.hardware.clock.timer.Timer;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;


@SuppressWarnings("rawtypes")
public class BeingsFrame extends JFrame {
	
	private static final int CELL_SIZE = 3;

	private static final long serialVersionUID = 1L;
	
	static final int WORLD_WIDTH = 181;  //Prime number to avoid "wind" effects of scanning adjacent cells in a specific direction.
	static final int WORLD_HEIGHT = 167; //Prime number to avoid "wind" effects of scanning adjacent cells in a specific direction.
	private static final long WORLD_CELL_COUNT = WORLD_WIDTH * WORLD_HEIGHT;

	private static final int MARGIN = 45;
	
	private static Being FOOD = new Food();
	
	private final Being[][] _world = new Being[WORLD_WIDTH][WORLD_HEIGHT];
	private final Random _random = new Random();

	@SuppressWarnings("unused")	private WeakContract _refToAvoidGc;

	
	public BeingsFrame() {
		setTitle("Beings");	  
	    setResizable(true);
	    setBounds(50, 50, WORLD_WIDTH * CELL_SIZE + (2 * MARGIN), WORLD_HEIGHT * CELL_SIZE + (2 * MARGIN));
	    setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		_refToAvoidGc = my(Timer.class).wakeUpEvery(10, new Runnable() { @Override public void run() {
			hitRandomCell();
			stepAllCells();
			repaint();
		}});
	}

	
	private void stepAllCells() {
		Map countByType = new HashMap();
		int x = 0;
		int y = 0;
		for (long i = WORLD_CELL_COUNT - 1; i >= 0; i--) {
			x = (x + 200) % WORLD_WIDTH;
			y = (y + 150) % WORLD_HEIGHT;
			Being occupant = _world[x][y];
			if (occupant == null) continue;
			
			occupant.step(_world, x, y);
			
			Class type = occupant.getClass();
			Integer count = (Integer) countByType.get(type);
			if (count == null) count = 0;
			countByType.put(type, count + 1);
		}
		
		System.out.println("\n\n\n\n");
		for (Object type : countByType.keySet())
			System.out.println("" + type + ": " + countByType.get(type) );
	}


	@Override
	public void paint(final Graphics graphics) {
		for (int x = WORLD_WIDTH - 1; x >= 0; x--) {
			for (int y = WORLD_HEIGHT - 1; y >= 0; y--) {
				Being occupant = _world[x][y];
				graphics.setColor(occupant == null ? Color.GRAY : occupant.color());
				graphics.fillRect(x * CELL_SIZE + MARGIN, y * CELL_SIZE + MARGIN, CELL_SIZE, CELL_SIZE);
			}
		}
	}

	
	private void hitRandomCell() {
		int rx = _random.nextInt(WORLD_WIDTH);
		int ry = _random.nextInt(WORLD_HEIGHT);
		Being occupant = _world[rx][ry];
		if (occupant == null)
			_world[rx][ry] = FOOD;
		else
			occupant.hit(_world, rx, ry);
	}
}
