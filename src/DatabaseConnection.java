import java.sql.*;

public class DatabaseConnection {
    private String dbName;
    private String dbUrl;
    private String usr;
    private String pass;

    public Connection getConnection() throws SQLException {
        initialize();
        return DriverManager.getConnection(dbUrl, usr, pass);
    }

    public void initialize() {
        dbName = "astronout_db";
        dbUrl = "jdbc:mysql://localhost:3306/" + dbName;
        usr = "root";
        pass = "";
    }
}