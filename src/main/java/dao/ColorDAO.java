package dao;

import model.Color;
import org.example.tema3.DatabaseConfig;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ColorDAO {

    public List<Color> getAllColors() {
        List<Color> colors = new ArrayList<>();
        String query = "SELECT * FROM color ORDER BY name";

        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Color color = mapResultSetToColor(rs);
                colors.add(color);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching colors: " + e.getMessage());
        }

        return colors;
    }

    public Color getColorById(int colorId) {
        String query = "SELECT * FROM color WHERE color_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, colorId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToColor(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching color by ID: " + e.getMessage());
        }

        return null;
    }

    public boolean addColor(Color color) {
        String query = "INSERT INTO color (name, hex_code) VALUES (?, ?)";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, color.getName());
            pstmt.setString(2, color.getHexCode());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                return false;
            }

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    color.setColorId(generatedKeys.getInt(1));
                    return true;
                } else {
                    return false;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error adding color: " + e.getMessage());
            return false;
        }
    }

    public boolean updateColor(Color color) {
        String query = "UPDATE color SET name = ?, hex_code = ? WHERE color_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, color.getName());
            pstmt.setString(2, color.getHexCode());
            pstmt.setInt(3, color.getColorId());

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error updating color: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteColor(int colorId) {
        String query = "DELETE FROM color WHERE color_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, colorId);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting color: " + e.getMessage());
            return false;
        }
    }

    private Color mapResultSetToColor(ResultSet rs) throws SQLException {
        Color color = new Color();
        color.setColorId(rs.getInt("color_id"));
        color.setName(rs.getString("name"));
        color.setHexCode(rs.getString("hex_code"));
        return color;
    }
}