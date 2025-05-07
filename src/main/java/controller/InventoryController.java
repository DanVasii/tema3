package controller;

import dao.ColorDAO;
import dao.InventoryDAO;
import dao.ManufacturerDAO;
import dao.ShoeTypeDAO;
import dao.StoreDAO;
import model.Color;
import model.Inventory;
import model.Manufacturer;
import model.Shoe;
import model.ShoeType;
import model.ShoeVariant;
import model.Store;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import util.LanguageManager;
import util.Observable;
import util.Observer;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class InventoryController implements Observer {

    @FXML
    private ComboBox<Store> storeComboBox;

    @FXML
    private Button viewAllButton;

    @FXML
    private Label totalItemsLabel;

    @FXML
    private CheckBox availabilityCheckBox;

    @FXML
    private ComboBox<ShoeType> typeComboBox;

    @FXML
    private ComboBox<Color> colorComboBox;

    @FXML
    private ComboBox<Manufacturer> manufacturerComboBox;

    @FXML
    private Button filterButton;

    @FXML
    private Button resetButton;

    @FXML
    private TextField searchField;

    @FXML
    private Button searchButton;

    @FXML
    private TableView<Inventory> inventoryTable;

    @FXML
    private TableColumn<Inventory, String> idColumn;

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
    private TableColumn<Inventory, Boolean> availableColumn;

    private StoreDAO storeDAO;
    private InventoryDAO inventoryDAO;
    private ShoeTypeDAO shoeTypeDAO;
    private ColorDAO colorDAO;
    private ManufacturerDAO manufacturerDAO;
    private ResourceBundle resources;

    private ObservableList<Inventory> inventoryList;

    public void initialize() {

        this.resources = LanguageManager.getResourceBundle();
        storeDAO = new StoreDAO();
        inventoryDAO = new InventoryDAO();
        shoeTypeDAO = new ShoeTypeDAO();
        colorDAO = new ColorDAO();
        manufacturerDAO = new ManufacturerDAO();

        inventoryList = FXCollections.observableArrayList();

        // Initialize ComboBoxes
        loadStores();
        loadShoeTypes();
        loadColors();
        loadManufacturers();

        // Initialize table columns
        setupTableColumns();

        // Initialize button actions
        viewAllButton.setOnAction(event -> handleViewAll());
        filterButton.setOnAction(event -> handleFilter());
        resetButton.setOnAction(event -> handleReset());
        searchButton.setOnAction(event -> handleSearch());

        // Update listener for store selection
        storeComboBox.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    if (newSelection != null) {
                        loadInventory(newSelection.getStoreId());
                    } else {
                        inventoryList.clear();
                        inventoryTable.setItems(inventoryList);
                        updateTotalItemsLabel();
                    }
                });

        // Configure Enter key for search
        searchField.setOnAction(event -> handleSearch());

        // Update UI text based on current language
        updateUIText();
    }

    private void updateUIText() {
        viewAllButton.setText(resources.getString("button.viewall"));
        filterButton.setText(resources.getString("button.filter"));
        resetButton.setText(resources.getString("button.reset"));
        searchButton.setText(resources.getString("button.search"));

        // Update table column headers
        idColumn.setText(resources.getString("inventory.id"));
        modelColumn.setText(resources.getString("inventory.model"));
        manufacturerColumn.setText(resources.getString("shoe.manufacturer"));
        typeColumn.setText(resources.getString("shoe.type"));
        colorColumn.setText(resources.getString("inventory.color"));
        sizeColumn.setText(resources.getString("inventory.size"));
        stockColumn.setText(resources.getString("inventory.stock"));
        availableColumn.setText(resources.getString("inventory.available"));

        // Update search field placeholder
        searchField.setPromptText(resources.getString("inventory.search.placeholder"));

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
            loadInventory(stores.get(0).getStoreId());
        }
    }

    private void loadShoeTypes() {
        List<ShoeType> types = shoeTypeDAO.getAllShoeTypes();
        typeComboBox.setItems(FXCollections.observableArrayList(types));
    }

    private void loadColors() {
        List<Color> colors = colorDAO.getAllColors();
        colorComboBox.setItems(FXCollections.observableArrayList(colors));
    }

    private void loadManufacturers() {
        List<Manufacturer> manufacturers = manufacturerDAO.getAllManufacturers();
        manufacturerComboBox.setItems(FXCollections.observableArrayList(manufacturers));
    }

    private void setupTableColumns() {
        // Configurare coloane tabel
        idColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(String.valueOf(cellData.getValue().getInventoryId())));

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

        availableColumn.setCellValueFactory(cellData -> {
            return new SimpleBooleanProperty(cellData.getValue().getStock() > 0);
        });

        availableColumn.setCellFactory(col -> new javafx.scene.control.cell.CheckBoxTableCell<>());
    }

    private void loadInventory(int storeId) {
        inventoryList.clear();
        List<Inventory> items = inventoryDAO.getInventoryByStore(storeId);
        inventoryList.addAll(items);
        inventoryTable.setItems(inventoryList);
        updateTotalItemsLabel();
    }

    private void updateTotalItemsLabel() {
        totalItemsLabel.setText(resources.getString("statistics.total") + ": " + inventoryList.size());
    }

    @FXML
    public void handleViewAll() {
        Store selectedStore = storeComboBox.getSelectionModel().getSelectedItem();
        if (selectedStore != null) {
            loadInventory(selectedStore.getStoreId());
        }
    }

    @FXML
    public void handleFilter() {
        Store selectedStore = storeComboBox.getSelectionModel().getSelectedItem();
        if (selectedStore == null) {
            return;
        }

        // Verificare filtru disponibilitate
        if (availabilityCheckBox.isSelected()) {
            List<Inventory> filteredItems = inventoryDAO.filterInventoryByAvailability(selectedStore.getStoreId(), true);
            inventoryList.clear();
            inventoryList.addAll(filteredItems);
        }

        // Verificare filtru tip
        ShoeType selectedType = typeComboBox.getSelectionModel().getSelectedItem();
        if (selectedType != null) {
            List<Inventory> filteredItems = inventoryDAO.filterInventoryByType(selectedStore.getStoreId(), selectedType.getTypeId());
            inventoryList.clear();
            inventoryList.addAll(filteredItems);
        }

        // Verificare filtru culoare
        Color selectedColor = colorComboBox.getSelectionModel().getSelectedItem();
        if (selectedColor != null) {
            List<Inventory> filteredItems = inventoryDAO.filterInventoryByColor(selectedStore.getStoreId(), selectedColor.getColorId());
            inventoryList.clear();
            inventoryList.addAll(filteredItems);
        }

        // Verificare filtru producător
        Manufacturer selectedManufacturer = manufacturerComboBox.getSelectionModel().getSelectedItem();
        if (selectedManufacturer != null) {
            List<Inventory> filteredItems = inventoryDAO.filterInventoryByManufacturer(selectedStore.getStoreId(), selectedManufacturer.getManufacturerId());
            inventoryList.clear();
            inventoryList.addAll(filteredItems);
        }

        inventoryTable.setItems(inventoryList);
        updateTotalItemsLabel();
    }

    @FXML
    public void handleReset() {
        // Resetare filtre
        availabilityCheckBox.setSelected(false);
        typeComboBox.getSelectionModel().clearSelection();
        colorComboBox.getSelectionModel().clearSelection();
        manufacturerComboBox.getSelectionModel().clearSelection();
        searchField.clear();

        // Reîncărcare date
        Store selectedStore = storeComboBox.getSelectionModel().getSelectedItem();
        if (selectedStore != null) {
            loadInventory(selectedStore.getStoreId());
        }
    }

    @FXML
    public void handleSearch() {
        String modelName = searchField.getText().trim();

        if (modelName.isEmpty()) {
            return;
        }

        // Deschidere fereastră de căutare
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/shoestoreapp/resources/fxml/search.fxml"));
            loader.setResources(resources);
            Scene scene = new Scene(loader.load());

            SearchController controller = loader.getController();

            Stage searchStage = new Stage();
            searchStage.setTitle(resources.getString("inventory.search.label"));
            searchStage.setScene(scene);
            searchStage.initModality(Modality.APPLICATION_MODAL);

            // Precompletare termen căutare
            controller.setSearchTerm(modelName);

            searchStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}