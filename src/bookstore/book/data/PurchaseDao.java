/**
 * Project: A01030427Assign02_Books2
 * File: BookDao.java
 * Date: Mar. 5, 2021
 * Time: 5:23:20 p.m.
 */

package bookstore.book.data;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import bookstore.book.ApplicationException;
import bookstore.book.db.Dao;
import bookstore.book.db.Database;
import bookstore.book.db.DbConstants;
import bookstore.book.io.PurchaseReader;

/**
 * @author Lavino Wei-Chung Chen, A01030427
 */
public class PurchaseDao extends Dao {

	public static final String TABLE_NAME = DbConstants.TABLE_ROOT + "Purchases";

	private static final String PURCHASES_DATA_FILENAME = "purchases.csv";
	private static Logger LOG = LogManager.getLogger(PurchaseDao.class);

	public PurchaseDao(Database database) throws ApplicationException {
		super(database, TABLE_NAME);

		load();
	}

	/**
	 * @param customerDataFile
	 * @throws ApplicationException
	 * @throws SQLException
	 */
	public void load() throws ApplicationException {
		File purchaseDataFile = new File(PURCHASES_DATA_FILENAME);
		try {
			if (!Database.tableExists(PurchaseDao.TABLE_NAME)) {

				create();

				LOG.debug("Inserting the purchases");

				if (!purchaseDataFile.exists()) {
					throw new ApplicationException(String.format("Required '%s' is missing.", PURCHASES_DATA_FILENAME));
				}

				PurchaseReader.read(purchaseDataFile, this);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new ApplicationException(e);
		}
	}

	@Override
	public void create() throws SQLException {
		LOG.debug("Creating database table " + TABLE_NAME);

		// With MS SQL Server, JOINED_DATE needs to be a DATETIME type.
		String sqlString = String.format("CREATE TABLE %s(" //
				+ "%s BIGINT, " // ID
				+ "%s BIGINT, " // CUSTOMERID
				+ "%s BIGINT, " // BOOKID
				+ "%s FLOAT, " // PRICE
				+ "PRIMARY KEY (%s))", // ID
				TABLE_NAME, //
				Column.ID.name, //
				Column.CUSTOMERID.name, //
				Column.BOOKID.name, //
				Column.PRICE.name, //
				Column.ID.name);

		super.create(sqlString);
	}

	public void add(Purchase purchase) throws SQLException {
		String sqlString = String.format("INSERT INTO %s values(?, ?, ?, ?)", TABLE_NAME);
		boolean result = execute(sqlString, //
				purchase.getId(), //
				purchase.getCustomerId(), //
				purchase.getBookId(), //
				purchase.getPrice()); //
		LOG.debug(String.format("Adding %s was %s", purchase, result ? "successful" : "unsuccessful"));
	}

	/**
	 * Update the customer.
	 * 
	 * @param customer
	 * @throws SQLException
	 */
	public void update(Purchase purchase) throws SQLException {
		String sqlString = String.format("UPDATE %s SET %s=?, %s=?, %s=? WHERE %s=?", TABLE_NAME, //
				Column.CUSTOMERID.name, //
				Column.BOOKID.name, //
				Column.PRICE.name, //
				Column.ID.name);
		LOG.debug("Update statment: " + sqlString);

		boolean result = execute(sqlString, purchase.getCustomerId(), purchase.getBookId(), purchase.getPrice());
		LOG.debug(String.format("Updating %s was %s", purchase, result ? "successful" : "unsuccessful"));
	}

	/**
	 * Delete the customer from the database.
	 * 
	 * @param customer
	 * @throws SQLException
	 */
	public void delete(Purchase purchase) throws SQLException {
		Connection connection;
		Statement statement = null;
		try {
			connection = Database.getConnection();
			statement = connection.createStatement();

			String sqlString = String.format("DELETE FROM %s WHERE %s='%s'", TABLE_NAME, Column.ID.name, purchase.getId());
			LOG.debug(sqlString);
			int rowcount = statement.executeUpdate(sqlString);
			LOG.debug(String.format("Deleted %d rows", rowcount));
		} finally {
			close(statement);
		}
	}

	public List<Purchase> getPurchases() throws SQLException {
		String selectString = String.format("SELECT * FROM %s", TABLE_NAME);
		List<Purchase> purchases = new ArrayList<>();

		LOG.debug(selectString);

		Statement statement = null;
		ResultSet resultSet = null;

		Connection connection;
		try {
			connection = Database.getConnection();
			statement = connection.createStatement();
			resultSet = statement.executeQuery(selectString);

			while (resultSet.next()) {
				long id = resultSet.getLong(Column.ID.name);
				long customerId = resultSet.getLong(Column.CUSTOMERID.name);
				long bookId = resultSet.getLong(Column.BOOKID.name);
				float price = resultSet.getFloat(Column.PRICE.name);

				Purchase purchase = new Purchase.Builder(id, customerId, bookId, price).build();
				purchases.add(purchase);
			}
		} finally {
			close(statement);
		}

		LOG.debug(String.format("Loaded %d purchases from the database", purchases.size()));

		return purchases;

	}

	public double totalPurchases() throws Exception {
		Statement statement = null;
		double totalPrice = 0;

		String sqlString = String.format("SELECT %s FROM %s", Column.PRICE.name, TABLE_NAME);
		LOG.debug(sqlString);

		try {
			Connection connection = Database.getConnection();
			statement = connection.createStatement();
			// Execute a statement

			ResultSet resultSet = statement.executeQuery(sqlString);
			while (resultSet.next()) {
				totalPrice += resultSet.getDouble(Column.PRICE.name);
			}
		} finally {
			close(statement);
		}
		LOG.debug(String.format("Total of purchases: %.2f calculated", totalPrice));
		return totalPrice;
	}

	public enum Column {
		ID("id", 15), //
		CUSTOMERID("customerId", 15), //
		BOOKID("bookId", 15), //
		PRICE("price", 30); //

		String name;
		int length;
		int size;
		int decimal;

		private Column(String name, int length) {
			this.name = name;
			this.length = length;
		}

	}

}
