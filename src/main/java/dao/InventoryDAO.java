package dao;

import model.Color;
import model.Inventory;
import model.Manufacturer;
import model.Shoe;
import model.ShoeType;
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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class InventoryDAO {

    public List<Inventory> getAllInventory() {
        List<Inventory> inventoryList = new ArrayList<>();
        String query = "SELECT * FROM inventory";

        // Step 1: Load basic inventory data
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Inventory inventory = new Inventory();
                inventory.setInventoryId(rs.getInt("inventory_id"));
                inventory.setStoreId(rs.getInt("store_id"));
                inventory.setVariantId(rs.getInt("variant_id"));
                inventory.setSizeId(rs.getInt("size_id"));
                inventory.setStock(rs.getInt("stock"));
                inventoryList.add(inventory);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching inventory: " + e.getMessage());
            return inventoryList;
        }

        // Step 2: Load associated data
        loadAssociatedData(inventoryList);

        return inventoryList;
    }

    public List<Inventory> getInventoryByStore(int storeId) {
        List<Inventory> inventoryList = new ArrayList<>();
        String query = "SELECT * FROM inventory WHERE store_id = ?";

        // Step 1: Load basic inventory data
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, storeId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Inventory inventory = new Inventory();
                    inventory.setInventoryId(rs.getInt("inventory_id"));
                    inventory.setStoreId(rs.getInt("store_id"));
                    inventory.setVariantId(rs.getInt("variant_id"));
                    inventory.setSizeId(rs.getInt("size_id"));
                    inventory.setStock(rs.getInt("stock"));
                    inventoryList.add(inventory);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching inventory by store: " + e.getMessage());
            return inventoryList;
        }

        // Step 2: Load associated data
        loadAssociatedData(inventoryList);

        return inventoryList;
    }

    public List<Inventory> getOutOfStockItemsByStore(int storeId) {
        List<Inventory> outOfStockList = new ArrayList<>();
        String query = "SELECT i.* FROM inventory i " +
                "JOIN shoe_variant sv ON i.variant_id = sv.variant_id " +
                "JOIN shoe s ON sv.shoe_id = s.shoe_id " +
                "WHERE i.store_id = ? AND i.stock = 0";

        // Step 1: Load basic inventory data
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, storeId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Inventory inventory = new Inventory();
                    inventory.setInventoryId(rs.getInt("inventory_id"));
                    inventory.setStoreId(rs.getInt("store_id"));
                    inventory.setVariantId(rs.getInt("variant_id"));
                    inventory.setSizeId(rs.getInt("size_id"));
                    inventory.setStock(rs.getInt("stock"));
                    outOfStockList.add(inventory);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching out of stock items: " + e.getMessage());
            return outOfStockList;
        }

        // Step 2: Load associated data
        loadAssociatedData(outOfStockList);

        return outOfStockList;
    }

    public List<Inventory> filterInventoryByAvailability(int storeId, boolean available) {
        List<Inventory> filteredList = new ArrayList<>();
        String query = "SELECT * FROM inventory WHERE store_id = ? AND stock " + (available ? "> 0" : "= 0");

        // Step 1: Load basic inventory data
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, storeId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Inventory inventory = new Inventory();
                    inventory.setInventoryId(rs.getInt("inventory_id"));
                    inventory.setStoreId(rs.getInt("store_id"));
                    inventory.setVariantId(rs.getInt("variant_id"));
                    inventory.setSizeId(rs.getInt("size_id"));
                    inventory.setStock(rs.getInt("stock"));
                    filteredList.add(inventory);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error filtering inventory by availability: " + e.getMessage());
            return filteredList;
        }

        // Step 2: Load associated data
        loadAssociatedData(filteredList);

        return filteredList;
    }

    public List<Inventory> filterInventoryByType(int storeId, int typeId) {
        List<Inventory> filteredList = new ArrayList<>();
        String query = "SELECT i.* FROM inventory i " +
                "JOIN shoe_variant sv ON i.variant_id = sv.variant_id " +
                "JOIN shoe s ON sv.shoe_id = s.shoe_id " +
                "WHERE i.store_id = ? AND s.type_id = ?";

        // Step 1: Load basic inventory data
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, storeId);
            pstmt.setInt(2, typeId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Inventory inventory = new Inventory();
                    inventory.setInventoryId(rs.getInt("inventory_id"));
                    inventory.setStoreId(rs.getInt("store_id"));
                    inventory.setVariantId(rs.getInt("variant_id"));
                    inventory.setSizeId(rs.getInt("size_id"));
                    inventory.setStock(rs.getInt("stock"));
                    filteredList.add(inventory);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error filtering inventory by type: " + e.getMessage());
            return filteredList;
        }

        // Step 2: Load associated data
        loadAssociatedData(filteredList);

        return filteredList;
    }

    public List<Inventory> filterInventoryByColor(int storeId, int colorId) {
        List<Inventory> filteredList = new ArrayList<>();
        String query = "SELECT i.* FROM inventory i " +
                "JOIN shoe_variant sv ON i.variant_id = sv.variant_id " +
                "WHERE i.store_id = ? AND sv.color_id = ?";

        // Step 1: Load basic inventory data
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, storeId);
            pstmt.setInt(2, colorId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Inventory inventory = new Inventory();
                    inventory.setInventoryId(rs.getInt("inventory_id"));
                    inventory.setStoreId(rs.getInt("store_id"));
                    inventory.setVariantId(rs.getInt("variant_id"));
                    inventory.setSizeId(rs.getInt("size_id"));
                    inventory.setStock(rs.getInt("stock"));
                    filteredList.add(inventory);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error filtering inventory by color: " + e.getMessage());
            return filteredList;
        }

        // Step 2: Load associated data
        loadAssociatedData(filteredList);

        return filteredList;
    }

    public List<Inventory> filterInventoryByManufacturer(int storeId, int manufacturerId) {
        List<Inventory> filteredList = new ArrayList<>();
        String query = "SELECT i.* FROM inventory i " +
                "JOIN shoe_variant sv ON i.variant_id = sv.variant_id " +
                "JOIN shoe s ON sv.shoe_id = s.shoe_id " +
                "WHERE i.store_id = ? AND s.manufacturer_id = ?";

        // Step 1: Load basic inventory data
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, storeId);
            pstmt.setInt(2, manufacturerId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Inventory inventory = new Inventory();
                    inventory.setInventoryId(rs.getInt("inventory_id"));
                    inventory.setStoreId(rs.getInt("store_id"));
                    inventory.setVariantId(rs.getInt("variant_id"));
                    inventory.setSizeId(rs.getInt("size_id"));
                    inventory.setStock(rs.getInt("stock"));
                    filteredList.add(inventory);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error filtering inventory by manufacturer: " + e.getMessage());
            return filteredList;
        }

        // Step 2: Load associated data
        loadAssociatedData(filteredList);

        return filteredList;
    }

    public List<Inventory> searchInventoryByModel(String modelName) {
        List<Inventory> searchResults = new ArrayList<>();
        String query = "SELECT i.* FROM inventory i " +
                "JOIN shoe_variant sv ON i.variant_id = sv.variant_id " +
                "JOIN shoe s ON sv.shoe_id = s.shoe_id " +
                "WHERE s.model LIKE ?";

        // Step 1: Load basic inventory data
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, "%" + modelName + "%");

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Inventory inventory = new Inventory();
                    inventory.setInventoryId(rs.getInt("inventory_id"));
                    inventory.setStoreId(rs.getInt("store_id"));
                    inventory.setVariantId(rs.getInt("variant_id"));
                    inventory.setSizeId(rs.getInt("size_id"));
                    inventory.setStock(rs.getInt("stock"));
                    searchResults.add(inventory);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error searching inventory by model: " + e.getMessage());
            return searchResults;
        }

        // Step 2: Load associated data
        loadAssociatedData(searchResults);

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

    // Helper method to load all associated data for a list of inventory items
    private void loadAssociatedData(List<Inventory> inventoryList) {
        if (inventoryList.isEmpty()) return;

        // Collect all store IDs, variant IDs, and size IDs
        Set<Integer> storeIds = new HashSet<>();
        Set<Integer> variantIds = new HashSet<>();
        Set<Integer> sizeIds = new HashSet<>();

        for (Inventory inventory : inventoryList) {
            storeIds.add(inventory.getStoreId());
            variantIds.add(inventory.getVariantId());
            sizeIds.add(inventory.getSizeId());
        }

        // Load all stores, variants, and sizes
        Map<Integer, Store> stores = loadStoresByIds(storeIds);
        Map<Integer, ShoeVariant> variants = loadVariantsByIds(variantIds);
        Map<Integer, Size> sizes = loadSizesByIds(sizeIds);

        // Assign associated data to each inventory item
        for (Inventory inventory : inventoryList) {
            inventory.setStore(stores.get(inventory.getStoreId()));
            inventory.setVariant(variants.get(inventory.getVariantId()));
            inventory.setSize(sizes.get(inventory.getSizeId()));
        }
    }

    private Map<Integer, Store> loadStoresByIds(Set<Integer> storeIds) {
        Map<Integer, Store> storesMap = new HashMap<>();
        if (storeIds.isEmpty()) return storesMap;

        StringBuilder placeholders = new StringBuilder();
        for (int i = 0; i < storeIds.size(); i++) {
            if (i > 0) placeholders.append(",");
            placeholders.append("?");
        }

        String query = "SELECT * FROM store WHERE store_id IN (" + placeholders + ")";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            int paramIndex = 1;
            for (Integer id : storeIds) {
                pstmt.setInt(paramIndex++, id);
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Store store = new Store();
                    store.setStoreId(rs.getInt("store_id"));
                    store.setName(rs.getString("name"));
                    store.setAddress(rs.getString("address"));
                    store.setPhone(rs.getString("phone"));
                    store.setEmail(rs.getString("email"));
                    store.setOpeningHours(rs.getString("opening_hours"));
                    storesMap.put(store.getStoreId(), store);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error loading stores: " + e.getMessage());
        }

        return storesMap;
    }

    private Map<Integer, Size> loadSizesByIds(Set<Integer> sizeIds) {
        Map<Integer, Size> sizesMap = new HashMap<>();
        if (sizeIds.isEmpty()) return sizesMap;

        StringBuilder placeholders = new StringBuilder();
        for (int i = 0; i < sizeIds.size(); i++) {
            if (i > 0) placeholders.append(",");
            placeholders.append("?");
        }

        String query = "SELECT * FROM size WHERE size_id IN (" + placeholders + ")";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            int paramIndex = 1;
            for (Integer id : sizeIds) {
                pstmt.setInt(paramIndex++, id);
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Size size = new Size();
                    size.setSizeId(rs.getInt("size_id"));
                    size.setEuSize(rs.getDouble("eu_size"));
                    size.setUkSize(rs.getDouble("uk_size"));
                    size.setUsSize(rs.getDouble("us_size"));
                    sizesMap.put(size.getSizeId(), size);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error loading sizes: " + e.getMessage());
        }

        return sizesMap;
    }

    private Map<Integer, ShoeVariant> loadVariantsByIds(Set<Integer> variantIds) {
        Map<Integer, ShoeVariant> variantsMap = new HashMap<>();
        if (variantIds.isEmpty()) return variantsMap;

        StringBuilder placeholders = new StringBuilder();
        for (int i = 0; i < variantIds.size(); i++) {
            if (i > 0) placeholders.append(",");
            placeholders.append("?");
        }

        String query = "SELECT * FROM shoe_variant WHERE variant_id IN (" + placeholders + ")";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            int paramIndex = 1;
            for (Integer id : variantIds) {
                pstmt.setInt(paramIndex++, id);
            }

            Map<Integer, ShoeVariant> variants = new HashMap<>();
            Set<Integer> shoeIds = new HashSet<>();
            Set<Integer> colorIds = new HashSet<>();

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    ShoeVariant variant = new ShoeVariant();
                    variant.setVariantId(rs.getInt("variant_id"));
                    variant.setShoeId(rs.getInt("shoe_id"));
                    variant.setColorId(rs.getInt("color_id"));
                    variant.setImagePath(rs.getString("image_path"));
                    variants.put(variant.getVariantId(), variant);

                    shoeIds.add(variant.getShoeId());
                    colorIds.add(variant.getColorId());
                }
            }

            // Load all shoes and colors
            Map<Integer, Shoe> shoes = loadShoesByIds(shoeIds);
            Map<Integer, Color> colors = loadColorsByIds(colorIds);

            // Assign shoes and colors to variants
            for (ShoeVariant variant : variants.values()) {
                variant.setShoe(shoes.get(variant.getShoeId()));
                variant.setColor(colors.get(variant.getColorId()));
                variantsMap.put(variant.getVariantId(), variant);
            }
        } catch (SQLException e) {
            System.err.println("Error loading variants: " + e.getMessage());
        }

        return variantsMap;
    }

    private Map<Integer, Shoe> loadShoesByIds(Set<Integer> shoeIds) {
        Map<Integer, Shoe> shoesMap = new HashMap<>();
        if (shoeIds.isEmpty()) return shoesMap;

        StringBuilder placeholders = new StringBuilder();
        for (int i = 0; i < shoeIds.size(); i++) {
            if (i > 0) placeholders.append(",");
            placeholders.append("?");
        }

        String query = "SELECT * FROM shoe WHERE shoe_id IN (" + placeholders + ")";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            int paramIndex = 1;
            for (Integer id : shoeIds) {
                pstmt.setInt(paramIndex++, id);
            }

            Map<Integer, Shoe> shoes = new HashMap<>();
            Set<Integer> manufacturerIds = new HashSet<>();
            Set<Integer> typeIds = new HashSet<>();

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Shoe shoe = new Shoe();
                    shoe.setShoeId(rs.getInt("shoe_id"));
                    shoe.setModel(rs.getString("model"));
                    shoe.setManufacturerId(rs.getInt("manufacturer_id"));
                    shoe.setTypeId(rs.getInt("type_id"));
                    shoe.setPrice(rs.getDouble("price"));
                    shoe.setDescription(rs.getString("description"));
                    shoes.put(shoe.getShoeId(), shoe);

                    manufacturerIds.add(shoe.getManufacturerId());
                    typeIds.add(shoe.getTypeId());
                }
            }

            // Load all manufacturers and types
            Map<Integer, Manufacturer> manufacturers = loadManufacturersByIds(manufacturerIds);
            Map<Integer, ShoeType> types = loadShoeTypesByIds(typeIds);

            // Assign manufacturers and types to shoes
            for (Shoe shoe : shoes.values()) {
                shoe.setManufacturer(manufacturers.get(shoe.getManufacturerId()));
                shoe.setType(types.get(shoe.getTypeId()));
                shoesMap.put(shoe.getShoeId(), shoe);
            }
        } catch (SQLException e) {
            System.err.println("Error loading shoes: " + e.getMessage());
        }

        return shoesMap;
    }

    private Map<Integer, Manufacturer> loadManufacturersByIds(Set<Integer> manufacturerIds) {
        Map<Integer, Manufacturer> manufacturersMap = new HashMap<>();
        if (manufacturerIds.isEmpty()) return manufacturersMap;

        StringBuilder placeholders = new StringBuilder();
        for (int i = 0; i < manufacturerIds.size(); i++) {
            if (i > 0) placeholders.append(",");
            placeholders.append("?");
        }

        String query = "SELECT * FROM manufacturer WHERE manufacturer_id IN (" + placeholders + ")";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            int paramIndex = 1;
            for (Integer id : manufacturerIds) {
                pstmt.setInt(paramIndex++, id);
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Manufacturer manufacturer = new Manufacturer();
                    manufacturer.setManufacturerId(rs.getInt("manufacturer_id"));
                    manufacturer.setName(rs.getString("name"));
                    manufacturer.setCountry(rs.getString("country"));
                    manufacturer.setWebsite(rs.getString("website"));
                    manufacturersMap.put(manufacturer.getManufacturerId(), manufacturer);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error loading manufacturers: " + e.getMessage());
        }

        return manufacturersMap;
    }

    private Map<Integer, ShoeType> loadShoeTypesByIds(Set<Integer> typeIds) {
        Map<Integer, ShoeType> typesMap = new HashMap<>();
        if (typeIds.isEmpty()) return typesMap;

        StringBuilder placeholders = new StringBuilder();
        for (int i = 0; i < typeIds.size(); i++) {
            if (i > 0) placeholders.append(",");
            placeholders.append("?");
        }

        String query = "SELECT * FROM shoe_type WHERE type_id IN (" + placeholders + ")";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            int paramIndex = 1;
            for (Integer id : typeIds) {
                pstmt.setInt(paramIndex++, id);
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    ShoeType type = new ShoeType();
                    type.setTypeId(rs.getInt("type_id"));
                    type.setName(rs.getString("name"));
                    typesMap.put(type.getTypeId(), type);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error loading shoe types: " + e.getMessage());
        }

        return typesMap;
    }

    private Map<Integer, Color> loadColorsByIds(Set<Integer> colorIds) {
        Map<Integer, Color> colorsMap = new HashMap<>();
        if (colorIds.isEmpty()) return colorsMap;

        StringBuilder placeholders = new StringBuilder();
        for (int i = 0; i < colorIds.size(); i++) {
            if (i > 0) placeholders.append(",");
            placeholders.append("?");
        }

        String query = "SELECT * FROM color WHERE color_id IN (" + placeholders + ")";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            int paramIndex = 1;
            for (Integer id : colorIds) {
                pstmt.setInt(paramIndex++, id);
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Color color = new Color();
                    color.setColorId(rs.getInt("color_id"));
                    color.setName(rs.getString("name"));
                    color.setHexCode(rs.getString("hex_code"));
                    colorsMap.put(color.getColorId(), color);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error loading colors: " + e.getMessage());
        }

        return colorsMap;
    }
}