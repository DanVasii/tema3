package controller;

import dao.StoreDAO;
import javafx.fxml.Initializable;
import model.Store;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import util.LanguageManager;
import util.Observable;
import util.Observer;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

public class StoreController  implements Observer {

    @FXML
    private TableView<Store> storeTable;

    @FXML
    private TableColumn<Store, Integer> idColumn;

    @FXML
    private TableColumn<Store, String> nameColumn;

    @FXML
    private TableColumn<Store, String> addressColumn;

    @FXML
    private TableColumn<Store, String> phoneColumn;

    @FXML
    private TableColumn<Store, String> emailColumn;

    @FXML
    private TableColumn<Store, String> hoursColumn;

    @FXML
    private TextField nameField;

    @FXML
    private TextField addressField;

    @FXML
    private TextField phoneField;

    @FXML
    private TextField emailField;

    @FXML
    private TextField hoursField;

    @FXML
    private Button addButton;

    @FXML
    private Button updateButton;

    @FXML
    private Button deleteButton;

    @FXML
    private Button clearButton;

    private StoreDAO storeDAO;
    private ObservableList<Store> storeList;
    private ResourceBundle resources;

    public void initialize() {

        this.resources = LanguageManager.getResourceBundle();
        storeDAO = new StoreDAO();
        storeList = FXCollections.observableArrayList();

        // Initialize table columns
        idColumn.setCellValueFactory(new PropertyValueFactory<>("storeId"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        addressColumn.setCellValueFactory(new PropertyValueFactory<>("address"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        hoursColumn.setCellValueFactory(new PropertyValueFactory<>("openingHours"));

        // Add listener for selection
        storeTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    if (newSelection != null) {
                        showStoreDetails(newSelection);
                        updateButton.setDisable(false);
                        deleteButton.setDisable(false);
                    } else {
                        clearFields();
                        updateButton.setDisable(true);
                        deleteButton.setDisable(true);
                    }
                });

        // Disable update and delete buttons until user selects a store
        updateButton.setDisable(true);
        deleteButton.setDisable(true);

        // Load stores
        loadStores();

        // Update UI text based on current language
        updateUIText();
    }

    private void updateUIText() {
        addButton.setText(resources.getString("button.add"));
        updateButton.setText(resources.getString("button.update"));
        deleteButton.setText(resources.getString("button.delete"));
        clearButton.setText(resources.getString("button.clear"));

        // Update table column headers
        idColumn.setText(resources.getString("store.id"));
        nameColumn.setText(resources.getString("store.name"));
        addressColumn.setText(resources.getString("store.address"));
        phoneColumn.setText(resources.getString("store.phone"));
        emailColumn.setText(resources.getString("store.email"));
        hoursColumn.setText(resources.getString("store.hours"));
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
        System.out.println("Loading Stores");
        storeList.clear();
        List<Store> stores = storeDAO.getAllStores();
        storeList.addAll(stores);
        storeTable.setItems(storeList);
    }

    private void showStoreDetails(Store store) {
        nameField.setText(store.getName());
        addressField.setText(store.getAddress());
        phoneField.setText(store.getPhone());
        emailField.setText(store.getEmail());
        hoursField.setText(store.getOpeningHours());
    }

    private void clearFields() {
        nameField.clear();
        addressField.clear();
        phoneField.clear();
        emailField.clear();
        hoursField.clear();
        storeTable.getSelectionModel().clearSelection();
        updateButton.setDisable(true);
        deleteButton.setDisable(true);
    }

    @FXML
    private void handleAddButton() {
        if (validateInput()) {
            Store store = new Store();
            store.setName(nameField.getText());
            store.setAddress(addressField.getText());
            store.setPhone(phoneField.getText());
            store.setEmail(emailField.getText());
            store.setOpeningHours(hoursField.getText());

            if (storeDAO.addStore(store)) {
                showAlert(Alert.AlertType.INFORMATION,
                        resources.getString("store.add.success.title"),
                        resources.getString("store.add.success.header"),
                        resources.getString("store.add.success.content"));
                loadStores();
                clearFields();
            } else {
                showAlert(Alert.AlertType.ERROR,
                        resources.getString("store.add.error.title"),
                        resources.getString("store.add.error.header"),
                        resources.getString("store.add.error.content"));
            }
        }
    }

    @FXML
    private void handleUpdateButton() {
        Store selectedStore = storeTable.getSelectionModel().getSelectedItem();
        if (selectedStore != null && validateInput()) {
            selectedStore.setName(nameField.getText());
            selectedStore.setAddress(addressField.getText());
            selectedStore.setPhone(phoneField.getText());
            selectedStore.setEmail(emailField.getText());
            selectedStore.setOpeningHours(hoursField.getText());

            if (storeDAO.updateStore(selectedStore)) {
                showAlert(Alert.AlertType.INFORMATION,
                        resources.getString("store.update.success.title"),
                        resources.getString("store.update.success.header"),
                        resources.getString("store.update.success.content"));
                loadStores();
                clearFields();
            } else {
                showAlert(Alert.AlertType.ERROR,
                        resources.getString("store.update.error.title"),
                        resources.getString("store.update.error.header"),
                        resources.getString("store.update.error.content"));
            }
        }
    }

    @FXML
    private void handleDeleteButton() {
        Store selectedStore = storeTable.getSelectionModel().getSelectedItem();
        if (selectedStore != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle(resources.getString("store.delete.confirm.title"));
            alert.setHeaderText(resources.getString("store.delete.confirm.header"));
            alert.setContentText(resources.getString("store.delete.confirm.content"));

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                if (storeDAO.deleteStore(selectedStore.getStoreId())) {
                    showAlert(Alert.AlertType.INFORMATION,
                            resources.getString("store.delete.success.title"),
                            resources.getString("store.delete.success.header"),
                            resources.getString("store.delete.success.content"));
                    loadStores();
                    clearFields();
                } else {
                    showAlert(Alert.AlertType.ERROR,
                            resources.getString("store.delete.error.title"),
                            resources.getString("store.delete.error.header"),
                            resources.getString("store.delete.error.content"));
                }
            }
        }
    }

    @FXML
    private void handleClearButton() {
        clearFields();
    }

    private boolean validateInput() {
        String errorMessage = "";

        if (nameField.getText() == null || nameField.getText().isEmpty()) {
            errorMessage += resources.getString("validation.name.empty") + "\n";
        }

        if (addressField.getText() == null || addressField.getText().isEmpty()) {
            errorMessage += resources.getString("validation.address.empty") + "\n";
        }

        if (phoneField.getText() == null || phoneField.getText().isEmpty()) {
            errorMessage += resources.getString("validation.phone.empty") + "\n";
        }

        if (errorMessage.isEmpty()) {
            return true;
        } else {
            showAlert(Alert.AlertType.ERROR,
                    resources.getString("validation.error.title"),
                    resources.getString("validation.error.header"),
                    errorMessage);
            return false;
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