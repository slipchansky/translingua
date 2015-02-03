package com.transling.api.beans;

public class WordTranslation {
	
	private String userName;
	private String vocabularyId;
	private String langFrom;
	private String langTo;
	private String word;
	
	TranslationFacet translations = new TranslationFacet ();

	public WordTranslation() {
		super();
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getVocabularyId() {
		return vocabularyId;
	}

	public void setVocabularyId(String vocabularyId) {
		this.vocabularyId = vocabularyId;
	}

	public String getLangFrom() {
		return langFrom;
	}

	public void setLangFrom(String langFrom) {
		this.langFrom = langFrom;
	}

	public String getLangTo() {
		return langTo;
	}

	public void setLangTo(String langTo) {
		this.langTo = langTo;
	}

	public String getWord() {
		return word;
	}

	public void setWord(String word) {
		this.word = word;
	}

	public TranslationFacet getTranslations() {
		return translations;
	}

	public void setTranslations(TranslationFacet translations) {
		this.translations = translations;
	}

	@Override
	public String toString() {
		return "WordTranslation [userName=" + userName + ", vocabularyId="
				+ vocabularyId + ", langFrom=" + langFrom + ", langTo="
				+ langTo + ", word=" + word + ", translations=" + translations
				+ "]";
	}

}
