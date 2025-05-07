package model;


public class ChartData {
    private String category;
    private int value;

    /**
     * Constructor
     * @param category Categoria (etichetă pe axa X sau numele segmentului de pie chart)
     * @param value Valoarea numerică asociată categoriei
     */
    public ChartData(String category, int value) {
        this.category = category;
        this.value = value;
    }

    /**
     * Obține categoria
     * @return Categoria
     */
    public String getCategory() {
        return category;
    }

    /**
     * Setează categoria
     * @param category Categoria
     */
    public void setCategory(String category) {
        this.category = category;
    }

    /**
     * Obține valoarea
     * @return Valoarea
     */
    public int getValue() {
        return value;
    }

    /**
     * Setează valoarea
     * @param value Valoarea
     */
    public void setValue(int value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return category + ": " + value;
    }
}