package com.transling.api.beans;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Bunch {
	private String id;
	private Date created = new Date ();
	private Date modified = new Date ();
	private String userName;
	private String vocabularyId;
	private Translations translations = new Translations ();
	
	public Bunch() {
		super();
	}
	
	public Bunch(String id, String user) {
		this.id  = id;
		this.userName = user;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public Date getModified() {
		return modified;
	}

	public void setModified(Date modified) {
		this.modified = modified;
	}
	

	public Translations getTranslations() {
		return translations;
	}

	public void setTranslations(Translations translations) {
		this.translations = translations;
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

	@Override
	public String toString() {
		return "Bunch [id=" + id + ", created=" + created + ", modified="
				+ modified + ", userName=" + userName + ", vocabularyId="
				+ vocabularyId + ", translations=" + translations + "]";
	}
	

}
