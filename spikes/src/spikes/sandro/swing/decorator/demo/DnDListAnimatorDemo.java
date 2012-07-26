package spikes.sandro.swing.decorator.demo;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JScrollPane;

import spikes.sandro.swing.decorator.ListAnimatorDecorator;
import spikes.sandro.swing.decorator.ListAnimatorMouseListener;

public class DnDListAnimatorDemo {

	public static void main(String[] args) throws Exception {
		JFrame f = new JFrame("Smooth List Drop");
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		DefaultListModel<String> model = new DefaultListModel<String>();
		model.addElement("Klaus");
		model.addElement("Bamboo");
		model.addElement("Sandro");
		model.addElement("Nell");
		
		final JList<String> list = new JList<String>(model);
		
		addDnDListeners(list);
		
		f.getContentPane().add(new JScrollPane(list));
		f.pack();
		f.setVisible(true);
	}

	private static void addDnDListeners(final JList<String> lst) {
		ListAnimatorDecorator<String> smoother = new ListAnimatorDecorator<String>(lst) {
			@Override
			protected void move(int fromIndex, int toIndex) {
				DefaultListModel<String> model = (DefaultListModel<String>)lst.getModel();
				String tmp = model.getElementAt(fromIndex);
				model.removeElementAt(fromIndex);
				model.add(toIndex,tmp );
				lst.revalidate();
				lst.repaint();
			}
		};

		ListAnimatorMouseListener listener = new ListAnimatorMouseListener(smoother);
		lst.addMouseListener(listener);
		lst.addMouseMotionListener(listener);
	}
}