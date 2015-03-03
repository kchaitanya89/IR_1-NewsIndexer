package edu.buffalo.cse.irf14.analysis;

import java.util.ArrayList;

public class AuthorAnalyzer extends AbstractAnalyzer {

	public AuthorAnalyzer(TokenStream stream) {
		this.stream = stream;
		filtersToApply = new ArrayList<TokenFilterType>();
		filtersToApply.add(TokenFilterType.ACCENT);
		filtersToApply.add(TokenFilterType.CAPITALIZATION);
		filterChain = filtersToApply.iterator();
	}
}
