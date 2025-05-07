package model;

public class ShoeVariant {
    private int variantId;
    private int shoeId;
    private int colorId;
    private String imagePath;

    // Pentru afișarea în UI
    private Shoe shoe;
    private Color color;

    public ShoeVariant() {
    }

    public ShoeVariant(int variantId, int shoeId, int colorId, String imagePath) {
        this.variantId = variantId;
        this.shoeId = shoeId;
        this.colorId = colorId;
        this.imagePath = imagePath;
    }

    // Getteri și setteri
    public int getVariantId() {
        return variantId;
    }

    public void setVariantId(int variantId) {
        this.variantId = variantId;
    }

    public int getShoeId() {
        return shoeId;
    }

    public void setShoeId(int shoeId) {
        this.shoeId = shoeId;
    }

    public int getColorId() {
        return colorId;
    }

    public void setColorId(int colorId) {
        this.colorId = colorId;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public Shoe getShoe() {
        return shoe;
    }

    public void setShoe(Shoe shoe) {
        this.shoe = shoe;
        if (shoe != null) {
            this.shoeId = shoe.getShoeId();
        }
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
        if (color != null) {
            this.colorId = color.getColorId();
        }
    }

    @Override
    public String toString() {
        return (shoe != null ? shoe.getModel() : "") + " - " + (color != null ? color.getName() : "");
    }
}
