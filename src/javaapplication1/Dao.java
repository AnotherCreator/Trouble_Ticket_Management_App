package javaapplication1;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.*;
import java.util.*;

public class Dao {
	// instance fields
	static Connection connect = null;
	Statement statement = null;

	// constructor
	public Dao() {
	  
	}

	public Connection getConnection() {
		// Setup the connection with the DB
		try {
			connect = DriverManager
					.getConnection("jdbc:mysql://www.papademas.net:3307/tickets?autoReconnect=true" +
							"&useSSL=false&user=fp411&password=411");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return connect;
	}

	// CRUD implementation

	public void createTables() {
		// variables for SQL Query table creations
		final String createTicketsTable = "CREATE TABLE jregi_tickets4" +
				"(ticket_id INT AUTO_INCREMENT PRIMARY KEY," +
				"ticket_issuer VARCHAR(30)," +
				"ticket_description VARCHAR(200)," +
				"ticket_start_date DATETIME," +
				"ticket_modified_date DATETIME," +
				"ticket_status VARCHAR(6)," +
				"ticket_end_date DATETIME)";

		final String createUsersTable = "CREATE TABLE jregi_users1" +
				"(uid INT AUTO_INCREMENT PRIMARY KEY, uname VARCHAR(30), upass VARCHAR(30), admin int)";

		try {

			// execute queries to create tables

			statement = getConnection().createStatement();

			statement.executeUpdate(createTicketsTable);
			statement.executeUpdate(createUsersTable);
			System.out.println("Created tables in given database...");

			// end create table
			// close connection/statement object
			statement.close();
			connect.close();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		// add users to user table
		addUsers();
	}

	public void addUsers() {
		// add list of users from userlist.csv file to users table

		// variables for SQL Query inserts
		String sql;

		Statement statement;
		BufferedReader br;
		List<List<String>> array = new ArrayList<>(); // list to hold (rows & cols)
		HashMap<Integer, String> userList = new HashMap<>(); // Hashmap for local userlist.csv
		HashMap<Integer, String> ExistingUserList = new HashMap<>(); // Hashmap for existing database users

		// read data from file
		try {
			br = new BufferedReader(new FileReader(new File("./userlist.csv")));
			String line;
			while ((line = br.readLine()) != null) {
				array.add(Arrays.asList(line.split(",")));
			}
		} catch (Exception e) {
			System.out.println("There was a problem loading the file");
		}

		// Query userlist.csv to check if new users have been added
		int i = 0;
		for (List<String> rowData : array) {
			if (!userList.containsValue(rowData.get(0))) { // If user does not exist, add to hashmap
				userList.put(i, rowData.get(0));
			}
			i++;
		}

		// Query database to create a hashmap of existing users
		try {
			PreparedStatement checkUsers = getConnection().prepareStatement(
					"SELECT uname FROM jregi_users1",
					Statement.RETURN_GENERATED_KEYS);
			ResultSet rs = checkUsers.executeQuery();

			int j = 0;
			while (rs.next()) {
				if (!ExistingUserList.containsValue(rs.getString(1))) { // If user does not exist, add to hashmap
					ExistingUserList.put(j, rs.getString(1));
					j++;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		// Insert users into database
		try {
			// create loop to grab each array index containing a list of values
			// and PASS (insert) that data into your User table

			for (List<String> rowData : array) {
				// Add user to database if they dont exist inside hashmap
				if (!(ExistingUserList.containsValue(rowData.get(0)))) {
					System.out.println("New user found, adding to database. . .");

					PreparedStatement addUser = getConnection().prepareStatement(
							"INSERT INTO jregi_users1" +
									"(uname, upass, admin) " +
									"VALUES(?, ?, ?)",
							Statement.RETURN_GENERATED_KEYS);

					addUser.setString(1, rowData.get(0));
					addUser.setString(2, rowData.get(1));
					addUser.setInt(3, Integer.parseInt(rowData.get(2)));
					addUser.executeUpdate();
				}
			}
			System.out.println("\nInserts completed in the given database...");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public int insertRecords(String ticketName, String ticketDesc) {
		int id = 0;
		try {
			java.util.Date dt = new java.util.Date();
			java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String currentTime = sdf.format(dt);

			// New tickets will always have a default "Open" status and current local time
			// Ticket issuer and desc will always be inputted by the user
			statement = getConnection().createStatement();
			statement.executeUpdate("Insert INTO jregi_tickets4" +
					"(ticket_issuer, ticket_description, ticket_start_date, ticket_status) VALUES(" +
					"'"+ticketName+"', '"+ticketDesc+"', '"+currentTime+"', '"+"Open"+"')",
					Statement.RETURN_GENERATED_KEYS);

			// retrieve ticket id number newly auto generated upon record insertion
			ResultSet resultSet = null;
			resultSet = statement.getGeneratedKeys();
			if (resultSet.next()) {
				// retrieve first field in table
				id = resultSet.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return id;
	}

	public ResultSet readRecords() {
		ResultSet results = null;
		try {
			statement = connect.createStatement();
			results = statement.executeQuery("SELECT * FROM jregi_tickets4");
//			connect.close();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		return results;
	}

	public int updateRecords(Integer ticketID, String ticketDesc, String ticketStatus) { // Update records by ticket_id
		try {
			java.util.Date dt = new java.util.Date();
			java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String currentTime = sdf.format(dt);

			// Open Ticket
			if (Objects.equals(ticketStatus, "1") || Objects.equals(ticketStatus, null)) {
				PreparedStatement updateOpen = getConnection().prepareStatement(
						"UPDATE jregi_tickets4 SET ticket_description = ?," +
								"ticket_modified_date = ?, " +
								"ticket_status = ? " +
								"WHERE ticket_id = ?",
						Statement.RETURN_GENERATED_KEYS);

				updateOpen.setString(1, ticketDesc);
				updateOpen.setString(2, currentTime);
				updateOpen.setString(3, "Open");
				updateOpen.setInt(4, ticketID);
				updateOpen.executeUpdate();
			// Close ticket
			} else {
				PreparedStatement updateClose = getConnection().prepareStatement(
						"UPDATE jregi_tickets4 SET ticket_description = ?," +
								"ticket_modified_date = ?, " +
								"ticket_status = ?, " +
								"ticket_end_date = ?" +
								"WHERE ticket_id = ?",
						Statement.RETURN_GENERATED_KEYS);
				updateClose.setString(1, ticketDesc);
				updateClose.setString(2, currentTime);
				updateClose.setString(3, "Closed");
				updateClose.setString(4, currentTime);
				updateClose.setInt(5, ticketID);
				updateClose.executeUpdate();
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		return ticketID;
	}

	public int deleteRecords(Integer ticketID) { // Delete record(s) by ticket_id
		try {
			PreparedStatement delete = getConnection().prepareStatement(
					"DELETE FROM jregi_tickets4 WHERE ticket_id = ?",
			Statement.RETURN_GENERATED_KEYS);

			delete.setInt(1, ticketID);
			delete.executeUpdate();

		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		return ticketID;
	}
}
