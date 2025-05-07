package model;

public class Size {
    private int sizeId;
    private double euSize;
    private double ukSize;
    private double usSize;

    public Size() {
    }

    public Size(int sizeId, double euSize, double ukSize, double usSize) {
        this.sizeId = sizeId;
        this.euSize = euSize;
        this.ukSize = ukSize;
        this.usSize = usSize;
    }

    // Getteri și setteri
    public int getSizeId() {
        return sizeId;
    }

    public void setSizeId(int sizeId) {
        this.sizeId = sizeId;
    }

    public double getEuSize() {
        return euSize;
    }

    public void setEuSize(double euSize) {
        this.euSize = euSize;
    }

    public double getUkSize() {
        return ukSize;
    }

    public void setUkSize(double ukSize) {
        this.ukSize = ukSize;
    }

    public double getUsSize() {
        return usSize;
    }

    public void setUsSize(double usSize) {
        this.usSize = usSize;
    }

    @Override
    public String toString() {
        return "EU: " + euSize;
    }
}

