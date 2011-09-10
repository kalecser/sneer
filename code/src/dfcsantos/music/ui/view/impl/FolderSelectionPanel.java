package dfcsantos.music.ui.view.impl;

import static sneer.foundation.environments.Environments.my;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JPanel;

import sneer.bricks.pulp.reactive.Signal;
import sneer.bricks.pulp.reactive.Signals;
import sneer.bricks.pulp.reactive.signalchooser.SignalChooser;
import sneer.bricks.skin.widgets.reactive.ReactiveWidgetFactory;
import dfcsantos.music.ui.view.MusicViewListener;


final class FolderSelectionPanel extends JPanel {
	private final MusicViewListener listener;

	FolderSelectionPanel(MusicViewListener listener) {
		this.listener = listener;
		setLayout(new FlowLayout(FlowLayout.LEFT));
		add(newComboBox());
	}

	private JComboBox newComboBox() {
		ComboBoxModel model = my(ReactiveWidgetFactory.class).newComboBoxSignalModel(listener.playingFolderChoices(), chooser());
		JComboBox comboBox = new JComboBox(model);
		comboBox.addActionListener(new ActionListener() {  @Override public void actionPerformed(ActionEvent e) {
			playingFolderChosen(e);
		}});
		return comboBox;
	}

	
	private void playingFolderChosen(ActionEvent e) {
		String folderChosen = (String)((JComboBox) e.getSource()).getModel().getSelectedItem();
		if (folderChosen != null)
			listener.playingFolderChosen(folderChosen);
	}

	
	private SignalChooser<String> chooser() {
		return new SignalChooser<String>(){ @Override public Signal<?>[] signalsToReceiveFrom(String element) {
			return new Signal<?>[]{ my(Signals.class).constant(element) };
		}};
	}
}