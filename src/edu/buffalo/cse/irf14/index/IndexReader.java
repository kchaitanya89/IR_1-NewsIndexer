/**
 *
 */
package edu.buffalo.cse.irf14.index;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * @author nikhillo Class that emulates reading data back from a written index
 */
public class IndexReader {
	Map<String, Posting> inMemIndex = new TreeMap<String, Posting>();

	/**
	 * Default constructor
	 *
	 * @param indexDir
	 *            : The root directory from which the index is to be read. This
	 *            will be exactly the same directory as passed on IndexWriter.
	 *            In case you make subdirectories etc., you will have to handle
	 *            it accordingly.
	 * @param type
	 *            The {@link IndexType} to read from
	 */
	public IndexReader(String indexDir, IndexType type) {
		Properties properties = new Properties();
		File file = new File(indexDir + File.separatorChar
				+ "indexFile.properties");
		FileInputStream fileInputStream;
		try {
			fileInputStream = new FileInputStream(file);
			properties.load(fileInputStream);
			fileInputStream.close();

			for (Object term : properties.keySet()) {
				String value = (String) properties.get(term);
				String[] split = value.split("@");
				String[] documentIDs = split[0].split("\\|");
				String[] frequencies = split[1].split("\\|");
				Integer totalTermFreq = Integer.parseInt(split[2]);
				Integer totalDocumentFreq = Integer.parseInt(split[3]);
				Map<String, Integer> individualPostingsMap = new TreeMap<String, Integer>();
				for (int i = 0; i < documentIDs.length; i++) {
					individualPostingsMap.put(documentIDs[i],
							Integer.parseInt(frequencies[i]));
				}
				Posting posting = new Posting();
				posting.setIndividualPostingsMap(individualPostingsMap);
				posting.setTotalTermFreq(totalTermFreq);
				posting.setTotalDocumentFreq(totalDocumentFreq);

				inMemIndex.put(term.toString(), posting);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Get total number of terms from the "key" dictionary associated with this
	 * index. A postings list is always created against the "key" dictionary
	 *
	 * @return The total number of terms
	 */
	public int getTotalKeyTerms() {
		return inMemIndex != null ? inMemIndex.size() : -1;
	}

	/**
	 * Get total number of terms from the "value" dictionary associated with
	 * this index. A postings list is always created with the "value" dictionary
	 *
	 * @return The total number of terms
	 */
	public int getTotalValueTerms() {
		Set<String> set = new TreeSet<String>();
		for (Object key : inMemIndex.keySet()) {

			Posting posting = inMemIndex.get(key);
			Map<String, Integer> individualPostingsMap = posting
					.getIndividualPostingsMap();

			for (Object innerKey : individualPostingsMap.keySet()) {
				set.add(innerKey.toString());
			}
		}
		return set != null ? set.size() : -1;
	}

	/**
	 * Method to get the postings for a given term. You can assume that the raw
	 * string that is used to query would be passed through the same Analyzer as
	 * the original field would have been.
	 *
	 * @param term
	 *            : The "analyzed" term to get postings for
	 * @return A Map containing the corresponding fileid as the key and the
	 *         number of occurrences as values if the given term was found, null
	 *         otherwise.
	 */
	public Map<String, Integer> getPostings(String term) {
		Posting posting = inMemIndex.get(term);
		Map<String, Integer> individualPostingsMap = null;
		if (posting != null) {
			individualPostingsMap = posting.getIndividualPostingsMap();
		}
		return individualPostingsMap;
	}

	/**
	 * Method to get the top k terms from the index in terms of the total number
	 * of occurrences.
	 *
	 * @param k
	 *            : The number of terms to fetch
	 * @return : An ordered list of results. Must be <=k fr valid k values null
	 *         for invalid k values
	 */
	public List<String> getTopK(int k) {
		SortedSet<Map.Entry<String, Posting>> sortedset = new TreeSet<Map.Entry<String, Posting>>(
				new Comparator<Map.Entry<String, Posting>>() {
					@Override
					public int compare(Map.Entry<String, Posting> e1,
							Map.Entry<String, Posting> e2) {
						return e1.getValue().compareTo(e2.getValue());
					}
				});

		sortedset.addAll(inMemIndex.entrySet());

		Iterator<Entry<String, Posting>> iterator = sortedset.iterator();
		int counter = 1;
		List<String> arrayList = new ArrayList<String>();
		while (iterator.hasNext() && counter <= k) {
			Map.Entry<String, Posting> entry = iterator.next();
			arrayList.add(entry.getKey());
			counter++;
		}

		return arrayList.isEmpty() ? null : arrayList;
	}

	/**
	 * Method to implement a simple boolean AND query on the given index
	 *
	 * @param terms
	 *            The ordered set of terms to AND, similar to getPostings() the
	 *            terms would be passed through the necessary Analyzer.
	 * @return A Map (if all terms are found) containing FileId as the key and
	 *         number of occurrences as the value, the number of occurrences
	 *         would be the sum of occurrences for each participating term.
	 *         return null if the given term list returns no results BONUS ONLY
	 */
	public Map<String, Integer> query(String... terms) {
		Map<String, Integer> resultMap = null;
		List<Map<String, Integer>> mapList = new ArrayList<Map<String, Integer>>();
		for (String term : terms) {
			Posting posting = inMemIndex.get(term);
			Map<String, Integer> individualPostingsMap = posting
					.getIndividualPostingsMap();
			mapList.add(individualPostingsMap);
		}

		if (!mapList.isEmpty()) {
			resultMap = new TreeMap<String, Integer>(mapList.get(0));
			// Intersection of docIDs
			for (Map<String, Integer> map : mapList) {
				resultMap.keySet().retainAll(map.keySet());
			}

			int freqSum = 0;
			// Update Frequencies
			for (String docID : resultMap.keySet()) {
				for (Map<String, Integer> map : mapList) {
					freqSum += map.get(docID);
				}
				resultMap.put(docID, freqSum);
				freqSum = 0;
			}
		}
		return resultMap.isEmpty() ? null : resultMap;
	}
}