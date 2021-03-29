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
import bookstore.book.io.BookReader;

/**
 * @author Lavino Wei-Chung Chen, A01030427
 */
public class BookDao extends Dao {

	public static final String TABLE_NAME = DbConstants.TABLE_ROOT + "Books";

	private static final String BOOKS_DATA_FILENAME = "books500.csv";
	private static Logger LOG = LogManager.getLogger(BookDao.class);

	public BookDao(Database database) throws ApplicationException {
		super(database, TABLE_NAME);

		load();
	}

	/**
	 * @param customerDataFile
	 * @throws ApplicationException
	 * @throws SQLException
	 */
	public void load() throws ApplicationException {
		File bookDataFile = new File(BOOKS_DATA_FILENAME);
		try {
			if (!Database.tableExists(BookDao.TABLE_NAME)) {

				create();

				LOG.debug("Inserting the books");

				if (!bookDataFile.exists()) {
					throw new ApplicationException(String.format("Required '%s' is missing.", BOOKS_DATA_FILENAME));
				}

				BookReader.read(bookDataFile, this);
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
				+ "%s VARCHAR(%d), " // ISBN
				+ "%s VARCHAR(%d), " // AUTHORS
				+ "%s VARCHAR(%d), " // TITLE
				+ "%s INT, " // YEAR
				+ "%s FLOAT, " // RATING
				+ "%s BIGINT, " // RATINGS_COUNT
				+ "%s VARCHAR(%d), " // IMAGE_URL
				+ "PRIMARY KEY (%s))", // ID
				TABLE_NAME, //
				Column.ID.name, //
				Column.ISBN.name, Column.ISBN.length, //
				Column.AUTHORS.name, Column.AUTHORS.length, //
				Column.TITLE.name, Column.TITLE.length, //
				Column.YEAR.name, //
				Column.RATING.name, //
				Column.RATINGS_COUNT.name, //
				Column.IMAGE_URL.name, Column.IMAGE_URL.length, //
				Column.ID.name);

		super.create(sqlString);
	}

	public void add(Book book) throws SQLException {
		String sqlString = String.format("INSERT INTO %s values(?, ?, ?, ?, ?, ?, ?, ?)", TABLE_NAME);
		boolean result = execute(sqlString, //
				book.getId(), //
				book.getIsbn(), //
				book.getAuthors(), //
				book.getTitle(), //
				book.getYear(), //
				book.getRating(), //
				book.getRatingsCount(), //
				book.getImageUrl()); //
		LOG.debug(String.format("Adding %s was %s", book, result ? "successful" : "unsuccessful"));
	}

	/**
	 * Update the customer.
	 * 
	 * @param customer
	 * @throws SQLException
	 */
	public void update(Book book) throws SQLException {
		String sqlString = String.format("UPDATE %s SET %s=?, %s=?, %s=?, %s=?, %s=?, %s=?, %s=? WHERE %s=?", TABLE_NAME, //
				Column.ISBN.name, //
				Column.AUTHORS.name, //
				Column.TITLE.name, //
				Column.YEAR.name, //
				Column.RATING.name, //
				Column.RATINGS_COUNT.name, //
				Column.IMAGE_URL.name, //
				Column.ID.name);
		LOG.debug("Update statment: " + sqlString);

		boolean result = execute(sqlString, book.getIsbn(), book.getAuthors(), book.getTitle(), book.getYear(), book.getRating(),
				book.getRatingsCount(), book.getImageUrl(), book.getId());
		LOG.debug(String.format("Updating %s was %s", book, result ? "successful" : "unsuccessful"));
	}

	/**
	 * Delete the customer from the database.
	 * 
	 * @param customer
	 * @throws SQLException
	 */
	public void delete(Book book) throws SQLException {
		Connection connection;
		Statement statement = null;
		try {
			connection = Database.getConnection();
			statement = connection.createStatement();

			String sqlString = String.format("DELETE FROM %s WHERE %s='%s'", TABLE_NAME, Column.ID.name, book.getId());
			LOG.debug(sqlString);
			int rowcount = statement.executeUpdate(sqlString);
			LOG.debug(String.format("Deleted %d rows", rowcount));
		} finally {
			close(statement);
		}
	}

	/**
	 * Retrieve all the book IDs from the database
	 * 
	 * @return the list of book IDs
	 * @throws SQLException
	 */
	public List<Long> getBookIds() throws SQLException {
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

		LOG.debug(String.format("Loaded %d book IDs from the database", ids.size()));

		return ids;
	}

	public List<Book> getBooks() throws SQLException {
		List<Book> books = new ArrayList<>();

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
				String isbn = resultSet.getString(Column.ISBN.name);
				String authors = resultSet.getString(Column.AUTHORS.name);
				int year = resultSet.getInt(Column.YEAR.name);
				String title = resultSet.getString(Column.TITLE.name);
				float rating = resultSet.getFloat(Column.RATING.name);
				int ratingCount = resultSet.getInt(Column.RATINGS_COUNT.name);
				String image_url = resultSet.getString(Column.IMAGE_URL.name);

				Book book = new Book.Builder(id). //
						setIsbn(isbn). //
						setAuthors(authors). //
						setYear(year). //
						setTitle(title). //
						setRating(rating). // //
						setRatingsCount(ratingCount). //
						setImageUrl(image_url).//
						build();
				books.add(book);
			}

		} finally {
			close(statement);
		}

		LOG.debug(String.format("Loaded %d books from the database", books.size()));

		return books;
	}

	public Book getBook(Long bookId) throws Exception {
		String sqlString = String.format("SELECT * FROM %s WHERE %s = %d", TABLE_NAME, Column.ID.name, bookId);
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

				long id = resultSet.getLong(Column.ID.name);
				String isbn = resultSet.getString(Column.ISBN.name);
				String authors = resultSet.getString(Column.AUTHORS.name);
				int year = resultSet.getInt(Column.YEAR.name);
				String title = resultSet.getString(Column.TITLE.name);
				float rating = resultSet.getFloat(Column.RATING.name);
				int ratingCount = resultSet.getInt(Column.RATINGS_COUNT.name);
				String image_url = resultSet.getString(Column.IMAGE_URL.name);

				Book book = new Book.Builder(id). //
						setIsbn(isbn). //
						setAuthors(authors). //
						setYear(year). //
						setTitle(title). //
						setRating(rating). // //
						setRatingsCount(ratingCount). //
						setImageUrl(image_url).//
						build();

				return book;
			}
		} finally {
			close(statement);
		}

		return null;
	}

	public int countAllBooks() throws Exception {
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
		ISBN("isbn", 20), //
		AUTHORS("authors", 100), //
		TITLE("title", 100), //
		YEAR("year", 4), //
		RATING("rating", 8), //
		RATINGS_COUNT("ratingsCount", 15), //
		IMAGE_URL("imageUrl", 100); //

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
