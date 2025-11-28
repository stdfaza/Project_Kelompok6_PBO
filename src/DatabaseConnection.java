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

    public boolean registerUser(String username, String password){
        try (Connection conn = getConnection()) {
            String query = "INSERT INTO astronout_user (astronout_username, astronout_password) VALUES (?, ?)";
            PreparedStatement ps = conn.prepareStatement(query);

            ps.setString(1, username);
            ps.setString(2, password);
            ps.executeUpdate();

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public int loginUser(String username, String password) {
        int astronoutId = 0;

        try (Connection conn = getConnection()) {
            String query = "SELECT astronout_id FROM astronout_user WHERE astronout_username = ? AND astronout_password = ?";
            PreparedStatement ps = conn.prepareStatement(query);

            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                astronoutId =  rs.getInt("astronout_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return astronoutId;
    }
}