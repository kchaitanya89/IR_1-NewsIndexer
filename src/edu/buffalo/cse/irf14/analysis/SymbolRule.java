package edu.buffalo.cse.irf14.analysis;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SymbolRule extends TokenFilter {
	public static final char SINGLE_QUOTE = '\'';
	public static final Pattern PUNCTUATION = Pattern.compile("(.*?)([\\.\\!\\?]+?)");
	public static final Pattern HYPHEN_POST_PRE = Pattern.compile("-*?([^-]*?)-*?");
	public static final Pattern HYPHEN_MID = Pattern.compile("([^-]+?)(-+?)([^-]+)");
	public static final Pattern HYPHEN_MID2 = Pattern.compile("(-+?)([^-]+)");
	public static final Pattern NUMBER_CHECK = Pattern.compile(".*?[0-9]");
	public static final HashMap<String,String> CONTRACTIONS_MAP = new HashMap<String, String>();

	public SymbolRule(TokenStream stream) {
		super(stream);
	}

	@Override
	public boolean increment() throws TokenizerException {
		Token token = stream.next();
		if(token==null || token.getTermText()==null) {
			return stream.hasNext();
		}
		String text = token.getTermText();
		String expandedForm = this.expandContractions(text);
		text = expandedForm==null?text:expandedForm;
		if(text.endsWith("'s")) {
			text = text.substring(0, text.length()-2);
		}
		text = text.replaceAll("'", "");
		Matcher pm = PUNCTUATION.matcher(text);
		if(pm.matches()) {
			text = pm.group(1);			
		}
		
		Matcher hyp = HYPHEN_POST_PRE.matcher(text);
		if(hyp.matches()) {
			text = hyp.group(1);
		}
		if(text==null || text.equals("")) {
			stream.remove();
			return stream.hasNext();
		}		
		
		Matcher m1 = HYPHEN_MID.matcher(text);
		if(m1.find()) {
			StringBuilder str = new StringBuilder();
			String before=null,after=null;
			before = m1.group(1);
			after = m1.group(3);
			boolean b = false;
			boolean a = NUMBER_CHECK.matcher(after).matches();
			str.append(before);
			if(a || (b = NUMBER_CHECK.matcher(before).matches())) {
				str.append('-');
			}else {
				str.append(' ');
			}
			str.append(after);
			int start = m1.end();
			Matcher m2 = HYPHEN_MID2.matcher(text);
			while(m2.find(start)) {
				before = after;
				b = a;
				after = m2.group(2);
				a = NUMBER_CHECK.matcher(after).matches();
				if(a || b) {
					str.append('-');
				}else {
					str.append(' ');
				}
				str.append(after);
				start = m2.end();
			}
			text = str.toString();
		}
		token.setTermText(text);
		return stream.hasNext();
	}
	
	private boolean zombie = true;
	
	public static void main(String[] args) throws TokenizerException {
		TokenStream stream = new Tokenizer().consume("--a-b1--c-d-e1-");
		TokenFilter filter = new SymbolRule(stream);
		while(filter.increment()) {}
	}
	
	public String expandContractions(String text) {
		if(!zombie) {
			return null;
		}
		if(CONTRACTIONS_MAP.isEmpty()) {
			populateContractionsMap();
		}
		String result = CONTRACTIONS_MAP.get(text.toLowerCase());
		boolean isUpper = Pattern.matches("[A-Z].*", text);
		result = isUpper ? (result!=null?(text.substring(0, 1)+result.substring(1)):null) : result;
		return result;
	}

	private void populateContractionsMap() {
		CONTRACTIONS_MAP.put("ain't","am not");
		CONTRACTIONS_MAP.put("aren't","are not");
		CONTRACTIONS_MAP.put("can't","cannot");
		CONTRACTIONS_MAP.put("could've","could have");
		CONTRACTIONS_MAP.put("couldn't","could not");
		CONTRACTIONS_MAP.put("couldn't've","could not have");
		CONTRACTIONS_MAP.put("didn't","did not");
		CONTRACTIONS_MAP.put("doesn't","does not");
		CONTRACTIONS_MAP.put("don't","do not");
		CONTRACTIONS_MAP.put("hadn't","had not");
		CONTRACTIONS_MAP.put("hadn't've","had not have");
		CONTRACTIONS_MAP.put("hasn't","has not");
		CONTRACTIONS_MAP.put("haven't","have not");
		CONTRACTIONS_MAP.put("he'd","he would");
		CONTRACTIONS_MAP.put("he'd've","he would have");
		CONTRACTIONS_MAP.put("he'll","he will");
		CONTRACTIONS_MAP.put("he's","he is");
		CONTRACTIONS_MAP.put("how'd","how would");
		CONTRACTIONS_MAP.put("how'll","how will");
		CONTRACTIONS_MAP.put("how's","how is");
		CONTRACTIONS_MAP.put("i'd","I would");
		CONTRACTIONS_MAP.put("i'd've","I would have");
		CONTRACTIONS_MAP.put("i'll","I will");
		CONTRACTIONS_MAP.put("i'm","I am");
		CONTRACTIONS_MAP.put("i've","I have");
		CONTRACTIONS_MAP.put("isn't","is not");
		CONTRACTIONS_MAP.put("it'd","it would");
		CONTRACTIONS_MAP.put("it'd've","it would have");
		CONTRACTIONS_MAP.put("it'll","it will");
		CONTRACTIONS_MAP.put("it's","it is");
		CONTRACTIONS_MAP.put("let's","let us");
		CONTRACTIONS_MAP.put("ma'am","madam");
		CONTRACTIONS_MAP.put("mightn't","might not");
		CONTRACTIONS_MAP.put("mightn't've","might not have");
		CONTRACTIONS_MAP.put("might've","might have");
		CONTRACTIONS_MAP.put("mustn't","must not");
		CONTRACTIONS_MAP.put("must've","must have");
		CONTRACTIONS_MAP.put("needn't","need not");
		CONTRACTIONS_MAP.put("not've","not have");
		CONTRACTIONS_MAP.put("o'clock","of the clock");
		CONTRACTIONS_MAP.put("shan't","shall not");
		CONTRACTIONS_MAP.put("she'd","she would");
		CONTRACTIONS_MAP.put("she'd've","she would have");
		CONTRACTIONS_MAP.put("she'll","she will");
		CONTRACTIONS_MAP.put("she's","she is");
		CONTRACTIONS_MAP.put("should've","should have");
		CONTRACTIONS_MAP.put("shouldn't","should not");
		CONTRACTIONS_MAP.put("shouldn't've","should not have");
		CONTRACTIONS_MAP.put("that's","that is");
		CONTRACTIONS_MAP.put("there'd","there would");
		CONTRACTIONS_MAP.put("there'd've","there would have");
		CONTRACTIONS_MAP.put("there're","there are");
		CONTRACTIONS_MAP.put("there's","there is");
		CONTRACTIONS_MAP.put("they'd","they would");
		CONTRACTIONS_MAP.put("they'd've","they would have");
		CONTRACTIONS_MAP.put("they'll","they will");
		CONTRACTIONS_MAP.put("they're","they are");
		CONTRACTIONS_MAP.put("they've","they have");
		CONTRACTIONS_MAP.put("wasn't","was not");
		CONTRACTIONS_MAP.put("we'd","we would");
		CONTRACTIONS_MAP.put("we'd've","we would have");
		CONTRACTIONS_MAP.put("we'll","we will");
		CONTRACTIONS_MAP.put("we're","we are");
		CONTRACTIONS_MAP.put("we've","we have");
		CONTRACTIONS_MAP.put("weren't","were not");
		CONTRACTIONS_MAP.put("what'll","what will");
		CONTRACTIONS_MAP.put("what're","what are");
		CONTRACTIONS_MAP.put("what's","what is");
		CONTRACTIONS_MAP.put("what've","what have");
		CONTRACTIONS_MAP.put("when's","when is");
		CONTRACTIONS_MAP.put("where'd","where did");
		CONTRACTIONS_MAP.put("where's","where is");
		CONTRACTIONS_MAP.put("where've","where have");
		CONTRACTIONS_MAP.put("who'd","who would");
		CONTRACTIONS_MAP.put("who'll","who shall");
		CONTRACTIONS_MAP.put("who're","who are");
		CONTRACTIONS_MAP.put("who's","who is");
		CONTRACTIONS_MAP.put("who've","who have");
		CONTRACTIONS_MAP.put("why'll","why will");
		CONTRACTIONS_MAP.put("why're","why are");
		CONTRACTIONS_MAP.put("why's","why is");
		CONTRACTIONS_MAP.put("won't","will not");
		CONTRACTIONS_MAP.put("would've","would have");
		CONTRACTIONS_MAP.put("wouldn't","would not");
		CONTRACTIONS_MAP.put("wouldn't've","would not have");
		CONTRACTIONS_MAP.put("y'all","you all");
		CONTRACTIONS_MAP.put("y'all'd've","you all should have");
		CONTRACTIONS_MAP.put("you'd","you would");
		CONTRACTIONS_MAP.put("you'd've","you would have");
		CONTRACTIONS_MAP.put("you'll","you will");
		CONTRACTIONS_MAP.put("you're","you are");
		CONTRACTIONS_MAP.put("you've","you have");	
		CONTRACTIONS_MAP.put("'em", "them");
	}

	public boolean isZombie() {
		return zombie;
	}

	public void setZombie(boolean zombie) {
		this.zombie = zombie;
	}

}
