package spikes.rene.jogoai;

class JogadorTeste implements Jogador {

	private int posicao;
	String[] script = new String[] {
		"Bem vindo ao Jogo Dos Animais 1.0 Console Edition.\nImagine um animal e eu tentarei adivinhar qual eh.\nNao se esqueca de que nao possuo animais predefinidos.\n",
		"O animal eh cachorro?",
		"n",
		"Desisto! qual era o animal???",
		"golfinho",
		"golfinho eh diferente de cachorro porque golfinho...",
		"vive no mar",
		"Quer jogar de novo?",
		"s",
		"Hmm... Por acaso o animal vive no mar?",
		"n",
		"O animal eh cachorro?",
		"n",
		"Desisto! qual era o animal???",
		"pulga",
		"pulga eh diferente de cachorro porque pulga...",
		"tem 6 patas",
		"Quer jogar de novo?",
		"s",
		"Hmm... Por acaso o animal vive no mar?",
		"n",
		"Hmm... Por acaso o animal tem 6 patas?",
		"s",
		"O animal eh pulga?",
		"s",
		"Ahaaa eu sabia xD!",
		"Quer jogar de novo?",
		"s",
		"Hmm... Por acaso o animal vive no mar?",
		"s",
		"O animal eh golfinho?",
		"n",
		"Desisto! qual era o animal???",
		"tubarao",
		"tubarao eh diferente de golfinho porque tubarao...",
		"tem dentes grandes",
		"Quer jogar de novo?",
		"s",
		"Hmm... Por acaso o animal vive no mar?",
		"s",
		"Hmm... Por acaso o animal tem dentes grandes?",
		"n",
		"O animal eh golfinho?",
		"n",
		"Desisto! qual era o animal???",
		"estrela do mar",
		"estrela do mar eh diferente de golfinho porque estrela do mar...",
		"se regenera",
		"Quer jogar de novo?",
		"s",
		"Hmm... Por acaso o animal vive no mar?",
		"s",
		"Hmm... Por acaso o animal tem dentes grandes?",
		"n",
		"Hmm... Por acaso o animal se regenera?",
		"n",
		"O animal eh golfinho?",
		"s",
		"Ahaaa eu sabia xD!",
		"Quer jogar de novo?",
		"n",
		"\nFechando..."
	};
	
	@Override
	public boolean confirm(String proposicao) {
		return answer(proposicao).equalsIgnoreCase("s");
	}
	
	@Override
	public String answer(String msg) {
		acknowledge(msg);
		return proximaLinha();
	}

	@Override
	public void acknowledge(String texto) {
		String esperado = proximaLinha();
		if (!esperado.equals(texto))
			throw new IllegalStateException("Esperava: \n'" + esperado + "'\nmas recebi \n'" + texto + "'. \nPosicao: " + posicao);
	}

	private String proximaLinha() {
		String linha = script[posicao++];
		if (posicao == script.length) System.out.println("Beleza!");
		return linha;
	}

}
