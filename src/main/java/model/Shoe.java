package model;

public class Shoe {
    private int shoeId;
    private String model;
    private int manufacturerId;
    private int typeId;
    private double price;
    private String description;

    // Pentru afișarea în UI
    private Manufacturer manufacturer;
    private ShoeType type;

    public Shoe() {
    }

    public Shoe(int shoeId, String model, int manufacturerId, int typeId, double price, String description) {
        this.shoeId = shoeId;
        this.model = model;
        this.manufacturerId = manufacturerId;
        this.typeId = typeId;
        this.price = price;
        this.description = description;
    }

    // Getteri și setteri
    public int getShoeId() {
        return shoeId;
    }

    public void setShoeId(int shoeId) {
        this.shoeId = shoeId;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public int getManufacturerId() {
        return manufacturerId;
    }

    public void setManufacturerId(int manufacturerId) {
        this.manufacturerId = manufacturerId;
    }

    public int getTypeId() {
        return typeId;
    }

    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Manufacturer getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(Manufacturer manufacturer) {
        this.manufacturer = manufacturer;
        if (manufacturer != null) {
            this.manufacturerId = manufacturer.getManufacturerId();
        }
    }

    public ShoeType getType() {
        return type;
    }

    public void setType(ShoeType type) {
        this.type = type;
        if (type != null) {
            this.typeId = type.getTypeId();
        }
    }

    @Override
    public String toString() {
        return model + " (" + (manufacturer != null ? manufacturer.getName() : "") + ")";
    }
}
