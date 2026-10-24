package dao;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.*;

public class Database {

    // This code creates a path to the SQLite database file
    private static final String DB_URL = "jdbc:sqlite:application.db";

    // This code creates a database connection
    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC"); // Load SQLite driver
        } catch (ClassNotFoundException e) {
            throw new SQLException("SQLite JDBC driver not found", e);
        }
        return DriverManager.getConnection(DB_URL);
    }

    // This code initialises all required tables
    public static void initialize() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            // This code drops existing tables to prevent mismatch issues
            System.out.println("🧹 Resetting database schema...");
            stmt.execute("DROP TABLE IF EXISTS registrations;");
            stmt.execute("DROP TABLE IF EXISTS projects;");
            stmt.execute("DROP TABLE IF EXISTS users;");

            // This code creates users table
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS users (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    fullname TEXT NOT NULL,
                    username TEXT UNIQUE NOT NULL,
                    email TEXT UNIQUE NOT NULL,
                    password_hash TEXT NOT NULL
                )
            """);

            // This code creates the projects table
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS projects (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    title TEXT NOT NULL,
                    location TEXT NOT NULL,
                    day TEXT NOT NULL,
                    hourly_value REAL NOT NULL,
                    registered_slots INTEGER DEFAULT 0,
                    total_slots INTEGER NOT NULL,
                    enabled INTEGER DEFAULT 1
                )
            """);

            // This code updates registrations table schema
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS registrations (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    user_id INTEGER NOT NULL,
                    title TEXT NOT NULL,
                    location TEXT NOT NULL,
                    day TEXT NOT NULL,
                    hourly_value REAL NOT NULL,
                    hours INTEGER NOT NULL,
                    datetime TEXT NOT NULL,
                    total_value REAL NOT NULL,
                    FOREIGN KEY(user_id) REFERENCES users(id)
                )
            """);

            System.out.println("✅ Database initialized successfully.");
        } catch (SQLException e) {
            System.err.println("❌ Error initializing database: " + e.getMessage());
        }
    }

    // This code import projects from CSV
    public static void importProjectsFromCSV(String resourcePath) {
        String sql = """
            INSERT INTO projects
            (title, location, day, hourly_value, registered_slots, total_slots, enabled)
            VALUES (?, ?, ?, ?, ?, ?, ?)
        """;

        try (Connection conn = getConnection()) {

            InputStream is = Database.class.getResourceAsStream(resourcePath);
            if (is == null) {
                throw new Exception("CSV file not found: " + resourcePath);
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line;
            boolean firstLine = true;

            while ((line = br.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue;
                }

                String[] parts = line.split(",");
                if (parts.length < 6) {
                    System.err.println("⚠️ Skipping invalid row: " + line);
                    continue;
                }

                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, parts[0].trim());  // title
                    stmt.setString(2, parts[1].trim());  // location
                    stmt.setString(3, parts[2].trim());  // day
                    stmt.setDouble(4, Double.parseDouble(parts[3].trim())); // hourly_value
                    stmt.setInt(5, Integer.parseInt(parts[4].trim()));      // registered_slots
                    stmt.setInt(6, Integer.parseInt(parts[5].trim()));      // total_slots
                    // This code enables the project by default when imported from CSV
                    stmt.setInt(7, 1);                                     // enabled = 1
                    stmt.executeUpdate();
                }
            }

            System.out.println("✅ Projects imported successfully from " + resourcePath);
        } catch (Exception e) {
            System.err.println("❌ Error importing projects: " + e.getMessage());
        }
    }

    // This code adds a default test user if not already in DB
    public static void addDefaultUser() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.executeUpdate("""
                INSERT OR IGNORE INTO users (fullname, username, email, password_hash)
                VALUES ('Test User', 'arya', 'arya@example.com', '1234')
            """);
            System.out.println("👤 Default test user added (arya / 1234)");

        } catch (SQLException e) {
            System.err.println("⚠️ Error inserting default user: " + e.getMessage());
        }
    }
}