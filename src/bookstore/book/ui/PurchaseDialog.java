package bookstore.book.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

import bookstore.book.data.Item;
import bookstore.book.sorters.ItemSorter;

@SuppressWarnings("serial")
public class PurchaseDialog extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private JTable table;
	private DefaultTableModel tableModel;
	private static String[] columnNames = { "Name", "Title", "Price" };

	private static final Logger LOG = LogManager.getLogger(PurchaseDialog.class);

	/**
	 * Create the dialog.
	 * 
	 * @throws Exception
	 */
	public PurchaseDialog(List<Item> items, boolean isByLastName, boolean isByTitle, boolean isDescending) throws Exception {
		setAlwaysOnTop(true);
		setTitle("Purchase Report");
		setBounds(100, 100, 620, 450);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		{

			setTableData(items, isByLastName, isByTitle, isDescending);
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

	private void setTableData(List<Item> items, boolean isByLastName, boolean isByTitle, boolean isDescending) {
		if (isByLastName) {
			LOG.debug("isByLastName = true");
			if (isDescending) {
				LOG.debug("isDescending = true");
				Collections.sort(items, new ItemSorter.CompareByLastNameDescending());
			} else {
				LOG.debug("isDescending = false");
				Collections.sort(items, new ItemSorter.CompareByLastName());
			}
		}

		if (isByTitle) {
			LOG.debug("isByTitle = true");
			if (isDescending) {
				LOG.debug("isDescending = true");
				Collections.sort(items, new ItemSorter.CompareByTitleDescending());
			} else {
				LOG.debug("isDescending = false");
				Collections.sort(items, new ItemSorter.CompareByTitle());
			}
		}

		tableModel = new DefaultTableModel();
		tableModel.setColumnIdentifiers(columnNames);

		for (Item item : items) {
			String[] purchaseDetails = new String[columnNames.length];

			String name = item.getFirstName() + " " + item.getLastName();
			String title = item.getTitle();

			purchaseDetails[0] = name;
			purchaseDetails[1] = title;
			purchaseDetails[2] = String.valueOf(item.getPrice());

			tableModel.addRow(purchaseDetails);
		}
	}

}
