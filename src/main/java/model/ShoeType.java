package model;

public class ShoeType {
    private int typeId;
    private String name;

    public ShoeType() {
    }

    public ShoeType(int typeId, String name) {
        this.typeId = typeId;
        this.name = name;
    }

    // Getteri și setteri
    public int getTypeId() {
        return typeId;
    }

    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
