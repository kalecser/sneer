package spikes.rene.jogoai;


interface Jogador {

	String answer(String question);
	
	boolean confirm(String proposicao);
	
	void acknowledge(String texto);

}