package com.transling.api.beans;

import java.util.ArrayList;
import java.util.Collection;

public class TranslationFacet extends ArrayList<WordFacet> {

	public TranslationFacet() {
		super();
	}

	public TranslationFacet(Collection<? extends WordFacet> c) {
		super(c);
	}

	public TranslationFacet(int initialCapacity) {
		super(initialCapacity);
	}

}
