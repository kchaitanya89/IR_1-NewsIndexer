package edu.buffalo.cse.irf14.analysis;

public class TSEntry {
	
	public Token token;
	public TSEntry next;
	public TSEntry previous;
	
	public TSEntry(Token token,TSEntry next,TSEntry previous) {
		this.token = token;
		this.next = next;
		this.previous = previous;
	}

}
