package edu.buffalo.cse.irf14.analysis;

import java.util.regex.Pattern;

public class CapitalizationRule extends TokenFilter {

	private static final Pattern CAMEL_CASE = Pattern.compile("[A-Z][^A-Z]*+");

	public CapitalizationRule(TokenStream stream) {
		super(stream);
	}

	@Override
	public boolean increment() throws TokenizerException {
		Token token = stream.next();
		if (token != null && token.getTermText() != null) {
			String text = token.getTermText();
			if (token.isTitleWord() || token.isSentenceStart()) {
				token.setTermText(text.toLowerCase());
			} else {
				if (CAMEL_CASE.matcher(text).matches()) {
					token.markAsCamelCase();
					Token prevToken = stream.getCurrentEntry().previous.token;
					if (prevToken != null && prevToken.isCamelCase()) {
						prevToken.merge(token);
						stream.remove();
					}
				}
			}
		}
		return stream.hasNext();
	}
}
