package dao;

import model.Manufacturer;
import model.Shoe;
import model.ShoeType;
import org.example.tema3.DatabaseConfig;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShoeDAO {

    private ManufacturerDAO manufacturerDAO;
    private ShoeTypeDAO shoeTypeDAO;

    public ShoeDAO() {
        this.manufacturerDAO = new ManufacturerDAO();
        this.shoeTypeDAO = new ShoeTypeDAO();
    }

    public List<Shoe> getAllShoes() {
        List<Shoe> shoes = new ArrayList<>();
        String query = "SELECT * FROM shoe ORDER BY model";

        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Shoe shoe = mapResultSetToShoe(rs);
                shoes.add(shoe);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching shoes: " + e.getMessage());
        }

        return shoes;
    }

    public Shoe getShoeById(int shoeId) {
        String query = "SELECT * FROM shoe WHERE shoe_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, shoeId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToShoe(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching shoe by ID: " + e.getMessage());
        }

        return null;
    }

    public List<Shoe> getShoesByManufacturer(int manufacturerId) {
        List<Shoe> shoes = new ArrayList<>();
        String query = "SELECT * FROM shoe WHERE manufacturer_id = ? ORDER BY model";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, manufacturerId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Shoe shoe = mapResultSetToShoe(rs);
                    shoes.add(shoe);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching shoes by manufacturer: " + e.getMessage());
        }

        return shoes;
    }

    public List<Shoe> getShoesByType(int typeId) {
        List<Shoe> shoes = new ArrayList<>();
        String query = "SELECT * FROM shoe WHERE type_id = ? ORDER BY model";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, typeId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Shoe shoe = mapResultSetToShoe(rs);
                    shoes.add(shoe);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching shoes by type: " + e.getMessage());
        }

        return shoes;
    }

    public List<Shoe> searchShoesByModel(String modelName) {
        List<Shoe> shoes = new ArrayList<>();
        String query = "SELECT * FROM shoe WHERE model LIKE ? ORDER BY model";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, "%" + modelName + "%");

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Shoe shoe = mapResultSetToShoe(rs);
                    shoes.add(shoe);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error searching shoes by model: " + e.getMessage());
        }

        return shoes;
    }

    public boolean addShoe(Shoe shoe) {
        String query = "INSERT INTO shoe (model, manufacturer_id, type_id, price, description) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, shoe.getModel());
            pstmt.setInt(2, shoe.getManufacturerId());
            pstmt.setInt(3, shoe.getTypeId());
            pstmt.setDouble(4, shoe.getPrice());
            pstmt.setString(5, shoe.getDescription());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                return false;
            }

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    shoe.setShoeId(generatedKeys.getInt(1));
                    return true;
                } else {
                    return false;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error adding shoe: " + e.getMessage());
            return false;
        }
    }

    public boolean updateShoe(Shoe shoe) {
        String query = "UPDATE shoe SET model = ?, manufacturer_id = ?, type_id = ?, price = ?, description = ? WHERE shoe_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, shoe.getModel());
            pstmt.setInt(2, shoe.getManufacturerId());
            pstmt.setInt(3, shoe.getTypeId());
            pstmt.setDouble(4, shoe.getPrice());
            pstmt.setString(5, shoe.getDescription());
            pstmt.setInt(6, shoe.getShoeId());

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error updating shoe: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteShoe(int shoeId) {
        String query = "DELETE FROM shoe WHERE shoe_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, shoeId);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting shoe: " + e.getMessage());
            return false;
        }
    }

    public Map<String, Integer> getShoeCountByType() {
        Map<String, Integer> typeData = new HashMap<>();
        String query = "SELECT t.name, COUNT(s.shoe_id) FROM shoe s " +
                "JOIN shoe_type t ON s.type_id = t.type_id " +
                "GROUP BY s.type_id";

        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                typeData.put(rs.getString(1), rs.getInt(2));
            }
        } catch (SQLException e) {
            System.err.println("Error getting shoe count by type: " + e.getMessage());
        }

        return typeData;
    }

    public Map<String, Integer> getShoeCountByManufacturer() {
        Map<String, Integer> manufacturerData = new HashMap<>();
        String query = "SELECT m.name, COUNT(s.shoe_id) FROM shoe s " +
                "JOIN manufacturer m ON s.manufacturer_id = m.manufacturer_id " +
                "GROUP BY s.manufacturer_id";

        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                manufacturerData.put(rs.getString(1), rs.getInt(2));
            }
        } catch (SQLException e) {
            System.err.println("Error getting shoe count by manufacturer: " + e.getMessage());
        }

        return manufacturerData;
    }

    private Shoe mapResultSetToShoe(ResultSet rs) throws SQLException {
        Shoe shoe = new Shoe();
        shoe.setShoeId(rs.getInt("shoe_id"));
        shoe.setModel(rs.getString("model"));
        shoe.setManufacturerId(rs.getInt("manufacturer_id"));
        shoe.setTypeId(rs.getInt("type_id"));
        shoe.setPrice(rs.getDouble("price"));
        shoe.setDescription(rs.getString("description"));

        // Încărcare date asociate
        shoe.setManufacturer(manufacturerDAO.getManufacturerById(shoe.getManufacturerId()));
        shoe.setType(shoeTypeDAO.getShoeTypeById(shoe.getTypeId()));

        return shoe;
    }
}