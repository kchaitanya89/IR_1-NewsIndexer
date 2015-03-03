/**
 * 
 */
package edu.buffalo.cse.irf14.analysis;

/**
 * @author nikhillo
 * This class represents the smallest indexable unit of text.
 * At the very least it is backed by a string representation that
 * can be interchangeably used with the backing char array.
 * 
 * You are encouraged to add more data structures and fields as you deem fit. 
 */
public class Token {
	//The backing string representation -- can contain extraneous information
	private String termText;
	//The char array backing termText
	private char[] termBuffer;
	
	private byte flags;
	
	public static final byte IS_TITLE_WORD = (byte)0x01;
	public static final byte IS_SENTENCE_START = (byte)0x02;
	public static final byte IS_CAMEL_CASE = (byte)0x04;
	public static final byte IS_DATE = (byte)0x08;
	public static final byte IS_TIME = (byte)0x10;
	
	public static final byte IS_TOKEN_MARKED_FOR_DEL = (byte)0x80;
	
	/**
	 * Method to set the termText to given text.
	 * This is a sample implementation and you CAN change this
	 * to suit your class definition and data structure needs.
	 * @param text
	 */
	protected void setTermText(String text) {
		termText = text;
		termBuffer = (termText != null) ? termText.toCharArray() : null;
	}
	
	/**
	 * Getter for termText
	 * This is a sample implementation and you CAN change this
	 * to suit your class definition and data structure needs.
	 * @return the underlying termText
	 */
	protected String getTermText() {
		return termText;
	}
	
	/**
	 * Method to set the termBuffer to the given buffer.
	 * This is a sample implementation and you CAN change this
	 * to suit your class definition and data structure needs.
	 * @param buffer: The buffer to be set
	 */
	protected void setTermBuffer(char[] buffer) {
		termBuffer = buffer;
		termText = new String(buffer);
	}
	
	/**
	 * Getter for the field termBuffer
	 * @return The termBuffer
	 */
	protected char[] getTermBuffer() {
		return termBuffer;
	}
	
	/**
	 * Method to merge this token with the given array of Tokens
	 * You are free to update termText and termBuffer as you please
	 * based upon your Token implementation. But the toString() method
	 * below must return whitespace separated value for all tokens combined
	 * Also the token order must be maintained.
	 * @param tokens The token array to be merged
	 */
	protected void merge(Token...tokens) {
		if(tokens==null || tokens.length==0) {
			return;
		}
		StringBuilder builder = new StringBuilder(termText);
		for(Token token:tokens) {
			builder.append(' ').append(token.toString());
		}
		this.termText = builder.toString();
		this.termBuffer = this.termText.toCharArray();
	}
	
	/**
	 * Returns the string representation of this token. It must adhere to the
	 * following rules:
	 * 1. Return only the associated "text" with the token. Any other information 
	 * must be suppressed.
	 * 2. Must return a non-empty value only for tokens that are going to be indexed
	 * If you introduce special token types (sentence boundary for example), return
	 * an empty string
	 * 3. IF the original token A (with string as "a") was merged with tokens B ("b"),
	 * C ("c") and D ("d"), toString should return "a b c d"
	 * @return The raw string representation of the token
	 */
	@Override
	public String toString() {
		return this.termText;
	}

	public short getFlags() {
		return flags;
	}

	public void setFlags(byte flags) {
		this.flags = flags;
	}
	
	public boolean isTitleWord() {
		return (this.flags&IS_TITLE_WORD)!=0;
	}
	
	public boolean isSentenceStart() {
		return (this.flags&IS_SENTENCE_START)!=0;
	}
	
	public boolean isCamelCase() {
		return (this.flags&IS_CAMEL_CASE)!=0;
	}
	
	public boolean isTokenMarkedForDel() {
		return (this.flags&IS_TOKEN_MARKED_FOR_DEL)!=0;
	}
	
	public boolean isDate(){
		return (this.flags&IS_DATE)!=0;
	}
	
	public boolean isTime(){
		return (this.flags&IS_TIME)!=0;
	}
	
	public void markThisForDel() {
		flags |= IS_TOKEN_MARKED_FOR_DEL;
	}
	
	public void markAsTitleWord() {
		flags |= IS_TITLE_WORD;
	}
	
	public void markAsSentenceStart() {
		flags |= IS_SENTENCE_START;
	}
	
	public void markAsCamelCase() {
		flags |= IS_CAMEL_CASE;
	}
	
	public void markAsDate(){
		flags |= IS_DATE;
	}
	
	public void markAsTime(){
		flags |= IS_TIME;
	}
}
