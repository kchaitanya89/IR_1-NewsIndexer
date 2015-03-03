package edu.buffalo.cse.irf14.index;

import java.util.Map;

public class Posting implements Comparable<Posting> {
	private Map<String, Integer> individualPostingsMap;
	private int totalTermFreq;
	private int totalDocumentFreq;

	@Override
	public int compareTo(Posting posting) {
		int compare = posting.getTotalTermFreq();
		return compare - this.totalTermFreq;
	}

	public Map<String, Integer> getIndividualPostingsMap() {
		return individualPostingsMap;
	}

	public void setIndividualPostingsMap(Map<String, Integer> individualPostings) {
		this.individualPostingsMap = individualPostings;
	}

	public int getTotalTermFreq() {
		return totalTermFreq;
	}

	public void setTotalTermFreq(int totalTermFreq) {
		this.totalTermFreq = totalTermFreq;
	}

	public int getTotalDocumentFreq() {
		return totalDocumentFreq;
	}

	public void setTotalDocumentFreq(int totalDocumentFreq) {
		this.totalDocumentFreq = totalDocumentFreq;
	}

}