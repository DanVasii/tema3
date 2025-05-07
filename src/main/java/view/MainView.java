// MainView.java - exemplu de controller care implementează Observer
package view;

import javafx.fxml.FXML;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import util.LanguageManager;
import util.Observable;
import util.Observer;

import java.util.Locale;
import java.util.ResourceBundle;

public class MainView {

    @FXML
    private TabPane mainTabPane;

    @FXML
    private Tab storesTab;

    @FXML
    private Tab shoesTab;

    @FXML
    private Tab inventoryTab;

    @FXML
    private Tab reportsTab;

    @FXML
    private Tab statisticsTab;

    public void initialize() {
        // Actualizare UI cu limba curentă
        updateUITexts();
    }

    private void updateUITexts() {
        ResourceBundle resources = LanguageManager.getResourceBundle();

        // Actualizare text tab-uri
        storesTab.setText(resources.getString("tab.stores"));
        shoesTab.setText(resources.getString("tab.shoes"));
        inventoryTab.setText(resources.getString("tab.inventory"));
        reportsTab.setText(resources.getString("tab.reports"));
        statisticsTab.setText(resources.getString("tab.statistics"));
    }


}