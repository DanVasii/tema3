package util;

import model.Inventory;
import model.Shoe;
import model.ShoeVariant;
import model.Store;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class CsvExporter {

    public static boolean exportOutOfStockItems(String filePath, List<Inventory> outOfStockItems) {
        try (FileWriter writer = new FileWriter(filePath)) {
            // Scriere antet
            writer.append("Store,Shoe Model,Manufacturer,Type,Color,Size,Stock\n");

            // Scriere date
            for (Inventory item : outOfStockItems) {
                ShoeVariant variant = item.getVariant();
                Shoe shoe = variant != null ? variant.getShoe() : null;
                Store store = item.getStore();

                if (shoe != null && store != null) {
                    StringBuilder line = new StringBuilder();
                    line.append(escapeSpecialCharacters(store.getName())).append(",");
                    line.append(escapeSpecialCharacters(shoe.getModel())).append(",");
                    line.append(escapeSpecialCharacters(shoe.getManufacturer() != null ? shoe.getManufacturer().getName() : "")).append(",");
                    line.append(escapeSpecialCharacters(shoe.getType() != null ? shoe.getType().getName() : "")).append(",");
                    line.append(escapeSpecialCharacters(variant.getColor() != null ? variant.getColor().getName() : "")).append(",");
                    line.append(escapeSpecialCharacters(item.getSize() != null ? String.valueOf(item.getSize().getEuSize()) : "")).append(",");
                    line.append(item.getStock()).append("\n");

                    writer.append(line.toString());
                }
            }

            writer.flush();
            return true;
        } catch (IOException e) {
            System.err.println("Error exporting to CSV: " + e.getMessage());
            return false;
        }
    }

    private static String escapeSpecialCharacters(String data) {
        if (data == null) {
            return "";
        }

        String result = data.replace("\"", "\"\"");
        if (result.contains(",") || result.contains("\"") || result.contains("\n")) {
            result = "\"" + result + "\"";
        }
        return result;
    }
}
