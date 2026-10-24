package model;

import java.sql.SQLException;

import dao.UserDao;
import dao.UserDaoImpl;

import java.util.ArrayList;
import java.util.List;
/**
 * This code defines the Model class, which manages user data, login state,
 * and project cart interactions across the application.
 */
public class Model {
	private UserDao userDao;
	private User currentUser;
    private final List<Project> cart = new ArrayList<>();
	// This code initializes the UserDao and connects to the database.
	public Model() {
		try {
			userDao = new UserDaoImpl(dao.Database.getConnection());
		} catch (SQLException e) {
			throw new RuntimeException("Failed to connect to database", e);
		}
	}
	// This code sets up the user database tables if needed.
	public void setup() throws SQLException {
		userDao.setup();
	}
	// This code returns the UserDao instance for user operations.
	public UserDao getUserDao() {
		return userDao;
	}
	// This code returns the currently logged-in user.
	public User getCurrentUser() {
		return this.currentUser;
	}
	// This code sets the currently logged-in user.
	public void setCurrentUser(User user) {
		currentUser = user;
	}

    // This code handles cart-related methods
    public List<Project> getCart() {
        return cart;
    }
	// This code clears all projects from the user's cart.
    public void clearCart() {
        cart.clear();
    }
}
