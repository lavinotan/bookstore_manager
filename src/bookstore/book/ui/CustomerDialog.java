package bookstore.book.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
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

import bookstore.book.data.Customer;
import bookstore.book.data.CustomerDao;
import bookstore.book.sorters.CustomerSorter;
import net.miginfocom.swing.MigLayout;

@SuppressWarnings("serial")
public class CustomerDialog extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private JTable table;
	private DefaultTableModel tableModel;
	private int selectedRow;
	private static String[] columnNames = { "ID", "First name", "Last name", "Street", "City", "Postal Code", "Phone", "Email", "Join Date" };
	public static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("MMM dd yyyy");

	private static Logger LOG = LogManager.getLogger(CustomerDialog.class);

	/**
	 * Create the dialog.
	 */
	public CustomerDialog(CustomerDao customerDao, boolean isByJoinDate) {
		setAlwaysOnTop(true);
		setTitle("Customer Report");
		setBounds(100, 100, 620, 450);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		{
			setTableData(customerDao, isByJoinDate);

			table = new JTable(tableModel);

			table.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					showCustomerDetailedInformation(customerDao);
				}
			});

			JScrollPane sp = new JScrollPane(table);
			contentPanel.add(sp, BorderLayout.CENTER);
			contentPanel.setPreferredSize(new Dimension(600, 350));
		}
		{
			JPanel buttonPane = new JPanel();
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");

				okButton.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						dispose();
					}
				});
				buttonPane.setLayout(new MigLayout("", "[85px][429.00][45px][63px]", "[21px]"));
				okButton.setActionCommand("OK");
				buttonPane.add(okButton, "cell 2 0,alignx left,aligny top");
				okButton.setMnemonic('O');
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
				buttonPane.add(cancelButton, "cell 3 0,alignx left,aligny top");
			}
		}

		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setVisible(true);
	}

	private void setTableData(CustomerDao customerDao, boolean isByJoinDate) {
		try {
			List<Customer> customers = customerDao.getCustomers();
			tableModel = new DefaultTableModel();
			tableModel.setColumnIdentifiers(columnNames);

			if (isByJoinDate) {
				LOG.debug("isByJoinDate = true");
				Collections.sort(customers, new CustomerSorter.CompareByJoinedDate());
			}

			for (Customer customer : customers) {
				Object[] customerDetails = new Object[columnNames.length];
				customerDetails[0] = customer.getId();
				customerDetails[1] = customer.getFirstName();
				customerDetails[2] = customer.getLastName();
				customerDetails[3] = customer.getStreet();
				customerDetails[4] = customer.getCity();
				customerDetails[5] = customer.getPostalCode();
				customerDetails[6] = customer.getPhone();
				customerDetails[7] = customer.getEmailAddress();
				customerDetails[8] = customer.getJoinedDate();

				tableModel.addRow(customerDetails);
			}

		} catch (SQLException e) {
			LOG.error(e.getMessage());
		}
	}

	private void showCustomerDetailedInformation(CustomerDao customerDao) {
		selectedRow = table.getSelectedRow();

		long id = Long.parseLong(tableModel.getValueAt(selectedRow, 0).toString());
		String firstName = tableModel.getValueAt(selectedRow, 1).toString();
		String lastName = tableModel.getValueAt(selectedRow, 2).toString();
		String street = tableModel.getValueAt(selectedRow, 3).toString();
		String city = tableModel.getValueAt(selectedRow, 4).toString();
		String postalCode = tableModel.getValueAt(selectedRow, 5).toString();
		String phone = tableModel.getValueAt(selectedRow, 6).toString();
		String emailAddress = tableModel.getValueAt(selectedRow, 7).toString();
		String joinedDate = tableModel.getValueAt(selectedRow, 8).toString();

		String dateSplite = "-";

		String[] dateValue = joinedDate.split(dateSplite);

		int year = Integer.parseInt(dateValue[0]);
		int month = Integer.parseInt(dateValue[1]);
		int day = Integer.parseInt(dateValue[2]);

		Customer customer = new Customer.Builder(id, phone).setFirstName(firstName).setLastName(lastName).setStreet(street).setCity(city)
				.setPostalCode(postalCode).setEmailAddress(emailAddress).setJoinedDate(year, month, day).build();

		new CustomerDetailsDialog(tableModel, selectedRow, customerDao, customer);
	}

}
