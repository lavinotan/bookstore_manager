/**
 * Project: A01030427Assign02_Books2
 * File: DbConstants.java
 * Date: Mar. 5, 2021
 * Time: 5:23:20 p.m.
 */

/**
 * @author Lavino Wei-Chung Chen, A01030427
 *
 */

package bookstore.book.db;

public interface DbConstants {

	String DB_PROPERTIES_FILENAME = "db.properties";
	String DB_DRIVER_KEY = "db.driver";
	String DB_URL_KEY = "db.url";
	String DB_USER_KEY = "db.user";
	String DB_PASSWORD_KEY = "db.password";
	String TABLE_ROOT = "A01030427_";
	String CUSTOMER_TABLE_NAME = TABLE_ROOT + "Customer";
	String STUDENT_TABLE_NAME = TABLE_ROOT + "Student";
	String COURSE_TABLE_NAME = TABLE_ROOT + "Course";
	String ENROLLMENT_TABLE_NAME = TABLE_ROOT + "Enrollment";

}
