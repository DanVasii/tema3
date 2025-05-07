package util;

import model.Inventory;
import model.Shoe;
import model.Store;
import model.ShoeVariant;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;

import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class DocExporter {

    public static boolean exportOutOfStockItems(String filePath, List<Inventory> outOfStockItems, String storeName) {
        try (XWPFDocument document = new XWPFDocument()) {
            // Adăugare titlu
            XWPFParagraph titleParagraph = document.createParagraph();
            XWPFRun titleRun = titleParagraph.createRun();
            titleRun.setText("Out of Stock Items Report");
            titleRun.setBold(true);
            titleRun.setFontSize(16);

            // Adăugare informații raport
            XWPFParagraph infoParagraph = document.createParagraph();
            XWPFRun infoRun = infoParagraph.createRun();
            infoRun.setText("Store: " + storeName);
            infoRun.addBreak();
            infoRun.setText("Date: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            infoRun.addBreak();
            infoRun.setText("Total items out of stock: " + outOfStockItems.size());

            // Adăugare tabel
            XWPFTable table = document.createTable();

            // Creare antet tabel
            XWPFTableRow headerRow = table.getRow(0);
            headerRow.getCell(0).setText("Model");
            headerRow.addNewTableCell().setText("Manufacturer");
            headerRow.addNewTableCell().setText("Type");
            headerRow.addNewTableCell().setText("Color");
            headerRow.addNewTableCell().setText("Size (EU)");

            // Creare rânduri tabel
            for (Inventory item : outOfStockItems) {
                ShoeVariant variant = item.getVariant();
                Shoe shoe = variant != null ? variant.getShoe() : null;

                if (shoe != null) {
                    XWPFTableRow row = table.createRow();
                    row.getCell(0).setText(shoe.getModel());
                    row.getCell(1).setText(shoe.getManufacturer() != null ? shoe.getManufacturer().getName() : "");
                    row.getCell(2).setText(shoe.getType() != null ? shoe.getType().getName() : "");
                    row.getCell(3).setText(variant.getColor() != null ? variant.getColor().getName() : "");
                    row.getCell(4).setText(item.getSize() != null ? String.valueOf(item.getSize().getEuSize()) : "");
                }
            }

            // Scriere document
            try (FileOutputStream out = new FileOutputStream(filePath)) {
                document.write(out);
            }

            return true;
        } catch (IOException e) {
            System.err.println("Error exporting to DOC: " + e.getMessage());
            return false;
        }
    }
}

