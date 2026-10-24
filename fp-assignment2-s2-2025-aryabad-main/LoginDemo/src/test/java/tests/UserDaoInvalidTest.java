package tests;

import dao.UserDaoImpl;
import dao.Database;
import model.User;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.*;

public class UserDaoInvalidTest {

    private static Connection conn;
    private static UserDaoImpl userDao;

    @BeforeAll
    static void setup() throws Exception {
        Database.initialize();
        conn = Database.getConnection();
        userDao = new UserDaoImpl(conn);
    }

    @Test
    void testAddUserWithMissingFields() {
        // This code handles missing email and password
        User invalidUser = new User("No Email", "noemailuser", "", "");
        boolean result = userDao.addUser(invalidUser);

        // This code should fail when there is invalid data
        assertFalse(result, "User with missing fields should not be added");
    }

    @Test
    void testAddUserWithInvalidEmail() {
        // This code should detect an invalid email format
        User invalidEmailUser = new User("Bad Email", "bademail", "notanemail", "1234");
        boolean result = userDao.addUser(invalidEmailUser);

        // This code should fail when there is an invalid email format
        assertFalse(result, "User with invalid email should not be added");
    }
}

// This test ensures that users with missing fields or invalid email formats are correctly rejected by the system.