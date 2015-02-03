package com.transling.api.beans;

public class WordFacet {
	private String word;
	private long count;

	public WordFacet() {
		super();
	}

	public WordFacet(String word, long count) {
		super();
		this.word = word;
		this.count = count;
	}


	public String getWord() {
		return word;
	}

	public void setWord(String word) {
		this.word = word;
	}

	public long getCount() {
		return count;
	}

	public void setCount(long count) {
		this.count = count;
	}

	@Override
	public String toString() {
		return "WordFacet [word=" + word + ", count=" + count + "]";
	}

}
