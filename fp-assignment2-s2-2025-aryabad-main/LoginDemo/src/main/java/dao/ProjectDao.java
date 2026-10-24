package dao;

import model.Project;
import java.sql.SQLException;
import java.util.List;

public interface ProjectDao {
    List<Project> getEnabledProjects() throws SQLException;
}
