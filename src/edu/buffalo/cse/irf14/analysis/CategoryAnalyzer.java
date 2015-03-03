package edu.buffalo.cse.irf14.analysis;

import java.util.ArrayList;

public class CategoryAnalyzer extends AbstractAnalyzer {

	public CategoryAnalyzer(TokenStream stream) {
		this.stream = stream;
		filtersToApply = new ArrayList<TokenFilterType>();
		filtersToApply.add(TokenFilterType.CAPITALIZATION);
		filterChain = filtersToApply.iterator();
	}

}
