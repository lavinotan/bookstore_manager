/**
 * Project: A01030427Assign02_Books2
 * File: AllData.java
 * Date: Mar. 5, 2021
 * Time: 5:23:20 p.m.
 */
package bookstore.book.data;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import bookstore.book.ApplicationException;
import bookstore.book.db.Database;

/**
 * @author Lavino Wei-Chung Chen, A01030427
 *
 */
public class AllData {

	private static final Logger LOG = LogManager.getLogger(AllData.class);

	private static final String DB_PROPERTIES_FILENAME = "db.properties";

	private static BookDao bookDao;
	private static CustomerDao customerDao;
	private static PurchaseDao purchaseDao;

	private AllData() {
	}

	/**
	 * @throws ApplicationException
	 * 
	 */
	public static void loadData() throws ApplicationException {
		LOG.debug("loading the data");

		try {
			Database db = connect();
			customerDao = loadCustomers(db);
			bookDao = loadBooks(db);
			purchaseDao = loadPurchases(db);

		} catch (Exception e) {
			LOG.error(e.getMessage());
		}

		LOG.debug("successfully loaded the data");
	}

	/**
	 * @return the customers
	 */
	public static CustomerDao getCustomerDao() {
		return customerDao;
	}

	/**
	 * @return the books
	 */
	public static BookDao getBookDao() {
		return bookDao;
	}

	/**
	 * @return the purchases
	 */
	public static PurchaseDao getPurchaseDao() {
		return purchaseDao;
	}

	/**
	 * Load the customers.
	 * 
	 * @TODO this method has much to much knowledge of the DB and should be refactored.
	 * 
	 * @throws IOException
	 * @throws SQLException
	 * @throws ApplicationException
	 */
	private static CustomerDao loadCustomers(Database db) throws ApplicationException {
		CustomerDao customerDao = new CustomerDao(db);
		return customerDao;
	}

	private static BookDao loadBooks(Database db) throws ApplicationException {
		BookDao bookDao = new BookDao(db);
		return bookDao;
	}

	private static PurchaseDao loadPurchases(Database db) throws ApplicationException {
		PurchaseDao purchaseDao = new PurchaseDao(db);
		return purchaseDao;
	}

	private static Database connect() throws IOException, SQLException, ApplicationException {
		Properties dbProperties = new Properties();
		dbProperties.load(new FileInputStream(DB_PROPERTIES_FILENAME));
		Database db = new Database(dbProperties);

		return db;
	}

}
