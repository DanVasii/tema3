package model;

public class Manufacturer {
    private int manufacturerId;
    private String name;
    private String country;
    private String website;

    public Manufacturer() {
    }

    public Manufacturer(int manufacturerId, String name, String country, String website) {
        this.manufacturerId = manufacturerId;
        this.name = name;
        this.country = country;
        this.website = website;
    }

    // Getteri și setteri
    public int getManufacturerId() {
        return manufacturerId;
    }

    public void setManufacturerId(int manufacturerId) {
        this.manufacturerId = manufacturerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    @Override
    public String toString() {
        return name;
    }
}
