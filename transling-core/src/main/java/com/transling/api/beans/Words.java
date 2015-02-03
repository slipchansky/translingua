package com.transling.api.beans;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class Words extends ArrayList<String> {
	
	
	public Words() {
		super();
	}

	public Words(Collection<String> c) {
		super();
		
		for (String s : c) {
			if (!contains(s)) add (s);
		}
	}

	public Words(int initialCapacity) {
		super(initialCapacity);
	}

	public Words(String ...  words) {
		addAll(Arrays.asList(words));
	}

	private static final long serialVersionUID = 8350934398718546536L;
}
