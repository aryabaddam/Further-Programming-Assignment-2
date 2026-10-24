package dao;

import java.sql.*;
import model.User;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

// This code implements the UserDao interface to manage user data in the database.
public class UserDaoImpl implements UserDao {
    private Connection conn;

    // This code initializes the UserDaoImpl with a database connection.
    public UserDaoImpl(Connection conn) {
        this.conn = conn;
    }
	private static final String TABLE_NAME = "users";

	// This code sets up the users table in the database if it does not already exist.
	@Override
	public void setup() throws SQLException {
		try (Connection connection = Database.getConnection();
			 Statement stmt = connection.createStatement()) {
			String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
					"id INTEGER PRIMARY KEY AUTOINCREMENT," +
					"fullname TEXT NOT NULL," +
					"username TEXT UNIQUE NOT NULL," +
					"email TEXT UNIQUE NOT NULL," +
					"password_hash TEXT NOT NULL)";
			stmt.executeUpdate(sql);
		}
	}

	// This code retrieves a user from the database matching the given username and password.
	@Override
	public User getUser(String username, String password) throws SQLException {
		String sql = "SELECT * FROM " + TABLE_NAME + " WHERE username = ? AND password_hash = ?";
		try (Connection connection = Database.getConnection();
			 PreparedStatement stmt = connection.prepareStatement(sql)) {

			stmt.setString(1, username);
			stmt.setString(2, hashPassword(password));

			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				return new User(
						rs.getString("fullname"),
						rs.getString("username"),
						rs.getString("email"),
						rs.getString("password_hash")
				);
			}
		}
		return null;
	}

	// This code creates a new user in the database with the provided details.
	@Override
	public User createUser(String fullname, String username, String email, String password) throws SQLException {
		String sql = "INSERT INTO " + TABLE_NAME + " (fullname, username, email, password_hash) VALUES (?, ?, ?, ?)";

		try (Connection connection = Database.getConnection();
			 PreparedStatement stmt = connection.prepareStatement(sql)) {

			stmt.setString(1, fullname);
			stmt.setString(2, username);
			stmt.setString(3, email);
			stmt.setString(4, hashPassword(password));

			stmt.executeUpdate();
			return new User(fullname, username, email, hashPassword(password));

		} catch (SQLException e) {
			// This code handles any duplicate username/email
			if (e.getMessage().contains("UNIQUE constraint failed")) {
				throw new SQLException("Username or email already exists.");
			}
			throw e;
		}
	}

    // This code adds a user to the database with basic validation and error handling.
    public boolean addUser(User user) {
        // This code handles basic validation before inserting
        if (user == null ||
            user.getFullname() == null || user.getFullname().isBlank() ||
            user.getUsername() == null || user.getUsername().isBlank() ||
            user.getEmail() == null || user.getEmail().isBlank() ||
            user.getPassword() == null || user.getPassword().isBlank()) {
            System.err.println("⚠️ Invalid user: missing required fields.");
            return false;
        }

        // This code handles simple email validation
        if (!user.getEmail().contains("@") || !user.getEmail().contains(".")) {
            System.err.println("⚠️ Invalid email format for user: " + user.getEmail());
            return false;
        }

        String sql = "INSERT INTO users (fullname, username, email, password_hash) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, user.getFullname());
            stmt.setString(2, user.getUsername());
            stmt.setString(3, user.getEmail());
            stmt.setString(4, user.getPassword());
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("⚠️ Error adding user: " + e.getMessage());
            return false;
        }
    }

	// This code hashes a password using the SHA-256 algorithm.
	private String hashPassword(String password) {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			byte[] bytes = md.digest(password.getBytes());
			StringBuilder sb = new StringBuilder();
			for (byte b : bytes) sb.append(String.format("%02x", b));
			return sb.toString();
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}
}