/**
 * 
 */
package edu.buffalo.cse.irf14.document;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author nikhillo Class that parses a given file into a Document
 */
public class Parser {

	private static final String AUTHOR_TAG = "(<author>)(.+)(</author>)";
	private static final String BY = "by";
	private static final String AND = "(.+?)\\s+?and\\s";
	private static final String PLACE_DATE = "(.+)((jan|feb|mar|apr|may|jun|jul|aug|sep|oct|nov|dec)[a-zA-Z]*\\s+\\d{1,2})\\s*-";

	private static final Pattern AUTH_PATTERN = Pattern.compile(AUTHOR_TAG,
			Pattern.CASE_INSENSITIVE);
	private static final Pattern BY_PATTERN = Pattern.compile(BY,
			Pattern.CASE_INSENSITIVE);
	private static final Pattern AND_PATTERN = Pattern.compile(AND,
			Pattern.CASE_INSENSITIVE);
	private static final Pattern PLACE_DATE_PATTERN = Pattern.compile(
			PLACE_DATE, Pattern.CASE_INSENSITIVE);

	/**
	 * Static method to parse the given file into the Document object
	 * 
	 * @param filename
	 *            : The fully qualified filename to be parsed
	 * @return The parsed and fully loaded Document object
	 * @throws ParserException
	 *             In case any error occurs during parsing
	 */
	public static Document parse(String filename) throws ParserException {
		// TODO YOU MUST IMPLEMENT THIS

		if (filename == null || filename.length() == 0) {
			throw new ParserException();
		}

		File articleFile = new File(filename);

		if (!articleFile.exists()) {
			throw new ParserException();
		}

		String category = articleFile.getParentFile().getName();
		Document document = new Document();
		document.setField(FieldNames.FILEID, articleFile.getName());
		document.setField(FieldNames.CATEGORY, category);
		Scanner s = null;
		try {
			s = new Scanner(articleFile);

			String title = getNextNonEmptyLine(s);

			if (title != null) {
				document.setField(FieldNames.TITLE, title);
			}

			String line = getNextNonEmptyLine(s);

			if (line != null) {
				String[] authors = retrieveAuthorInfo(line);
				if (authors != null) {
					String authOrg = authors[authors.length - 1];
					String[] toks = authOrg.split(",");
					if (toks.length > 1) {
						authors[authors.length - 1] = toks[0].trim();
						document.setField(FieldNames.AUTHORORG, toks[1].trim());
					}
					document.setField(FieldNames.AUTHOR, authors);

					line = getNextNonEmptyLine(s);
				}
			}

			if (line != null) {
				String[] placeAndDate = retrievePlaceAndDate(line);
				if (placeAndDate != null) {
					String place = placeAndDate[0].trim();
					document.setField(
							FieldNames.PLACE,
							place.endsWith(",") ? place.substring(0,
									place.length() - 1) : place);
					document.setField(FieldNames.NEWSDATE,
							placeAndDate[1].trim());

					line = placeAndDate[2];
				}

				StringBuilder content = new StringBuilder(line);
				while (s.hasNext()) {
					content.append(s.nextLine());
				}

				document.setField(FieldNames.CONTENT, content.toString());
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw (ParserException) e;
		}
		return document;
	}

	private static String[] retrievePlaceAndDate(String line) {
		Matcher m = PLACE_DATE_PATTERN.matcher(line);
		if (m.lookingAt()) {
			String[] placeAndDate = new String[3];
			placeAndDate[0] = m.group(1);
			placeAndDate[1] = m.group(2);
			placeAndDate[2] = line.substring(m.end());
			return placeAndDate;
		}
		return null;
	}

	private static String[] retrieveAuthorInfo(String line) {
		line = line.trim();
		ArrayList<String> authors = null;
		Matcher m = AUTH_PATTERN.matcher(line);
		if (m.lookingAt()) {
			line = m.group(2).trim();
			m = BY_PATTERN.matcher(line);
			if (m.lookingAt()) {
				line = line.substring(m.end()).trim();
			}

			m = AND_PATTERN.matcher(line);

			authors = new ArrayList<String>();
			int lastEnd = -1;

			while (m.find()) {
				authors.add(m.group(1));
				lastEnd = m.end();
			}
			if (lastEnd != -1) {
				authors.add(line.substring(lastEnd).trim());
			}

			if (authors.isEmpty()) {
				return new String[] { line };
			} else {
				String[] auths = new String[authors.size()];
				return authors.toArray(auths);
			}
		}

		return null;
	}

	private static String getNextNonEmptyLine(Scanner s) {
		String line = null;
		if (s.hasNextLine()) {
			line = s.nextLine();
			while (line.length() == 0 && s.hasNextLine()) {
				line = s.nextLine();
			}
		}
		return line;
	}

}
