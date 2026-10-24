package tests;

import dao.UserDaoImpl;
import dao.Database;
import model.User;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import static org.junit.jupiter.api.Assertions.*;

public class UserDaoUpdateTest {

    private static Connection conn;
    private static UserDaoImpl userDao;

    @BeforeAll
    static void setup() throws Exception {
        Database.initialize();
        conn = Database.getConnection();
        userDao = new UserDaoImpl(conn);
    }

    @Test
    void testUpdateUserEmail() throws Exception {
        // Step 1: this code tests user insert
        User user = new User("Update User", "updateuser", "oldemail@example.com", "password123");
        userDao.addUser(user);

        // Step 2: this code tests the email update
        String newEmail = "newemail@example.com";
        String sql = "UPDATE users SET email = ? WHERE username = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, newEmail);
            stmt.setString(2, "updateuser");
            stmt.executeUpdate();
        }

        // Step 3: This code verifies the update
        String query = "SELECT email FROM users WHERE username = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, "updateuser");
            ResultSet rs = stmt.executeQuery();
            assertTrue(rs.next());
            assertEquals(newEmail, rs.getString("email"), "Email should be updated successfully");
        }
    }
}

// This test ensures that a user's email address can be successfully updated and verified in the database.