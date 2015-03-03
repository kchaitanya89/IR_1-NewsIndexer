package edu.buffalo.cse.irf14.analysis;

import java.util.ArrayList;

public class TermAnalyzer extends AbstractAnalyzer {

	public TermAnalyzer(TokenStream stream) {
		this.stream = stream;
		filtersToApply = new ArrayList<TokenFilterType>();
		filtersToApply.add(TokenFilterType.DATE);
		filtersToApply.add(TokenFilterType.NUMERIC);
		filtersToApply.add(TokenFilterType.SPECIALCHARS);
		filtersToApply.add(TokenFilterType.SYMBOL);
		filtersToApply.add(TokenFilterType.ACCENT);
		filtersToApply.add(TokenFilterType.CAPITALIZATION);
		filtersToApply.add(TokenFilterType.STOPWORD);
		filtersToApply.add(TokenFilterType.STEMMER);
		filterChain = filtersToApply.iterator();
	}

}
