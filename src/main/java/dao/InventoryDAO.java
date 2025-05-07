package dao;

import model.Color;
import model.Inventory;
import model.Shoe;
import model.ShoeVariant;
import model.Size;
import model.Store;
import org.example.tema3.DatabaseConfig;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InventoryDAO {

    private StoreDAO storeDAO;
    private ShoeVariantDAO variantDAO;
    private SizeDAO sizeDAO;

    public InventoryDAO() {
        this.storeDAO = new StoreDAO();
        this.variantDAO = new ShoeVariantDAO();
        this.sizeDAO = new SizeDAO();
    }

    public List<Inventory> getAllInventory() {
        List<Inventory> inventoryList = new ArrayList<>();
        String query = "SELECT * FROM inventory";

        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Inventory inventory = mapResultSetToInventory(rs);
                inventoryList.add(inventory);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching inventory: " + e.getMessage());
        }

        return inventoryList;
    }

    public List<Inventory> getInventoryByStore(int storeId) {
        List<Inventory> inventoryList = new ArrayList<>();
        String query = "SELECT * FROM inventory WHERE store_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, storeId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Inventory inventory = mapResultSetToInventory(rs);
                    inventoryList.add(inventory);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching inventory by store: " + e.getMessage());
        }

        return inventoryList;
    }

    public List<Inventory> getOutOfStockItemsByStore(int storeId) {
        List<Inventory> outOfStockList = new ArrayList<>();
        String query = "SELECT i.* FROM inventory i " +
                "JOIN shoe_variant sv ON i.variant_id = sv.variant_id " +
                "JOIN shoe s ON sv.shoe_id = s.shoe_id " +
                "WHERE i.store_id = ? AND i.stock = 0";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, storeId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Inventory inventory = mapResultSetToInventory(rs);
                    outOfStockList.add(inventory);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching out of stock items: " + e.getMessage());
        }

        return outOfStockList;
    }

    public List<Inventory> filterInventoryByAvailability(int storeId, boolean available) {
        List<Inventory> filteredList = new ArrayList<>();
        String query = "SELECT * FROM inventory WHERE store_id = ? AND stock " + (available ? "> 0" : "= 0");

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, storeId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Inventory inventory = mapResultSetToInventory(rs);
                    filteredList.add(inventory);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error filtering inventory by availability: " + e.getMessage());
        }

        return filteredList;
    }

    public List<Inventory> filterInventoryByType(int storeId, int typeId) {
        List<Inventory> filteredList = new ArrayList<>();
        String query = "SELECT i.* FROM inventory i " +
                "JOIN shoe_variant sv ON i.variant_id = sv.variant_id " +
                "JOIN shoe s ON sv.shoe_id = s.shoe_id " +
                "WHERE i.store_id = ? AND s.type_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, storeId);
            pstmt.setInt(2, typeId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Inventory inventory = mapResultSetToInventory(rs);
                    filteredList.add(inventory);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error filtering inventory by type: " + e.getMessage());
        }

        return filteredList;
    }

    public List<Inventory> filterInventoryByColor(int storeId, int colorId) {
        List<Inventory> filteredList = new ArrayList<>();
        String query = "SELECT i.* FROM inventory i " +
                "JOIN shoe_variant sv ON i.variant_id = sv.variant_id " +
                "WHERE i.store_id = ? AND sv.color_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, storeId);
            pstmt.setInt(2, colorId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Inventory inventory = mapResultSetToInventory(rs);
                    filteredList.add(inventory);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error filtering inventory by color: " + e.getMessage());
        }

        return filteredList;
    }

    public List<Inventory> filterInventoryByManufacturer(int storeId, int manufacturerId) {
        List<Inventory> filteredList = new ArrayList<>();
        String query = "SELECT i.* FROM inventory i " +
                "JOIN shoe_variant sv ON i.variant_id = sv.variant_id " +
                "JOIN shoe s ON sv.shoe_id = s.shoe_id " +
                "WHERE i.store_id = ? AND s.manufacturer_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, storeId);
            pstmt.setInt(2, manufacturerId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Inventory inventory = mapResultSetToInventory(rs);
                    filteredList.add(inventory);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error filtering inventory by manufacturer: " + e.getMessage());
        }

        return filteredList;
    }

    public List<Inventory> searchInventoryByModel(String modelName) {
        List<Inventory> searchResults = new ArrayList<>();
        String query = "SELECT i.* FROM inventory i " +
                "JOIN shoe_variant sv ON i.variant_id = sv.variant_id " +
                "JOIN shoe s ON sv.shoe_id = s.shoe_id " +
                "WHERE s.model LIKE ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, "%" + modelName + "%");

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Inventory inventory = mapResultSetToInventory(rs);
                    searchResults.add(inventory);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error searching inventory by model: " + e.getMessage());
        }

        return searchResults;
    }

    public boolean addInventory(Inventory inventory) {
        String query = "INSERT INTO inventory (store_id, variant_id, size_id, stock) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, inventory.getStoreId());
            pstmt.setInt(2, inventory.getVariantId());
            pstmt.setInt(3, inventory.getSizeId());
            pstmt.setInt(4, inventory.getStock());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                return false;
            }

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    inventory.setInventoryId(generatedKeys.getInt(1));
                    return true;
                } else {
                    return false;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error adding inventory: " + e.getMessage());
            return false;
        }
    }

    public boolean updateInventory(Inventory inventory) {
        String query = "UPDATE inventory SET store_id = ?, variant_id = ?, size_id = ?, stock = ? WHERE inventory_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, inventory.getStoreId());
            pstmt.setInt(2, inventory.getVariantId());
            pstmt.setInt(3, inventory.getSizeId());
            pstmt.setInt(4, inventory.getStock());
            pstmt.setInt(5, inventory.getInventoryId());

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error updating inventory: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteInventory(int inventoryId) {
        String query = "DELETE FROM inventory WHERE inventory_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, inventoryId);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting inventory: " + e.getMessage());
            return false;
        }
    }

    public int getTotalInventoryCount() {
        String query = "SELECT COUNT(*) FROM inventory";

        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error getting total inventory count: " + e.getMessage());
        }

        return 0;
    }

    public Map<String, Integer> getInventoryCountByStore() {
        Map<String, Integer> storeData = new HashMap<>();
        String query = "SELECT s.name, COUNT(i.inventory_id) FROM inventory i " +
                "JOIN store s ON i.store_id = s.store_id " +
                "GROUP BY i.store_id";

        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                storeData.put(rs.getString(1), rs.getInt(2));
            }
        } catch (SQLException e) {
            System.err.println("Error getting inventory count by store: " + e.getMessage());
        }

        return storeData;
    }

    public Map<String, Integer> getInventoryCountByColor() {
        Map<String, Integer> colorData = new HashMap<>();
        String query = "SELECT c.name, COUNT(i.inventory_id) FROM inventory i " +
                "JOIN shoe_variant sv ON i.variant_id = sv.variant_id " +
                "JOIN color c ON sv.color_id = c.color_id " +
                "GROUP BY c.color_id";

        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                colorData.put(rs.getString(1), rs.getInt(2));
            }
        } catch (SQLException e) {
            System.err.println("Error getting inventory count by color: " + e.getMessage());
        }

        return colorData;
    }

    public Map<String, Integer> getOutOfStockCountByStore() {
        Map<String, Integer> outOfStockData = new HashMap<>();
        String query = "SELECT s.name, COUNT(i.inventory_id) FROM inventory i " +
                "JOIN store s ON i.store_id = s.store_id " +
                "WHERE i.stock = 0 " +
                "GROUP BY i.store_id";

        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                outOfStockData.put(rs.getString(1), rs.getInt(2));
            }
        } catch (SQLException e) {
            System.err.println("Error getting out of stock count by store: " + e.getMessage());
        }

        return outOfStockData;
    }

    private Inventory mapResultSetToInventory(ResultSet rs) throws SQLException {
        Inventory inventory = new Inventory();
        inventory.setInventoryId(rs.getInt("inventory_id"));
        inventory.setStoreId(rs.getInt("store_id"));
        inventory.setVariantId(rs.getInt("variant_id"));
        inventory.setSizeId(rs.getInt("size_id"));
        inventory.setStock(rs.getInt("stock"));

        // Încărcare date asociate
        inventory.setStore(storeDAO.getStoreById(inventory.getStoreId()));
        inventory.setVariant(variantDAO.getVariantById(inventory.getVariantId()));
        inventory.setSize(sizeDAO.getSizeById(inventory.getSizeId()));

        return inventory;
    }
}