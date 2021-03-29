/**
 * Project: A01030427Assign02_Books
 * File: Common.java
 * Date: Feb. 4, 2021
 * Time: 8:37:45 p.m.
 */

package bookstore.book.data.util;

/**
 * @author Lavino Wei-Chung Chen, A01030427
 *
 */

public class Common {

	/**
	 * If the input string exceeds the length then truncate the string to the length - 3 characters and add "..."
	 * 
	 * @param title
	 * @param length
	 * @return a string
	 */
	public static String truncateIfRequired(String title, int length) {
		if (title.length() > length) {
			title = title.substring(0, length - 3) + "...";
		}

		return title;
	}

}
