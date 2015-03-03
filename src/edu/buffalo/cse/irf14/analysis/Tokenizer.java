/**
 * 
 */
package edu.buffalo.cse.irf14.analysis;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author nikhillo
 * Class that converts a given string into a {@link TokenStream} instance
 */
public class Tokenizer {
	
	private final String delim;
	public final static String SPACE = "\\s";
	public static final String EMPTY_STRING = "";
	private static final Pattern SENTENCE_PATTERN = Pattern.compile(".+?[\\.\\!\\?]\\s+?");
	/**
	 * Default constructor. Assumes tokens are whitespace delimited
	 */
	public Tokenizer() {
		this.delim = SPACE;
	}
	
	/**
	 * Overloaded constructor. Creates the tokenizer with the given delimiter
	 * @param delim : The delimiter to be used
	 */
	public Tokenizer(String delim) {
		this.delim = delim;
	}
	
	/**
	 * Method to convert the given string into a TokenStream instance.
	 * This must only break it into tokens and initialize the stream.
	 * No other processing must be performed. Also the number of tokens
	 * would be determined by the string and the delimiter.
	 * So if the string were "hello world" with a whitespace delimited
	 * tokenizer, you would get two tokens in the stream. But for the same
	 * text used with lets say "~" as a delimiter would return just one
	 * token in the stream.
	 * @param str : The string to be consumed
	 * @return : The converted TokenStream as defined above
	 * @throws TokenizerException : In case any exception occurs during
	 * tokenization
	 */
	public TokenStream consume(String str) throws TokenizerException {
		if(str==null || str.equals(EMPTY_STRING)) {
			throw new TokenizerException();
		}
		Matcher m = SENTENCE_PATTERN.matcher(str);
		int lastMatchEndsAt = 0;
		ArrayList<String> sentences = new ArrayList<String>();
		while(m.find()) {
			lastMatchEndsAt = m.end();
			sentences.add(m.group());
		}
		if(lastMatchEndsAt<str.length()) {
			sentences.add(str.substring(lastMatchEndsAt, str.length()));			
		}
		String[][] texts = new String[sentences.size()][];
		int textsCount = 0;
		for(int i=0;i<sentences.size();i++) {
			texts[i] = sentences.get(i).trim().split(this.delim);
			textsCount += texts[i].length;
		}		
		Token[] tokens = new Token[textsCount];
		int k = 0;
		for(int i=0;i<texts.length;i++) {
			for(int j=0;j<texts[i].length;j++) {
				Token token = new Token();
				token.setTermText(texts[i][j]);
				if(j==0) {
					token.markAsSentenceStart();
				}
				tokens[k++] = token;
			}			
		}
		
		TokenStream stream = new TokenStream(tokens);
		return stream;
	}
}
