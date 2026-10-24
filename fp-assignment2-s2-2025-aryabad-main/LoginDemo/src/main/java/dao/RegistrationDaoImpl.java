package dao;

import model.Project;
import model.User;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

// This code defines the DAO implementation for registration-related database operations.
public class RegistrationDaoImpl implements RegistrationDao {
    private final Connection conn;

    // This code initializes the DAO with a database connection.
    public RegistrationDaoImpl(Connection conn) {
        this.conn = conn;
    }

    // This code sets up the database schema for registrations, recreating the table as needed.
    @Override
    public void setup() throws SQLException {
        File dbFile = new File("application.db");
        if (dbFile.exists()) {
            if (dbFile.delete()) {
                System.out.println("⚠️ Existing database deleted to rebuild schema.");
            } else {
                System.out.println("⚠️ Failed to delete existing database.");
            }
        }
        try (Statement stmt = conn.createStatement()) {
            // This code drops the table to remove any outdated schema
            stmt.executeUpdate("DROP TABLE IF EXISTS registrations");

            // This code ensures the table is rebuilt correctly when schema mismatches occur
            String sql = "CREATE TABLE registrations (" +
                         "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                         "user_id INTEGER NOT NULL, " +
                         "title TEXT NOT NULL, " +
                         "location TEXT NOT NULL, " +
                         "day TEXT NOT NULL, " +
                         "hourly_value REAL NOT NULL, " +
                         "hours INTEGER NOT NULL, " +
                         "datetime TEXT NOT NULL, " +
                         "total_value REAL NOT NULL)";
            stmt.executeUpdate(sql);
            System.out.println("✅ 'registrations' table recreated successfully with correct schema (includes 'title' column).");
        }
    }

    // This code saves a list of project registrations for a user into the database.
    @Override
    public void saveRegistrations(User user, List<Project> projects) throws SQLException {
        String insertSQL = "INSERT INTO registrations (user_id, title, location, day, hourly_value, hours, datetime, total_value) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(insertSQL)) {
            for (Project p : projects) {
                stmt.setInt(1, user.getId());
                stmt.setString(2, p.getTitle());
                stmt.setString(3, p.getLocation());
                stmt.setString(4, p.getDay());
                stmt.setDouble(5, p.getHourlyValue());
                stmt.setInt(6, p.getVolunteerHours());
                stmt.setString(7, new java.sql.Timestamp(System.currentTimeMillis()).toString());
                double totalValue = p.getHourlyValue() * p.getVolunteerHours();
                stmt.setDouble(8, totalValue);
                stmt.addBatch();
            }
            // This code executes the batch insertion of all project registrations.
            stmt.executeBatch();
        }
    }
}