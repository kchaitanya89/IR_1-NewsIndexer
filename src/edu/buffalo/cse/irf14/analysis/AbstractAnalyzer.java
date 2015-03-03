package edu.buffalo.cse.irf14.analysis;

import java.util.ArrayList;
import java.util.Iterator;

public abstract class AbstractAnalyzer implements Analyzer {
	protected TokenStream stream;
	protected ArrayList<TokenFilterType> filtersToApply;
	protected Iterator<TokenFilterType> filterChain;
	protected TokenFilterFactory factory = TokenFilterFactory.getInstance();

	@Override
	public TokenStream getStream() {
		return this.stream;
	}

	@Override
	public boolean increment() throws TokenizerException {
		TokenFilter filter = factory.getFilterByType(filterChain.next(),
				getStream());

		while (filter.increment()) {
		}

		stream = filter.getStream();
		stream.reset();

		return filterChain.hasNext();
	}

}
