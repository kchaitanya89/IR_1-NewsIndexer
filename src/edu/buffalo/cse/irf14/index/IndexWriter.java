/**
 * 
 */
package edu.buffalo.cse.irf14.index;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

import edu.buffalo.cse.irf14.analysis.Analyzer;
import edu.buffalo.cse.irf14.analysis.AnalyzerFactory;
import edu.buffalo.cse.irf14.analysis.Token;
import edu.buffalo.cse.irf14.analysis.TokenStream;
import edu.buffalo.cse.irf14.analysis.Tokenizer;
import edu.buffalo.cse.irf14.analysis.TokenizerException;
import edu.buffalo.cse.irf14.document.Document;
import edu.buffalo.cse.irf14.document.FieldNames;

/**
 * @author nikhillo Class responsible for writing indexes to disk
 */
public class IndexWriter {

	Map<String, Posting> inMemIndex = new TreeMap<String, Posting>();
	Tokenizer tokenizer = new Tokenizer();
	AnalyzerFactory analyzerFactory = AnalyzerFactory.getInstance();
	FieldNames fields[];
	File folder;

	/**
	 * Default constructor
	 * 
	 * @param indexDir
	 *            : The root directory to be sued for indexing
	 */
	public IndexWriter(String indexDir) {
		folder = new File(indexDir);
		fields = new FieldNames[] { FieldNames.AUTHOR, FieldNames.AUTHORORG,
				FieldNames.CATEGORY, FieldNames.CONTENT, FieldNames.NEWSDATE,
				FieldNames.PLACE };
	}

	/**
	 * Method to add the given Document to the index This method should take
	 * care of reading the filed values, passing them through corresponding
	 * analyzers and then indexing the results for each indexable field within
	 * the document.
	 * 
	 * @param d
	 *            : The Document to be added
	 * @throws IndexerException
	 *             : In case any error occurs
	 */
	public void addDocument(Document document) throws IndexerException {

		String docID = document.getField(FieldNames.FILEID)[0];
		TokenStream tokenStream = null;

		String[] title = document.getField(FieldNames.TITLE);
		if (title != null) {
			addTitleToIndex(title[0], docID);
		}

		for (FieldNames fieldName : fields) {

			String[] fieldContent = document.getField(fieldName);

			if (fieldContent != null) {
				for (String fieldContentLine : fieldContent) {
					try {
						tokenStream = tokenizer.consume(fieldContentLine);
						Analyzer analyzerForField = analyzerFactory
								.getAnalyzerForField(fieldName, tokenStream);

						while (analyzerForField.increment()) {
						}
						tokenStream = analyzerForField.getStream();
						tokenStream.reset();

						addStreamToIndex(tokenStream, docID);

					} catch (TokenizerException e) {
						e.printStackTrace();
						// throw new IndexerException();
					}

				}
			}
		}

	}

	private void addTitleToIndex(String title, String docID)
			throws IndexerException {

		TokenStream stream = null;
		try {
			stream = tokenizer.consume(title);
			while (stream.hasNext()) {
				stream.next().markAsTitleWord();
			}
			stream.reset();
			Analyzer analyzerForField = analyzerFactory.getAnalyzerForField(
					FieldNames.TITLE, stream);
			while (analyzerForField.increment()) {
			}
			stream = analyzerForField.getStream();
			stream.reset();
			addStreamToIndex(stream, docID);
		} catch (TokenizerException ex) {
			throw new IndexerException();
		}

	}

	private void addStreamToIndex(TokenStream tokenStream, String docID) {
		while (tokenStream.hasNext()) {
			Token nextToken = tokenStream.next();
			String nextTokenString = nextToken.toString();

			Posting posting = inMemIndex.get(nextTokenString);
			Map<String, Integer> postingMap = null;
			Integer currentFreq = 1;
			if (posting == null) {
				posting = new Posting();
				postingMap = new TreeMap<String, Integer>();
				postingMap.put(docID, currentFreq);
				posting.setTotalTermFreq(1);
				posting.setTotalDocumentFreq(1);
				posting.setIndividualPostingsMap(postingMap);
			} else {
				currentFreq = posting.getIndividualPostingsMap().get(docID);
				if (currentFreq != null) {
					currentFreq += 1;
				} else {
					currentFreq = 1;
				}
				postingMap = posting.getIndividualPostingsMap();
				postingMap.put(docID, currentFreq);
				posting.setTotalTermFreq(posting.getTotalTermFreq() + 1);
				posting.setTotalDocumentFreq(postingMap.size());
				posting.setIndividualPostingsMap(postingMap);
			}

			inMemIndex.put(nextTokenString, posting);
		}
	}

	/**
	 * Method that indicates that all open resources must be closed and cleaned
	 * and that the entire indexing operation has been completed.
	 * 
	 * @throws IndexerException
	 *             : In case any error occurs
	 */
	public void close() throws IndexerException {
		Properties properties = new Properties();

		for (String term : inMemIndex.keySet()) {
			Posting posting = inMemIndex.get(term);
			Map<String, Integer> termMap = posting.getIndividualPostingsMap();

			StringBuilder docID = new StringBuilder();
			StringBuilder freq = new StringBuilder();
			for (String keyDocID : termMap.keySet()) {
				Integer valueFreq = termMap.get(keyDocID);

				docID.append(keyDocID).append("|");
				freq.append(valueFreq).append("|");

			}

			String docIDString = docID.toString();
			String freqString = freq.toString();

			properties.put(
					term,
					docIDString.substring(0, docIDString.length() - 1) + "@"
							+ freqString.substring(0, freqString.length() - 1)
							+ "@" + posting.getTotalTermFreq() + "@"
							+ posting.getTotalDocumentFreq());

		}

		File file = new File(folder.getAbsolutePath() + File.separatorChar
				+ "indexFile.properties");
		FileOutputStream fileOut;
		try {
			fileOut = new FileOutputStream(file);
			properties.store(fileOut, null);
			fileOut.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			throw new IndexerException();
		} catch (IOException e) {
			e.printStackTrace();
			throw new IndexerException();
		}
	}
}
