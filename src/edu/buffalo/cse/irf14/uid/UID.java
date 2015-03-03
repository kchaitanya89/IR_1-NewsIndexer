package edu.buffalo.cse.irf14.uid;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

import edu.buffalo.cse.irf14.index.IndexType;

public class UID {
	
	private static final AtomicInteger AI = new AtomicInteger();
	private static final HashMap<IndexType,UID> UID_CONTAINER = new HashMap<IndexType,UID>(4); 
	private UID(){
		UID_CONTAINER.put(IndexType.AUTHOR, new UID());
		UID_CONTAINER.put(IndexType.PLACE, new UID());
		UID_CONTAINER.put(IndexType.TERM, new UID());
		UID_CONTAINER.put(IndexType.CATEGORY, new UID());
	}
	
	public static UID getInstance(IndexType type) {		
		return UID_CONTAINER.get(type);
	}
	
	public int next(){
		return AI.getAndIncrement();
	}

}
