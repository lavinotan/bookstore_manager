/**
 * Project: A01030427Assign02_Books2
 * File: Item.java
 * Date: Mar. 8, 2021
 * Time: 7:06:36 p.m.
 */

package bookstore.book.data;

/**
 * @author Lavino Wei-Chung Chen, A01030427
 *
 */

public class Item {
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
		setFirstName(firstName);
		setLastName(lastName);
		setTitle(title);
		setPrice(price);
	}

	/**
	 * @return the firstName
	 */
	public String getFirstName() {
		return firstName;
	}

	/**
	 * @param firstName
	 *            the firstName to set
	 */
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	/**
	 * @return the lastName
	 */
	public String getLastName() {
		return lastName;
	}

	/**
	 * @param lastName
	 *            the lastName to set
	 */
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title
	 *            the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return the price
	 */
	public float getPrice() {
		return price;
	}

	/**
	 * @param price
	 *            the price to set
	 */
	public void setPrice(float price) {
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
