package edu.buffalo.cse.irf14.analysis;

import java.util.regex.Pattern;

public class StemmerRule extends TokenFilter {
	
	private static final PorterStemmer stemmer = new PorterStemmer();
	private static final Pattern ENGLISH_WORD = Pattern.compile(".*?[^a-zA-Z]+?.*");

	public StemmerRule(TokenStream stream) {
		super(stream);
	}

	@Override
	public boolean increment() throws TokenizerException {
		Token token = stream.next();
		if(token!=null && token.getTermText()!=null) {
			String text = token.getTermText();
			if(!ENGLISH_WORD.matcher(text).matches()) {
				char termBuffer[] = token.getTermBuffer();
				stemmer.add(termBuffer, termBuffer.length);
				stemmer.stem();
				text = stemmer.toString();
				token.setTermText(text);			
			}
		}		
		return stream.hasNext();
	}

}
