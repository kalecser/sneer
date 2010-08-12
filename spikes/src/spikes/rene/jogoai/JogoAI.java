package spikes.rene.jogoai;

public class JogoAI {

	private Jogador _jogador;
	private Elemento _raiz;

	
	JogoAI(Jogador jogador) {
		_jogador = jogador;
		_raiz = new Animal("cachorro");
		print("Bem vindo ao Jogo Dos Animais 1.0 Console Edition.\nImagine um animal e eu tentarei adivinhar qual eh.\nNao se esqueca de que nao possuo animais predefinidos.\n");
		
		do _raiz = _raiz.learn();
		while (confirm("Quer jogar de novo?"));
		
		print("\nFechando...");
	}

	
	private boolean confirm(String proposicao) {
		return _jogador.confirm(proposicao);
	}

	
	private String responde(String pergunta) {
		return _jogador.answer(pergunta);
	}
	
	
	private void print(String texto) {
		_jogador.acknowledge(texto);
	}


	
	interface Elemento {
		Elemento learn();
	}


	class Animal implements Elemento {
		
		final String _nome;

		Animal(String nome) {
			_nome = nome;
		}

		
		@Override
		public Elemento learn() {
			if (confirm("O animal eh " + _nome + "?")) {
				print("Ahaaa eu sabia xD!");
				return this;
			}
			
			String novoanimal = responde("Desisto! qual era o animal???");
			String novacaract = responde(novoanimal+" eh diferente de "+_nome+" porque "+novoanimal+"...");

			return new No(novacaract, new Animal(novoanimal), new Animal(_nome));
		}
		
	}


	class No implements Elemento {

		final String _caracteristica;
		Elemento _ladoSim;
		Elemento _ladoNao;
		
		No(String caracteristica, Elemento ladoSim, Elemento ladoNao) {
			_caracteristica = caracteristica;
			_ladoSim = ladoSim;
			_ladoNao = ladoNao;
		}
		
		@Override
		public Elemento learn() {
			if (confirm("Hmm... Por acaso o animal " + _caracteristica + "?"))
				_ladoSim = _ladoSim.learn();
			else
				_ladoNao = _ladoNao.learn();
			
			return this;
		}
	}

	
	
	
}



