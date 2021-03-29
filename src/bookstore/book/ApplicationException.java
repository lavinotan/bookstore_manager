/**
 * Project: A01030427Assign02_Books2
 * File: ApplicationException.java
 * Date: Mar. 5, 2021
 * Time: 5:23:20 p.m.
 */

package bookstore.book;

/**
 * @author Lavino Wei-Chung Chen, A01030427
 *
 */

@SuppressWarnings("serial")
public class ApplicationException extends Exception {

	/**
	 * Construct an ApplicationException
	 * 
	 * @param message
	 *            the exception message.
	 */
	public ApplicationException(String message) {
		super(message);
	}

	/**
	 * Construct an ApplicationException
	 * 
	 * @param throwable
	 *            a Throwable.
	 */
	public ApplicationException(Throwable throwable) {
		super(throwable);
	}

}