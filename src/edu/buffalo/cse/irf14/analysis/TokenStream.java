/**
 * 
 */
package edu.buffalo.cse.irf14.analysis;

import java.util.Iterator;
/**
 * @author nikhillo Class that represents a stream of Tokens. All
 *         {@link Analyzer} and {@link TokenFilter} instances operate on this to
 *         implement their behavior
 */
public class TokenStream implements Iterator<Token> {
	
	private final TSEntry header = new TSEntry(null, null, null);
	private TSEntry current;
	private TSEntry lastReturned;
	
	public TokenStream(Token[] tokens) {
		header.next = header.previous = header;
		lastReturned = current = header;
		for(int i=0;i<tokens.length;i++) {
			if(tokens[i]==null) {
				continue;
			}
			addToken(tokens[i]);
		}
	}
	
	private void addToken(Token token) {
		TSEntry entry = new TSEntry(token,null,null);
		header.previous.next = entry;
		entry.previous = header.previous;
		entry.next = header;
		header.previous = entry;
	}

	/**
	 * Method that checks if there is any Token left in the stream with regards
	 * to the current pointer. DOES NOT ADVANCE THE POINTER
	 * 
	 * @return true if at least one Token exists, false otherwise
	 */
	@Override
	public boolean hasNext() {
		return lastReturned.next!=header;
	}

	/**
	 * Method to return the next Token in the stream. If a previous hasNext()
	 * call returned true, this method must return a non-null Token. If for any
	 * reason, it is called at the end of the stream, when all tokens have
	 * already been iterated, return null
	 */
	@Override
	public Token next() {
		Token token = null;
		if(hasNext()) {
			current = lastReturned.next;
			lastReturned = current;
			token = current.token;
		}else {
			current = header;
		}
		return token;
	}

	/**
	 * Method to remove the current Token from the stream. Note that "current"
	 * token refers to the Token just returned by the next method. Must thus be
	 * NO-OP when at the beginning of the stream or at the end
	 */
	@Override
	public void remove() {
		if(current==header) {
			return;
		}
		current.previous.next = current.next;
		current.next.previous = current.previous;
		lastReturned = current.previous;
		current.next = current.previous = null;
		current.token = null;
		current = header;
	}

	/**
	 * Method to reset the stream to bring the iterator back to the beginning of
	 * the stream. Unless the stream has no tokens, hasNext() after calling
	 * reset() must always return true.
	 */
	public void reset() {
		lastReturned = current = header;
	}

	/**
	 * Method to append the given TokenStream to the end of the current stream
	 * The append must always occur at the end irrespective of where the
	 * iterator currently stands. After appending, the iterator position must be
	 * unchanged Of course this means if the iterator was at the end of the
	 * stream and a new stream was appended, the iterator hasn't moved but that
	 * is no longer the end of the stream.
	 * 
	 * @param stream
	 *            : The stream to be appended
	 */
	public void append(TokenStream stream) {
		if(stream==null) {
			return;
		}
		while(stream.hasNext()) {
			this.addToken(stream.next());
		}
		stream.reset();
	}

	/**
	 * Method to get the current Token from the stream without iteration. The
	 * only difference between this method and {@link TokenStream#next()} is
	 * that the latter moves the stream forward, this one does not. Calling this
	 * method multiple times would not alter the return value of
	 * {@link TokenStream#hasNext()}
	 * 
	 * @return The current {@link Token} if one exists, null if end of stream
	 *         has been reached or the current Token was removed
	 */
	public Token getCurrent() {
		return current.token;
	}
	
	public TSEntry getCurrentEntry() {
		return current==header?null:current;
	}
	
	public boolean isFirstOrLast() {
		return current.next==header || current.previous==header;
	}
	
}
