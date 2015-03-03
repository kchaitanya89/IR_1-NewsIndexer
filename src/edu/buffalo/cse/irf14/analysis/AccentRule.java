package edu.buffalo.cse.irf14.analysis;

import java.text.Normalizer;
import java.text.Normalizer.Form;

public class AccentRule extends TokenFilter {
	
	public AccentRule(TokenStream stream) {
		super(stream);
	}

	@Override
	public boolean increment() throws TokenizerException {
		Token tok = stream.next();
		if(tok!=null && tok.getTermText()!=null) {
			String decomposed = Normalizer.normalize(tok.getTermText(), Form.NFD);			
			String english = decomposed.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
			tok.setTermText(english);
		}
		return stream.hasNext();
	}

}
