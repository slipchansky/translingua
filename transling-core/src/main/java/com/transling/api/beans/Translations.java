package com.transling.api.beans;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Translations extends HashMap<String, Words> {
	public Translations() {
		super();
	}
	public Translations(int initialCapacity, float loadFactor) {
		super(initialCapacity, loadFactor);
	}

	public Translations(int initialCapacity) {
		super(initialCapacity);
	}

	public Translations(Map<String, ? extends Collection<String>> m) {
		super();
		if (m != null) {
			for (String k : m.keySet()) {
				Words w = new Words (m.get(k));
				put (k, w);
			}
		}
	}
}
