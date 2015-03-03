package edu.buffalo.cse.irf14.analysis;

import java.util.ArrayList;

public class PlaceAnalyzer extends AbstractAnalyzer {

	public PlaceAnalyzer(TokenStream stream) {
		this.stream = stream;
		filtersToApply = new ArrayList<TokenFilterType>();
		filtersToApply.add(TokenFilterType.ACCENT);
		filtersToApply.add(TokenFilterType.CAPITALIZATION);
		filtersToApply.add(TokenFilterType.SPECIALCHARS);
		filterChain = filtersToApply.iterator();
	}

}
