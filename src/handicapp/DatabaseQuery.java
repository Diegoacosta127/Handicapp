package handicapp;
import java.sql.*;
import java.util.Properties;
import javax.swing.JOptionPane;

public class DatabaseQuery {
    private Connection conn;

    public DatabaseQuery() {
        try {
            Properties props = new Properties();
            props.setProperty("user", System.getProperty("user.name"));
            conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/handicapp", props);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error connecting to data base: " + ex,
                                          "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public ResultSet selectQuery(String query) throws SQLException {
        Statement stmt = conn.createStatement();
        return stmt.executeQuery(query);
    }

    public int updateQuery(String query) throws SQLException {
        Statement stmt = conn.createStatement();
        int rowsAffected = stmt.executeUpdate(query);
        stmt.close();
        return rowsAffected;
    }
}
