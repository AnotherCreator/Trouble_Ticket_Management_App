package javaapplication1;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

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
		final String createTicketsTable = "CREATE TABLE jregi_tickets3" +
				"(ticket_id INT AUTO_INCREMENT PRIMARY KEY," +
				"ticket_issuer VARCHAR(30)," +
				"ticket_description VARCHAR(200)," +
				"ticket_start_date DATETIME," +
				"ticket_modified_date DATETIME," +
				"ticket_status VARCHAR(5)," +
				"ticket_end_date DATETIME)";

		final String createUsersTable = "CREATE TABLE jregi_users" +
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

		try {
			statement = getConnection().createStatement();

			// create loop to grab each array index containing a list of values
			// and PASS (insert) that data into your User table
			for (List<String> rowData : array) {
				// Check if user exists in the table
				if (statement.execute("SELECT * FROM jregi_users WHERE uname = '"+rowData.get(0)+"'")) {
					continue;
				} else {
					// Add new user(s) to table
					sql = "insert into jregi_users(uname,upass,admin) " + "values('" + rowData.get(0) + "'," +
							" '" + rowData.get(1) + "','" + rowData.get(2) + "');";
					statement.executeUpdate(sql);
				}
			}
			System.out.println("Inserts completed in the given database...");

			// close statement object
			statement.close();

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	public int insertRecords(String ticketName, String ticketDesc) {
		int id = 0;
		try {
			java.util.Date dt = new java.util.Date();
			java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String currentTime = sdf.format(dt);

			statement = getConnection().createStatement();
			statement.executeUpdate("Insert INTO jregi_tickets3" +
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
			results = statement.executeQuery("SELECT * FROM jregi_tickets3");
			//connect.close();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		return results;
	}

	public void updateRecords(Integer ticketID, String ticketDesc, String ticketStatus) { // Update records by ticket_id
		try {
			java.util.Date dt = new java.util.Date();
			java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String currentTime = sdf.format(dt);

			PreparedStatement update = getConnection().prepareStatement(
					"UPDATE jregi_tickets3 SET ticket_description = ?, " +
							"ticket_modified_date = ?, " +
							"ticket_status = ?, " +
							"ticket_end_date = ?" +
							"WHERE ticket_id = ?",
					Statement.RETURN_GENERATED_KEYS);

			update.setString(1, ticketDesc);
			update.setString(2, currentTime);
			if (Objects.equals(ticketStatus, "1") || Objects.equals(ticketStatus, null)) {
				update.setString(3, "Open");
			} else {
				update.setString(3, "Close");
				update.setString(4, currentTime);
			}
			update.setInt(5, ticketID);
			update.executeUpdate();

		} catch (SQLException e1) {
			e1.printStackTrace();
		}
	}

	public void deleteRecords() { // Delete record(s) by ticket_id
		try {
			statement = connect.createStatement();

		} catch (SQLException e1) {
			e1.printStackTrace();
		}
	}
}
