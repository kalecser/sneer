package spikes.rene.jogoai;

import java.util.Scanner;

class JogadorConsole implements Jogador {

	public String getString() {
		return new Scanner(System.in).nextLine();
	}

	public void print(String texto) {
		System.out.println(texto);
	}

}

