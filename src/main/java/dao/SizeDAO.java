package dao;

import model.Size;
import org.example.tema3.DatabaseConfig;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SizeDAO {

    public List<Size> getAllSizes() {
        List<Size> sizes = new ArrayList<>();
        String query = "SELECT * FROM size ORDER BY eu_size";

        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Size size = mapResultSetToSize(rs);
                sizes.add(size);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching sizes: " + e.getMessage());
        }

        return sizes;
    }

    public Size getSizeById(int sizeId) {
        String query = "SELECT * FROM size WHERE size_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, sizeId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToSize(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching size by ID: " + e.getMessage());
        }

        return null;
    }

    public boolean addSize(Size size) {
        String query = "INSERT INTO size (eu_size, uk_size, us_size) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setDouble(1, size.getEuSize());
            pstmt.setDouble(2, size.getUkSize());
            pstmt.setDouble(3, size.getUsSize());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                return false;
            }

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    size.setSizeId(generatedKeys.getInt(1));
                    return true;
                } else {
                    return false;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error adding size: " + e.getMessage());
            return false;
        }
    }

    public boolean updateSize(Size size) {
        String query = "UPDATE size SET eu_size = ?, uk_size = ?, us_size = ? WHERE size_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setDouble(1, size.getEuSize());
            pstmt.setDouble(2, size.getUkSize());
            pstmt.setDouble(3, size.getUsSize());
            pstmt.setInt(4, size.getSizeId());

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error updating size: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteSize(int sizeId) {
        String query = "DELETE FROM size WHERE size_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, sizeId);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting size: " + e.getMessage());
            return false;
        }
    }

    private Size mapResultSetToSize(ResultSet rs) throws SQLException {
        Size size = new Size();
        size.setSizeId(rs.getInt("size_id"));
        size.setEuSize(rs.getDouble("eu_size"));
        size.setUkSize(rs.getDouble("uk_size"));
        size.setUsSize(rs.getDouble("us_size"));
        return size;
    }
}