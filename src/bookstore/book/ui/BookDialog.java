package bookstore.book.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import bookstore.book.data.Book;
import bookstore.book.data.BookDao;
import bookstore.book.sorters.BookSorter;

@SuppressWarnings("serial")
public class BookDialog extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private JTable table;
	private DefaultTableModel tableModel;
	private static String[] columnNames = { "ID", "ISBN", "Authors", "Title", "Year", "Rating", "Ratings Count", "Image URL" };

	private static final Logger LOG = LogManager.getLogger(BookDialog.class);

	/**
	 * Create the dialog.
	 */
	public BookDialog(BookDao bookDao, boolean isByAuthor, boolean isDescending) {
		setAlwaysOnTop(true);
		setTitle("Book Report");
		setBounds(100, 100, 620, 450);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		{
			setTableData(bookDao, isByAuthor, isDescending);
			table = new JTable(tableModel);

			JScrollPane sp = new JScrollPane(table);
			contentPanel.add(sp, BorderLayout.CENTER);
			contentPanel.setPreferredSize(new Dimension(600, 350));
		}

		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.setMnemonic('O');
				okButton.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						dispose();
					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.setMnemonic('C');
				cancelButton.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						dispose();
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}

		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setVisible(true);
	}

	private void setTableData(BookDao bookDao, boolean isByAuthor, boolean isDescending) {
		try {
			List<Book> books = bookDao.getBooks();
			tableModel = new DefaultTableModel();
			tableModel.setColumnIdentifiers(columnNames);

			if (isByAuthor) {
				LOG.debug("isByAuthor = true");
				if (isDescending) {
					LOG.debug("isDescending = true");
					Collections.sort(books, new BookSorter.CompareByAuthorDescending());
				} else {
					LOG.debug("isDescending = false");
					Collections.sort(books, new BookSorter.CompareByAuthor());
				}
			}

			for (Book book : books) {
				String[] bookDetails = new String[columnNames.length];
				bookDetails[0] = String.valueOf(book.getId());
				bookDetails[1] = book.getIsbn();
				bookDetails[2] = book.getAuthors();
				bookDetails[3] = book.getTitle();
				bookDetails[4] = String.valueOf(book.getYear());
				bookDetails[5] = String.valueOf(book.getRating());
				bookDetails[6] = String.valueOf(book.getRatingsCount());
				bookDetails[7] = book.getImageUrl();

				tableModel.addRow(bookDetails);
			}

		} catch (SQLException e) {
			LOG.error(e.getMessage());
		}
	}
}
