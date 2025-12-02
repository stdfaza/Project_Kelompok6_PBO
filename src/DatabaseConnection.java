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

    // --- SAVE SYSTEM UPDATED ---

    // 1. Cek apakah ada save data
    public boolean hasSaveData(int userId) {
        try (Connection conn = getConnection()) {
            String query = "SELECT save_id FROM game_saves WHERE user_id = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 2. Cek apakah save data tersebut SUDAH SELESAI (Menang/Kalah)
    public boolean isGameFinished(int userId) {
        try (Connection conn = getConnection()) {
            String query = "SELECT is_finished FROM game_saves WHERE user_id = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getBoolean("is_finished");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false; 
    }

    // 3. Save Game (Update dengan status isFinished)
    public void saveGame(int userId, int day, int oxygen, int food, int power, boolean isFinished) {
        try (Connection conn = getConnection()) {
            String query = "INSERT INTO game_saves (user_id, day_count, oxygen_level, food_level, power_level, is_finished) " +
                           "VALUES (?, ?, ?, ?, ?, ?) " +
                           "ON DUPLICATE KEY UPDATE " +
                           "day_count = VALUES(day_count), " +
                           "oxygen_level = VALUES(oxygen_level), " +
                           "food_level = VALUES(food_level), " +
                           "power_level = VALUES(power_level), " +
                           "is_finished = VALUES(is_finished)";
            
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, userId);
            ps.setInt(2, day);
            ps.setInt(3, oxygen);
            ps.setInt(4, food);
            ps.setInt(5, power);
            ps.setBoolean(6, isFinished); // Parameter baru
            
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 4. Load Game
    public int[] loadGame(int userId) {
        int[] data = null;
        try (Connection conn = getConnection()) {
            String query = "SELECT day_count, oxygen_level, food_level, power_level FROM game_saves WHERE user_id = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                data = new int[4];
                data[0] = rs.getInt("day_count");
                data[1] = rs.getInt("oxygen_level");
                data[2] = rs.getInt("food_level");
                data[3] = rs.getInt("power_level");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return data;
    }
}