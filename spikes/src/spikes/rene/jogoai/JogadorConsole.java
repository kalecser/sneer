package spikes.rene.jogoai;

import java.util.Scanner;

class JogadorConsole implements Jogador {

	@Override
	public boolean confirm(String proposicao) {
		return answer(proposicao).equalsIgnoreCase("s");
	}
	
	@Override
	public String answer(String msg) {
		acknowledge(msg);
		return new Scanner(System.in).nextLine();
	}

	@Override
	public void acknowledge(String texto) {
		System.out.println(texto);
	}

}

