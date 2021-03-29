package bookstore.book.ui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.border.EmptyBorder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import bookstore.book.data.Book;
import bookstore.book.data.BookDao;
import bookstore.book.data.Customer;
import bookstore.book.data.CustomerDao;
import bookstore.book.data.Item;
import bookstore.book.data.Purchase;
import bookstore.book.data.PurchaseDao;

@SuppressWarnings("serial")
public class MainFrame extends JFrame {

	private static final Logger LOG = LogManager.getLogger(MainFrame.class);

	private JPanel contentPane;

	private List<Item> items;

	/**
	 * Create the frame.
	 */
	public MainFrame(CustomerDao customerDao, BookDao bookDao, PurchaseDao purchaseDao) {
		setTitle("Bookstore");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// setBounds(100, 100, 450, 300);
		setSize(450, 300);
		setLocationRelativeTo(null);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);

		JMenuBar mainMenuBar = new JMenuBar();
		setJMenuBar(mainMenuBar);

		try {
			buildMenu(customerDao, bookDao, purchaseDao);
		} catch (Exception e1) {
			LOG.error(e1.getMessage());
		}

		setVisible(true);
	}

	@SuppressWarnings("deprecation")
	public void buildMenu(CustomerDao customerDao, BookDao bookDao, PurchaseDao purchaseDao) {
		JMenuBar mainMenuBar = new JMenuBar();
		setJMenuBar(mainMenuBar);

		JMenu fileMenu = new JMenu("File");
		fileMenu.setMnemonic('F');
		mainMenuBar.add(fileMenu);
		JMenu bookdsMenu = new JMenu("Books");
		bookdsMenu.setMnemonic('B');
		mainMenuBar.add(bookdsMenu);
		JMenu customersMenu = new JMenu("Customers");
		customersMenu.setMnemonic('C');
		mainMenuBar.add(customersMenu);
		JMenu purchasesMenu = new JMenu("Purchases");
		purchasesMenu.setMnemonic('P');
		mainMenuBar.add(purchasesMenu);
		JMenu helpMenu = new JMenu("Help");
		helpMenu.setMnemonic('H');
		mainMenuBar.add(helpMenu);

		JMenuItem dropItem = new JMenuItem("Drop");
		dropItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int answer = JOptionPane.showConfirmDialog(MainFrame.this, "Are you sure you would like to delete all Bookstore input data?",
						"Data Input Deletion", JOptionPane.YES_NO_OPTION);
				if (answer == JOptionPane.YES_OPTION) {
					try {
						customerDao.drop();
						bookDao.drop();
						purchaseDao.drop();
					} catch (SQLException e1) {
						LOG.error(e1.getMessage());
					} finally {
						System.exit(0);
					}
				}
			}
		});
		fileMenu.add(dropItem);

		fileMenu.addSeparator();

		JMenuItem quit = new JMenuItem("Quit", KeyEvent.VK_X);
		quit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.ALT_MASK));
		quit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				LOG.debug("Books2 closed");
				System.exit(0);
			}
		});
		fileMenu.add(quit);

		JMenuItem booksCount = new JMenuItem("Count");
		booksCount.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int totalNumberOfBooks = 0;
				try {
					totalNumberOfBooks = bookDao.countAllBooks();
				} catch (Exception e1) {
					LOG.error(e1.getMessage());
				}
				JOptionPane.showMessageDialog(MainFrame.this, "Total number of books: " + totalNumberOfBooks, "Books Count",
						JOptionPane.INFORMATION_MESSAGE);

			}
		});
		bookdsMenu.add(booksCount);

		JCheckBoxMenuItem byAuthor = new JCheckBoxMenuItem("By Author");
		bookdsMenu.add(byAuthor);

		JCheckBoxMenuItem descendingBooks = new JCheckBoxMenuItem("Descending");
		bookdsMenu.add(descendingBooks);

		JMenuItem booksList = new JMenuItem("List");
		booksList.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				boolean isByAuthor = byAuthor.getState();
				boolean isDescending = descendingBooks.getState();

				new BookDialog(bookDao, isByAuthor, isDescending);
			}
		});
		bookdsMenu.add(booksList);

		JMenuItem customersCount = new JMenuItem("Count");
		customersCount.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int totalNumberOfCustomers = 0;
				try {
					totalNumberOfCustomers = customerDao.countAllCustomers();
				} catch (Exception e1) {
					LOG.error(e1.getMessage());
				}
				JOptionPane.showMessageDialog(MainFrame.this, "Total number of customers: " + totalNumberOfCustomers, "Customers Count",
						JOptionPane.INFORMATION_MESSAGE);
			}
		});
		customersMenu.add(customersCount);

		JCheckBoxMenuItem byJoinDate = new JCheckBoxMenuItem("By Join Date");
		customersMenu.add(byJoinDate);

		JMenuItem customersList = new JMenuItem("List");
		customersList.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				boolean isByJoinDate = byJoinDate.getState();

				new CustomerDialog(customerDao, isByJoinDate);
			}
		});
		customersMenu.add(customersList);

		JMenuItem total = new JMenuItem("Total");
		total.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				double totalPurchases = 0;
				try {

					if (items != null) {
						for (Item item : items) {
							totalPurchases += item.getPrice();
						}
					} else {
						totalPurchases = purchaseDao.totalPurchases();
					}

				} catch (Exception e1) {
					LOG.error(e1.getMessage());
				}
				JOptionPane.showMessageDialog(MainFrame.this, String.format("Total of purchases: %.2f", totalPurchases), "Total Purchases",
						JOptionPane.INFORMATION_MESSAGE);
			}
		});
		purchasesMenu.add(total);

		JCheckBoxMenuItem byLastName = new JCheckBoxMenuItem("By Last Name");
		purchasesMenu.add(byLastName);

		JCheckBoxMenuItem byTitle = new JCheckBoxMenuItem("By Title");
		purchasesMenu.add(byTitle);

		JCheckBoxMenuItem descendingPurchases = new JCheckBoxMenuItem("Descending");
		purchasesMenu.add(descendingPurchases);

		JMenuItem filterByCustomerId = new JMenuItem("Filter by Customer ID");
		Long[] customerIdForFilter = new Long[1];
		filterByCustomerId.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				boolean validInput = false;

				while (!validInput) {
					try {
						String inputValue = JOptionPane.showInputDialog(MainFrame.this, "Enter the Customer ID:", "Filter by Customer ID",
								JOptionPane.INFORMATION_MESSAGE);

						if (inputValue == null || inputValue.isEmpty()) {
							customerIdForFilter[0] = null;
						} else {
							customerIdForFilter[0] = Long.parseLong(inputValue);
						}

						try {
							items = generateItems(customerDao, bookDao, purchaseDao, customerIdForFilter[0]);
						} catch (Exception e2) {
							LOG.error(e2.getMessage());
						}

						validInput = true;
					} catch (NumberFormatException e1) {
						JOptionPane.showMessageDialog(null, "Please enter valid customer ID.", "Invalid Input!", JOptionPane.WARNING_MESSAGE);
					}
				}
			}
		});
		purchasesMenu.add(filterByCustomerId);

		JMenuItem purchasesList = new JMenuItem("List");
		purchasesList.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				try {
					items = generateItems(customerDao, bookDao, purchaseDao, customerIdForFilter[0]);
				} catch (Exception e2) {
					LOG.error(e2.getMessage());
				}

				try {
					boolean isByLastName = byLastName.getState();
					boolean isByTitle = byTitle.getState();
					boolean isDescending = descendingPurchases.getState();

					new PurchaseDialog(items, isByLastName, isByTitle, isDescending);
				} catch (Exception e1) {
					LOG.error(e1.getMessage());
				}
			}
		});
		purchasesMenu.add(purchasesList);

		JMenuItem about = new JMenuItem("About");
		about.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0));
		about.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				JOptionPane.showMessageDialog(MainFrame.this, "Books \nBy Lavino Wei-Chung Chen A01030427", "About Books2",
						JOptionPane.INFORMATION_MESSAGE);

			}
		});
		helpMenu.add(about);

	}

	public List<Item> generateItems(CustomerDao customerDao, BookDao bookDao, PurchaseDao purchaseDao, Long customerIdForFilter) throws Exception {

		List<Item> items = new ArrayList<>();
		try {
			LOG.debug("Start creating purchase items with information of customer IDs and book IDs");
			List<Purchase> allPurchases = purchaseDao.getPurchases();
			List<Purchase> filteredPurchases = allPurchases;

			if (customerIdForFilter != null) {
				filteredPurchases = allPurchases.stream().filter(map -> customerIdForFilter == map.getCustomerId()).collect(Collectors.toList());
			}

			int i = 0;
			for (Purchase purchase : filteredPurchases) {

				long customerId = purchase.getCustomerId();
				long bookId = purchase.getBookId();

				Customer customer = customerDao.getCustomer(customerId);
				Book book = bookDao.getBook(bookId);

				String firstName = "";
				String lastName = "";
				String title = "";

				if (customer != null && book != null) {
					firstName = customer.getFirstName();
					lastName = customer.getLastName();
					title = book.getTitle();
				}

				Item item = new Item(firstName, lastName, title, purchase.getPrice());

				items.add(item);
				LOG.debug(String.format("Purchase item %d created successfully", ++i));
			}
		} catch (SQLException e) {
			LOG.error(e.getMessage());
		}

		return items;
	}

}
