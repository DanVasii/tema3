package dao;

import model.ShoeType;
import org.example.tema3.DatabaseConfig;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ShoeTypeDAO {

    public List<ShoeType> getAllShoeTypes() {
        List<ShoeType> types = new ArrayList<>();
        String query = "SELECT * FROM shoe_type ORDER BY name";

        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                ShoeType type = mapResultSetToShoeType(rs);
                types.add(type);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching shoe types: " + e.getMessage());
        }

        return types;
    }

    public ShoeType getShoeTypeById(int typeId) {
        String query = "SELECT * FROM shoe_type WHERE type_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, typeId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToShoeType(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching shoe type by ID: " + e.getMessage());
        }

        return null;
    }

    public boolean addShoeType(ShoeType shoeType) {
        String query = "INSERT INTO shoe_type (name) VALUES (?)";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, shoeType.getName());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                return false;
            }

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    shoeType.setTypeId(generatedKeys.getInt(1));
                    return true;
                } else {
                    return false;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error adding shoe type: " + e.getMessage());
            return false;
        }
    }

    public boolean updateShoeType(ShoeType shoeType) {
        String query = "UPDATE shoe_type SET name = ? WHERE type_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, shoeType.getName());
            pstmt.setInt(2, shoeType.getTypeId());

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error updating shoe type: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteShoeType(int typeId) {
        String query = "DELETE FROM shoe_type WHERE type_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, typeId);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting shoe type: " + e.getMessage());
            return false;
        }
    }

    private ShoeType mapResultSetToShoeType(ResultSet rs) throws SQLException {
        ShoeType shoeType = new ShoeType();
        shoeType.setTypeId(rs.getInt("type_id"));
        shoeType.setName(rs.getString("name"));
        return shoeType;
    }
}