package spikes.klaus.wanderer.sneer;


public class NameGenerator {

	private static final String[] MAN_NAMES = new String[]{"Pedro", "Luiz", "Andre", "William", "Maicon", "Fernando", "Walter", "Cesar", "Charles", "Thomas"};
	private static final String[] WOMAN_NAMES = new String[]{"Neide", "Carla", "Julia", "Maria", "Tamara", "Tatiana", "Samantha", "Luiza", "Ana", "Paula"};
	private static final String[] LASTNAMES = new String[]{"Silva", "de Oliveira", "Downey", "Wuestefeld", "Harrison", "dos Santos", "Codling", "Turing", "Jancso", "Govier", "Roemer", "Bihaiko", "Binhara", "Arouca", "von Goedel"};

	
	private final Chooser _chooser;

	
	NameGenerator(Chooser chooser) {
		_chooser = chooser;
	}


	public String generateName() {
		String[] genderNames = _chooser.nextBoolean()
			? MAN_NAMES
			: WOMAN_NAMES;
		String firstName = _chooser.pickOne(genderNames); // Neide
		String middleName = _chooser.pickOneExcept(genderNames, firstName); // Maria
		String lastName = _chooser.pickOne(LASTNAMES); // Govier
		
		String result = firstName + " " + middleName + " " + lastName;
		System.out.println(result);
		return result; //Neide Maria Govier
	}

}
