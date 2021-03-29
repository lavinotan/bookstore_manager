/**
 * Project: A01030427Assign02_Books2
 * File: PurchaseReader.java
 * Date: Mar. 5, 2021
 * Time: 5:23:20 p.m.
 */

/**
 * @author Lavino Wei-Chung Chen, A01030427
 *
 */

package bookstore.book.io;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import bookstore.book.ApplicationException;
import bookstore.book.data.AllData;
import bookstore.book.data.Purchase;
import bookstore.book.data.PurchaseDao;

public class PurchaseReader extends Reader {

	public static final String FILENAME = "purchases.csv";

	private static final Logger LOG = LogManager.getLogger(PurchaseReader.class);
	private static Set<Long> customerIds;
	private static Long[] customerIdArray;
	private static int customerIdCount;
	private static Set<Long> bookIds;
	private static Long[] bookIdArray;
	private static int bookIdCount;

	/**
	 * private constructor to prevent instantiation
	 * 
	 * @throws ApplicationException
	 */
	private PurchaseReader() throws ApplicationException {

	}

	/**
	 * Read the inventory input data.
	 * 
	 * @return the inventory.
	 * @throws ApplicationException
	 */
	public static void read(File purchaseDataFile, PurchaseDao dao) throws ApplicationException {

		try {
			customerIds = AllData.getCustomerDao().getCustomerIds().stream().collect(Collectors.toSet());
			bookIds = AllData.getBookDao().getBookIds().stream().collect(Collectors.toSet());

			customerIdArray = customerIds.toArray(new Long[0]);
			customerIdCount = customerIdArray.length;

			bookIdArray = bookIds.toArray(new Long[0]);
			bookIdCount = bookIdArray.length;

		} catch (SQLException e) {
			throw new ApplicationException(e);
		}

		FileReader in;
		Iterable<CSVRecord> records;
		try {
			in = new FileReader(purchaseDataFile);
			records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(in);
		} catch (IOException e) {
			throw new ApplicationException(e);
		}

		LOG.debug("Reading" + purchaseDataFile.getAbsolutePath());
		for (CSVRecord record : records) {
			long id = Long.parseLong(record.get("id"));
			long customerId = Long.parseLong(record.get("customer_id"));
			long bookId = Long.parseLong(record.get("book_id"));
			float price = Float.parseFloat(record.get("price"));

			Purchase purchase = new Purchase.Builder(id, customerId, bookId, price).build();
			try {
				dao.add(purchase);
			} catch (SQLException e) {
				throw new ApplicationException(e);
			}
			LOG.debug("Added " + purchase.toString() + " as " + purchase.getId());

			if (!customerIds.contains(customerId)) {
				customerId = customerIdArray[(int) (Math.random() * customerIdCount)];
			}
			if (!bookIds.contains(bookId)) {
				bookId = bookIdArray[(int) (Math.random() * bookIdCount)];
			}
		}
	}

}
