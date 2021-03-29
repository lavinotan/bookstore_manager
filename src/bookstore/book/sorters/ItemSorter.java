/**
 * Project: A01030427Assign02_Books2
 * File: ItemSorter.java
 * Date: Mar. 8, 2021
 * Time: 7:31:32 p.m.
 */

package bookstore.book.sorters;

import java.util.Comparator;

import bookstore.book.data.Item;

/**
 * @author Lavino Wei-Chung Chen, A01030427
 *
 */

public class ItemSorter {
	public static class CompareByLastName implements Comparator<Item> {
		@Override
		public int compare(Item item1, Item item2) {
			return item1.getLastName().compareTo(item2.getLastName());
		}
	}

	public static class CompareByLastNameDescending implements Comparator<Item> {
		@Override
		public int compare(Item item1, Item item2) {
			return item2.getLastName().compareTo(item1.getLastName());
		}
	}

	public static class CompareByTitle implements Comparator<Item> {
		@Override
		public int compare(Item item1, Item item2) {
			return item1.getTitle().compareToIgnoreCase(item2.getTitle());
		}
	}

	public static class CompareByTitleDescending implements Comparator<Item> {
		@Override
		public int compare(Item item1, Item item2) {
			return item2.getTitle().compareToIgnoreCase(item1.getTitle());
		}
	}
}
