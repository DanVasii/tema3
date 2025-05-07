package view;

import javafx.fxml.FXML;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import util.LanguageManager;
import util.Observable;
import util.Observer;

import java.util.Locale;
import java.util.ResourceBundle;

public class MainView implements Observer {

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
        // Update UI with current language
        updateUITexts();
    }

    private void updateUITexts() {
        ResourceBundle resources = LanguageManager.getResourceBundle();

        // Update tab texts
        storesTab.setText(resources.getString("tab.stores"));
        shoesTab.setText(resources.getString("tab.shoes"));
        inventoryTab.setText(resources.getString("tab.inventory"));
        reportsTab.setText(resources.getString("tab.reports"));
        statisticsTab.setText(resources.getString("tab.statistics"));
    }

    @Override
    public void update(Observable observable, Object data) {
        if (data instanceof Locale) {
            // Update UI texts when language changes
            updateUITexts();
        }
    }
}