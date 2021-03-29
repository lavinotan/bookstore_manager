/**
 * Project: A01030427Assign02_Books2
 * File: BooksReport.java
 * Date: Mar. 5, 2021
 * Time: 5:23:20 p.m.
 */

/**
 * @author Lavino Wei-Chung Chen, A01030427
 *
 */

package bookstore.book.io;

import java.io.PrintStream;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import bookstore.book.BookOptions;
import bookstore.book.data.AllData;
import bookstore.book.data.Book;
import bookstore.book.data.util.Common;
import bookstore.book.sorters.BookSorter;

public class BooksReport {

	public static final String REPORT_FILENAME = "book_report.txt";
	public static final String HORIZONTAL_LINE = "----------------------------------------------------------------------------------";
	public static final String HEADER_FORMAT = "%-8s %-12s %-40s %-40s %4s %-6s %-13s %-60s";
	public static final String ROW_FORMAT = "%08d %-12s %-40s %-40s %4d %6.3f %13d %-60s";
	private static final Logger LOG = LogManager.getLogger(BooksReport.class);

	/**
	 * Print the report.
	 * 
	 * @param out
	 */
	public static void print(PrintStream out) {
		LOG.debug("Printing the Books Report");
		String text = null;
		out.println("\nBooks Report");
		out.println(HORIZONTAL_LINE);
		text = String.format(HEADER_FORMAT, "ID", "ISBN", "Authors", "Title", "Year", "Rating", "Ratings Count", "Image URL");
		out.println(text);
		out.println(HORIZONTAL_LINE);

		List<Book> books = null;
		try {
			books = AllData.getBookDao().getBooks();
		} catch (SQLException e) {
			LOG.error(e.getMessage());
		}

		if (BookOptions.isByAuthorOptionSet()) {
			LOG.debug("isByAuthorOptionSet = true");

			if (BookOptions.isDescendingOptionSet()) {
				LOG.debug("isDescendingOptionSet = true");
				Collections.sort(books, new BookSorter.CompareByAuthorDescending());
			} else {
				LOG.debug("isDescendingOptionSet = false");
				Collections.sort(books, new BookSorter.CompareByAuthor());
			}
		}

		for (Book book : books) {
			long id = book.getId();
			String isbn = book.getIsbn();
			String authors = Common.truncateIfRequired(book.getAuthors(), 40);
			int year = book.getYear();
			String title = Common.truncateIfRequired(book.getTitle(), 40);
			float rating = book.getRating();
			int ratingsCount = book.getRatingsCount();
			String imageUrl = Common.truncateIfRequired(book.getImageUrl(), 60);
			text = String.format(ROW_FORMAT, id, isbn, authors, title, year, rating, ratingsCount, imageUrl);
			LOG.trace(text);
			out.println(text);
		}
	}

}
