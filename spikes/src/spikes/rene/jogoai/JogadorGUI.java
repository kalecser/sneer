package spikes.rene.jogoai;
import javax.swing.JOptionPane;

class JogadorGUI implements Jogador {

	@Override
	public boolean confirm(String proposicao) {
		return JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(null,
                proposicao, "Confirm", JOptionPane.YES_NO_OPTION);
	}

@Override
public String answer(String msg) {
			return JOptionPane.showInputDialog(null, msg);
	}

@Override
public void acknowledge(String texto) {
		JOptionPane.showMessageDialog(null, texto);
	}

}
