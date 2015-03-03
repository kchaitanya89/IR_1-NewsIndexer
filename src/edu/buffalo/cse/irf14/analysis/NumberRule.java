package edu.buffalo.cse.irf14.analysis;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NumberRule extends TokenFilter {
	
	public static final Pattern DECIMAL_AND_COMA = Pattern.compile("([0-9]+?[,\\.]?[0-9]*)([^0-9]*)");
	public static final String DIGIT = "[0-9]"; 

	public NumberRule(TokenStream stream) {
		super(stream);
	}

	@Override
	public boolean increment() throws TokenizerException {
		Token token = stream.next();
		if(token!=null && !token.isDate() && !token.isTime() && token.getTermText()!=null) {
			String text = token.getTermText();
			Matcher dcm = DECIMAL_AND_COMA.matcher(text);
			if(dcm.matches()) {
				String rem = dcm.group(2);
				if(rem!=null && !rem.equals("")) {
					text = rem;
				}else {
					stream.remove();
					return stream.hasNext();
				}
			} else {
				text = text.replaceAll(DIGIT, "");
			}
			token.setTermText(text);
		}
		return stream.hasNext();
	}

}
