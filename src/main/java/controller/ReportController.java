package controller;

import dao.InventoryDAO;
import dao.StoreDAO;
import model.Inventory;
import model.Shoe;
import model.ShoeVariant;
import model.Store;
import util.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class ReportController implements Observer {

    @FXML
    private ComboBox<Store> storeComboBox;

    @FXML
    private Button refreshButton;

    @FXML
    private Label totalItemsLabel;

    @FXML
    private TableView<Inventory> outOfStockTable;

    @FXML
    private TableColumn<Inventory, String> shoeColumn;

    @FXML
    private TableColumn<Inventory, String> modelColumn;

    @FXML
    private TableColumn<Inventory, String> manufacturerColumn;

    @FXML
    private TableColumn<Inventory, String> typeColumn;

    @FXML
    private TableColumn<Inventory, String> colorColumn;

    @FXML
    private TableColumn<Inventory, String> sizeColumn;

    @FXML
    private TableColumn<Inventory, String> stockColumn;

    @FXML
    private Button exportCsvButton;

    @FXML
    private Button exportDocButton;

    private StoreDAO storeDAO;
    private InventoryDAO inventoryDAO;
    private ResourceBundle resources;
    private ObservableList<Inventory> outOfStockList;

    public void initialize() {

        this.resources = LanguageManager.getResourceBundle();
        storeDAO = new StoreDAO();
        inventoryDAO = new InventoryDAO();
        outOfStockList = FXCollections.observableArrayList();

        // Initialize store ComboBox
        loadStores();

        // Initialize table columns
        setupTableColumns();

        // Initialize button actions
        refreshButton.setOnAction(event -> handleRefresh());
        exportCsvButton.setOnAction(event -> handleExportCsv());
        exportDocButton.setOnAction(event -> handleExportDoc());

        // Initially disable export buttons
        exportCsvButton.setDisable(true);
        exportDocButton.setDisable(true);

        // Update listener for store selection
        storeComboBox.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    if (newSelection != null) {
                        loadOutOfStockItems(newSelection.getStoreId());
                    } else {
                        outOfStockList.clear();
                        outOfStockTable.setItems(outOfStockList);
                        updateTotalItemsLabel();
                        exportCsvButton.setDisable(true);
                        exportDocButton.setDisable(true);
                    }
                });

        // Update UI text based on current language
        updateUIText();
    }

    private void updateUIText() {
        refreshButton.setText(resources.getString("button.refresh"));
        exportCsvButton.setText(resources.getString("report.exportcsv"));
        exportDocButton.setText(resources.getString("report.exportdoc"));

        // Update table column headers
        shoeColumn.setText(resources.getString("inventory.shoe"));
        modelColumn.setText(resources.getString("inventory.model"));
        manufacturerColumn.setText(resources.getString("shoe.manufacturer"));
        typeColumn.setText(resources.getString("shoe.type"));
        colorColumn.setText(resources.getString("inventory.color"));
        sizeColumn.setText(resources.getString("inventory.size"));
        stockColumn.setText(resources.getString("inventory.stock"));

        // Update total items label
        updateTotalItemsLabel();
    }

    @Override
    public void update(Observable observable, Object data) {
        if (data instanceof Locale) {
            // Update resources when language changes
            resources = LanguageManager.getResourceBundle();

            // Update UI texts
            updateUIText();
        }
    }


    private void loadStores() {
        List<Store> stores = storeDAO.getAllStores();
        storeComboBox.setItems(FXCollections.observableArrayList(stores));

        if (!stores.isEmpty()) {
            storeComboBox.getSelectionModel().selectFirst();
            loadOutOfStockItems(stores.get(0).getStoreId());
        }
    }

    private void setupTableColumns() {
        // Configurare coloane tabel
        shoeColumn.setCellValueFactory(cellData -> {
            ShoeVariant variant = cellData.getValue().getVariant();
            Shoe shoe = variant != null ? variant.getShoe() : null;
            return new SimpleStringProperty(shoe != null ? shoe.getModel() : "");
        });

        modelColumn.setCellValueFactory(cellData -> {
            ShoeVariant variant = cellData.getValue().getVariant();
            Shoe shoe = variant != null ? variant.getShoe() : null;
            return new SimpleStringProperty(shoe != null ? shoe.getModel() : "");
        });

        manufacturerColumn.setCellValueFactory(cellData -> {
            ShoeVariant variant = cellData.getValue().getVariant();
            Shoe shoe = variant != null ? variant.getShoe() : null;
            return new SimpleStringProperty(shoe != null && shoe.getManufacturer() != null ?
                    shoe.getManufacturer().getName() : "");
        });

        typeColumn.setCellValueFactory(cellData -> {
            ShoeVariant variant = cellData.getValue().getVariant();
            Shoe shoe = variant != null ? variant.getShoe() : null;
            return new SimpleStringProperty(shoe != null && shoe.getType() != null ?
                    shoe.getType().getName() : "");
        });

        colorColumn.setCellValueFactory(cellData -> {
            ShoeVariant variant = cellData.getValue().getVariant();
            return new SimpleStringProperty(variant != null && variant.getColor() != null ?
                    variant.getColor().getName() : "");
        });

        sizeColumn.setCellValueFactory(cellData -> {
            return new SimpleStringProperty(cellData.getValue().getSize() != null ?
                    String.valueOf(cellData.getValue().getSize().getEuSize()) : "");
        });

        stockColumn.setCellValueFactory(cellData -> {
            return new SimpleStringProperty(String.valueOf(cellData.getValue().getStock()));
        });
    }

    private void loadOutOfStockItems(int storeId) {
        outOfStockList.clear();
        List<Inventory> items = inventoryDAO.getOutOfStockItemsByStore(storeId);
        outOfStockList.addAll(items);
        outOfStockTable.setItems(outOfStockList);
        updateTotalItemsLabel();

        // Activare butoane export doar dacă există produse
        boolean hasItems = !items.isEmpty();
        exportCsvButton.setDisable(!hasItems);
        exportDocButton.setDisable(!hasItems);
    }

    private void updateTotalItemsLabel() {
        totalItemsLabel.setText(resources.getString("statistics.total") + ": " + outOfStockList.size());
    }

    @FXML
    public void handleRefresh() {
        Store selectedStore = storeComboBox.getSelectionModel().getSelectedItem();
        if (selectedStore != null) {
            loadOutOfStockItems(selectedStore.getStoreId());
        }
    }

    @FXML
    public void handleExportCsv() {
        Store selectedStore = storeComboBox.getSelectionModel().getSelectedItem();
        if (selectedStore == null || outOfStockList.isEmpty()) {
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(resources.getString("report.exportcsv"));
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        fileChooser.setInitialFileName("out_of_stock_" + selectedStore.getName().replaceAll("\\s+", "_") + ".csv");

        File file = fileChooser.showSaveDialog(exportCsvButton.getScene().getWindow());
        if (file != null) {
            boolean success = CsvExporter.exportOutOfStockItems(file.getAbsolutePath(), outOfStockList);

            if (success) {
                showAlert(Alert.AlertType.INFORMATION,
                        resources.getString("report.success.title"),
                        resources.getString("report.success.header"),
                        resources.getString("report.success.content"));
            } else {
                showAlert(Alert.AlertType.ERROR,
                        resources.getString("report.error.title"),
                        resources.getString("report.error.header"),
                        resources.getString("report.error.content"));
            }
        }
    }

    @FXML
    public void handleExportDoc() {
        Store selectedStore = storeComboBox.getSelectionModel().getSelectedItem();
        if (selectedStore == null || outOfStockList.isEmpty()) {
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(resources.getString("report.exportdoc"));
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("DOC Files", "*.doc"));
        fileChooser.setInitialFileName("out_of_stock_" + selectedStore.getName().replaceAll("\\s+", "_") + ".doc");

        File file = fileChooser.showSaveDialog(exportDocButton.getScene().getWindow());
        if (file != null) {
            boolean success = DocExporter.exportOutOfStockItems(file.getAbsolutePath(), outOfStockList, selectedStore.getName());

            if (success) {
                showAlert(Alert.AlertType.INFORMATION,
                        resources.getString("report.success.title"),
                        resources.getString("report.success.header"),
                        resources.getString("report.success.content"));
            } else {
                showAlert(Alert.AlertType.ERROR,
                        resources.getString("report.error.title"),
                        resources.getString("report.error.header"),
                        resources.getString("report.error.content"));
            }
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String header, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}