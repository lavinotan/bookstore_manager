package bookstore.book.sorters;

import java.util.Comparator;

import bookstore.book.data.Book;

public class BookSorter {

	public static class CompareByAuthor implements Comparator<Book> {
		@Override
		public int compare(Book book1, Book book2) {
			return book1.getAuthors().compareToIgnoreCase(book2.getAuthors());
		}
	}

	public static class CompareByAuthorDescending implements Comparator<Book> {
		@Override
		public int compare(Book book1, Book book2) {
			return -book1.getAuthors().compareToIgnoreCase(book2.getAuthors());
		}
	}

}
