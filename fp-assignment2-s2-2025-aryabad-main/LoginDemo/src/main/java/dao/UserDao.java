package dao;

import java.sql.SQLException;
import model.User;

/**
 * A Data Access Object (DAO) provides an abstract interface
 * to a database or persistence mechanism.
 * It defines methods for interacting with User data without
 * exposing the database implementation details.
 */
public interface UserDao {
	// Creates tables if they do not exist
	void setup() throws SQLException;

	// Retrieve a user by username and password (for login)
	User getUser(String username, String password) throws SQLException;

	// Create a new user (for signup)
	User createUser(String fullname, String username, String email, String password) throws SQLException;
}