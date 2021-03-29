/**
 * Project: A01030427Assign02_Books2
 * File: CustomerDao.java
 * Date: Mar. 5, 2021
 * Time: 5:23:20 p.m.
 */

package bookstore.book.data;

import java.io.File;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import bookstore.book.ApplicationException;
import bookstore.book.db.Dao;
import bookstore.book.db.Database;
import bookstore.book.db.DbConstants;
import bookstore.book.io.CustomerReader;

/**
 * @author Lavino Wei-Chung Chen, A01030427
 */
public class CustomerDao extends Dao {

	public static final String TABLE_NAME = DbConstants.TABLE_ROOT + "Customers";

	private static final String CUSTOMERS_DATA_FILENAME = "customers.dat";
	private static Logger LOG = LogManager.getLogger(CustomerDao.class);

	public CustomerDao(Database database) throws ApplicationException {
		super(database, TABLE_NAME);

		load();
	}

	/**
	 * @param customerDataFile
	 * @throws ApplicationException
	 * @throws SQLException
	 */
	public void load() throws ApplicationException {
		File customerDataFile = new File(CUSTOMERS_DATA_FILENAME);
		try {
			if (!Database.tableExists(CustomerDao.TABLE_NAME)) {
				// if (Database.tableExists(CustomerDao.TABLE_NAME) && Database.dbTableDropRequested()) {
				// drop();
				// }

				create();

				LOG.debug("Inserting the customers");

				if (!customerDataFile.exists()) {
					throw new ApplicationException(String.format("Required '%s' is missing.", CUSTOMERS_DATA_FILENAME));
				}

				CustomerReader.read(customerDataFile, this);
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
				+ "%s VARCHAR(%d), " // FIRST_NAME
				+ "%s VARCHAR(%d), " // LAST_NAME
				+ "%s VARCHAR(%d), " // STREET
				+ "%s VARCHAR(%d), " // CITY
				+ "%s VARCHAR(%d), " // POSTAL_CODE
				+ "%s VARCHAR(%d), " // PHONE
				+ "%s VARCHAR(%d), " // EMAIL_ADDRESS
				+ "%s DATETIME, " // JOINED_DATE
				+ "PRIMARY KEY (%s))", // ID
				TABLE_NAME, //
				Column.ID.name, //
				Column.FIRST_NAME.name, Column.FIRST_NAME.length, //
				Column.LAST_NAME.name, Column.LAST_NAME.length, //
				Column.STREET.name, Column.STREET.length, //
				Column.CITY.name, Column.CITY.length, //
				Column.POSTAL_CODE.name, Column.POSTAL_CODE.length, //
				Column.PHONE.name, Column.PHONE.length, //
				Column.EMAIL_ADDRESS.name, Column.EMAIL_ADDRESS.length, //
				Column.JOINED_DATE.name, //
				Column.ID.name);

		super.create(sqlString);
	}

	public void add(Customer customer) throws SQLException {
		String sqlString = String.format("INSERT INTO %s values(?, ?, ?, ?, ?, ?, ?, ?, ?)", TABLE_NAME);
		boolean result = execute(sqlString, //
				customer.getId(), //
				customer.getFirstName(), //
				customer.getLastName(), //
				customer.getStreet(), //
				customer.getCity(), //
				customer.getPostalCode(), //
				customer.getPhone(), //
				customer.getEmailAddress(), //
				toTimestamp(customer.getJoinedDate()));
		LOG.debug(String.format("Adding %s was %s", customer, result ? "successful" : "unsuccessful"));
	}

	/**
	 * Update the customer.
	 * 
	 * @param customer
	 * @throws SQLException
	 */
	public void update(Customer customer) throws SQLException {
		String sqlString = String.format("UPDATE %s SET %s=?, %s=?, %s=?, %s=?, %s=?, %s=?, %s=?, %s=? WHERE %s=?", TABLE_NAME, //
				Column.FIRST_NAME.name, //
				Column.LAST_NAME.name, //
				Column.STREET.name, //
				Column.CITY.name, //
				Column.POSTAL_CODE.name, //
				Column.PHONE.name, //
				Column.EMAIL_ADDRESS.name, //
				Column.JOINED_DATE.name, //
				Column.ID.name);
		LOG.debug("Update statment: " + sqlString);

		boolean result = execute(sqlString, customer.getFirstName(), customer.getLastName(), customer.getStreet(), customer.getCity(),
				customer.getPostalCode(), customer.getPhone(), customer.getEmailAddress(), toTimestamp(customer.getJoinedDate()), customer.getId());
		LOG.debug(String.format("Updating %s was %s", customer, result ? "successful" : "unsuccessful"));
	}

	/**
	 * Delete the customer from the database.
	 * 
	 * @param customer
	 * @throws SQLException
	 */
	public void delete(Customer customer) throws SQLException {
		Connection connection;
		Statement statement = null;
		try {
			connection = Database.getConnection();
			statement = connection.createStatement();

			String sqlString = String.format("DELETE FROM %s WHERE %s='%s'", TABLE_NAME, Column.ID.name, customer.getId());
			LOG.debug(sqlString);
			int rowcount = statement.executeUpdate(sqlString);
			LOG.debug(String.format("Deleted %d rows", rowcount));
		} finally {
			close(statement);
		}
	}

	/**
	 * Retrieve all the customer IDs from the database
	 * 
	 * @return the list of customer IDs
	 * @throws SQLException
	 */
	public List<Long> getCustomerIds() throws SQLException {
		List<Long> ids = new ArrayList<>();

		String selectString = String.format("SELECT %s FROM %s", Column.ID.name, TABLE_NAME);
		LOG.debug(selectString);

		Statement statement = null;
		ResultSet resultSet = null;
		try {
			Connection connection = Database.getConnection();
			statement = connection.createStatement();
			resultSet = statement.executeQuery(selectString);

			while (resultSet.next()) {
				ids.add(resultSet.getLong(Column.ID.name));
			}

		} finally {
			close(statement);
		}

		LOG.debug(String.format("Loaded %d customer IDs from the database", ids.size()));

		return ids;
	}

	public List<Customer> getCustomers() throws SQLException {
		List<Customer> customers = new ArrayList<>();

		String selectString = String.format("SELECT * FROM %s", TABLE_NAME);
		LOG.debug(selectString);

		Statement statement = null;
		ResultSet resultSet = null;
		try {
			Connection connection = Database.getConnection();
			statement = connection.createStatement();
			resultSet = statement.executeQuery(selectString);

			while (resultSet.next()) {
				long id = resultSet.getLong(Column.ID.name);
				String phone = resultSet.getString(Column.PHONE.name);
				String firstName = resultSet.getString(Column.FIRST_NAME.name);
				String lastName = resultSet.getString(Column.LAST_NAME.name);
				String street = resultSet.getString(Column.STREET.name);
				String city = resultSet.getString(Column.CITY.name);
				String postalCode = resultSet.getString(Column.POSTAL_CODE.name);
				String emailAddress = resultSet.getString(Column.EMAIL_ADDRESS.name);
				Date date = resultSet.getDate(Column.JOINED_DATE.name);
				Calendar cal = Calendar.getInstance();
				cal.setTime(date);
				int year = cal.get(Calendar.YEAR);
				int month = cal.get(Calendar.MONTH) + 1;
				int day = cal.get(Calendar.DAY_OF_MONTH);

				Customer customer = new Customer.Builder(id, phone).setFirstName(firstName).setLastName(lastName).setStreet(street).setCity(city)
						.setPostalCode(postalCode).setEmailAddress(emailAddress).setJoinedDate(year, month, day).build();
				customers.add(customer);
			}

		} finally {
			close(statement);
		}

		LOG.debug(String.format("Loaded %d customers from the database", customers.size()));

		return customers;
	}

	/**
	 * @param customerId
	 * @return
	 * @throws Exception
	 */
	public Customer getCustomer(Long customerId) throws Exception {
		String sqlString = String.format("SELECT * FROM %s WHERE %s = %d", TABLE_NAME, Column.ID.name, customerId);
		LOG.debug(sqlString);

		Statement statement = null;
		ResultSet resultSet = null;
		try {
			Connection connection = Database.getConnection();
			statement = connection.createStatement();
			resultSet = statement.executeQuery(sqlString);

			int count = 0;
			while (resultSet.next()) {
				count++;
				if (count > 1) {
					throw new ApplicationException(String.format("Expected one result, got %d", count));
				}

				Timestamp timestamp = resultSet.getTimestamp(Column.JOINED_DATE.name);
				LocalDate date = timestamp.toLocalDateTime().toLocalDate();

				Customer customer = new Customer.Builder(resultSet.getInt(Column.ID.name), resultSet.getString(Column.PHONE.name)) //
						.setFirstName(resultSet.getString(Column.FIRST_NAME.name)) //
						.setLastName(resultSet.getString(Column.LAST_NAME.name)) //
						.setStreet(resultSet.getString(Column.STREET.name)) //
						.setCity(resultSet.getString(Column.CITY.name)) //
						.setPostalCode(resultSet.getString(Column.POSTAL_CODE.name)) //
						.setEmailAddress(resultSet.getString(Column.EMAIL_ADDRESS.name)) //
						.setJoinedDate(date).build();

				return customer;
			}
		} finally {
			close(statement);
		}

		return null;
	}

	public int countAllCustomers() throws Exception {
		Statement statement = null;
		int count = 0;
		try {
			Connection connection = Database.getConnection();
			statement = connection.createStatement();
			// Execute a statement
			String sqlString = String.format("SELECT COUNT(*) AS total FROM %s", tableName);
			ResultSet resultSet = statement.executeQuery(sqlString);
			if (resultSet.next()) {
				count = resultSet.getInt("total");
			}
		} finally {
			close(statement);
		}
		return count;
	}

	public enum Column {
		ID("id", 15), //
		FIRST_NAME("firstName", 25), //
		LAST_NAME("lastName", 25), //
		STREET("street", 50), //
		CITY("city", 25), //
		POSTAL_CODE("postalCode", 10), //
		PHONE("phone", 20), //
		EMAIL_ADDRESS("emailAddress", 60), //
		JOINED_DATE("joinedDate", 8); //

		String name;
		int length;

		private Column(String name, int length) {
			this.name = name;
			this.length = length;
		}

	}

}
