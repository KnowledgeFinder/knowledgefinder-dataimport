/*******************************************************************************
 * Copyright 2016 DLR - German Aerospace Center
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package de.dlr.knowledgefinderII.dataimport.utils.transformer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.solr.handler.dataimport.Context;
import org.apache.solr.handler.dataimport.DataImporter;
import org.apache.solr.handler.dataimport.Transformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * The Class DateIncompleteFormatTransformer. Transformer for parsing an
 * incomplete date in a {@link Date} object. The variable parseToDate defines if
 * the incomplete data should be completed with the last or first value. For
 * example:
 * </p>
 * <p>
 * parteToDate = "start"
 * <ul>
 * <li>2004 -> 2004-01-01 at 00:00:00
 * <li>2004-09 -> 2004-09-01 at 00:00:00
 * <li>2004-09-08 -> 2004-09-08 at 00:00:00
 * </ul>
 * parteToDate = "end"
 * <ul>
 * <li>2004 -> 2004-12-31 at 23:59:59
 * <li>2004-09 -> 2004-09-31 at 23:59:59
 * <li>2004-09-08 -> 2004-09-08 at at 23:59:59
 * </ul>
 * <p>
 * The values of year, month and day are read using the matched groups from the
 * variable regex. Year group is required.
 * 
 * @see {@link Pattern}
 * @see {@link Matcher#group()}
 * 
 */
public class DateIncompleteFormatTransformer extends Transformer {

	private static final Logger LOG = LoggerFactory.getLogger(DateIncompleteFormatTransformer.class);

	private static Map<String, Pattern> PATTERN_CACHE = new TreeMap<String, Pattern>();
	public static final String DATE_TIME_PARSE = "parseToDate";
	public static final String SRC_COL_NAME = "srcColName";
	public static final String DATE_START = "start";
	public static final String DATE_END = "end";
	public static final String DATE_INCOMPLETE_REGEX = "regex";

	/*
	 * @see
	 * org.apache.solr.handler.dataimport.Transformer#transformRow(java.util.
	 * Map, org.apache.solr.handler.dataimport.Context)
	 */
	public Map<String, Object> transformRow(Map<String, Object> row, Context context) {

		for (Map<String, String> map : context.getAllEntityFields()) {

			String column = map.get(DataImporter.COLUMN);
			String startEnd = map.get(DATE_TIME_PARSE);
			String srcCol = map.get(SRC_COL_NAME);
			String dateRegex = map.get(DATE_INCOMPLETE_REGEX);

			if (startEnd == null || dateRegex == null)
				continue;
			if (!startEnd.equals(DATE_END) && !startEnd.equals(DATE_START))
				throw new IllegalArgumentException(DATE_TIME_PARSE + " muss be " + DATE_END + " or " + DATE_START
						+ "(Current value " + startEnd + ")");

			srcCol = srcCol == null ? column : srcCol;

			try {
				Object currentRowValue = row.get(srcCol);
				if (currentRowValue instanceof Object[]) {
					List<Object> values = new ArrayList<Object>();
					values.addAll(Arrays.asList((Object[]) currentRowValue));

					List<Date> results = new ArrayList<Date>();
					for (Object input : values) {
						results.add(process(input, startEnd, dateRegex));
					}
					row.put(column, results);
				} else {
					if (currentRowValue != null) {
						row.put(column, process(currentRowValue, startEnd, dateRegex));
					}
				}
			} catch (Exception e) {
				LOG.warn(e.getMessage());
			}
		}
		return row;
	}

	private Date process(Object value, String startEnd, String dateRegex) throws Exception {
		if (value == null)
			return null;
		String strVal = value.toString().trim();
		if (strVal.length() == 0)
			return null;
		return completeDate(strVal, startEnd, dateRegex);
	}

	private Date completeDate(String value, String startEnd, String dateRegex) throws Exception {

		TimeZone GMT = TimeZone.getTimeZone("GMT");
		Locale locale = Locale.ROOT;

		int year, month, day, hour, minute, second, millisecond;
		year = month = day = hour = minute = second = millisecond = -1;

		Pattern p = getPattern(dateRegex);
		Matcher m = p.matcher(value);
		if (m.matches()) {
			Calendar cal = Calendar.getInstance(GMT, locale);

			String gr_y = null;
			String gr_m = null;
			String gr_d = null;
			try {
				gr_y = m.group("year");
			} catch (IllegalArgumentException e) {
			}
			try {
				gr_m = m.group("month");
			} catch (IllegalArgumentException e) {
			}
			try {
				gr_d = m.group("day");
			} catch (IllegalArgumentException e) {
			}

			year = gr_y != null ? Integer.parseInt(gr_y) : -1;
			month = gr_m != null ? Integer.parseInt(gr_m) - 1 : -1;
			day = gr_d != null ? Integer.parseInt(gr_d) : -1;

			if (year == -1) {
				throw new Exception("Error parsing date " + value + ", year value not found.");
			}

			cal.set(Calendar.YEAR, year);

			if (startEnd.equals(DATE_START)) {
				createMinimalDate(cal, month, day);

			} else if (startEnd.equals(DATE_END)) {
				month = month == -1 ? cal.getActualMaximum(Calendar.MONTH) : month;
				cal.set(Calendar.MONTH, month);
				day = day == -1 ? cal.getActualMaximum(Calendar.DAY_OF_MONTH) : day;
				cal.set(Calendar.DAY_OF_MONTH, day);
				hour = cal.getMaximum(Calendar.HOUR_OF_DAY);
				cal.set(Calendar.HOUR_OF_DAY, hour);
				minute = cal.getMaximum(Calendar.MINUTE);
				cal.set(Calendar.MINUTE, minute);
				second = cal.getMaximum(Calendar.SECOND);
				cal.set(Calendar.SECOND, second);
				millisecond = cal.getMaximum(Calendar.MILLISECOND);
				cal.set(Calendar.MILLISECOND, millisecond);
			}

			return cal.getTime();
		} else {
			throw new Exception("Error parsing date " + value);
		}
	}

	private Pattern getPattern(String reStr) {
		Pattern result = PATTERN_CACHE.get(reStr);
		if (result == null) {
			PATTERN_CACHE.put(reStr, result = Pattern.compile(reStr));
		}
		return result;
	}

	private void createMinimalDate(Calendar cal, int month, int day) {

		month = month == -1 ? cal.getActualMinimum(Calendar.MONTH) : month;
		cal.set(Calendar.MONTH, month);
		day = day == -1 ? cal.getActualMinimum(Calendar.DAY_OF_MONTH) : day;
		cal.set(Calendar.DAY_OF_MONTH, day);
		cal.set(Calendar.HOUR_OF_DAY, cal.getActualMinimum(Calendar.HOUR_OF_DAY));
		cal.set(Calendar.MINUTE, cal.getActualMinimum(Calendar.MINUTE));
		cal.set(Calendar.SECOND, cal.getActualMinimum(Calendar.SECOND));
		cal.set(Calendar.MILLISECOND, cal.getActualMinimum(Calendar.MILLISECOND));
	}
}
