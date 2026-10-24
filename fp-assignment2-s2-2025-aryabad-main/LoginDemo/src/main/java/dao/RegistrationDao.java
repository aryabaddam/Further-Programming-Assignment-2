package dao;

import model.Project;
import model.User;
import java.sql.SQLException;
import java.util.List;

public interface RegistrationDao {
    void setup() throws SQLException;
    void saveRegistrations(User user, List<Project> projects) throws SQLException;
}