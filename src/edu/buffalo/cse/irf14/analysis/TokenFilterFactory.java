/**
 * 
 */
package edu.buffalo.cse.irf14.analysis;


/**
 * Factory class for instantiating a given TokenFilter
 * @author nikhillo
 *
 */
public class TokenFilterFactory {
	
	private static final TokenFilterFactory INSTANCE = new TokenFilterFactory();
	
	private TokenFilterFactory(){
		
	}
	
	/**
	 * Static method to return an instance of the factory class.
	 * Usually factory classes are defined as singletons, i.e. 
	 * only one instance of the class exists at any instance.
	 * This is usually achieved by defining a private static instance
	 * that is initialized by the "private" constructor.
	 * On the method being called, you return the static instance.
	 * This allows you to reuse expensive objects that you may create
	 * during instantiation
	 * @return An instance of the factory
	 */
	public static TokenFilterFactory getInstance() {
		return INSTANCE;
	}
	
	/**
	 * Returns a fully constructed {@link TokenFilter} instance
	 * for a given {@link TokenFilterType} type
	 * @param type: The {@link TokenFilterType} for which the {@link TokenFilter}
	 * is requested
	 * @param stream: The TokenStream instance to be wrapped
	 * @return The built {@link TokenFilter} instance
	 */
	public TokenFilter getFilterByType(TokenFilterType type, TokenStream stream) {
		if(stream==null) {
			return null;
		}
		if(type==TokenFilterType.ACCENT) {
			return new AccentRule(stream);
		}else if(type==TokenFilterType.CAPITALIZATION) {
			return new CapitalizationRule(stream);
		}else if(type==TokenFilterType.STOPWORD) {
			return new StopWordsRule(stream);
		}else if(type==TokenFilterType.SYMBOL) {
			return new SymbolRule(stream);
		}else if(type==TokenFilterType.STEMMER) {
			return new StemmerRule(stream);
		}else if(type==TokenFilterType.NUMERIC) {
			return new NumberRule(stream);
		}else if(type==TokenFilterType.DATE) {
			return new DateRule(stream);
		}else if(type==TokenFilterType.SPECIALCHARS) {
			return new SpecialCharRule(stream);
		}
		return null;
	}
}
