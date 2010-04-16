package spikes.rene.jogoai;
import javax.swing.JOptionPane;

class JogadorGUI implements Jogador {

	public boolean confirm(String proposicao) {
		return JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(null,
                proposicao, "Confirm", JOptionPane.YES_NO_OPTION);
	}

public String answer(String msg) {
			return JOptionPane.showInputDialog(null, msg);
	}

public void acknowledge(String texto) {
		JOptionPane.showMessageDialog(null, texto);
	}

}
