package javaapplication1;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.SQLException;

@SuppressWarnings("serial")
public class Tickets extends JFrame implements ActionListener {

	// class level member objects
	Dao dao = new Dao(); // for CRUD operations
	Boolean chkIfAdmin = null;

	// Main menu object items
	private final JMenu mnuFile = new JMenu("File");
	private final JMenu mnuAdmin = new JMenu("Admin");
	private final JMenu mnuTickets = new JMenu("Tickets");

	// Sub menu item objects for all Main menu item objects
	JMenuItem mnuItemExit;
	JMenuItem mnuItemUpdate;
	JMenuItem mnuItemDelete;
	JMenuItem mnuItemOpenTicket;
	JMenuItem mnuItemViewTicket;

	public Tickets(Boolean isAdmin) {
		// Admin view
		if (chkIfAdmin = isAdmin) {
			createMenu(true);
			prepareGUI(true);
		} else { // Non-Admin view
			createMenu(false);
			prepareGUI(false);
		}

	}

	private void createMenu(boolean isAdmin) {

		/* Initialize sub menu items **************************************/

		// initialize sub menu item for File main menu
		mnuItemExit = new JMenuItem("Exit");
		// add to File main menu item
		mnuFile.add(mnuItemExit);

		if (isAdmin) {
			// initialize first sub menu items for Admin main menu
			mnuItemUpdate = new JMenuItem("Update Ticket");
			// add to Admin main menu item
			mnuAdmin.add(mnuItemUpdate);

			// initialize second sub menu items for Admin main menu
			mnuItemDelete = new JMenuItem("Delete Ticket");
			// add to Admin main menu item
			mnuAdmin.add(mnuItemDelete);
		}

		// initialize first sub menu item for Tickets main menu
		mnuItemOpenTicket = new JMenuItem("Open Ticket");
		// add to Ticket Main menu item
		mnuTickets.add(mnuItemOpenTicket);

		// initialize second sub menu item for Tickets main menu
		mnuItemViewTicket = new JMenuItem("View Ticket");
		// add to Ticket Main menu item
		mnuTickets.add(mnuItemViewTicket);

		// initialize any more desired sub menu items below

		/* Add action listeners for each desired menu item *************/
		mnuItemExit.addActionListener(this);
		if (isAdmin) {
			mnuItemUpdate.addActionListener(this);
			mnuItemDelete.addActionListener(this);
		}
		mnuItemOpenTicket.addActionListener(this);
		mnuItemViewTicket.addActionListener(this);

		 /*
		  * continue implementing any other desired sub menu items (like 
		  * for update and delete sub menus for example) with similar 
		  * syntax & logic as shown above
		 */
	}

	private void prepareGUI(boolean isAdmin) {

		// create JMenu bar
		JMenuBar bar = new JMenuBar();
		bar.add(mnuFile); // add main menu items in order, to JMenuBar
		if (isAdmin) {
			bar.add(mnuAdmin);
		}
		bar.add(mnuTickets);
		// add menu bar components to frame
		setJMenuBar(bar);

		addWindowListener(new WindowAdapter() {
			// define a window close operation
			public void windowClosing(WindowEvent wE) {
				System.exit(0);
			}
		});
		// set frame options
		setSize(400, 400);
		getContentPane().setBackground(Color.LIGHT_GRAY);
		setLocationRelativeTo(null);
		setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// implement actions for sub menu items
		if (e.getSource() == mnuItemExit) {
			System.exit(0);

		} else if (e.getSource() == mnuItemOpenTicket) {
			// get ticket information
			String ticketName = JOptionPane.showInputDialog(null, "Enter your name");
			String ticketDesc = JOptionPane.showInputDialog(null, "Enter a ticket description");

			// insert ticket information to database
			int id = dao.insertRecords(ticketName, ticketDesc);

			// display results if successful or not to console / dialog box
			if (id != 0) {
				System.out.println("Ticket ID : " + id + " created successfully!!!");
				JOptionPane.showMessageDialog(null, "Ticket id: " + id + " created");
			} else
				System.out.println("Ticket cannot be created!!!");

		} else if (e.getSource() == mnuItemViewTicket) {

			// retrieve all tickets details for viewing in JTable
			try {
				// Use JTable built in functionality to build a table model and
				// display the table model off your result set!!!
				JTable jt = new JTable(ticketsJTable.buildTableModel(dao.readRecords()));
				jt.setBounds(30, 40, 200, 400);
				JScrollPane sp = new JScrollPane(jt);
				add(sp);
				setVisible(true); // refreshes or repaints frame on screen

			} catch (SQLException e1) {
				e1.printStackTrace();
			}

		} else if (e.getSource() == mnuItemUpdate) {

			// retrieve all tickets details for viewing in JTable
			try {
				// Use JTable built in functionality to build a table model and
				// display the table model off your result set!!!
				JTable jt = new JTable(ticketsJTable.buildTableModel(dao.readRecords()));
				jt.setBounds(30, 40, 200, 400);
				JScrollPane sp = new JScrollPane(jt);
				add(sp);
				setVisible(true); // refreshes or repaints frame on screen

			} catch (SQLException e1) {
				e1.printStackTrace();
			}

			// get ticket information
			String ticketID = JOptionPane.showInputDialog(null, "Enter the ticket ID to update");
			String ticketDesc = JOptionPane.showInputDialog(null, "Enter new description");
			String ticketStatus = JOptionPane.showInputDialog(null, "Close ticket? (1 = NO / 2 = YES)");
			dao.updateRecords(Integer.valueOf(ticketID), ticketDesc, ticketStatus);

		} else if (e.getSource() == mnuItemDelete) {
			// retrieve all tickets details for viewing in JTable
			try {
				// Use JTable built in functionality to build a table model and
				// display the table model off your result set!!!
				JTable jt = new JTable(ticketsJTable.buildTableModel(dao.readRecords()));
				jt.setBounds(30, 40, 200, 400);
				JScrollPane sp = new JScrollPane(jt);
				add(sp);
				setVisible(true); // refreshes or repaints frame on screen

			} catch (SQLException e1) {
				e1.printStackTrace();
			}

			// get ticket information
			String ticketID = JOptionPane.showInputDialog(null, "Enter the ticket ID to delete");
		}
	}
}
