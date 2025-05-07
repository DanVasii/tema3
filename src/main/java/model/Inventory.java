package model;

public class Inventory {
    private int inventoryId;
    private int storeId;
    private int variantId;
    private int sizeId;
    private int stock;

    // Pentru afișarea în UI
    private Store store;
    private ShoeVariant variant;
    private Size size;

    public Inventory() {
    }

    public Inventory(int inventoryId, int storeId, int variantId, int sizeId, int stock) {
        this.inventoryId = inventoryId;
        this.storeId = storeId;
        this.variantId = variantId;
        this.sizeId = sizeId;
        this.stock = stock;
    }

    // Getteri și setteri
    public int getInventoryId() {
        return inventoryId;
    }

    public void setInventoryId(int inventoryId) {
        this.inventoryId = inventoryId;
    }

    public int getStoreId() {
        return storeId;
    }

    public void setStoreId(int storeId) {
        this.storeId = storeId;
    }

    public int getVariantId() {
        return variantId;
    }

    public void setVariantId(int variantId) {
        this.variantId = variantId;
    }

    public int getSizeId() {
        return sizeId;
    }

    public void setSizeId(int sizeId) {
        this.sizeId = sizeId;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public Store getStore() {
        return store;
    }

    public void setStore(Store store) {
        this.store = store;
        if (store != null) {
            this.storeId = store.getStoreId();
        }
    }

    public ShoeVariant getVariant() {
        return variant;
    }

    public void setVariant(ShoeVariant variant) {
        this.variant = variant;
        if (variant != null) {
            this.variantId = variant.getVariantId();
        }
    }

    public Size getSize() {
        return size;
    }

    public void setSize(Size size) {
        this.size = size;
        if (size != null) {
            this.sizeId = size.getSizeId();
        }
    }

    public boolean isAvailable() {
        return stock > 0;
    }
}