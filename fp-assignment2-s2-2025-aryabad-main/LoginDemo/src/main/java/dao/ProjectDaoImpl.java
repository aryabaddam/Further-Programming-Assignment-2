package dao;

import model.Project;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

// This code implements the ProjectDao interface to interact with the projects database table
public class ProjectDaoImpl implements ProjectDao {
    private final Connection conn;

    // This code acts as a constructor to accept a connection
    public ProjectDaoImpl(Connection conn) {
        this.conn = conn;
    }

    // This code retrieves a list of enabled projects from the database
    @Override
    public List<Project> getEnabledProjects() throws SQLException {
        List<Project> projects = new ArrayList<>();
        String sql = "SELECT * FROM projects WHERE enabled = 1";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            // This code loads each enabled project from the ResultSet into a Project object
            while (rs.next()) {
                Project project = new Project(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("location"),
                        rs.getString("day"),
                        rs.getDouble("hourly_value"),
                        rs.getInt("registered_slots"),
                        rs.getInt("total_slots"),
                        rs.getInt("enabled") == 1
                );
                projects.add(project);
            }
        }
        return projects;
    }
}
