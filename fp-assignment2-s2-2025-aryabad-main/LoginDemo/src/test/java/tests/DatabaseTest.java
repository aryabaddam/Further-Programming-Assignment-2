package tests;

import dao.Database;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.*;

public class DatabaseTest {

    @BeforeAll
    static void setup() {
        Database.initialize();
    }

    @Test
    void testDatabaseConnection() {
        try (Connection conn = Database.getConnection()) {
            assertNotNull(conn, "Database connection should not be null");
            assertTrue(conn.isValid(1), "Connection should be valid");
        } catch (Exception e) {
            fail("Database connection failed: " + e.getMessage());
        }
    }
}

// This test ensures the SQLite database initializes and connects successfully without errors.