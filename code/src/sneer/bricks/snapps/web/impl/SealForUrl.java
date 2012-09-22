package sneer.bricks.snapps.web.impl;

import sneer.bricks.identity.seals.Seal;


public class SealForUrl {

	private ContactProvider contactProvider;

	public SealForUrl(ContactProvider contactProvider) {
		this.contactProvider = contactProvider;
	}

	public Seal getSealForUrlOrNull(String url) {
		String nickName = url.replaceAll("/([^/]*).*", "$1");
		return contactProvider.getSealForNickOrNull(nickName);
	}

}
