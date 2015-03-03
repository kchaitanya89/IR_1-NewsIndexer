package edu.buffalo.cse.irf14.analysis;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DateRule extends TokenFilter {

	private static HashMap<String, String> months = new HashMap<String, String>();

	static {
		months.put("jan", "01");
		months.put("feb", "02");
		months.put("mar", "03");
		months.put("apr", "04");
		months.put("may", "05");
		months.put("jun", "06");
		months.put("jul", "07");
		months.put("aug", "08");
		months.put("sep", "09");
		months.put("oct", "10");
		months.put("nov", "11");
		months.put("dec", "12");
	}

	// Number pattern
	static final String numberPatternString = "([0-9]{1,4})((-)([0-9]{1,4}))?+(AD|BC)?(,|\\.)?";
	static final Pattern numberPattern = Pattern.compile(numberPatternString);

	// Month pattern
	static final String monthPatternString = "(jan|feb|mar|apr|may|jun|jul|aug|sep|oct|nov|dec)[a-zA-Z]*(,|\\.)?";
	static final Pattern monthPattern = Pattern.compile(monthPatternString,
			Pattern.CASE_INSENSITIVE);

	// Dating system pattern
	static final String datingSystemPatternString = "(AD|BC)([,]?)";
	static final Pattern datingSystemPattern = Pattern.compile(
			datingSystemPatternString, Pattern.CASE_INSENSITIVE);

	// Year + dating system pattern
	static final String yearDatingSystemPatternString = "(AD|BC)([,]?)";
	static final Pattern yearDatingSystemPattern = Pattern.compile(
			datingSystemPatternString, Pattern.CASE_INSENSITIVE);

	// Time pattern
	static final String timePatternString = "([0-9]|0[0-9]|1[0-2]):([0-5][0-9]):?([0-5][0-9])?(AM|PM)?(\\.|,)?";
	static final Pattern timePattern = Pattern.compile(timePatternString);

	// AM - PM pattern
	static final String amPMPatternString = "(AM|PM)(,|\\.)?";
	static final Pattern amPMPattern = Pattern.compile(amPMPatternString,
			Pattern.CASE_INSENSITIVE);

	public DateRule(TokenStream stream) {
		super(stream);
	}

	@Override
	public boolean increment() throws TokenizerException {

		Token currentToken = stream.next();
		if (currentToken == null || currentToken.getTermText() == null) {
			return stream.hasNext();
		}

		if (currentToken.isTokenMarkedForDel()) {
			stream.remove();
			return stream.hasNext();
		}
		Token nextToken = currentToken != null ? stream.getCurrentEntry().next.token
				: null;
		Token nextToken2 = nextToken != null ? stream.getCurrentEntry().next.next.token
				: null;

		Object[] currentMatches = new Object[9];
		Object[] nextMatches = new Object[9];
		Object[] nextMatches2 = new Object[9];

		currentMatches = findMatches(currentToken);
		nextMatches = findMatches(nextToken);
		nextMatches2 = findMatches(nextToken2);

		StringBuilder finalTermText = null;

		Time time = (Time) currentMatches[4];
		if (time != null) {
			if (time.amPM != null) {
				if (time.amPM.equalsIgnoreCase("pm")) {
					time.hours = time.hours + 12;
				}

				currentToken.setTermText(time.toString());
				currentToken.markAsTime();
			} else if (nextMatches[5] != null) {
				if (((String) nextMatches[5]).equalsIgnoreCase("pm")) {
					time.hours = time.hours + 12;
				}
				if (nextMatches[3] != null) {
					currentToken.setTermText(time.toString() + nextMatches[3]);
					currentToken.markAsTime();
				} else {
					currentToken.setTermText(time.toString());
					currentToken.markAsTime();
				}
				nextToken.markThisForDel();
			}

		} else {

			if (currentMatches[0] != null && nextMatches[2] != null) {
				// 84 BC
				finalTermText = new StringBuilder();

				currentMatches[0] = yearPadding((String) currentMatches[0]);
				if (((String) nextMatches[2]).equalsIgnoreCase("BC")) {
					currentMatches[0] = "-" + currentMatches[0];
				}

				nextToken.markThisForDel();

				finalTermText.append(currentMatches[0]).append("01")
						.append("01");
				currentToken.setTermText(finalTermText.toString());
				currentToken.markAsDate();
			} else if (currentMatches[0] != null && currentMatches[6] == null
					&& currentMatches[8] == null && nextMatches[0] == null
					&& nextMatches2[0] == null) {
				// only number
				finalTermText = new StringBuilder((String) currentMatches[0]);
				finalTermText.append("0101");
				verifyDeilmiter(currentMatches, finalTermText);
				currentToken.setTermText(finalTermText.toString());
				currentToken.markAsDate();
			} else if (currentMatches[0] != null && nextMatches[1] != null
					&& nextMatches2[0] != null) {
				// 1 Jan 1978
				if (((String) currentMatches[0]).length() == 1) {
					currentMatches[0] = "0" + currentMatches[0];
				}
				finalTermText = new StringBuilder(nextMatches2[0]
						+ months.get(((String)nextMatches[1]).toLowerCase()) + currentMatches[0]);

				nextToken.markThisForDel();
				nextToken2.markThisForDel();

				verifyDeilmiter(nextMatches2, finalTermText);
				currentToken.setTermText(finalTermText.toString());
				currentToken.markAsDate();
			} else if (currentMatches[1] != null && nextMatches[0] != null
					&& nextMatches2[0] != null) {
				// Dec 7, 1989
				if (((String) nextMatches[0]).length() == 1) {
					nextMatches[0] = "0" + nextMatches[0];
				}
				nextToken.markThisForDel();
				nextToken2.markThisForDel();

				finalTermText = new StringBuilder(nextMatches2[0]
						+ months.get(((String)currentMatches[1]).toLowerCase()) + nextMatches[0]);

				verifyDeilmiter(nextMatches2, finalTermText);

				currentToken.setTermText(finalTermText.toString());
				currentToken.markAsDate();
			} else if (currentMatches[1] != null && nextMatches[0] != null
					&& nextMatches2[0] == null) {
				// April 1
				if (((String) nextMatches[0]).length() == 1) {
					nextMatches[0] = "0" + nextMatches[0];
				}
				nextToken.markThisForDel();
				finalTermText = new StringBuilder("1900");
				finalTermText.append(months.get(((String)currentMatches[1]).toLowerCase()));
				finalTermText.append(nextMatches[0]);

				currentToken.setTermText(finalTermText.toString());
				currentToken.markAsDate();
			} else if (currentMatches[1] != null && nextMatches[0] == null
					&& nextMatches2[0] == null) {
				// April
				finalTermText = new StringBuilder("1900");
				finalTermText.append(months.get(((String) currentMatches[1])
						.toLowerCase()));
				finalTermText.append("01");
				verifyDeilmiter(currentMatches, finalTermText);
				currentToken.setTermText(finalTermText.toString());
				currentToken.markAsDate();
			} else if (currentMatches[0] != null && currentMatches[8] != null) {
				// 874AD
				finalTermText = new StringBuilder();
				currentMatches[0] = yearPadding((String) currentMatches[0]);
				if (((String) currentMatches[8]).equalsIgnoreCase("BC")) {
					currentMatches[0] = "-" + currentMatches[0];
				}
				finalTermText.append(currentMatches[0] + "0101");
				verifyDeilmiter(currentMatches, finalTermText);
				currentToken.setTermText(finalTermText.toString());
				currentToken.markAsDate();

			} else if (currentMatches[0] != null && currentMatches[6] != null
					&& currentMatches[7] != null) {
				// 2011-12 = 20110101-20120101
				String year1 = (String) currentMatches[0];
				String year2 = (String) currentMatches[7];
				String datingSystem = (String) currentMatches[2];
				finalTermText = new StringBuilder();

				if (year1.length() < year2.length()) {
					year1 = yearPadding(year1);
					year2 = yearPadding(year2);
				} else {
					year2 = year1.substring(0, year2.length()) + year2;
					year1 = yearPadding(year1);
					year2 = yearPadding(year2);
				}

				if (datingSystem != null && datingSystem.equalsIgnoreCase("BC")) {
					year1 = "-" + year1;
				}
				finalTermText.append(year1 + "0101").append(currentMatches[6])
						.append(year2 + "0101");
				verifyDeilmiter(currentMatches, finalTermText);
				currentToken.setTermText(finalTermText.toString());
				currentToken.markAsDate();
			}
		}

		return stream.hasNext();
	}

	private String yearPadding(String year) {
		switch (year.length()) {
		case 1:
			year = "000" + year;
			break;
		case 2:
			year = "00" + year;
			break;
		case 3:
			year = "0" + year;
			break;

		default:
			break;
		}
		return year;
	}

	private void verifyDeilmiter(Object[] matcherArray,
			StringBuilder finalTermText) {
		if (matcherArray[3] != null) {
			finalTermText.append(matcherArray[3]);
		}
	}

	private Object[] findMatches(Token token) {
		String tokenTermText;
		Matcher numberMatcher;
		Object[] matches = new Object[9];
		String number = null;
		String month = null;
		String rangeDelimiter = null;
		String number2 = null;
		String delimiter = null;
		String datingSystem = null;
		String datingSystemInNumber = null;
		String amPM = null;

		if (token != null) {
			tokenTermText = token.getTermText();
			if (tokenTermText != null) {

				numberMatcher = numberPattern.matcher(tokenTermText);
				if (numberMatcher.matches()) {
					number = numberMatcher.group(1);
					rangeDelimiter = numberMatcher.group(3);
					number2 = numberMatcher.group(4);
					datingSystemInNumber = numberMatcher.group(5);
					delimiter = numberMatcher.group(6);
				}
				Matcher monthMatcher = monthPattern.matcher(tokenTermText);
				if (monthMatcher.matches()) {
					month = monthMatcher.group(1);
					try {
						delimiter = monthMatcher.group(2);
					} catch (Exception e) {
						// e.printStackTrace();
					}
				}
				Matcher datingSystemMatcher = datingSystemPattern
						.matcher(tokenTermText);
				if (datingSystemMatcher.matches()) {
					datingSystem = datingSystemMatcher.group(1);
					try {
						delimiter = datingSystemMatcher.group(2);
					} catch (Exception e) {
						// e.printStackTrace();
					}
				}
				Matcher timeMatcher = timePattern.matcher(tokenTermText);
				if (timeMatcher.matches()) {
					Time time = new Time();
					time.hours = Integer.parseInt(timeMatcher.group(1));
					time.minutes = Integer.parseInt(timeMatcher.group(2));
					if (timeMatcher.group(3) != null) {
						time.seconds = Integer.parseInt(timeMatcher.group(3));
					}
					time.amPM = timeMatcher.group(4);
					time.delimiter = timeMatcher.group(5);

					matches[4] = time;
				}
				Matcher amPMMatcher = amPMPattern.matcher(tokenTermText);
				if (amPMMatcher.matches()) {
					amPM = amPMMatcher.group(1);
					try {
						delimiter = amPMMatcher.group(2);
					} catch (Exception e) {
						// e.printStackTrace();
					}
				}
			}
		}

		matches[0] = number != null && number.length() > 0 ? number : null;
		matches[1] = month;
		matches[2] = datingSystem;
		matches[3] = delimiter;
		matches[5] = amPM;
		matches[6] = rangeDelimiter != null && rangeDelimiter.length() > 0 ? rangeDelimiter
				: null;
		matches[7] = number2 != null && number2.length() > 0 ? number2 : null;
		matches[8] = datingSystemInNumber != null
				&& datingSystemInNumber.length() > 0 ? datingSystemInNumber
				: null;

		return matches;
	}
}

class Time {
	Integer hours;
	Integer minutes;
	Integer seconds;
	String amPM;
	String delimiter;

	@Override
	public String toString() {
		StringBuilder finalTermText = new StringBuilder();
		if (seconds == null) {
			finalTermText.append(String.format("%02d", hours)).append(":")
					.append(String.format("%02d", minutes)).append(":")
					.append("00");
		} else {
			finalTermText.append(String.format("%02d", hours)).append(":")
					.append(String.format("%02d", minutes)).append(":")
					.append(String.format("%02d", seconds));
		}

		if (delimiter != null) {
			finalTermText.append(delimiter);
		}
		return finalTermText.toString();
	}
}
