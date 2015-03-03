package edu.buffalo.cse.irf14.analysis;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SpecialCharRule extends TokenFilter {
	
	static final String simpleNumberPatternString = ".*[0-9].*";
	static final Pattern numberPattern = Pattern.compile(simpleNumberPatternString);

	public SpecialCharRule(TokenStream stream) {
		super(stream);
	}

	@Override
	public boolean increment() throws TokenizerException {		

		Token token = stream.next();		

		if (token != null && token.getTermText() != null) {
			String termText = token.getTermText();
			Matcher numberMatcher = numberPattern.matcher(termText);
			if (numberMatcher.find() && termText.contains("-")) {
				termText = termText.replaceAll("[^a-zA-Z0-9-\\.]", "");
			} else {
				termText = termText.replaceAll("[^a-zA-Z0-9\\.]", "");
			}
			if(termText.endsWith(".")){
				termText = termText.substring(0,termText.length()-1);
			}
			token.setTermText(termText);
		}
		return stream.hasNext();
	}

}
