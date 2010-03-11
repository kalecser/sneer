package spikes.rene.jogoai;


public class JogoAIWorkInProgress {
	
	
//isso sao o joga e chuta antigos
	
	
//	private void joga() {
//	if (_raiz instanceof Animal) {			
//		chuta((Animal)_raiz, null, false);
//		return;
//	}
//	
//	
//	No noAtual = (No)_raiz;
//	while (true) {
//		boolean lado = confirm("Hmm... Por acaso o animal " + noAtual._caracteristica + "?");
//		Elemento proximoElemento = lado ? noAtual._ladoSim : noAtual._ladoNao ;
//		
//		if (proximoElemento instanceof Animal) {
//			chuta((Animal)proximoElemento, noAtual, lado);
//			break;
//		}
//
//		noAtual = (No) proximoElemento;
//	}
//}


//private void chuta(Animal animal, No no, boolean lado) {
//	if (confirm("O animal eh " + animal._nome + "?")) {
//		print("Ahaaa eu sabia xD!");
//		return;
//	}
//
//	String novoanimal=responde("Desisto! qual era o animal???");
//	String novacaract=responde(novoanimal+" eh diferente de "+animal._nome+" porque "+novoanimal+"...");
//
//	No novoNo = new No(novacaract, novoanimal, animal._nome);
//	if (no == null) 	_raiz=novoNo;
//	else {
//		if (lado) no._ladoSim=novoNo;
//		else no._ladoNao=novoNo;
//	}
//}
	
	
	
	
	
	
	
	
	
	
//
//	private Jogador _jogador;
//	private Elemento _raiz;
//
//	
//	JogoAIWorkInProgress(Jogador jogador) {
//
//		_jogador = jogador;
//
//		_raiz = new No("vive no mar", "golfinho", "cachorro");
//
//		print("Bem vindo ao Jogo Dos Animais 1.0 Console Edition.\nImagine um animal e eu tentarei adivinhar qual eh.\nNao se esqueca de que nao possuo animais predefinidos.\n");
//		do {
//			joga();
//		} while (confirm("Quer jogar de novo?"));
//		
//		print("\nFechando...");
//	}
//
//
//	private void joga() {
//		Elemento atual = _raiz;
//		while (true) {
//			if (atual.fazalgumacoisa()) break;
//
//			//No
//			//boolean lado = confirm("Hmm... Por acaso o animal " + _caracteristica + "?");
//			//Elemento proximoElemento = lado ? _ladoSim : _ladoNao ;
//			//return false;
//			
//			//Animal
//			//chuta((Animal)proximoElemento, noAtual, lado);
//			//return true;
//
//			atual = proximoElemento;
//
//		}
//	}
//
//
//	private void chuta(Animal animal, No no, boolean lado) {
//		if (confirm("O animal eh " + animal._nome + "?")) {
//			print("Ahaaa eu sabia xD!");
//			return;
//		}
//
//		String novoanimal=responde("Desisto! qual era o animal???");
//		String novacaract=responde(novoanimal+" eh diferente de "+animal._nome+" porque "+novoanimal+"...");
//
//		No novoNo = new No(novacaract, novoanimal, animal._nome);
//		if (lado) {
//			no._ladoSim=novoNo;
//		} else {
//			no._ladoNao=novoNo;
//		}
//	}
//	
//	
//	private boolean confirm(String proposicao) {
//		return responde(proposicao).toLowerCase().startsWith("s");
//	}
//
//	
//	private String responde(String pergunta) {
//		print(pergunta);
//		return _jogador.getString();
//	}
//	
//	
//	private void print(String texto) {
//		_jogador.print(texto);
//	}
//
//
//	
//	interface Elemento {
//		
//		boolean isAnimal();
//		
//	}
//
//
//	class Animal implements Elemento {
//		
//		final String _nome;
//
//		Animal(String nome) {
//			_nome = nome;
//		}
//
//		@Override
//		public boolean isAnimal() {
//			return true;
//		}
//
//	}
//
//
//	class No implements Elemento {
//
//		final String _caracteristica;
//		Elemento _ladoSim;
//		Elemento _ladoNao;
//		
//		No(String caracteristica, String animalSim, String animalNao) {
//			_caracteristica = caracteristica;
//			_ladoSim = new Animal(animalSim);
//			_ladoNao = new Animal(animalNao);
//		}
//
//		@Override
//		public boolean isAnimal() {
//			return false;
//		}
//	}
//
//	
	
	
}



