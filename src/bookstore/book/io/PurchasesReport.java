/**
 * Project: A01030427Assign02_Books2
 * File: PurchaseReport.java
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import bookstore.book.BookOptions;
import bookstore.book.data.AllData;
import bookstore.book.data.Book;
import bookstore.book.data.Customer;
import bookstore.book.data.Purchase;
import bookstore.book.data.util.Common;

public class PurchasesReport {

	public static final String REPORT_FILENAME = "purchases_report.txt";
	public static final String HORIZONTAL_LINE = "-------------------------------------------------------------------------------------------------------------------";
	public static final String HEADER_FORMAT = "%-24s %-80s %-8s";
	public static final String ROW_FORMAT = "%-24s %-80s $%.2f";

	private static final Logger LOG = LogManager.getLogger(PurchasesReport.class);

	private static List<Item> items;

	/**
	 * Print the report.
	 *
	 * @param out
	 */
	public static void print(PrintStream out) {
		LOG.debug("Printing the Purchases Report");
		String text = null;
		boolean hasTotal = BookOptions.isTotalOptionSet();
		LOG.debug("isTotalOptionSet = " + hasTotal);
		boolean descending = BookOptions.isDescendingOptionSet();
		LOG.debug("isDescendingOptionSet = " + descending);

		out.println("\nPurchases Report");

		out.println(HORIZONTAL_LINE);
		text = String.format(HEADER_FORMAT, "Name", "Title", "Price");
		out.println(text);
		out.println(HORIZONTAL_LINE);

		List<Purchase> purchases = null;
		Map<Long, Book> books = null;
		Map<Long, Customer> customers = null;

		try {
			purchases = AllData.getPurchaseDao().getPurchases();
			books = AllData.getBookDao().getBooks().stream().collect(Collectors.toMap(Book::getId, book -> book));
			customers = AllData.getCustomerDao().getCustomers().stream().collect(Collectors.toMap(Customer::getId, customer -> customer));
		} catch (SQLException e) {
			LOG.error(e.getMessage());
		}

		String customerIdString = BookOptions.getCustomerId();
		long optionsCustomerId = customerIdString == null ? -1L : Long.parseLong(customerIdString);
		items = new ArrayList<>();
		for (Purchase purchase : purchases) {
			long customerId = purchase.getCustomerId();
			if (customerIdString != null && customerId != optionsCustomerId) {
				continue;
			}

			long bookId = purchase.getBookId();
			Book book = books.get(bookId);
			Customer customer = customers.get(customerId);
			float price = purchase.getPrice();
			Item item = new Item(customer.getFirstName(), customer.getLastName(), book.getTitle(), price);
			items.add(item);
		}

		if (BookOptions.isByLastnameOptionSet()) {
			LOG.debug("isByLastnameOptionSet = true");
			if (descending) {
				Collections.sort(items, new CompareByLastNameDescending());
			} else {
				Collections.sort(items, new CompareByLastName());
			}
		}

		if (BookOptions.isByTitleOptionSet()) {
			LOG.debug("isByTitleOptionSet = true");
			if (descending) {
				Collections.sort(items, new CompareByTitleDescending());
			} else {
				Collections.sort(items, new CompareByTitle());
			}
		}

		float total = 0;
		for (Item item : items) {
			if (hasTotal) {
				total += item.price;
			}

			text = String.format(ROW_FORMAT, item.firstName + " " + item.lastName, item.title, item.price);
			out.println(text);
			LOG.trace(text);
		}

		if (hasTotal) {
			text = String.format("%nValue of purchases: $%,.2f%n", total);
			out.println(text);
			LOG.trace(text);
		}

	}

	public static class CompareByLastName implements Comparator<Item> {
		@Override
		public int compare(Item item1, Item item2) {
			return item1.lastName.compareTo(item2.lastName);
		}
	}

	public static class CompareByLastNameDescending implements Comparator<Item> {
		@Override
		public int compare(Item item1, Item item2) {
			return item2.lastName.compareTo(item1.lastName);
		}
	}

	public static class CompareByTitle implements Comparator<Item> {
		@Override
		public int compare(Item item1, Item item2) {
			return item1.title.compareToIgnoreCase(item2.title);
		}
	}

	public static class CompareByTitleDescending implements Comparator<Item> {
		@Override
		public int compare(Item item1, Item item2) {
			return item2.title.compareToIgnoreCase(item1.title);
		}
	}

	private static class Item {
		private String firstName;
		private String lastName;
		private String title;
		private float price;

		/**
		 * @param firstName
		 * @param lastName
		 * @param title
		 * @param price
		 */
		public Item(String firstName, String lastName, String title, float price) {
			this.firstName = firstName;
			this.lastName = lastName;
			this.title = Common.truncateIfRequired(title, 80);
			this.price = price;
		}

		/*
		 * (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return "Item [firstName=" + firstName + ", lastName=" + lastName + ", title=" + title + ", price=" + price + "]";
		}

	}
}
