package tests;

import dao.UserDaoImpl;
import model.User;
import dao.Database;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import java.sql.Connection;
import static org.junit.jupiter.api.Assertions.*;

public class UserDaoTest {

    private static Connection conn;
    private static UserDaoImpl userDao;

    @BeforeAll
    static void setup() throws Exception {
        Database.initialize();
        conn = Database.getConnection();
        userDao = new UserDaoImpl(conn);
    }

    @Test
    void testAddUser() {
        User user = new User("Test User", "testuser", "test@example.com", "password123");
        boolean result = userDao.addUser(user);
        assertTrue(result, "User should be added successfully");
    }
}

// This test verifies that a valid user can be successfully added to the database without any errors.