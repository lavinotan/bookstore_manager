/**
 * Project: A01030427Assign02_Books2
 * File: Reader.java
 * Date: Mar. 5, 2021
 * Time: 5:23:20 p.m.
 */

/**
 * @author Lavino Wei-Chung Chen, A01030427
 *
 */

package bookstore.book.io;

import java.util.Arrays;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import bookstore.book.ApplicationException;

public class Reader {

	public static final String RECORD_DELIMITER = ":";
	public static final String FIELD_DELIMITER = "\\|";
	private static final Logger LOG = LogManager.getLogger(Reader.class);

	/**
	 * Given a FIELD_DELIMITER delimited input string, parts the string into a String array.
	 * 
	 * @param row
	 *            input string
	 * @return the parsed String array
	 * @throws ApplicationException
	 *             if the element count doesn't match the expected count.
	 */
	public static String[] getElements(String row, int attributeCount) throws ApplicationException {
		String[] elements = row.split(FIELD_DELIMITER);
		LOG.trace(elements);
		if (elements.length != attributeCount) {
			throw new ApplicationException(String.format("Expected %d but got %d: %s", attributeCount, elements.length, Arrays.toString(elements)));
		}

		return elements;
	}
}
