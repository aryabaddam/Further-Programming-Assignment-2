package tests;

import dao.UserDaoImpl;
import dao.Database;
import model.User;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.*;

public class UserDaoDuplicateTest {

    private static Connection conn;
    private static UserDaoImpl userDao;

    @BeforeAll
    static void setup() throws Exception {
        // This code re-inits database to start clean
        Database.initialize();
        conn = Database.getConnection();
        userDao = new UserDaoImpl(conn);
    }

    @Test
    void testDuplicateUserInsertion() {
        // This first insert should succeed
        User user1 = new User("John Doe", "john", "john@example.com", "1234");
        boolean firstInsert = userDao.addUser(user1);
        assertTrue(firstInsert, "First user insertion should succeed");

        // This second insert with same username should fail
        User user2 = new User("Johnny", "john", "johnny@example.com", "abcd");
        boolean secondInsert = userDao.addUser(user2);
        assertFalse(secondInsert, "Duplicate username should be rejected");
    }
}

// This test checks that duplicate usernames are not allowed in the database by rejecting the second insert.