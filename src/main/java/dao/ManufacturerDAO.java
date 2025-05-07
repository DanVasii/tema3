package dao;

import model.Manufacturer;
import org.example.tema3.DatabaseConfig;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ManufacturerDAO {

    public List<Manufacturer> getAllManufacturers() {
        List<Manufacturer> manufacturers = new ArrayList<>();
        String query = "SELECT * FROM manufacturer ORDER BY name";

        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Manufacturer manufacturer = mapResultSetToManufacturer(rs);
                manufacturers.add(manufacturer);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching manufacturers: " + e.getMessage());
        }

        return manufacturers;
    }

    public Manufacturer getManufacturerById(int manufacturerId) {
        String query = "SELECT * FROM manufacturer WHERE manufacturer_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, manufacturerId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToManufacturer(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching manufacturer by ID: " + e.getMessage());
        }

        return null;
    }

    public boolean addManufacturer(Manufacturer manufacturer) {
        String query = "INSERT INTO manufacturer (name, country, website) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, manufacturer.getName());
            pstmt.setString(2, manufacturer.getCountry());
            pstmt.setString(3, manufacturer.getWebsite());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                return false;
            }

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    manufacturer.setManufacturerId(generatedKeys.getInt(1));
                    return true;
                } else {
                    return false;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error adding manufacturer: " + e.getMessage());
            return false;
        }
    }

    public boolean updateManufacturer(Manufacturer manufacturer) {
        String query = "UPDATE manufacturer SET name = ?, country = ?, website = ? WHERE manufacturer_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, manufacturer.getName());
            pstmt.setString(2, manufacturer.getCountry());
            pstmt.setString(3, manufacturer.getWebsite());
            pstmt.setInt(4, manufacturer.getManufacturerId());

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error updating manufacturer: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteManufacturer(int manufacturerId) {
        String query = "DELETE FROM manufacturer WHERE manufacturer_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, manufacturerId);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting manufacturer: " + e.getMessage());
            return false;
        }
    }

    private Manufacturer mapResultSetToManufacturer(ResultSet rs) throws SQLException {
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setManufacturerId(rs.getInt("manufacturer_id"));
        manufacturer.setName(rs.getString("name"));
        manufacturer.setCountry(rs.getString("country"));
        manufacturer.setWebsite(rs.getString("website"));
        return manufacturer;
    }
}