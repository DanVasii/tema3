package dao;

import model.Store;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import org.example.tema3.DatabaseConfig;

public class StoreDAO {

    public List<Store> getAllStores() {
        List<Store> stores = new ArrayList<>();
        String query = "SELECT * FROM store ORDER BY name";

        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Store store = mapResultSetToStore(rs);
                stores.add(store);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching stores: " + e.getMessage());
        }

        return stores;
    }

    public Store getStoreById(int storeId) {
        String query = "SELECT * FROM store WHERE store_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, storeId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToStore(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching store by ID: " + e.getMessage());
        }

        return null;
    }

    public boolean addStore(Store store) {
        String query = "INSERT INTO store (name, address, phone, email, opening_hours) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, store.getName());
            pstmt.setString(2, store.getAddress());
            pstmt.setString(3, store.getPhone());
            pstmt.setString(4, store.getEmail());
            pstmt.setString(5, store.getOpeningHours());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                return false;
            }

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    store.setStoreId(generatedKeys.getInt(1));
                    return true;
                } else {
                    return false;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error adding store: " + e.getMessage());
            return false;
        }
    }

    public boolean updateStore(Store store) {
        String query = "UPDATE store SET name = ?, address = ?, phone = ?, email = ?, opening_hours = ? WHERE store_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, store.getName());
            pstmt.setString(2, store.getAddress());
            pstmt.setString(3, store.getPhone());
            pstmt.setString(4, store.getEmail());
            pstmt.setString(5, store.getOpeningHours());
            pstmt.setInt(6, store.getStoreId());

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error updating store: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteStore(int storeId) {
        String query = "DELETE FROM store WHERE store_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, storeId);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting store: " + e.getMessage());
            return false;
        }
    }

    private Store mapResultSetToStore(ResultSet rs) throws SQLException {
        Store store = new Store();
        store.setStoreId(rs.getInt("store_id"));
        store.setName(rs.getString("name"));
        store.setAddress(rs.getString("address"));
        store.setPhone(rs.getString("phone"));
        store.setEmail(rs.getString("email"));
        store.setOpeningHours(rs.getString("opening_hours"));
        return store;
    }
}