package bookstore.book.ui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import bookstore.book.data.Customer;
import bookstore.book.data.CustomerDao;
import net.miginfocom.swing.MigLayout;

@SuppressWarnings("serial")
public class CustomerDetailsDialog extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private JTextField textFieldID;
	private JTextField textFieldFirstName;
	private JTextField textFieldLastName;
	private JTextField textFieldStreet;
	private JTextField textFieldCity;
	private JTextField textFieldPostalCode;
	private JTextField textFieldPhone;
	private JTextField textFieldEmail;
	private JFormattedTextField formattedTextFieldJoinedDate;

	private Object[] rowValueArrary;

	public static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("YYYY-MM-dd");

	private static Logger LOG = LogManager.getLogger(CustomerDetailsDialog.class);

	/**
	 * Create the dialog.
	 */
	public CustomerDetailsDialog(DefaultTableModel tableModel, int selectedRow, CustomerDao customerDao, Customer customer) {
		setTitle("Customer Details");
		setAlwaysOnTop(true);
		setBounds(100, 100, 450, 320);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new MigLayout("", "[10px][96px,grow]", "[19px][][][][][][][][]"));
		{
			JLabel lblID = new JLabel("ID");
			contentPanel.add(lblID, "cell 0 0,alignx right,aligny center");
		}
		{
			textFieldID = new JTextField();
			textFieldID.setEditable(false);
			contentPanel.add(textFieldID, "cell 1 0,alignx left,aligny top");
			textFieldID.setColumns(10);

			textFieldID.setText(String.valueOf(customer.getId()));
		}
		{
			JLabel lblFirstName = new JLabel("First Name");
			contentPanel.add(lblFirstName, "cell 0 1,alignx trailing");
		}
		{
			textFieldFirstName = new JTextField();
			contentPanel.add(textFieldFirstName, "cell 1 1,growx");
			textFieldFirstName.setColumns(10);

			textFieldFirstName.setText(customer.getFirstName());
		}
		{
			JLabel lblLastName = new JLabel("Last Name");
			contentPanel.add(lblLastName, "cell 0 2,alignx right");
		}
		{
			textFieldLastName = new JTextField();
			contentPanel.add(textFieldLastName, "cell 1 2,growx");
			textFieldLastName.setColumns(10);

			textFieldLastName.setText(customer.getLastName());
		}
		{
			JLabel lblStreet = new JLabel("Street");
			contentPanel.add(lblStreet, "cell 0 3,alignx trailing");
		}
		{
			textFieldStreet = new JTextField();
			textFieldStreet.setColumns(10);
			contentPanel.add(textFieldStreet, "cell 1 3,growx");

			textFieldStreet.setText(customer.getStreet());
		}
		{
			JLabel lblCity = new JLabel("City");
			contentPanel.add(lblCity, "cell 0 4,alignx trailing");
		}
		{
			textFieldCity = new JTextField();
			textFieldCity.setColumns(10);
			contentPanel.add(textFieldCity, "cell 1 4,growx");

			textFieldCity.setText(customer.getCity());
		}
		{
			JLabel lblPostalCode = new JLabel("Postal Code");
			contentPanel.add(lblPostalCode, "cell 0 5,alignx trailing");
		}
		{
			textFieldPostalCode = new JTextField();
			textFieldPostalCode.setColumns(10);
			contentPanel.add(textFieldPostalCode, "cell 1 5,growx");

			textFieldPostalCode.setText(customer.getPostalCode());
		}
		{
			JLabel lblPhone = new JLabel("Phone");
			contentPanel.add(lblPhone, "cell 0 6,alignx trailing");
		}
		{
			textFieldPhone = new JTextField();
			textFieldPhone.setColumns(10);
			contentPanel.add(textFieldPhone, "cell 1 6,growx");

			textFieldPhone.setText(customer.getPhone());
		}
		{
			JLabel lblEmail = new JLabel("Email");
			contentPanel.add(lblEmail, "cell 0 7,alignx trailing");
		}
		{
			textFieldEmail = new JTextField();
			textFieldEmail.setColumns(10);
			contentPanel.add(textFieldEmail, "cell 1 7,growx");

			textFieldEmail.setText(customer.getEmailAddress());
		}
		{
			JLabel lblJoinedDate = new JLabel("Joined Date");
			contentPanel.add(lblJoinedDate, "cell 0 8,alignx trailing");
		}

		{

			formattedTextFieldJoinedDate = new JFormattedTextField(DATE_FORMAT);
			formattedTextFieldJoinedDate.setColumns(10);
			contentPanel.add(formattedTextFieldJoinedDate, "cell 1 8,growx");

			formattedTextFieldJoinedDate.setValue(customer.getJoinedDate());
		}
		{
			JPanel buttonPane = new JPanel();
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				buttonPane.setLayout(new MigLayout("", "[85px][258.00][45px][63px]", "[21px]"));
				{
					JButton btnDelete = new JButton("Delete");
					btnDelete.setMnemonic('D');
					btnDelete.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							Customer customerForDeletion = getSelectedCustomer();
							try {
								customerDao.delete(customerForDeletion);
								tableModel.removeRow(selectedRow);
							} catch (SQLException e1) {
								LOG.error(e1.getMessage());
							}
							dispose();
						}
					});
					buttonPane.add(btnDelete, "cell 0 0,alignx left,aligny top");
				}
				JButton okButton = new JButton("OK");
				okButton.setMnemonic('O');
				okButton.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						updateTableModel(tableModel, selectedRow, customerDao, customer);
						dispose();
					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton, "cell 2 0,alignx left,aligny top");
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

	private void updateTableModel(DefaultTableModel tableModel, int selectedRow, CustomerDao customerDao, Customer customer) {
		Customer updatedCustomer = getSelectedCustomer();

		if (!updatedCustomer.equals(customer)) {
			LOG.debug(String.format("Selected customer details: %s is modified to %s", customer, updatedCustomer));
			try {
				customerDao.update(updatedCustomer);
				int column = 0;
				for (Object cellValue : rowValueArrary) {
					tableModel.setValueAt(cellValue, selectedRow, column);
					column++;
				}
			} catch (SQLException e1) {
				LOG.error(e1.getMessage());
			}
		}
	}

	private Customer getSelectedCustomer() {
		Long id = Long.parseLong(textFieldID.getText());
		String firstName = textFieldFirstName.getText();
		String lastName = textFieldLastName.getText();
		String street = textFieldStreet.getText();
		String city = textFieldCity.getText();
		String postalCode = textFieldPostalCode.getText();
		String emailAddress = textFieldEmail.getText();
		String phone = textFieldPhone.getText();
		String date = formattedTextFieldJoinedDate.getText();

		rowValueArrary = new Object[] { id, firstName, lastName, street, city, postalCode, emailAddress, phone, date };

		String dateSplit = "-";
		String[] dateValue = date.split(dateSplit);

		int year = Integer.parseInt(dateValue[0]);
		int month = Integer.parseInt(dateValue[1]);
		int day = Integer.parseInt(dateValue[2]);

		Customer updatedCustomer = new Customer.Builder(id, phone).setFirstName(firstName).setLastName(lastName).setStreet(street).setCity(city)
				.setPostalCode(postalCode).setEmailAddress(emailAddress).setJoinedDate(year, month, day).build();

		return updatedCustomer;
	}

}
