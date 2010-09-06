package spikes.klaus.wanderer.sneer;

import java.util.Random;

public class NameGenerator {

	private static final String[] MAN_NAMES = new String[]{"Pedro", "Luiz", "Andre", "William", "Maicon", "Fernando", "Walter", "Cesar", "Charles", "Thomas"};
	private static final String[] WOMAN_NAMES = new String[]{"Neide", "Carla", "Julia", "Maria", "Tamara", "Tatiana", "Samantha", "Luiza", "Ana", "Paula"};
	private static final String[] LASTNAMES = new String[]{"Govier", "Comber", "Downey", "Bettenson", "Harrison", "dos Santos", "Codling", "Landecker", "Jancso", "Cochrane", "Cordeiro", "Bihaiko", "Binhara", "Arouca", "Cusumano"};

	
	public String generateName(Random random) {
		String[] genderNames = random.nextBoolean()
			? MAN_NAMES
			: WOMAN_NAMES;
		String firstName = pick(genderNames, random); //Neide
		String middleName = pick(genderNames, random); //Maria
		String lastName = pick(LASTNAMES, random); // Govier
		
		String result = firstName + " " + middleName + " " + lastName;
		System.out.println(result);
		return result; //Neide Maria Govier
	}


	private <T> T pick(T[] elements, Random random) {
		return elements[random.nextInt(elements.length)];
	}

}
