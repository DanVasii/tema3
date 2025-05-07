package dao;

import model.ShoeVariant;
import org.example.tema3.DatabaseConfig;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ShoeVariantDAO {

    private ShoeDAO shoeDAO;
    private ColorDAO colorDAO;

    public ShoeVariantDAO() {
        this.shoeDAO = new ShoeDAO();
        this.colorDAO = new ColorDAO();
    }

    public List<ShoeVariant> getAllVariants() {
        List<ShoeVariant> variants = new ArrayList<>();
        String query = "SELECT * FROM shoe_variant";

        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                ShoeVariant variant = mapResultSetToVariant(rs);
                variants.add(variant);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching shoe variants: " + e.getMessage());
        }

        return variants;
    }

    public ShoeVariant getVariantById(int variantId) {
        String query = "SELECT * FROM shoe_variant WHERE variant_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, variantId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToVariant(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching shoe variant by ID: " + e.getMessage());
        }

        return null;
    }

    public List<ShoeVariant> getVariantsByShoeId(int shoeId) {
        List<ShoeVariant> variants = new ArrayList<>();
        String query = "SELECT * FROM shoe_variant WHERE shoe_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, shoeId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    ShoeVariant variant = mapResultSetToVariant(rs);
                    variants.add(variant);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching shoe variants by shoe ID: " + e.getMessage());
        }

        return variants;
    }

    public boolean addVariant(ShoeVariant variant) {
        String query = "INSERT INTO shoe_variant (shoe_id, color_id, image_path) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, variant.getShoeId());
            pstmt.setInt(2, variant.getColorId());
            pstmt.setString(3, variant.getImagePath());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                return false;
            }

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    variant.setVariantId(generatedKeys.getInt(1));
                    return true;
                } else {
                    return false;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error adding shoe variant: " + e.getMessage());
            return false;
        }
    }

    public boolean updateVariant(ShoeVariant variant) {
        String query = "UPDATE shoe_variant SET shoe_id = ?, color_id = ?, image_path = ? WHERE variant_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, variant.getShoeId());
            pstmt.setInt(2, variant.getColorId());
            pstmt.setString(3, variant.getImagePath());
            pstmt.setInt(4, variant.getVariantId());

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error updating shoe variant: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteVariant(int variantId) {
        String query = "DELETE FROM shoe_variant WHERE variant_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, variantId);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting shoe variant: " + e.getMessage());
            return false;
        }
    }

    private ShoeVariant mapResultSetToVariant(ResultSet rs) throws SQLException {
        ShoeVariant variant = new ShoeVariant();
        variant.setVariantId(rs.getInt("variant_id"));
        variant.setShoeId(rs.getInt("shoe_id"));
        variant.setColorId(rs.getInt("color_id"));
        variant.setImagePath(rs.getString("image_path"));

        // Încărcare date asociate
        variant.setShoe(shoeDAO.getShoeById(variant.getShoeId()));
        variant.setColor(colorDAO.getColorById(variant.getColorId()));

        return variant;
    }
}