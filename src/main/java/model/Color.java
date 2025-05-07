package model;

public class Color {
    private int colorId;
    private String name;
    private String hexCode;

    public Color() {
    }

    public Color(int colorId, String name, String hexCode) {
        this.colorId = colorId;
        this.name = name;
        this.hexCode = hexCode;
    }

    // Getteri și setteri
    public int getColorId() {
        return colorId;
    }

    public void setColorId(int colorId) {
        this.colorId = colorId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHexCode() {
        return hexCode;
    }

    public void setHexCode(String hexCode) {
        this.hexCode = hexCode;
    }

    @Override
    public String toString() {
        return name;
    }
}
